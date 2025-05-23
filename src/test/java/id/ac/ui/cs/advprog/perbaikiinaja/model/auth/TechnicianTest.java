package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

class TechnicianTest {
    
    private Technician technician;
    
    @BeforeEach
    void setUp() {
        technician = new Technician();
        technician.setFullName("Test Technician");
        technician.setEmail("tech@example.com");
        technician.setPassword("password123");
        technician.setPhoneNumber("1234567890");
        technician.setRole(UserRole.TECHNICIAN.getValue());
        technician.setAddress("456 Tech Street");
        technician.setCompletedJobs(10);
        technician.setTotalEarnings(500000);
    }
    
    @Test
    void testGetterMethods() {
        assertEquals("Test Technician", technician.getFullName());
        assertEquals("tech@example.com", technician.getEmail());
        assertEquals("password123", technician.getPassword());
        assertEquals("1234567890", technician.getPhoneNumber());
        assertEquals(UserRole.TECHNICIAN.getValue(), technician.getRole());
        assertEquals("456 Tech Street", technician.getAddress());
        assertEquals(10, technician.getCompletedJobs());
        assertEquals(500000, technician.getTotalEarnings());
    }
    
    @Test
    void testTechnicianDetailsImplementation() {
        assertEquals("tech@example.com", technician.getUsername());
        assertTrue(technician.isAccountNonExpired());
        assertTrue(technician.isAccountNonLocked());
        assertTrue(technician.isCredentialsNonExpired());
        assertTrue(technician.isEnabled());
        assertFalse(technician.getAuthorities().isEmpty());
    }
    
    @Test
    void testSetterMethods() {
        technician.setFullName("New Technician");
        technician.setEmail("new.tech@example.com");
        technician.setPassword("newPassword");
        technician.setPhoneNumber("9876543210");
        technician.setRole("NEW_ROLE");
        technician.setAddress("789 New Street");
        technician.setCompletedJobs(15);
        technician.setTotalEarnings(750000);
        
        assertEquals("New Technician", technician.getFullName());
        assertEquals("new.tech@example.com", technician.getEmail());
        assertEquals("newPassword", technician.getPassword());
        assertEquals("9876543210", technician.getPhoneNumber());
        assertEquals("NEW_ROLE", technician.getRole());
        assertEquals("789 New Street", technician.getAddress());
        assertEquals(15, technician.getCompletedJobs());
        assertEquals(750000, technician.getTotalEarnings());
        assertEquals("new.tech@example.com", technician.getUsername());
    }
    
    @Test
    void testNullableAddress() {
        Technician techWithNullAddress = new Technician();
        techWithNullAddress.setAddress(null);
        
        assertNull(techWithNullAddress.getAddress());
    }
    
    @Test
    void testDefaultValues() {
        Technician newTechnician = new Technician();
        assertEquals(0, newTechnician.getCompletedJobs());
        assertEquals(0, newTechnician.getTotalEarnings());
    }
}
