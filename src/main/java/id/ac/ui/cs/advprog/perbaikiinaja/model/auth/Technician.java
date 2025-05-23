package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "technician")
public class Technician extends User{
    @Getter
    @Setter
    @Column(nullable = true)
    private String address;

    @Getter
    @Setter
    @Column(nullable = false)
    private int completedJobs;

    @Getter
    @Setter
    @Column(nullable = false)
    private double totalEarnings;

    public Technician() {
        super();
    }
    
    public static TechnicianBuilder builder() {
        return new TechnicianBuilder();
    }

    public int getCompletedJobCount() {
        return completedJobs;
    }

    public void incrementCompletedJobCount() {
        this.completedJobs++;
    }

    public void addEarnings(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Earnings amount cannot be negative");
        }
        this.totalEarnings += amount;
    }
}