package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;

@Entity
@Table(name = "customer")
public class Customer extends User{
    @Getter
    @Setter
    @Column(nullable = true)
    private String address;

    public Customer() {
        super();
    }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }
}
