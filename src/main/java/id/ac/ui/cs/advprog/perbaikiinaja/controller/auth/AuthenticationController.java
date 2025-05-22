package id.ac.ui.cs.advprog.perbaikiinaja.controller.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.LoginUserDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.RegisterAdminDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.RegisterCustomerDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.RegisterTechnicianDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.LoginResponseDto;
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

    @PostMapping("/signup/technician")
    public ResponseEntity<User> registerTechnician(@RequestBody RegisterTechnicianDto registerTechnicianDto) {
        User registeredTechnician = authenticationService.signupTechnician(registerTechnicianDto);

        return ResponseEntity.ok(registeredTechnician);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        loginResponse.setRole(authenticatedUser.getRole());
        loginResponse.setFullName(authenticatedUser.getFullName());
        loginResponse.setEmail(authenticatedUser.getEmail());

        return ResponseEntity.ok(loginResponse);
    }
}