package id.ac.ui.cs.advprog.perbaikiinaja.controller.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private WalletController walletController;

    private Customer customer;
    private Technician technician;
    private Admin admin;
    private Wallet wallet;
    private UUID walletId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        userId = UUID.randomUUID();

        // Create Customer
        customer = new Customer();
        customer.setFullName("John Customer");
        customer.setEmail("john@example.com");
        customer.setRole(UserRole.CUSTOMER.getValue());

        // Create Technician
        technician = new Technician();
        technician.setFullName("Jane Technician");
        technician.setEmail("jane@example.com");
        technician.setRole(UserRole.TECHNICIAN.getValue());

        // Create Admin
        admin = new Admin();
        admin.setFullName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setRole(UserRole.ADMIN.getValue());

        // Create Wallet
        wallet = new Wallet(customer, BigDecimal.valueOf(100.0));
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getMyWallet_WithCustomer_Success() {
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));

        ResponseEntity<Map<String, Object>> response = walletController.getMyWallet(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(wallet.getId(), response.getBody().get("id"));
        assertEquals(wallet.getUser().getId(), response.getBody().get("userId"));
        assertEquals(wallet.getBalance(), response.getBody().get("balance"));
    }

    @Test
    void getMyWallet_WithTechnician_Success() {
        Wallet technicianWallet = new Wallet(technician, BigDecimal.valueOf(50.0));
        when(authentication.getPrincipal()).thenReturn(technician);
        when(walletService.getWalletByUser(technician)).thenReturn(Optional.of(technicianWallet));

        ResponseEntity<Map<String, Object>> response = walletController.getMyWallet(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(technicianWallet.getId(), response.getBody().get("id"));
    }

    @Test
    void getMyWallet_WithAdmin_Forbidden() {
        when(authentication.getPrincipal()).thenReturn(admin);

        ResponseEntity<Map<String, Object>> response = walletController.getMyWallet(authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Only customers and technicians have wallets", response.getBody().get("message"));
    }

    @Test
    void getMyWallet_WalletNotFound() {
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = walletController.getMyWallet(authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wallet not found. Create a wallet first.", response.getBody().get("message"));
    }

    @Test
    void createMyWallet_Success() {
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.hasWallet(customer)).thenReturn(false);
        when(walletService.createWallet(customer)).thenReturn(wallet);

        ResponseEntity<Map<String, Object>> response = walletController.createMyWallet(authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(wallet.getId(), response.getBody().get("id"));
    }

    @Test
    void createMyWallet_AdminUser_Forbidden() {
        when(authentication.getPrincipal()).thenReturn(admin);

        ResponseEntity<Map<String, Object>> response = walletController.createMyWallet(authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Admin users cannot have wallets", response.getBody().get("message"));
    }

    @Test
    void createMyWallet_AlreadyHasWallet_Conflict() {
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.hasWallet(customer)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = walletController.createMyWallet(authentication);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You already have a wallet", response.getBody().get("message"));
    }

    @Test
    void deposit_Success() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "50.0");
        requestBody.put("description", "Test deposit");

        Wallet updatedWallet = new Wallet(customer, BigDecimal.valueOf(150.0));

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.deposit(wallet.getId(), BigDecimal.valueOf(50.0),"Test deposit"))
                .thenReturn(updatedWallet);

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedWallet.getBalance(), response.getBody().get("balance"));
    }

    @Test
    void deposit_WithDefaultDescription() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "25.0");

        Wallet updatedWallet = new Wallet(customer, BigDecimal.valueOf(125.0));

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.deposit(wallet.getId(), BigDecimal.valueOf(25.0),"Deposit"))
                .thenReturn(updatedWallet);

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(walletService).deposit(wallet.getId(), BigDecimal.valueOf(25.0), "Deposit");
    }

    @Test
    void deposit_AdminUser_Forbidden() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "50.0");

        when(authentication.getPrincipal()).thenReturn(admin);

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Only customers and technicians have wallets", response.getBody().get("message"));
    }

    @Test
    void deposit_WalletNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "50.0");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wallet not found. Create a wallet first.", response.getBody().get("message"));
    }

    @Test
    void deposit_InvalidAmount() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "invalid");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid amount", response.getBody().get("message"));
    }

    @Test
    void deposit_ServiceThrowsException() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "-10.0");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.deposit(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Deposit amount must be positive"));

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Deposit amount must be positive", response.getBody().get("message"));
    }

    @Test
    void withdraw_Success() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "30.0");
        requestBody.put("description", "Test withdrawal");

        Wallet updatedWallet = new Wallet(customer, BigDecimal.valueOf(70.0));

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.withdraw(wallet.getId(), BigDecimal.valueOf(30.0),"Test withdrawal"))
                .thenReturn(updatedWallet);

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedWallet.getBalance(), response.getBody().get("balance"));
    }

    @Test
    void withdraw_WithDefaultDescription() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "20.0");

        Wallet updatedWallet = new Wallet(customer, BigDecimal.valueOf(80.0));

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.withdraw(wallet.getId(), BigDecimal.valueOf(20.0),"Withdrawal"))
                .thenReturn(updatedWallet);

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(walletService).withdraw(wallet.getId(),BigDecimal.valueOf(20.0),"Withdrawal");
    }

    @Test
    void withdraw_AdminUser_Forbidden() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "30.0");

        when(authentication.getPrincipal()).thenReturn(admin);

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Only customers and technicians have wallets", response.getBody().get("message"));
    }

    @Test
    void withdraw_WalletNotFound() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "30.0");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wallet not found. Create a wallet first.", response.getBody().get("message"));
    }

    @Test
    void withdraw_InvalidAmount() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "not-a-number");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid amount", response.getBody().get("message"));
    }

    @Test
    void withdraw_ServiceThrowsException() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "200.0");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.withdraw(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Insufficient funds"));

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Insufficient funds", response.getBody().get("message"));
    }

    @Test
    void getWallet_Success() {
        when(walletService.getWalletById(walletId)).thenReturn(Optional.of(wallet));

        ResponseEntity<Map<String, Object>> response = walletController.getWallet(walletId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(wallet.getId(), response.getBody().get("id"));
        assertEquals(wallet.getUser().getId(), response.getBody().get("userId"));
        assertEquals(wallet.getBalance(), response.getBody().get("balance"));
    }

    @Test
    void getWallet_NotFound() {
        when(walletService.getWalletById(walletId)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = walletController.getWallet(walletId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wallet not found", response.getBody().get("message"));
    }

    @Test
    void createWalletForUser_NotImplemented() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("initialBalance", "100.0");

        ResponseEntity<Map<String, Object>> response = walletController.createWalletForUser(userId, requestBody);

        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("This endpoint is not fully implemented", response.getBody().get("message"));
    }

    @Test
    void createWalletForUser_WithNullRequestBody() {
        ResponseEntity<Map<String, Object>> response = walletController.createWalletForUser(userId, null);

        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("This endpoint is not fully implemented", response.getBody().get("message"));
    }

    @Test
    void deposit_WithTechnician_Success() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "40.0");

        Wallet technicianWallet = new Wallet(technician, BigDecimal.valueOf(90.0));
        Wallet updatedWallet = new Wallet(technician, BigDecimal.valueOf(130.0));

        when(authentication.getPrincipal()).thenReturn(technician);
        when(walletService.getWalletByUser(technician)).thenReturn(Optional.of(technicianWallet));
        when(walletService.deposit(technicianWallet.getId(), BigDecimal.valueOf(40.0),"Deposit"))
                .thenReturn(updatedWallet);

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void withdraw_WithTechnician_Success() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "15.0");

        Wallet technicianWallet = new Wallet(technician, BigDecimal.valueOf(90.0));
        Wallet updatedWallet = new Wallet(technician, BigDecimal.valueOf(75.0));

        when(authentication.getPrincipal()).thenReturn(technician);
        when(walletService.getWalletByUser(technician)).thenReturn(Optional.of(technicianWallet));
        when(walletService.withdraw(technicianWallet.getId(), BigDecimal.valueOf(15.0),"Withdrawal"))
                .thenReturn(updatedWallet);

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deposit_AmountExceptionDuringParsing() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", null);

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));

        ResponseEntity<Map<String, Object>> response = walletController.deposit(authentication, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid amount", response.getBody().get("message"));
    }

    @Test
    void withdraw_AmountExceptionDuringParsing() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", null);

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));

        ResponseEntity<Map<String, Object>> response = walletController.withdraw(authentication, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid amount", response.getBody().get("message"));
    }
}