package id.ac.ui.cs.advprog.perbaikiinaja.controller.payment;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.payment.PaymentMethod;
import id.ac.ui.cs.advprog.perbaikiinaja.service.payment.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    // Helper method for authorization based on principal type
    private void authorizeAdmin(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Authentication required. Access is denied.");
        }

        Object principal = authentication.getPrincipal();

        // Check if the principal object is an instance of your Admin class
        if (!(principal instanceof Admin)) { // Use your actual Admin class here
            throw new AccessDeniedException("Access is denied. Admin role required. Principal type: " + principal.getClass().getName());
        }
        // If it's an instance of Admin, we assume they are authorized for admin actions
    }

    @PostMapping
    // REMOVED @PreAuthorize - we are doing manual checks now
    public ResponseEntity<PaymentMethod> create(@RequestBody PaymentMethod paymentMethod, Authentication authentication) {
        authorizeAdmin(authentication); // Manual authorization check
        PaymentMethod createdPaymentMethod = paymentMethodService.save(paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPaymentMethod);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentMethod> getById(@PathVariable UUID id) {
        Optional<PaymentMethod> paymentMethodOptional = paymentMethodService.findById(id);
        return paymentMethodOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<PaymentMethod>> getAll(Authentication authentication) {
        List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
        return ResponseEntity.ok(paymentMethods);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody PaymentMethod paymentMethod, Authentication authentication) {
        authorizeAdmin(authentication); // Manual authorization check

        if (paymentMethod.getId() == null || !id.equals(paymentMethod.getId())) {
            return ResponseEntity.badRequest().body("ID in path (" + id + ") and body (" + paymentMethod.getId() + ") must match.");
        }

        try {
            PaymentMethod updatedPaymentMethod = paymentMethodService.update(paymentMethod);
            return ResponseEntity.ok(updatedPaymentMethod);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        authorizeAdmin(authentication); // Manual authorization check
        try {
            paymentMethodService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}