package id.ac.ui.cs.advprog.perbaikiinaja.controller.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import id.ac.ui.cs.advprog.perbaikiinaja.service.payment.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @Autowired
    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @PostMapping
    public PaymentMethod create(@RequestBody PaymentMethod paymentMethod) {
        return paymentMethodService.save(paymentMethod);
    }

    @GetMapping("/{id}")
    public Optional<PaymentMethod> getById(@PathVariable UUID id) {
        return paymentMethodService.findById(id);
    }

    @GetMapping
    public List<PaymentMethod> getAll() {
        return paymentMethodService.findAll();
    }

    @PutMapping("/{id}")
    public PaymentMethod update(@PathVariable UUID id, @RequestBody PaymentMethod paymentMethod) {
        if (!id.equals(paymentMethod.getId())) {
            throw new IllegalArgumentException("ID in path and body must match");
        }

        return paymentMethodService.update(paymentMethod);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        paymentMethodService.deleteById(id);
    }
}
