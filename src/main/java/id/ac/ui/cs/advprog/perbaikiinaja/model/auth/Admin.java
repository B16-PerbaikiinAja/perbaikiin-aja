package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin extends User{
    public Admin() {
        super();
    }

    public static AdminBuilder builder() {
        return new AdminBuilder();
    }
}