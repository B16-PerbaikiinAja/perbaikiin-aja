package id.ac.ui.cs.advprog.perbaikiinaja.services.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.dtos.auth.*;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.*;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private WalletService walletService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                userRepository,
                authenticationManager,
                passwordEncoder,
                walletService);
    }

    @Test
    void signupAdmin_savesAdmin() {
        RegisterAdminDto dto = new RegisterAdminDto();
        dto.setFullName("Admin");
        dto.setEmail("admin@mail.com");
        dto.setPassword("password123");
        dto.setPhoneNumber("123");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        Admin saved = Admin.builder().fullName("Admin").email("admin@mail.com").password("encodedPassword").phoneNumber("123").build();
        when(userRepository.save(any(Admin.class))).thenReturn(saved);

        User result = authenticationService.signupAdmin(dto);

        assertEquals("Admin", result.getFullName());
        assertEquals("admin@mail.com", result.getEmail());
        verify(userRepository).save(any(Admin.class));
    }

    @Test
    void signupCustomer_savesCustomer() {
        RegisterCustomerDto dto = new RegisterCustomerDto();
        dto.setFullName("Cust");
        dto.setEmail("cust@mail.com");
        dto.setPassword("password123");
        dto.setPhoneNumber("123");
        dto.setAddress("Addr");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        Customer saved = Customer.builder().fullName("Cust").email("cust@mail.com").password("encodedPassword").phoneNumber("123").address("Addr").build();
        when(userRepository.save(any(Customer.class))).thenReturn(saved);
        when(walletService.createWallet(any(User.class))).thenReturn(mock(Wallet.class));
        User result = authenticationService.signupCustomer(dto);

        assertEquals("Cust", result.getFullName());
        assertEquals("cust@mail.com", result.getEmail());
        verify(walletService).createWallet(any(Customer.class));
        verify(userRepository).save(any(Customer.class));
    }

    @Test
    void signupTechnician_savesTechnician() {
        RegisterTechnicianDto dto = new RegisterTechnicianDto();
        dto.setFullName("Tech");
        dto.setEmail("tech@mail.com");
        dto.setPassword("password123");
        dto.setPhoneNumber("123");
        dto.setAddress("Addr");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        Technician saved = Technician.builder().fullName("Tech").email("tech@mail.com").password("encodedPassword").phoneNumber("123").address("Addr").build();
        when(userRepository.save(any(Technician.class))).thenReturn(saved);
        when(walletService.createWallet(any(User.class))).thenReturn(mock(Wallet.class));
        User result = authenticationService.signupTechnician(dto);

        assertEquals("Tech", result.getFullName());
        assertEquals("tech@mail.com", result.getEmail());
        verify(userRepository).save(any(Technician.class));
        verify(walletService).createWallet(any(Technician.class));
    }

    @Test
    void authenticate_authenticatesAndReturnsUser() {
        LoginUserDto dto = new LoginUserDto();
        dto.setEmail("user@mail.com");
        dto.setPassword("password123");
        User user = mock(User.class);
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));

        User result = authenticationService.authenticate(dto);

        assertEquals(user, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_userNotFound_throws() {
        LoginUserDto dto = new LoginUserDto();
        dto.setEmail("notfound@mail.com");
        dto.setPassword("password123");
        when(userRepository.findByEmail("notfound@mail.com")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> authenticationService.authenticate(dto));
    }
}
