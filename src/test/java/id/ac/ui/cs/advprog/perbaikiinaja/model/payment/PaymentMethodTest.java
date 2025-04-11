package id.ac.ui.cs.advprog.perbaikiinaja.model.payment;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentMethodTest {

    @Test
    void testNoArgsConstructor() {
        PaymentMethod method = new PaymentMethod();
        assertNotNull(method);
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String name = "Bank Transfer";
        String provider = "Bank XYZ";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        PaymentMethod method = new PaymentMethod(id, name, provider, createdAt, updatedAt);

        assertEquals(name, method.getName());
        assertEquals(provider, method.getProvider());
        assertEquals(id, method.getId());
    }

    @Test
    void testAuditAnnotations() {
        PaymentMethod method = new PaymentMethod();
        method.onCreate();
        assertNotNull(method.getCreatedAt());

        method.onUpdate();
        assertNotNull(method.getUpdatedAt());
    }

    @Test
    void testSetterAndGetter() {
        PaymentMethod method = new PaymentMethod();
        UUID id = UUID.randomUUID();
        method.setId(id);
        method.setName("E-Wallet");
        method.setProvider("ABC Pay");

        assertEquals(id, method.getId());
        assertEquals("E-Wallet", method.getName());
        assertEquals("ABC Pay", method.getProvider());
    }
}
