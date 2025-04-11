package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents an estimate for a repair service.
 * Contains information about the estimated cost and completion date.
 */
public class RepairEstimate {
    private UUID id;
    private double cost;
    private LocalDate completionDate;
    private String notes;
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
        if (completionDate.isBefore(LocalDate.now())) {
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
        return cost > 0 && completionDate != null && completionDate.isAfter(LocalDate.now());
    }
}