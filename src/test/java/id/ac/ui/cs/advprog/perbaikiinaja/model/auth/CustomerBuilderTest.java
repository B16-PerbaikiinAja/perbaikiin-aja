package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

class CustomerBuilderTest {

    private CustomerBuilder builder;

    @BeforeEach
    void setUp() {
        builder = Customer.builder();
    }
    
    @Test
    void testChainedCalls() {
        Customer customer = builder
                .fullName("Test Customer")
                .email("customer@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .address("123 Test Street")
                .build();

        assertEquals("Test Customer", customer.getFullName());
        assertEquals("customer@example.com", customer.getEmail());
        assertEquals("password123", customer.getPassword());
        assertEquals("9876543210", customer.getPhoneNumber());
        assertEquals("123 Test Street", customer.getAddress());
        assertEquals(UserRole.CUSTOMER.getValue(), customer.getRole());
    }

    @Test
    void testBuildSequence() {
        builder.fullName("First Customer")
                .email("first@example.com")
                .password("password123")
                .phoneNumber("1234567890")
                .address("First Address");
        Customer firstCustomer = builder.build();

        builder.fullName("Second Customer")
                .email("second@example.com")
                .address("Second Address");
        Customer secondCustomer = builder.build();

        assertEquals("First Customer", firstCustomer.getFullName());
        assertEquals("first@example.com", firstCustomer.getEmail());
        assertEquals("First Address", firstCustomer.getAddress());

        assertEquals("Second Customer", secondCustomer.getFullName());
        assertEquals("second@example.com", secondCustomer.getEmail());
        assertEquals("password123", secondCustomer.getPassword());
        assertEquals("1234567890", secondCustomer.getPhoneNumber());
        assertEquals("Second Address", secondCustomer.getAddress());
    }
    
    @Test
    void testBuildCustomerWithoutAddress() {
        Customer customer = builder
                .fullName("Test Customer")
                .email("customer@example.com")
                .password("password123")
                .phoneNumber("9876543210")
                .build();

        assertEquals("Test Customer", customer.getFullName());
        assertEquals("customer@example.com", customer.getEmail());
        assertNull(customer.getAddress());
        assertEquals(UserRole.CUSTOMER.getValue(), customer.getRole());
    }
}
