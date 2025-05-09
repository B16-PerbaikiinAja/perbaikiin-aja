package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    
    private TestUser user;
    
    @BeforeEach
    void setUp() {
        user = new TestUser();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
    }
    
    @Test
    void testGetterMethods() {
        assertEquals("Test User", user.getFullName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("TEST_ROLE", user.getRole());
    }
    
    @Test
    void testUserDetailsImplementation() {
        assertEquals("test@example.com", user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertTrue(user.getAuthorities().isEmpty());
    }
    
    @Test
    void testSetterMethods() {
        user.setFullName("New Name");
        user.setEmail("new@example.com");
        user.setPassword("newPassword");
        user.setPhoneNumber("9876543210");
        user.setRole("NEW_ROLE");
        
        assertEquals("New Name", user.getFullName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newPassword", user.getPassword());
        assertEquals("9876543210", user.getPhoneNumber());
        assertEquals("NEW_ROLE", user.getRole());
        assertEquals("new@example.com", user.getUsername());
    }
}
