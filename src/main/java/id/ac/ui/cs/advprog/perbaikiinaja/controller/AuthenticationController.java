package id.ac.ui.cs.advprog.perbaikiinaja.controller;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.dto.auth.LoginUserDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dto.auth.RegisterUserDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dto.auth.RegisterAdminDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dto.auth.RegisterCustomerDto;
import id.ac.ui.cs.advprog.perbaikiinaja.responses.auth.LoginResponse;
import id.ac.ui.cs.advprog.perbaikiinaja.services.auth.AuthenticationService;
import id.ac.ui.cs.advprog.perbaikiinaja.services.auth.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup/customer")
    public ResponseEntity<User> registerCustomer(@RequestBody RegisterCustomerDto registerCustomerDto) {
        User registeredCustomer = authenticationService.signupCustomer(registerCustomerDto);

        return ResponseEntity.ok(registeredCustomer);
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<User> registerAdmin(@RequestBody RegisterAdminDto registerAdminDto) {
        User registeredAdmin = authenticationService.signupAdmin(registerAdminDto);

        return ResponseEntity.ok(registeredAdmin);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                                        .token(jwtToken)
                                        .expiresIn(jwtService.getExpirationTime())
                                        .build();

        return ResponseEntity.ok(loginResponse);
    }
}