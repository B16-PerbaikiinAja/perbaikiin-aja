package id.ac.ui.cs.advprog.perbaikiinaja.responses.auth;

public class LoginResponseBuilder {
    private String token;
    private long expiresIn;

    public LoginResponseBuilder token(String token) {
        this.token = token;
        return this;
    }

    public LoginResponseBuilder expiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public LoginResponse build() {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setExpiresIn(expiresIn);
        return response;
    }
}
