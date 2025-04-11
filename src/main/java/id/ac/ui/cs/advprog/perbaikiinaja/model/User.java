package id.ac.ui.cs.advprog.perbaikiinaja.model;

import java.util.UUID;

/**
 * Base user class
 */
public abstract class User {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String password;

    public User() {
        this.id = UUID.randomUUID();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}