package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

class CustomerTest {
    
    private Customer customer;
    
    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setFullName("Test Customer");
        customer.setEmail("customer@example.com");
        customer.setPassword("password123");
        customer.setPhoneNumber("1234567890");
        customer.setRole(UserRole.CUSTOMER.getValue());
        customer.setAddress("123 Test Street");
    }
    
    @Test
    void testGetterMethods() {
        assertEquals("Test Customer", customer.getFullName());
        assertEquals("customer@example.com", customer.getEmail());
        assertEquals("password123", customer.getPassword());
        assertEquals("1234567890", customer.getPhoneNumber());
        assertEquals(UserRole.CUSTOMER.getValue(), customer.getRole());
        assertEquals("123 Test Street", customer.getAddress());
    }
    
    @Test
    void testCustomerDetailsImplementation() {
        assertEquals("customer@example.com", customer.getUsername());
        assertTrue(customer.isAccountNonExpired());
        assertTrue(customer.isAccountNonLocked());
        assertTrue(customer.isCredentialsNonExpired());
        assertTrue(customer.isEnabled());
        assertTrue(customer.getAuthorities().isEmpty());
    }
    
    @Test
    void testSetterMethods() {
        customer.setFullName("New Customer");
        customer.setEmail("new.customer@example.com");
        customer.setPassword("newPassword");
        customer.setPhoneNumber("9876543210");
        customer.setRole("NEW_ROLE");
        customer.setAddress("456 New Avenue");
        
        assertEquals("New Customer", customer.getFullName());
        assertEquals("new.customer@example.com", customer.getEmail());
        assertEquals("newPassword", customer.getPassword());
        assertEquals("9876543210", customer.getPhoneNumber());
        assertEquals("NEW_ROLE", customer.getRole());
        assertEquals("456 New Avenue", customer.getAddress());
        assertEquals("new.customer@example.com", customer.getUsername());
    }
    
    @Test
    void testNullableAddress() {
        Customer customerWithNullAddress = new Customer();
        customerWithNullAddress.setAddress(null);
        
        assertNull(customerWithNullAddress.getAddress());
    }
}
