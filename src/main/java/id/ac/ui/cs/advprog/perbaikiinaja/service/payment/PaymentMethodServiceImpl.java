package id.ac.ui.cs.advprog.perbaikiinaja.service.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        entityManager.persist(paymentMethod);
        return paymentMethod;
    }

    @Override
    public Optional<PaymentMethod> findById(UUID id) {
        PaymentMethod method = entityManager.find(PaymentMethod.class, id);
        return Optional.ofNullable(method);
    }

    @Override
    public List<PaymentMethod> findAll() {
        return entityManager.createQuery("SELECT p FROM PaymentMethod p", PaymentMethod.class)
                .getResultList();
    }

    @Override
    public PaymentMethod update(PaymentMethod paymentMethod) {
        return entityManager.merge(paymentMethod);
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresent(method -> entityManager.remove(method));
    }
}
