package id.ac.ui.cs.advprog.perbaikiinaja.model;

/**
 * Represents a technician who performs repairs.
 */
public class Technician extends User {
    private String experience;
    private String address;
    private int completedJobCount;
    private double totalEarnings;

    // Getters and setters
    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCompletedJobCount() {
        return completedJobCount;
    }

    public void incrementCompletedJobCount() {
        this.completedJobCount++;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void addEarnings(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Earnings amount cannot be negative");
        }
        this.totalEarnings += amount;
    }
}
