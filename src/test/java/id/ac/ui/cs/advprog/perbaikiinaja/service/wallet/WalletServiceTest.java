package id.ac.ui.cs.advprog.perbaikiinaja.service.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Admin;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.TransactionRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private Customer customer;
    private Technician technician;
    private Admin admin;
    private Wallet wallet;
    private UUID walletId;
    private UUID customerId;
    private UUID technicianId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        technicianId = UUID.randomUUID();

        customer = Customer.builder()
                .email("customer@example.com")
                .fullName("Test Customer")
                .password("password")
                .build();

        technician = Technician.builder()
                .email("technician@example.com")
                .fullName("Test Technician")
                .password("password")
                .build();

        admin = Admin.builder()
                .email("admin@example.com")
                .fullName("Test Admin")
                .password("password")
                .build();

        walletId = UUID.randomUUID();
        wallet = new Wallet(customer);
        wallet.setId(walletId);
    }

    @Test
    void testCreateWallet() {
        // Setup mocks
        when(walletRepository.existsByUser(customer)).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet savedWallet = invocation.getArgument(0);
            savedWallet.setId(walletId);
            return savedWallet;
        });

        // Execute test
        Wallet result = walletService.createWallet(customer);

        // Verify
        assertNotNull(result);
        assertEquals(walletId, result.getId());
        assertEquals(customer, result.getUser());
        assertEquals(BigDecimal.ZERO, result.getBalance());

        verify(walletRepository).existsByUser(customer);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void testCreateWalletWithInitialBalance() {
        // Setup
        BigDecimal initialBalance = new BigDecimal("100.00");
        when(walletRepository.existsByUser(customer)).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {
            Wallet savedWallet = invocation.getArgument(0);
            savedWallet.setId(walletId);
            return savedWallet;
        });

        // Execute
        Wallet result = walletService.createWallet(customer, initialBalance);

        // Verify
        assertNotNull(result);
        assertEquals(walletId, result.getId());
        assertEquals(customer, result.getUser());
        assertEquals(initialBalance, result.getBalance());

        // Verify transaction was created for initial deposit
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testCreateWalletForAdmin() {
        // Setup
//        when(admin.getRole()).thenReturn(UserRole.ADMIN.getValue());

        // Execute & Verify
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.createWallet(admin, BigDecimal.TEN);
        });
        assertEquals("Admin users cannot have wallets", exception.getMessage());

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testCreateWalletWhenAlreadyExists() {
        // Setup
        when(walletRepository.existsByUser(customer)).thenReturn(true);

        // Execute & Verify
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            walletService.createWallet(customer);
        });
        assertEquals("User already has a wallet", exception.getMessage());

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testGetWalletById() {
        // Setup
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Execute
        Optional<Wallet> result = walletService.getWalletById(walletId);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(wallet, result.get());
        verify(walletRepository).findById(walletId);
    }

    @Test
    void testGetWalletByUser() {
        // Setup
        when(walletRepository.findByUser(customer)).thenReturn(Optional.of(wallet));

        // Execute
        Optional<Wallet> result = walletService.getWalletByUser(customer);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(wallet, result.get());
        verify(walletRepository).findByUser(customer);
    }

    @Test
    void testGetWalletByUserId() {
        // Setup
        when(walletRepository.findByUserId(customerId)).thenReturn(Optional.of(wallet));

        // Execute
        Optional<Wallet> result = walletService.getWalletByUserId(customerId);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(wallet, result.get());
        verify(walletRepository).findByUserId(customerId);
    }

    @Test
    void testDeposit() {
        // Setup
        BigDecimal amount = new BigDecimal("50.00");
        String description = "Test deposit";

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Capture transaction to verify its properties
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Execute
        Wallet result = walletService.deposit(walletId, amount, description);

        // Verify
        assertEquals(amount, result.getBalance());
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction capturedTransaction = transactionCaptor.getValue();
        assertEquals(wallet, capturedTransaction.getWallet());
        assertEquals(amount, capturedTransaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, capturedTransaction.getType());
        assertEquals(description, capturedTransaction.getDescription());
    }

    @Test
    void testDepositWalletNotFound() {
        // Setup
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Execute & Verify
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.deposit(walletId, BigDecimal.TEN, "Test");
        });
        assertEquals("Wallet not found", exception.getMessage());

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testWithdraw() {
        // Setup
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal withdrawAmount = new BigDecimal("30.00");
        String description = "Test withdrawal";

        wallet.deposit(initialBalance);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // Capture transaction
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Execute
        Wallet result = walletService.withdraw(walletId, withdrawAmount, description);

        // Verify
        assertEquals(new BigDecimal("70.00"), result.getBalance());
        verify(walletRepository).findById(walletId);
        verify(walletRepository).save(wallet);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction capturedTransaction = transactionCaptor.getValue();
        assertEquals(wallet, capturedTransaction.getWallet());
        assertEquals(withdrawAmount, capturedTransaction.getAmount());
        assertEquals(TransactionType.WITHDRAWAL, capturedTransaction.getType());
        assertEquals(description, capturedTransaction.getDescription());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        // Setup
        BigDecimal initialBalance = new BigDecimal("20.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        wallet.deposit(initialBalance);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Execute & Verify
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.withdraw(walletId, withdrawAmount, "Test");
        });
        assertEquals("Insufficient funds", exception.getMessage());

        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testProcessServicePayment() {
        // Setup
        BigDecimal amount = new BigDecimal("75.00");
        UUID serviceRequestId = UUID.randomUUID();

        Wallet customerWallet = new Wallet(customer);
        customerWallet.deposit(new BigDecimal("100.00"));

        Wallet technicianWallet = new Wallet(technician);

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(userRepository.findById(technicianId)).thenReturn(Optional.of(technician));
        when(walletRepository.findByUser(customer)).thenReturn(Optional.of(customerWallet));
        when(walletRepository.findByUser(technician)).thenReturn(Optional.of(technicianWallet));
        when(walletRepository.save(customerWallet)).thenReturn(customerWallet);
        when(walletRepository.save(technicianWallet)).thenReturn(technicianWallet);

        // Capture transactions
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Execute
        List<Wallet> result = walletService.processServicePayment(customerId, technicianId, amount, serviceRequestId);

        // Verify
        assertEquals(2, result.size());
        assertEquals(customerWallet, result.get(0));
        assertEquals(technicianWallet, result.get(1));

        assertEquals(new BigDecimal("25.00"), customerWallet.getBalance());
        assertEquals(amount, technicianWallet.getBalance());

        verify(transactionRepository, times(2)).save(transactionCaptor.capture());

        List<Transaction> capturedTransactions = transactionCaptor.getAllValues();
        assertEquals(2, capturedTransactions.size());

        // First transaction (customer payment)
        Transaction customerTransaction = capturedTransactions.get(0);
        assertEquals(customerWallet, customerTransaction.getWallet());
        assertEquals(amount, customerTransaction.getAmount());
        assertEquals(TransactionType.PAYMENT, customerTransaction.getType());
        assertEquals(technicianWallet, customerTransaction.getRelatedWallet());

        // Second transaction (technician earning)
        Transaction technicianTransaction = capturedTransactions.get(1);
        assertEquals(technicianWallet, technicianTransaction.getWallet());
        assertEquals(amount, technicianTransaction.getAmount());
        assertEquals(TransactionType.EARNING, technicianTransaction.getType());
        assertEquals(customerWallet, technicianTransaction.getRelatedWallet());
    }

    @Test
    void testGetBalance() {
        // Setup
        BigDecimal balance = new BigDecimal("150.00");
        wallet.deposit(balance);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));

        // Execute
        BigDecimal result = walletService.getBalance(walletId);

        // Verify
        assertEquals(balance, result);
        verify(walletRepository).findById(walletId);
    }

    @Test
    void testHasWallet() {
        // Setup
        when(walletRepository.existsByUser(customer)).thenReturn(true);
        when(walletRepository.existsByUser(admin)).thenReturn(false);

        // Execute & Verify
        assertTrue(walletService.hasWallet(customer));
        assertFalse(walletService.hasWallet(admin));

        verify(walletRepository).existsByUser(customer);
        verify(walletRepository).existsByUser(admin);
    }

    @Test
    void testSaveWallet() {
        // Setup
        when(walletRepository.save(wallet)).thenReturn(wallet);

        // Execute
        Wallet result = walletService.saveWallet(wallet);

        // Verify
        assertEquals(wallet, result);
        verify(walletRepository).save(wallet);
    }
}