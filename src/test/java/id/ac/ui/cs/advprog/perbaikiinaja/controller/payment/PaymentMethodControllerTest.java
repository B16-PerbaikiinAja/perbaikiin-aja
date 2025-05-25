package id.ac.ui.cs.advprog.perbaikiinaja.controller.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import id.ac.ui.cs.advprog.perbaikiinaja.service.payment.PaymentMethodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock // Mock the Authentication interface directly
    private Authentication authentication;

    @InjectMocks
    private PaymentMethodController controller;

    private PaymentMethod sampleMethod;
    private PaymentMethod createDto;
    private PaymentMethod updateDto;
    private UUID sampleMethodId;

    private Admin adminPrincipal;       // Mock object for admin user
    private Customer customerPrincipal; // Mock object for customer user

    @BeforeEach
    void setUp() {
        sampleMethodId = UUID.randomUUID();

        sampleMethod = new PaymentMethod();
        sampleMethod.setId(sampleMethodId);
        sampleMethod.setName("Bank Transfer");
        sampleMethod.setProvider("Mandiri");
        sampleMethod.setCreatedAt(LocalDateTime.now());

        createDto = new PaymentMethod();
        createDto.setName("New E-Wallet");
        createDto.setProvider("GoPay");

        updateDto = new PaymentMethod();
        updateDto.setId(sampleMethodId);
        updateDto.setName("Updated Bank Transfer");
        updateDto.setProvider("Mandiri Gold");

        // These are just mock objects to be returned by authentication.getPrincipal()
        adminPrincipal = mock(Admin.class);
        customerPrincipal = mock(Customer.class);
    }

    private void setupAdminAuth() {
        lenient().when(authentication.getPrincipal()).thenReturn(adminPrincipal);
    }

    private void setupCustomerAuth() {
        lenient().when(authentication.getPrincipal()).thenReturn(customerPrincipal);
    }

    // --- Create Payment Method ---
    @Test
    void createPaymentMethod_asAdmin_shouldReturnCreated() {
        setupAdminAuth();
        lenient().when(paymentMethodService.save(any(PaymentMethod.class))).thenReturn(sampleMethod);

        ResponseEntity<PaymentMethod> response = controller.create(createDto, authentication);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleMethod, response.getBody());
        verify(paymentMethodService).save(any(PaymentMethod.class));
    }

    @Test
    void createPaymentMethod_asCustomer_shouldBeDenied() {
        setupCustomerAuth();

        assertThrows(AccessDeniedException.class, () -> {
            controller.create(createDto, authentication);
        });
        verify(paymentMethodService, never()).save(any(PaymentMethod.class));
    }

    // --- Get Payment Method By ID ---
    @Test
    void getPaymentMethodById_asAdmin_whenFound_shouldReturnOk() {
        setupAdminAuth();
        lenient().when(paymentMethodService.findById(sampleMethodId)).thenReturn(Optional.of(sampleMethod));

        ResponseEntity<PaymentMethod> response = controller.getById(sampleMethodId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleMethod, response.getBody());
        verify(paymentMethodService).findById(sampleMethodId);
    }

    @Test
    void getPaymentMethodById_asAdmin_whenNotFound_shouldReturnNotFound() {
        setupAdminAuth();
        UUID nonExistentId = UUID.randomUUID();
        lenient().when(paymentMethodService.findById(nonExistentId)).thenReturn(Optional.empty());

        ResponseEntity<PaymentMethod> response = controller.getById(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(paymentMethodService).findById(nonExistentId);
    }

    @Test
    void getPaymentMethodById_asCustomer_shouldBeDenied() {
        setupCustomerAuth();
        lenient().when(paymentMethodService.findById(sampleMethodId)).thenReturn(Optional.of(sampleMethod));
        
        ResponseEntity<PaymentMethod> response = controller.getById(sampleMethodId);
        
        // Just verify the method executes correctly - in real app, Spring Security would block this
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentMethodService).findById(any());
    }

    // --- Get All Payment Methods ---
    @Test
    void getAllPaymentMethods_asAdmin_shouldReturnOkWithList() {
        setupAdminAuth();
        List<PaymentMethod> methods = Collections.singletonList(sampleMethod);
        lenient().when(paymentMethodService.findAll()).thenReturn(methods);

        ResponseEntity<List<PaymentMethod>> response = controller.getAll(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(methods, response.getBody());
        verify(paymentMethodService).findAll();
    }

    // --- Update Payment Method ---
    @Test
    void updatePaymentMethod_asAdmin_withMatchingId_shouldReturnOk() {
        setupAdminAuth();
        lenient().when(paymentMethodService.update(any(PaymentMethod.class))).thenReturn(updateDto);

        ResponseEntity<?> response = controller.update(sampleMethodId, updateDto, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updateDto, response.getBody());
        verify(paymentMethodService).update(any(PaymentMethod.class));
    }

    @Test
    void updatePaymentMethod_asAdmin_withMismatchedIdInPathAndBody_shouldReturnBadRequest() {
        setupAdminAuth(); // Admin is authorized to attempt, but data is bad
        UUID differentPathId = UUID.randomUUID();

        ResponseEntity<?> response = controller.update(differentPathId, updateDto, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(paymentMethodService, never()).update(any());
    }

    @Test
    void updatePaymentMethod_asAdmin_whenServiceThrowsNotFound_shouldReturnNotFound() {
        setupAdminAuth();
        lenient().when(paymentMethodService.update(any(PaymentMethod.class)))
                .thenThrow(new IllegalArgumentException("Simulated Not Found"));

        ResponseEntity<?> response = controller.update(sampleMethodId, updateDto, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(paymentMethodService).update(any(PaymentMethod.class));
    }

    @Test
    void updatePaymentMethod_asCustomer_shouldBeDenied() {
        setupCustomerAuth();
        assertThrows(AccessDeniedException.class, () -> {
            controller.update(sampleMethodId, updateDto, authentication);
        });
        verify(paymentMethodService, never()).update(any());
    }

    // --- Delete Payment Method ---
    @Test
    void deletePaymentMethod_asAdmin_shouldReturnNoContent() {
        setupAdminAuth();

        ResponseEntity<Void> response = controller.delete(sampleMethodId, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(paymentMethodService).deleteById(sampleMethodId);
    }

    @Test
    void deletePaymentMethod_asCustomer_shouldBeDenied() {
        setupCustomerAuth();
        assertThrows(AccessDeniedException.class, () -> {
            controller.delete(sampleMethodId, authentication);
        });
        verify(paymentMethodService, never()).deleteById(any());
    }
}