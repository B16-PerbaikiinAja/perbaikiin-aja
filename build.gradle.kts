plugins {
    java
    id("jacoco")
    id("org.sonarqube") version "6.0.1.5171"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

tasks.bootJar{
    mainClass.set("id.ac.ui.cs.advprog.perbaikiinaja.PerbaikiinAjaApplication")
}

sonar {
  properties {
    property("sonar.projectKey", "B16-Perbaikiin-aja")
    property("sonar.projectName", "B16-Perbaikiin-aja")
  }
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val junitJupiterVersion = "5.9.1"
val jjwtVersion = "0.11.5"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.security:spring-security-test")
}

tasks.register<Test>("unitTest") {
	description = "Run unit tests."
	group = "verification"
	filter {
		excludeTestsMatching("*FunctionalTest")
	}
}


tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

tasks.test {
	filter {
		excludeTestsMatching("*FunctionalTest")
	}

	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		html.required = true
		xml.required = true
	}
}