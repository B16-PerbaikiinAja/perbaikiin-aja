package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "customer")

public class Customer extends User {
    @Getter
    @Setter
    @Column(nullable = true)
    private String address;

    public Customer() {
        super();
    }

    public static CustomerBuilder builder(){
        return new CustomerBuilder();
    }

}