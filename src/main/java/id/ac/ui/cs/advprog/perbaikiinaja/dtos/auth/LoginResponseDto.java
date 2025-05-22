package id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String token;

    private long expiresIn;

    private String role;        // Add this
    private String fullName;    // Add this
    private String email;       // Add this
}