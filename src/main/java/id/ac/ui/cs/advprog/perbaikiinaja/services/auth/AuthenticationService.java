package id.ac.ui.cs.advprog.perbaikiinaja.services.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.LoginUserDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.RegisterAdminDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.RegisterCustomerDto;
import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.RegisterTechnicianDto;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User signupAdmin(RegisterAdminDto input) {
        User user = Admin.builder()
                .fullName(input.getFullName())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .phoneNumber(input.getPhoneNumber())
                .build();

        return userRepository.save(user);
    }
    
    public User signupCustomer(RegisterCustomerDto input) {
        User user = Customer.builder()
                .fullName(input.getFullName())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .phoneNumber(input.getPhoneNumber())
                .address(input.getAddress())
                .build();

        return userRepository.save(user);
    }

    public User signupTechnician(RegisterTechnicianDto input) {
        User user = Technician.builder()
                .fullName(input.getFullName())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .phoneNumber(input.getPhoneNumber())
                .address(input.getAddress())
                .build();

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}