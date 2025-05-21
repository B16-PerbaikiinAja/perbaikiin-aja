package id.ac.ui.cs.advprog.perbaikiinaja.service.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.TransactionRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the TransactionService interface.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public Optional<Transaction> getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId);
    }

    @Override
    public List<Transaction> getTransactionsByWallet(UUID walletId) {
        Wallet wallet = getWalletOrThrow(walletId);
        return transactionRepository.findByWallet(wallet);
    }

    @Override
    public Page<Transaction> getTransactionsByWallet(UUID walletId, Pageable pageable) {
        Wallet wallet = getWalletOrThrow(walletId);
        return transactionRepository.findByWallet(wallet, pageable);
    }

    @Override
    public List<Transaction> getTransactionsByWalletAndType(UUID walletId, TransactionType type) {
        Wallet wallet = getWalletOrThrow(walletId);
        return transactionRepository.findByWalletAndType(wallet, type);
    }

    @Override
    public List<Transaction> getTransactionsByWalletAndDateRange(UUID walletId, LocalDateTime startDate, LocalDateTime endDate) {
        Wallet wallet = getWalletOrThrow(walletId);
        return transactionRepository.findByWalletAndTimestampBetween(wallet, startDate, endDate);
    }

    @Override
    public List<Transaction> getTransactionsBetweenWallets(UUID walletId, UUID relatedWalletId) {
        Wallet wallet = getWalletOrThrow(walletId);
        Wallet relatedWallet = getWalletOrThrow(relatedWalletId);
        return transactionRepository.findByWalletAndRelatedWallet(wallet, relatedWallet);
    }

    @Override
    @Transactional
    public Transaction createTransaction(UUID walletId, double amount, TransactionType type, String description) {
        Wallet wallet = getWalletOrThrow(walletId);

        Transaction transaction = new Transaction(
                wallet,
                BigDecimal.valueOf(amount),
                type,
                description
        );

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction createTransaction(UUID walletId, double amount, TransactionType type, String description, UUID relatedWalletId) {
        Wallet wallet = getWalletOrThrow(walletId);
        Wallet relatedWallet = getWalletOrThrow(relatedWalletId);

        Transaction transaction = new Transaction(
                wallet,
                BigDecimal.valueOf(amount),
                type,
                description,
                relatedWallet
        );

        return transactionRepository.save(transaction);
    }

    /**
     * Helper method to get a wallet by ID or throw an exception.
     *
     * @param walletId The ID of the wallet
     * @return The wallet
     * @throws IllegalArgumentException if the wallet is not found
     */
    private Wallet getWalletOrThrow(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with ID: " + walletId));
    }
}