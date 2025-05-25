package id.ac.ui.cs.advprog.perbaikiinaja.controller.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.TransactionService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionController transactionController;

    private User user;
    private Wallet wallet;
    private Transaction transaction1;
    private Transaction transaction2;
    private UUID walletId;
    private UUID transactionId;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        wallet = new Wallet(user);
        wallet.setId(walletId);
        wallet.setBalance(new BigDecimal("200.00"));

        transactionId = UUID.randomUUID();
        transaction1 = new Transaction(wallet, new BigDecimal("100.00"), TransactionType.DEPOSIT, "Initial deposit");
        transaction1.setId(transactionId);
        transaction1.setTimestamp(LocalDateTime.now().minusDays(1));

        transaction2 = new Transaction(wallet, new BigDecimal("50.00"), TransactionType.WITHDRAWAL, "Withdrawal");
        transaction2.setId(UUID.randomUUID());
        transaction2.setTimestamp(LocalDateTime.now());
    }

    @Test
    void testGetMyTransactions() {
        // Setup
        int page = 0;
        int size = 20;
        String sortBy = "timestamp";
        String direction = "DESC";

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());

        when(authentication.getPrincipal()).thenReturn(user);
        when(walletService.getWalletByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionService.getTransactionsByWallet(wallet.getId(), pageable)).thenReturn(transactionPage);

        // Execute
        ResponseEntity<?> response = transactionController.getMyTransactions(
                authentication, page, size, sortBy, direction);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> transactionList = (List<Map<String, Object>>) responseBody.get("transactions");
        assertEquals(2, transactionList.size());
        assertEquals(0, responseBody.get("currentPage"));
        assertEquals(2L, responseBody.get("totalItems"));
        assertEquals(1, responseBody.get("totalPages"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(user);
        verify(transactionService).getTransactionsByWallet(wallet.getId(), pageable);
    }

    @Test
    void testGetMyTransactionsWhenWalletNotFound() {
        // Setup
        when(authentication.getPrincipal()).thenReturn(user);
        when(walletService.getWalletByUser(user)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<?> response = transactionController.getMyTransactions(
                authentication, 0, 20, "timestamp", "DESC");

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Wallet not found. Create a wallet first.", responseBody.get("message"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(user);
        verify(transactionService, never()).getTransactionsByWallet(any(), any());
    }

    @Test
    void testGetMyTransactionsByType() {
        // Setup
        TransactionType type = TransactionType.DEPOSIT;
        List<Transaction> transactions = Collections.singletonList(transaction1);

        when(authentication.getPrincipal()).thenReturn(user);
        when(walletService.getWalletByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionService.getTransactionsByWalletAndType(wallet.getId(), type)).thenReturn(transactions);

        // Execute
        ResponseEntity<?> response = transactionController.getMyTransactionsByType(authentication, type);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> transactionList = (List<Map<String, Object>>) responseBody.get("transactions");
        assertEquals(1, transactionList.size());
        assertEquals(1, responseBody.get("count"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(user);
        verify(transactionService).getTransactionsByWalletAndType(wallet.getId(), type);
    }

    @Test
    void testGetMyTransactionsByDateRange() {
        // Setup
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

        when(authentication.getPrincipal()).thenReturn(user);
        when(walletService.getWalletByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionService.getTransactionsByWalletAndDateRange(wallet.getId(), startDate, endDate))
                .thenReturn(transactions);

        // Execute
        ResponseEntity<?> response = transactionController.getMyTransactionsByDateRange(
                authentication, startDate, endDate);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> transactionList = (List<Map<String, Object>>) responseBody.get("transactions");
        assertEquals(2, transactionList.size());
        assertEquals(2, responseBody.get("count"));

        verify(authentication).getPrincipal();
        verify(walletService).getWalletByUser(user);
        verify(transactionService).getTransactionsByWalletAndDateRange(wallet.getId(), startDate, endDate);
    }

    @Test
    void testGetTransaction() {
        // Setup - This is an admin-only endpoint
        when(transactionService.getTransactionById(transactionId)).thenReturn(Optional.of(transaction1));

        // Execute
        ResponseEntity<?> response = transactionController.getTransaction(transactionId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(transaction1.getId(), responseBody.get("id"));
        assertEquals(wallet.getId(), responseBody.get("walletId"));
        assertEquals(transaction1.getAmount(), responseBody.get("amount"));
        assertEquals(transaction1.getType(), responseBody.get("type"));
        assertEquals(transaction1.getTimestamp(), responseBody.get("timestamp"));
        assertEquals(transaction1.getDescription(), responseBody.get("description"));

        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void testGetTransactionWhenNotFound() {
        // Setup
        when(transactionService.getTransactionById(transactionId)).thenReturn(Optional.empty());

        // Execute
        ResponseEntity<?> response = transactionController.getTransaction(transactionId);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Transaction not found", responseBody.get("message"));

        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void testCreateTransactionResponse() {
        // Setup
        // Creating a transaction with a related wallet to test all response fields
        Wallet relatedWallet = new Wallet();
        relatedWallet.setId(UUID.randomUUID());

        Transaction testTransaction = new Transaction(
                wallet, new BigDecimal("25.00"), TransactionType.PAYMENT, "Test payment", relatedWallet);
        testTransaction.setId(UUID.randomUUID());

        // Access private method via reflection
        try {
            java.lang.reflect.Method createTransactionResponse =
                    TransactionController.class.getDeclaredMethod("createTransactionResponse", Transaction.class);
            createTransactionResponse.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) createTransactionResponse.invoke(
                    transactionController, testTransaction);

            // Verify
            assertNotNull(result);
            assertEquals(testTransaction.getId(), result.get("id"));
            assertEquals(wallet.getId(), result.get("walletId"));
            assertEquals(testTransaction.getAmount(), result.get("amount"));
            assertEquals(testTransaction.getType(), result.get("type"));
            assertEquals(testTransaction.getTimestamp(), result.get("timestamp"));
            assertEquals(testTransaction.getDescription(), result.get("description"));
            assertEquals(relatedWallet.getId(), result.get("relatedWalletId"));

        } catch (Exception e) {
            fail("Failed to invoke createTransactionResponse: " + e.getMessage());
        }
    }
}