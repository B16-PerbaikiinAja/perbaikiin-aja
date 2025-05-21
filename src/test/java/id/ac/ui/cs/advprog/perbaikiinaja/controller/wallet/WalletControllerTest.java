package id.ac.ui.cs.advprog.perbaikiinaja.controller.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
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
import static org.mockito.ArgumentMatchers.any;
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
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();

        customer = Customer.builder()
                .email("customer@example.com")
                .fullName("Test Customer")
                .build();

        technician = Technician.builder()
                .email("technician@example.com")
                .fullName("Test Technician")
                .build();

        admin = Admin.builder()
                .email("admin@example.com")
                .fullName("Test Admin")
                .build();
        admin.setRole(UserRole.ADMIN.getValue());

        wallet = new Wallet(customer);
        wallet.setId(walletId);
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setCreatedAt(LocalDateTime.now().minusDays(1));
        wallet.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetMyWalletForCustomer() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));

        // Execute
        ResponseEntity<?> response = walletController.getMyWallet(authentication);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(wallet.getId(), responseBody.get("id"));
        assertEquals(customer.getId(), responseBody.get("userId"));
        assertEquals(wallet.getBalance(), responseBody.get("balance"));
        assertEquals(wallet.getCreatedAt(), responseBody.get("createdAt"));
        assertEquals(wallet.getUpdatedAt(), responseBody.get("updatedAt"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(customer);
    }

    @Test
    void testGetMyWalletForTechnician() {
        // Setup
        Wallet technicianWallet = new Wallet(technician);
        technicianWallet.setId(UUID.randomUUID());

        when(authentication.getPrincipal()).thenReturn(technician);
        when(walletService.getWalletByUser(technician)).thenReturn(Optional.of(technicianWallet));

        // Execute
        ResponseEntity<?> response = walletController.getMyWallet(authentication);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(technician);
    }

    @Test
    void testGetMyWalletForAdmin() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(admin);

        // Execute
        ResponseEntity<?> response = walletController.getMyWallet(authentication);

        // Verify
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Only customers and technicians have wallets", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService, never()).getWalletByUser(any());
    }

    @Test
    void testGetMyWalletWhenWalletNotFound() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<?> response = walletController.getMyWallet(authentication);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Wallet not found. Create a wallet first.", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(customer);
    }

    @Test
    void testCreateMyWalletForCustomer() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.hasWallet(customer)).thenReturn(false);
        when(walletService.createWallet(customer)).thenReturn(wallet);

        // Execute
        ResponseEntity<?> response = walletController.createMyWallet(authentication);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(wallet.getId(), responseBody.get("id"));

        verify(authentication).getPrincipal();
        verify(walletService).hasWallet(customer);
        verify(walletService).createWallet(customer);
    }

    @Test
    void testCreateMyWalletForAdmin() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(admin);

        // Execute
        ResponseEntity<?> response = walletController.createMyWallet(authentication);

        // Verify
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Admin users cannot have wallets", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService, never()).createWallet(any(User.class));
    }

    @Test
    void testCreateMyWalletWhenWalletAlreadyExists() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.hasWallet(customer)).thenReturn(true);

        // Execute
        ResponseEntity<?> response = walletController.createMyWallet(authentication);

        // Verify
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("You already have a wallet", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService).hasWallet(customer);
        verify(walletService, never()).createWallet(any(User.class));
    }

    @Test
    void testDeposit() {
        // Setup
        BigDecimal amount = new BigDecimal("50.00");
        String description = "Test deposit";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount.toString());
        requestBody.put("description", description);

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.deposit(wallet.getId(), amount, description)).thenReturn(wallet);

        // Execute
        ResponseEntity<?> response = walletController.deposit(authentication, requestBody);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(wallet.getId(), responseBody.get("id"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(customer);
        verify(walletService).deposit(wallet.getId(), amount, description);
    }

    @Test
    void testDepositWithInvalidAmount() {
        // Setup
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "invalid-amount");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));

        // Execute
        ResponseEntity<?> response = walletController.deposit(authentication, requestBody);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Invalid amount", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(customer);
        verify(walletService, never()).deposit(any(), any(), any());
    }

    @Test
    void testDepositWhenWalletNotFound() {
        // Setup
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", "50.00");

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<?> response = walletController.deposit(authentication, requestBody);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Wallet not found. Create a wallet first.", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(customer);
        verify(walletService, never()).deposit(any(), any(), any());
    }

    @Test
    void testWithdraw() {
        // Setup
        BigDecimal amount = new BigDecimal("30.00");
        String description = "Test withdrawal";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount.toString());
        requestBody.put("description", description);

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.withdraw(wallet.getId(), amount, description)).thenReturn(wallet);

        // Execute
        ResponseEntity<?> response = walletController.withdraw(authentication, requestBody);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(wallet.getId(), responseBody.get("id"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(customer);
        verify(walletService).withdraw(wallet.getId(), amount, description);
    }

    @Test
    void testWithdrawWithInsufficientFunds() {
        // Setup
        BigDecimal amount = new BigDecimal("200.00");
        String description = "Test withdrawal";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount.toString());
        requestBody.put("description", description);

        when(authentication.getPrincipal()).thenReturn(customer);
        when(walletService.getWalletByUser(customer)).thenReturn(Optional.of(wallet));
        when(walletService.withdraw(wallet.getId(), amount, description))
                .thenThrow(new IllegalArgumentException("Insufficient funds"));

        // Execute
        ResponseEntity<?> response = walletController.withdraw(authentication, requestBody);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Insufficient funds", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(customer);
        verify(walletService).withdraw(wallet.getId(), amount, description);
    }

    @Test
    void testGetWallet() {
        // Setup - this is an admin-only endpoint
        when(walletService.getWalletById(walletId)).thenReturn(Optional.of(wallet));

        // Execute
        ResponseEntity<?> response = walletController.getWallet(walletId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(wallet.getId(), responseBody.get("id"));
        assertEquals(customer.getId(), responseBody.get("userId"));

        verify(walletService).getWalletById(walletId);
    }

    @Test
    void testGetWalletWhenNotFound() {
        // Setup
        when(walletService.getWalletById(walletId)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<?> response = walletController.getWallet(walletId);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Wallet not found", responseBody.get("message"));

        verify(walletService).getWalletById(walletId);
    }

    @Test
    void testCreateWalletForUser() {
        // Setup - this is an admin-only endpoint
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("initialBalance", "100.00");

        // Execute
        ResponseEntity<?> response = walletController.createWalletForUser(userId, requestBody);

        // Verify
        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("This endpoint is not fully implemented", responseBody.get("message"));
    }
}