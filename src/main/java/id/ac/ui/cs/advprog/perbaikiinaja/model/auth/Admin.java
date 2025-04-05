package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;

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
