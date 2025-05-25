package id.ac.ui.cs.advprog.perbaikiinaja.service.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.lang.reflect.Field;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PaymentMethodServiceImplTest {

    private PaymentMethodServiceImpl service;
    private EntityManager entityManager;

    private PaymentMethod method;

    @BeforeEach
    void setUp () throws Exception {
        entityManager = mock(EntityManager.class);
        service = new PaymentMethodServiceImpl();

        Field field = PaymentMethodServiceImpl.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(service, entityManager);

        method = new PaymentMethod();
        method.setId(UUID.randomUUID());
        method.setName("Bank Transfer");
        method.setProvider("BCA");
        method.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSave() {
        doNothing().when(entityManager).persist(method);

        PaymentMethod result = service.save(method);

        verify(entityManager, times(1)).persist(method);
        assertEquals(method, result);
    }

    @Test
    void testFindByIdFound() {
        UUID id = method.getId();
        when(entityManager.find(PaymentMethod.class, id)).thenReturn(method);

        Optional<PaymentMethod> found = service.findById(id);

        assertTrue(found.isPresent());
        assertEquals("BCA", found.get().getProvider());
    }

    @Test
    void testFindByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(PaymentMethod.class, id)).thenReturn(null);

        Optional<PaymentMethod> found = service.findById(id);

        assertTrue(found.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAll() {
        TypedQuery<PaymentMethod> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM PaymentMethod p", PaymentMethod.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(method));

        List<PaymentMethod> all = service.findAll();

        assertEquals(1, all.size());
        assertEquals("Bank Transfer", all.get(0).getName());
    }

    @Test
    void testUpdate() {
        when(entityManager.merge(method)).thenReturn(method);

        PaymentMethod updated = service.update(method);

        verify(entityManager, times(1)).merge(method);
        assertEquals("BCA", updated.getProvider());
    }

    @Test
    void testDeleteById() {
        UUID id = method.getId();
        when(entityManager.find(PaymentMethod.class, id)).thenReturn(method);

        service.deleteById(id);

        verify(entityManager, times(1)).remove(method);
    }

    @Test
    void testDeleteByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(entityManager.find(PaymentMethod.class, id)).thenReturn(null);

        service.deleteById(id);

        verify(entityManager, never()).remove(any());
    }
}
