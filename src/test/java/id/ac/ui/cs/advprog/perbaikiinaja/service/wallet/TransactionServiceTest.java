package id.ac.ui.cs.advprog.perbaikiinaja.service.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.TransactionRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UUID walletId;
    private UUID transactionId;
    private Wallet wallet;
    private Wallet relatedWallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .email("customer@example.com")
                .fullName("Test Customer")
                .build();

        wallet = new Wallet(customer);
        wallet.setId(walletId);

        Customer relatedCustomer = Customer.builder()
                .email("related@example.com")
                .fullName("Test Related Customer")
                .build();

        relatedWallet = new Wallet(relatedCustomer);
        relatedWallet.setId(UUID.randomUUID());

        transaction = new Transaction(wallet, new BigDecimal("50.00"), TransactionType.DEPOSIT, "Test transaction");
        transaction.setId(transactionId);
    }

    @Test
    void testGetTransactionById() {
        // Setup
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // Execute
        Optional<Transaction> result = transactionService.getTransactionById(transactionId);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(transaction, result.get());
        verify(transactionRepository).findById(transactionId);
    }

    @Test
    void testGetTransactionsByWallet() {
        // Setup
        List<Transaction> expectedTransactions = Arrays.asList(transaction);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWallet(wallet)).thenReturn(expectedTransactions);

        // Execute
        List<Transaction> result = transactionService.getTransactionsByWallet(walletId);

        // Verify
        assertEquals(expectedTransactions, result);
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).findByWallet(wallet);
    }

    @Test
    void testGetTransactionsByWalletWhenWalletNotFound() {
        // Setup
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Execute & Verify
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getTransactionsByWallet(walletId);
        });
        assertEquals("Wallet not found with ID: " + walletId, exception.getMessage());

        verify(transactionRepository, never()).findByWallet(any());
    }

    @Test
    void testGetTransactionsByWalletWithPagination() {
        // Setup
        Pageable pageable = PageRequest.of(0, 10);
        List<Transaction> transactions = Arrays.asList(transaction);
        Page<Transaction> expectedPage = new PageImpl<>(transactions, pageable, transactions.size());

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWallet(wallet, pageable)).thenReturn(expectedPage);

        // Execute
        Page<Transaction> result = transactionService.getTransactionsByWallet(walletId, pageable);

        // Verify
        assertEquals(expectedPage, result);
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).findByWallet(wallet, pageable);
    }

    @Test
    void testGetTransactionsByWalletAndType() {
        // Setup
        List<Transaction> expectedTransactions = Arrays.asList(transaction);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletAndType(wallet, TransactionType.DEPOSIT)).thenReturn(expectedTransactions);

        // Execute
        List<Transaction> result = transactionService.getTransactionsByWalletAndType(walletId, TransactionType.DEPOSIT);

        // Verify
        assertEquals(expectedTransactions, result);
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).findByWalletAndType(wallet, TransactionType.DEPOSIT);
    }

    @Test
    void testGetTransactionsByWalletAndDateRange() {
        // Setup
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Transaction> expectedTransactions = Arrays.asList(transaction);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletAndTimestampBetween(wallet, startDate, endDate))
                .thenReturn(expectedTransactions);

        // Execute
        List<Transaction> result = transactionService.getTransactionsByWalletAndDateRange(walletId, startDate, endDate);

        // Verify
        assertEquals(expectedTransactions, result);
        verify(walletRepository).findById(walletId);
        verify(transactionRepository).findByWalletAndTimestampBetween(wallet, startDate, endDate);
    }

    @Test
    void testGetTransactionsBetweenWallets() {
        // Setup
        UUID relatedWalletId = relatedWallet.getId();
        List<Transaction> expectedTransactions = Arrays.asList(transaction);

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.findById(relatedWalletId)).thenReturn(Optional.of(relatedWallet));
        when(transactionRepository.findByWalletAndRelatedWallet(wallet, relatedWallet))
                .thenReturn(expectedTransactions);

        // Execute
        List<Transaction> result = transactionService.getTransactionsBetweenWallets(walletId, relatedWalletId);

        // Verify
        assertEquals(expectedTransactions, result);
        verify(walletRepository).findById(walletId);
        verify(walletRepository).findById(relatedWalletId);
        verify(transactionRepository).findByWalletAndRelatedWallet(wallet, relatedWallet);
    }

    @Test
    void testCreateTransaction() {
        // Setup
        double amount = 75.0;
        String description = "New transaction";

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setId(UUID.randomUUID());
            return savedTransaction;
        });

        // Capture the transaction to verify its properties
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Execute
        Transaction result = transactionService.createTransaction(walletId, amount, TransactionType.DEPOSIT, description);

        // Verify
        assertNotNull(result);
        assertNotNull(result.getId());

        verify(walletRepository).findById(walletId);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction capturedTransaction = transactionCaptor.getValue();
        assertEquals(wallet, capturedTransaction.getWallet());
        assertEquals(BigDecimal.valueOf(amount), capturedTransaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, capturedTransaction.getType());
        assertEquals(description, capturedTransaction.getDescription());
        assertNull(capturedTransaction.getRelatedWallet());
    }

    @Test
    void testCreateTransactionWithRelatedWallet() {
        // Setup
        double amount = 100.0;
        String description = "Transfer transaction";
        UUID relatedWalletId = relatedWallet.getId();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.findById(relatedWalletId)).thenReturn(Optional.of(relatedWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setId(UUID.randomUUID());
            return savedTransaction;
        });

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Execute
        Transaction result = transactionService.createTransaction(
                walletId, amount, TransactionType.PAYMENT, description, relatedWalletId);

        // Verify
        assertNotNull(result);
        assertNotNull(result.getId());

        verify(walletRepository).findById(walletId);
        verify(walletRepository).findById(relatedWalletId);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction capturedTransaction = transactionCaptor.getValue();
        assertEquals(wallet, capturedTransaction.getWallet());
        assertEquals(BigDecimal.valueOf(amount), capturedTransaction.getAmount());
        assertEquals(TransactionType.PAYMENT, capturedTransaction.getType());
        assertEquals(description, capturedTransaction.getDescription());
        assertEquals(relatedWallet, capturedTransaction.getRelatedWallet());
    }

    @Test
    void testCreateTransactionWalletNotFound() {
        // Setup
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        // Execute & Verify
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(walletId, 50.0, TransactionType.DEPOSIT, "Test");
        });
        assertEquals("Wallet not found with ID: " + walletId, exception.getMessage());

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testCreateTransactionRelatedWalletNotFound() {
        // Setup
        UUID relatedWalletId = UUID.randomUUID();
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.findById(relatedWalletId)).thenReturn(Optional.empty());

        // Execute & Verify
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(
                    walletId, 50.0, TransactionType.PAYMENT, "Test", relatedWalletId);
        });
        assertEquals("Wallet not found with ID: " + relatedWalletId, exception.getMessage());

        verify(transactionRepository, never()).save(any());
    }
}