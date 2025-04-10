package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserBuilderTest {
    
    private TestUserBuilder builder;
    
    @BeforeEach
    void setUp() {
        builder = new TestUserBuilder();
    }
    
    @Test
    void testFullNameBuilder() {
        String fullName = "Test User";
        builder.fullName(fullName);
        User user = builder.build();
        assertEquals(fullName, user.getFullName());
    }
    
    @Test
    void testEmailBuilder() {
        String email = "test@example.com";
        builder.email(email);
        User user = builder.build();
        assertEquals(email, user.getEmail());
        assertEquals(email, user.getUsername());
    }
    
    @Test
    void testPasswordBuilder() {
        String password = "securePassword123";
        builder.password(password);
        User user = builder.build();
        assertEquals(password, user.getPassword());
    }
    
    @Test
    void testPhoneNumberBuilder() {
        String phoneNumber = "1234567890";
        builder.phoneNumber(phoneNumber);
        User user = builder.build();
        assertEquals(phoneNumber, user.getPhoneNumber());
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
}
