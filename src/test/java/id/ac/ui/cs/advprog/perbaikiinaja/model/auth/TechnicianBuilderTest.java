package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

class TechnicianBuilderTest {

    private TechnicianBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TechnicianBuilder();
    }
    
    @Test
    void testChainedCalls() {
        Technician technician = builder
                .fullName("Test Technician")
                .email("technician@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .address("123 Test Street")
                .completedJobs(5)
                .totalEarnings(1000.50)
                .build();

        assertEquals("Test Technician", technician.getFullName());
        assertEquals("technician@example.com", technician.getEmail());
        assertEquals("password123", technician.getPassword());
        assertEquals("9876543210", technician.getPhoneNumber());
        assertEquals("123 Test Street", technician.getAddress());
        assertEquals(5, technician.getCompletedJobs());
        assertEquals(1000.50, technician.getTotalEarnings());
        assertEquals(UserRole.TECHNICIAN.getValue(), technician.getRole());
    }
    
    @Test
    void testBuildTechnicianWithoutAddress() {
        Technician technician = builder
                .fullName("Test Technician")
                .email("technician@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .build();

        assertEquals("Test Technician", technician.getFullName());
        assertEquals("technician@example.com", technician.getEmail());
        assertNull(technician.getAddress());
        assertEquals(0, technician.getCompletedJobs());
        assertEquals(0.0, technician.getTotalEarnings());
        assertEquals(UserRole.TECHNICIAN.getValue(), technician.getRole());
    }
    
    @Test
    void testBuildTechnicianWithCompletedJobsOnly() {
        Technician technician = builder
                .fullName("Test Technician")
                .email("technician@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .completedJobs(20)
                .build();

        assertEquals("Test Technician", technician.getFullName());
        assertEquals("technician@example.com", technician.getEmail());
        assertEquals(20, technician.getCompletedJobs());
        assertEquals(0.0, technician.getTotalEarnings());
        assertEquals(UserRole.TECHNICIAN.getValue(), technician.getRole());
    }
    
    @Test
    void testBuildTechnicianWithTotalEarningsOnly() {
        Technician technician = builder
                .fullName("Test Technician")
                .email("technician@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .totalEarnings(5000.0)
                .build();

        assertEquals("Test Technician", technician.getFullName());
        assertEquals("technician@example.com", technician.getEmail());
        assertEquals(0, technician.getCompletedJobs());
        assertEquals(5000.0, technician.getTotalEarnings());
        assertEquals(UserRole.TECHNICIAN.getValue(), technician.getRole());
    }
}
