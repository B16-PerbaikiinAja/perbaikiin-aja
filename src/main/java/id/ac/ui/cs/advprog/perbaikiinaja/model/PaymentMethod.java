package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.util.UUID;

/**
 * Represents a payment method used for transactions.
 */
public class PaymentMethod {
    private UUID id;
    private String name;
    private String provider;

    public PaymentMethod() {
        this.id = UUID.randomUUID();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
