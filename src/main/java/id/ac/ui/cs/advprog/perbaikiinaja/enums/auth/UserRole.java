package id.ac.ui.cs.advprog.perbaikiinaja.enums.auth;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    TECHNICIAN("TECHNICIAN"),
    CUSTOMER("CUSTOMER");
    
    private final String value;
 

    private UserRole(String value) {
        this.value = value;
    }
}