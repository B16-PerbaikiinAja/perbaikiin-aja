package id.ac.ui.cs.advprog.perbaikiinaja.model;

/**
 * Represents a customer who requests repairs.
 */
public class Customer extends User {
    private String address;

    // Getters and setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
