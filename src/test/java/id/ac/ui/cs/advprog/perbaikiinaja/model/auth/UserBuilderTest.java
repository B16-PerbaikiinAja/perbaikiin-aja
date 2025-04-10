package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserBuilderTest {

    private TestUserBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TestUserBuilder();
    }

    @Test
    void testChainedCalls() {
        User user = builder
                .fullName("Test User")
                .email("test@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .build();

        assertEquals("Test User", user.getFullName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("9876543210", user.getPhoneNumber());
        assertEquals("TEST_ROLE", user.getRole());
    }

    @Test
    void testSelfReturnsBuilderInstance() {
        TestUserBuilder self = builder.self();
        assertSame(builder, self);
    }

    @Test
    void testFullNameValidation_EmptyName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.fullName("");
        });
        assertEquals("Full Name cannot be empty", exception.getMessage());
    }

    @Test
    void testFullNameValidation_NullName() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.fullName(null);
        });
        assertEquals("Full Name cannot be empty", exception.getMessage());
    }

    @Test
    void testEmailValidation_EmptyEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.email("");
        });
        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @Test
    void testEmailValidation_NullEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.email(null);
        });
        assertEquals("Email cannot be empty", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "user@",
            "@domain.com",
            "user@domain",
            "user@.com",
            "user name@domain.com",
    })
    void testEmailValidation_InvalidFormat(String invalidEmail) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.email(invalidEmail);
        });
        assertEquals("Invalid email", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@domain.com",
            "user.name@domain.com",
            "user+name@domain.com",
            "user-name@domain.com",
            "user_name@domain.com",
            "user123@domain.co.uk"
    })
    void testEmailValidation_ValidFormats(String validEmail) {
        assertDoesNotThrow(() -> builder.email(validEmail));
    }

    @Test
    void testPasswordValidation_TooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.password("1234567"); // 7 characters
        });
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    void testPasswordValidation_ExactlyMinLength() {
        assertDoesNotThrow(() -> builder.password("12345678"));
    }

    @Test
    void testPasswordValidation_LongerPassword() {
        assertDoesNotThrow(() -> builder.password("ThisIsAVeryLongPasswordForTesting123!"));
    }

    @Test
    void testPhoneNumberValidation_EmptyNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.phoneNumber("");
        });
        assertEquals("Phone Number cannot be empty", exception.getMessage());
    }

    @Test
    void testPhoneNumberValidation_NullNumber() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.phoneNumber(null);
        });
        assertEquals("Phone Number cannot be empty", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123-456-7890",
            "+62 812 3456 7890",
            "(123) 456-7890",
            "123.456.7890",
            "abc123456",
            "123abc456"
    })
    void testPhoneNumberValidation_NonNumeric(String invalidPhone) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.phoneNumber(invalidPhone);
        });
        assertEquals("Phone Number must be numeric", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890",
            "08123456789",
            "628123456789"
    })
    void testPhoneNumberValidation_ValidNumeric(String validPhone) {
        assertDoesNotThrow(() -> builder.phoneNumber(validPhone));
    }

    @Test
    void testCompleteValidBuild() {
        User user = builder
                .fullName("Valid Name")
                .email("valid@example.com")
                .password("validpassword123")
                .phoneNumber("1234567890")
                .build();

        assertEquals("Valid Name", user.getFullName());
        assertEquals("valid@example.com", user.getEmail());
        assertEquals("validpassword123", user.getPassword());
        assertEquals("1234567890", user.getPhoneNumber());
    }

    @Test
    void testBuildSequence() {
        builder.fullName("First User")
                .email("first@example.com")
                .password("password123")
                .phoneNumber("1234567890");
        User firstUser = builder.build();

        builder.fullName("Second User")
                .email("second@example.com");
        User secondUser = builder.build();

        assertEquals("First User", firstUser.getFullName());
        assertEquals("first@example.com", firstUser.getEmail());

        assertEquals("Second User", secondUser.getFullName());
        assertEquals("second@example.com", secondUser.getEmail());
        assertEquals("password123", secondUser.getPassword());
        assertEquals("1234567890", secondUser.getPhoneNumber());
    }

    @Test
    void testMissingRequiredFieldsOnBuild() {
        builder.password("validpassword")
                .phoneNumber("1234567890");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            builder.build();
        }, "Cannot build User: missing required field(s): fullName, email,");
    }
}
