package id.ac.ui.cs.advprog.perbaikiinaja.repository.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodRepositoryTest {

    private PaymentMethodRepository repository;
    private PaymentMethod method;

    @BeforeEach
    void setUp() {
        repository = new PaymentMethodRepository();

        method = new PaymentMethod();
        method.setName("Bank Transfer");
        method.setProvider("BCA");
        repository.save(method);
    }

    @Test
    void testSaveShouldAddPaymentMethod() {
        List<PaymentMethod> all = repository.findAll();
        assertEquals(1, all.size());
        assertNotNull(all.get(0).getId());
        assertEquals("BCA", all.get(0).getProvider());
        assertNotNull(all.get(0).getCreatedAt());
    }

    @Test
    void testFindByIdShouldReturnCorrectMethod() {
        UUID id = method.getId();
        Optional<PaymentMethod> found = repository.findById(id);
        assertTrue(found.isPresent());
        assertEquals("Bank Transfer", found.get().getName());
    }

    @Test
    void testFindAllShouldReturnAll() {
        PaymentMethod method2 = new PaymentMethod();
        method2.setName("E-Wallet");
        method2.setProvider("OVO");
        repository.save(method2);

        List<PaymentMethod> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdateShouldModifyExistingMethod() {
        UUID id = method.getId();
        PaymentMethod updated = new PaymentMethod();
        updated.setId(id);
        updated.setName("Updated Transfer");
        updated.setProvider("BRI");

        PaymentMethod result = repository.update(updated);
        assertEquals("BRI", result.getProvider());
        assertNotNull(result.getUpdatedAt());

        Optional<PaymentMethod> fromRepo = repository.findById(id);
        assertEquals("Updated Transfer", fromRepo.get().getName());
    }

    @Test
    void testUpdateReturnsNullWhenPaymentMethodNotFound() {
        PaymentMethod nonExistent = new PaymentMethod();
        nonExistent.setId(UUID.randomUUID());
        nonExistent.setName("Nonexistent Method");
        nonExistent.setProvider("ImaginaryBank");

        PaymentMethod result = repository.update(nonExistent);

        assertNull(result);
    }

    @Test
    void testDeleteByIdShouldRemove() {
        UUID id = method.getId();
        repository.deleteById(id);

        Optional<PaymentMethod> deleted = repository.findById(id);
        assertTrue(deleted.isEmpty());
    }
}
