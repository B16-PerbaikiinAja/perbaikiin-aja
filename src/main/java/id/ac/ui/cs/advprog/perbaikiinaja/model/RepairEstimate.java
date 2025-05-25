package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import id.ac.ui.cs.advprog.perbaikiinaja.model.coupon.Coupon;
import id.ac.ui.cs.advprog.perbaikiinaja.utils.PriceCalculationUtils;
import jakarta.persistence.*;

/**
 * Represents an estimate for a repair service.
 * Contains information about the estimated cost and completion date.
 */

@Entity
@Table(name = "repair_estimates")
public class RepairEstimate {

    @Id
    private UUID id;

    @Column(nullable = false)
    private double cost;

    @Column(nullable = false)
    private LocalDate completionDate;

    @Column(nullable = true)
    private String notes;

    @Column(nullable = false)
    private LocalDate createdDate;

    public RepairEstimate() {
        this.id = UUID.randomUUID();
        this.createdDate = LocalDate.now();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        this.cost = cost;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        if (completionDate != null && completionDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Completion date cannot be in the past");
        }
        this.completionDate = completionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    /**
     * Validates that the estimate is complete with all required fields.
     * @return true if the estimate is valid, false otherwise
     */
    public boolean isValid() {
        return cost > 0 && completionDate != null;
    }

    /**
     * Calculate final cost after applying coupon discount
     * @param coupon The coupon to apply
     * @return The final cost after discount
     */
    public double getFinalCost(Coupon coupon) {
        if (coupon == null) {
            return this.cost;
        }

        BigDecimal originalCost = BigDecimal.valueOf(this.cost);
        BigDecimal finalCost = PriceCalculationUtils.calculateFinalPrice(originalCost, coupon);
        return finalCost.doubleValue();
    }

    /**
     * Calculate discount amount from coupon
     * @param coupon The coupon to apply
     * @return The discount amount
     */
    public double getDiscountAmount(Coupon coupon) {
        if (coupon == null) {
            return 0.0;
        }

        BigDecimal originalCost = BigDecimal.valueOf(this.cost);
        BigDecimal discountAmount = PriceCalculationUtils.calculateDiscountAmount(originalCost, coupon);
        return discountAmount.doubleValue();
    }
}