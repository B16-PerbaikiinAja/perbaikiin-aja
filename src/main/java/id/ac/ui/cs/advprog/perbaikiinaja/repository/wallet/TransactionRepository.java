package id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    /**
     * Find all transactions for a specific wallet.
     *
     * @param wallet The wallet to find transactions for
     * @return A list of transactions
     */
    List<Transaction> findByWallet(Wallet wallet);

    /**
     * Find all transactions for a specific wallet with pagination.
     *
     * @param wallet The wallet to find transactions for
     * @param pageable Pagination information
     * @return A page of transactions
     */
    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);

    /**
     * Find transactions of a specific type for a wallet.
     *
     * @param wallet The wallet to find transactions for
     * @param type The type of transactions to find
     * @return A list of transactions
     */
    List<Transaction> findByWalletAndType(Wallet wallet, TransactionType type);

    /**
     * Find transactions within a date range for a wallet.
     *
     * @param wallet The wallet to find transactions for
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return A list of transactions
     */
    List<Transaction> findByWalletAndTimestampBetween(Wallet wallet, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find transactions between two wallets.
     *
     * @param wallet The primary wallet
     * @param relatedWallet The related wallet
     * @return A list of transactions
     */
    List<Transaction> findByWalletAndRelatedWallet(Wallet wallet, Wallet relatedWallet);
}