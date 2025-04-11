package id.ac.ui.cs.advprog.perbaikiinaja.repository.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.time.LocalDateTime;

@Repository
public class PaymentMethodRepository {
    private final List<PaymentMethod> paymentMethods = new ArrayList<>();

    public PaymentMethod save(PaymentMethod method) {
        method.setId(UUID.randomUUID());
        method.setCreatedAt(LocalDateTime.now());
        paymentMethods.add(method);
        return method;
    }

    public Optional<PaymentMethod> findById(UUID id) {
        return paymentMethods.stream()
                .filter(pm -> pm.getId().equals(id))
                .findFirst();
    }

    public List<PaymentMethod> findAll() {
        return new ArrayList<>(paymentMethods);
    }

    public PaymentMethod update(PaymentMethod updatedMethod) {
        for (int i = 0; i < paymentMethods.size(); i++) {
            if (paymentMethods.get(i).getId().equals(updatedMethod.getId())) {
                updatedMethod.setUpdatedAt(LocalDateTime.now());
                paymentMethods.set(i, updatedMethod);
                return updatedMethod;
            }
        }
        return null;
    }

    public void deleteById(UUID id) {
        paymentMethods.removeIf(pm -> pm.getId().equals(id));
    }
}
