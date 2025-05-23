package id.ac.ui.cs.advprog.perbaikiinaja.service.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PaymentMethodService {
    PaymentMethod save(PaymentMethod paymentMethod);
    Optional<PaymentMethod> findById(UUID id);
    CompletableFuture<List<PaymentMethod>> findAll();
    PaymentMethod update(PaymentMethod paymentMethod);
    void deleteById(UUID id);
}
