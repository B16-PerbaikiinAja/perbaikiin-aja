package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.time.LocalDate;
import java.util.UUID;

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
}