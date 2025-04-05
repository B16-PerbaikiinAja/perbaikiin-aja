package id.ac.ui.cs.advprog.perbaikiinaja.responses.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;

    private long expiresIn;

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    } 
}