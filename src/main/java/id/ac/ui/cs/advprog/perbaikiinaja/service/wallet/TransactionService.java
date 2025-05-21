package id.ac.ui.cs.advprog.perbaikiinaja.service.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for transaction-related operations.
 */
public interface TransactionService {
    /**
     * Get a transaction by its ID.
     *
     * @param transactionId The ID of the transaction
     * @return An Optional containing the transaction if found
     */
    Optional<Transaction> getTransactionById(UUID transactionId);

    /**
     * Get all transactions for a wallet.
     *
     * @param walletId The ID of the wallet
     * @return A list of transactions
     */
    List<Transaction> getTransactionsByWallet(UUID walletId);

    /**
     * Get transactions for a wallet with pagination.
     *
     * @param walletId The ID of the wallet
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> getTransactionsByWallet(UUID walletId, Pageable pageable);

    /**
     * Get transactions of a specific type for a wallet.
     *
     * @param walletId The ID of the wallet
     * @param type The type of transactions to get
     * @return A list of transactions
     */
    List<Transaction> getTransactionsByWalletAndType(UUID walletId, TransactionType type);

    /**
     * Get transactions for a wallet within a date range.
     *
     * @param walletId The ID of the wallet
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of transactions
     */
    List<Transaction> getTransactionsByWalletAndDateRange(UUID walletId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get transactions between two wallets.
     *
     * @param walletId The ID of the primary wallet
     * @param relatedWalletId The ID of the related wallet
     * @return A list of transactions
     */
    List<Transaction> getTransactionsBetweenWallets(UUID walletId, UUID relatedWalletId);

    /**
     * Create a transaction.
     *
     * @param walletId The ID of the wallet
     * @param amount The amount of the transaction
     * @param type The type of transaction
     * @param description A description of the transaction
     * @return The created transaction
     */
    Transaction createTransaction(UUID walletId, double amount, TransactionType type, String description);

    /**
     * Create a transaction with a related wallet.
     *
     * @param walletId The ID of the wallet
     * @param amount The amount of the transaction
     * @param type The type of transaction
     * @param description A description of the transaction
     * @param relatedWalletId The ID of the related wallet
     * @return The created transaction
     */
    Transaction createTransaction(UUID walletId, double amount, TransactionType type, String description, UUID relatedWalletId);
}