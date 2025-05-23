package id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    /**
     * Find a wallet by its user.
     *
     * @param user The user who owns the wallet
     * @return An Optional containing the wallet if found
     */
    Optional<Wallet> findByUser(User user);

    /**
     * Find a wallet by user ID.
     *
     * @param userId The ID of the user
     * @return An Optional containing the wallet if found
     */
    Optional<Wallet> findByUserId(UUID userId);

    /**
     * Check if a wallet exists for the specified user.
     *
     * @param user The user to check
     * @return true if a wallet exists, false otherwise
     */
    boolean existsByUser(User user);
}