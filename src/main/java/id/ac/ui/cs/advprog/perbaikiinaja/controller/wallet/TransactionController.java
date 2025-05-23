package id.ac.ui.cs.advprog.perbaikiinaja.controller.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.TransactionService;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST controller for transaction-related operations.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final WalletService walletService;
    private final String MESSAGE_STR = "message";
    private final String WALLET_NOT_FOUND_STR = "Wallet not found. Create a wallet first.";

    @Autowired
    public TransactionController(
            TransactionService transactionService,
            WalletService walletService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    /**
     * Get transactions for the authenticated user's wallet.
     *
     * @param authentication The authentication object containing the user
     * @param page The page number (zero-based)
     * @param size The page size
     * @param sortBy The field to sort by
     * @param direction The sort direction
     * @return A page of transactions
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        User user = (User) authentication.getPrincipal();
        Optional<Wallet> walletOpt = walletService.getWalletByUser(user);

        if (walletOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_STR, WALLET_NOT_FOUND_STR);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Transaction> transactions = transactionService.getTransactionsByWallet(walletOpt.get().getId(), pageable);

        return ResponseEntity.ok(createTransactionPageResponse(transactions));
    }

    /**
     * Get transactions of a specific type for the authenticated user's wallet.
     *
     * @param authentication The authentication object containing the user
     * @param type The transaction type
     * @return A list of transactions
     */
    @GetMapping("/me/type/{type}")
    public ResponseEntity<Map<String, Object>> getMyTransactionsByType(
            Authentication authentication,
            @PathVariable TransactionType type) {
        User user = (User) authentication.getPrincipal();
        Optional<Wallet> walletOpt = walletService.getWalletByUser(user);

        if (walletOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_STR, WALLET_NOT_FOUND_STR);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<Transaction> transactions = transactionService.getTransactionsByWalletAndType(walletOpt.get().getId(), type);

        return ResponseEntity.ok(createTransactionListResponse(transactions));
    }

    /**
     * Get transactions within a date range for the authenticated user's wallet.
     *
     * @param authentication The authentication object containing the user
     * @param startDate The start date
     * @param endDate The end date
     * @return A list of transactions
     */
    @GetMapping("/me/date-range")
    public ResponseEntity<Map<String, Object>> getMyTransactionsByDateRange(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        User user = (User) authentication.getPrincipal();
        Optional<Wallet> walletOpt = walletService.getWalletByUser(user);

        if (walletOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_STR, WALLET_NOT_FOUND_STR);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<Transaction> transactions = transactionService.getTransactionsByWalletAndDateRange(
                walletOpt.get().getId(), startDate, endDate);

        return ResponseEntity.ok(createTransactionListResponse(transactions));
    }

    /**
     * Get a transaction by its ID (admin only).
     *
     * @param transactionId The ID of the transaction
     * @return The transaction information
     */
    @GetMapping("/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTransaction(@PathVariable UUID transactionId) {
        Optional<Transaction> transactionOpt = transactionService.getTransactionById(transactionId);

        if (transactionOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_STR, "Transaction not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(createTransactionResponse(transactionOpt.get()));
    }

    /**
     * Create a response map from a transaction.
     *
     * @param transaction The transaction
     * @return A map containing transaction information
     */
    private Map<String, Object> createTransactionResponse(Transaction transaction) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", transaction.getId());
        response.put("walletId", transaction.getWallet().getId());
        response.put("amount", transaction.getAmount());
        response.put("type", transaction.getType());
        response.put("timestamp", transaction.getTimestamp());
        response.put("description", transaction.getDescription());

        if (transaction.getRelatedWallet() != null) {
            response.put("relatedWalletId", transaction.getRelatedWallet().getId());
        }

        return response;
    }

    /**
     * Create a response map from a list of transactions.
     *
     * @param transactions The list of transactions
     * @return A map containing a list of transaction information
     */
    private Map<String, Object> createTransactionListResponse(List<Transaction> transactions) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> transactionList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            transactionList.add(createTransactionResponse(transaction));
        }

        response.put("transactions", transactionList);
        response.put("count", transactions.size());

        return response;
    }

    /**
     * Create a response map from a page of transactions.
     *
     * @param transactions The page of transactions
     * @return A map containing pagination information and transaction data
     */
    private Map<String, Object> createTransactionPageResponse(Page<Transaction> transactions) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> transactionList = new ArrayList<>();

        for (Transaction transaction : transactions.getContent()) {
            transactionList.add(createTransactionResponse(transaction));
        }

        response.put("transactions", transactionList);
        response.put("currentPage", transactions.getNumber());
        response.put("totalItems", transactions.getTotalElements());
        response.put("totalPages", transactions.getTotalPages());

        return response;
    }
}