package id.ac.ui.cs.advprog.perbaikiinaja.service.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for wallet operations.
 */
public interface WalletService {
    /**
     * Create a new wallet for a user.
     *
     * @param user The user who will own the wallet
     * @return The created wallet
     */
    Wallet createWallet(User user);

    /**
     * Create a new wallet for a user with initial balance.
     *
     * @param user The user who will own the wallet
     * @param initialBalance The initial balance for the wallet
     * @return The created wallet
     */
    Wallet createWallet(User user, BigDecimal initialBalance);

    /**
     * Get a wallet by its ID.
     *
     * @param walletId The ID of the wallet
     * @return An Optional containing the wallet if found
     */
    Optional<Wallet> getWalletById(UUID walletId);

    /**
     * Get a wallet by its user.
     *
     * @param user The user who owns the wallet
     * @return An Optional containing the wallet if found
     */
    Optional<Wallet> getWalletByUser(User user);

    /**
     * Get a wallet by its user ID.
     *
     * @param userId The ID of the user
     * @return An Optional containing the wallet if found
     */
    Optional<Wallet> getWalletByUserId(UUID userId);

    /**
     * Add funds to a wallet and create a deposit transaction.
     *
     * @param walletId The ID of the wallet
     * @param amount The amount to deposit
     * @param description A description of the deposit
     * @return The updated wallet
     */
    Wallet deposit(UUID walletId, BigDecimal amount, String description);

    /**
     * Withdraw funds from a wallet and create a withdrawal transaction.
     *
     * @param walletId The ID of the wallet
     * @param amount The amount to withdraw
     * @param description A description of the withdrawal
     * @return The updated wallet
     * @throws IllegalArgumentException if there are insufficient funds
     */
    Wallet withdraw(UUID walletId, BigDecimal amount, String description);

    /**
     * Process a payment for a service.
     *
     * @param customerId The ID of the customer making the payment
     * @param technicianId The ID of the technician receiving the payment
     * @param amount The amount of the payment
     * @param serviceRequestId The ID of the service request being paid for
     * @return A list containing both wallets (customer first, technician second)
     * @throws IllegalArgumentException if there are insufficient funds
     */
    List<Wallet> processServicePayment(UUID customerId, UUID technicianId, BigDecimal amount, UUID serviceRequestId);

    /**
     * Get the balance of a wallet.
     *
     * @param walletId The ID of the wallet
     * @return The wallet's balance
     */
    BigDecimal getBalance(UUID walletId);

    /**
     * Check if a user has a wallet.
     *
     * @param user The user to check
     * @return true if the user has a wallet, false otherwise
     */
    boolean hasWallet(User user);

    /**
     * Save a wallet (create or update).
     *
     * @param wallet The wallet to save
     * @return The saved wallet
     */
    Wallet saveWallet(Wallet wallet);
}