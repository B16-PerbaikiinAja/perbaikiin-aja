package id.ac.ui.cs.advprog.perbaikiinaja.repository.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.config.TestRepositorySecurityConfig;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestRepositorySecurityConfig.class)
class UserRepositoryTest {

    @TestConfiguration
    @EntityScan(basePackages = "id.ac.ui.cs.advprog.perbaikiinaja.model.auth")
    @EnableJpaRepositories(basePackages = "id.ac.ui.cs.advprog.perbaikiinaja.repository.auth")
    static class Config {}

    @Autowired
    private UserRepository userRepository;

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setFullName("Test Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password123");
        admin.setPhoneNumber("1234567890");
        admin.setRole(UserRole.ADMIN.getValue());
    }

    @Test
    void testSaveAndFindById() {
        User saved = userRepository.save(admin);
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Admin", found.get().getFullName());
    }

    @Test
    void testFindByEmail() {
        userRepository.save(admin);
        Optional<User> found = userRepository.findByEmail("admin@example.com");
        assertTrue(found.isPresent());
        assertEquals("Test Admin", found.get().getFullName());
    }

    @Test
    void testDelete() {
        User saved = userRepository.save(admin);
        userRepository.delete(saved);
        Optional<User> found = userRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
