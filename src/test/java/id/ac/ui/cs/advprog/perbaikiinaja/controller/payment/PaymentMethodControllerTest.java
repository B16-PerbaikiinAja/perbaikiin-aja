package id.ac.ui.cs.advprog.perbaikiinaja.controller.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import id.ac.ui.cs.advprog.perbaikiinaja.service.payment.PaymentMethodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentMethodControllerTest {

    private PaymentMethodController controller;
    private PaymentMethodService service;

    private PaymentMethod method;

    @BeforeEach
    void setUp() {
        service = mock(PaymentMethodService.class);
        controller = new PaymentMethodController(service);

        method = new PaymentMethod();
        method.setId(UUID.randomUUID());
        method.setName("Bank Transfer");
        method.setProvider("Mandiri");
        method.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreatePaymentMethod() {
        when(service.save(method)).thenReturn(method);

        PaymentMethod result = controller.create(method);

        assertEquals(method, result);
        verify(service, times(1)).save(method);
    }

    @Test
    void testGetPaymentMethodById_Found() {
        when(service.findById(method.getId())).thenReturn(Optional.of(method));

        Optional<PaymentMethod> found = controller.getById(method.getId());

        assertTrue(found.isPresent());
        assertEquals("Mandiri", found.get().getProvider());
    }

    @Test
    void testGetPaymentMethodById_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(service.findById(randomId)).thenReturn(Optional.empty());

        Optional<PaymentMethod> found = controller.getById(randomId);

        assertTrue(found.isEmpty());
    }

    @Test
    void testGetAllPaymentMethods() {
        when(service.findAll()).thenReturn(List.of(method));

        List<PaymentMethod> all = controller.getAll();

        assertEquals(1, all.size());
        assertEquals("Bank Transfer", all.get(0).getName());
    }

    @Test
    void testUpdatePaymentMethod() {
        when(service.update(method)).thenReturn(method);

        PaymentMethod updated = controller.update(method.getId(), method);

        assertEquals(method, updated);
        verify(service, times(1)).update(method);
    }

    @Test
    void testUpdateThrowsExceptionWhenIdMismatch() {
        UUID pathId = UUID.randomUUID();
        UUID bodyId = UUID.randomUUID(); // different from pathId

        PaymentMethod method = new PaymentMethod();
        method.setId(bodyId);
        method.setName("Bank Transfer");
        method.setProvider("BCA");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.update(pathId, method);
        });

        assertEquals("ID in path and body must match", exception.getMessage());
    }

    @Test
    void testDeletePaymentMethod() {
        UUID id = method.getId();

        controller.delete(id);

        verify(service, times(1)).deleteById(id);
    }
}
