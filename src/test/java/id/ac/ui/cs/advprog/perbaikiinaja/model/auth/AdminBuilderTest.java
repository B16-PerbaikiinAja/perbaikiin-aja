package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

public class AdminBuilderTest {

    private AdminBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new AdminBuilder();
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
        assertEquals(UserRole.ADMIN.getValue(), user.getRole());
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
}
