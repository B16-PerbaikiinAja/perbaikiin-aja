package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {
    
    private Admin admin;
    
    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setFullName("Test Admin");
        admin.setEmail("test@example.com");
        admin.setPassword("password123");
        admin.setPhoneNumber("1234567890");
    }
    
    @Test
    void testGetterMethods() {
        assertEquals("Test Admin", admin.getFullName());
        assertEquals("test@example.com", admin.getEmail());
        assertEquals("password123", admin.getPassword());
        assertEquals("1234567890", admin.getPhoneNumber());
        assertEquals("TEST_ROLE", admin.getRole());
    }
    
    @Test
    void testAdminDetailsImplementation() {
        assertEquals("test@example.com", admin.getUsername());
        assertTrue(admin.isAccountNonExpired());
        assertTrue(admin.isAccountNonLocked());
        assertTrue(admin.isCredentialsNonExpired());
        assertTrue(admin.isEnabled());
        assertTrue(admin.getAuthorities().isEmpty());
    }
    
    @Test
    void testSetterMethods() {
        admin.setFullName("New Name");
        admin.setEmail("new@example.com");
        admin.setPassword("newPassword");
        admin.setPhoneNumber("9876543210");
        admin.setRole("NEW_ROLE");
        
        assertEquals("New Name", admin.getFullName());
        assertEquals("new@example.com", admin.getEmail());
        assertEquals("newPassword", admin.getPassword());
        assertEquals("9876543210", admin.getPhoneNumber());
        assertEquals("NEW_ROLE", admin.getRole());
        assertEquals("new@example.com", admin.getUsername());
    }
}
