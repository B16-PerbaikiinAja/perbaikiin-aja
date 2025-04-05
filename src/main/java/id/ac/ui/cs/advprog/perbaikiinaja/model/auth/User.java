package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @Getter
    private UUID id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String fullName;

    @Getter
    @Setter
    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    @Column(nullable = false)
    private String phoneNumber;

    @Getter
    @Setter
    @Column(nullable = false)
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public String getUsername() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
