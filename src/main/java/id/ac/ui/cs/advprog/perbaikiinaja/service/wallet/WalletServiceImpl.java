package id.ac.ui.cs.advprog.perbaikiinaja.service.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.TransactionRepository;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the wallet service interface.
 */
@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private static final String walletNotFoundStr = "Wallet not found";

    @Autowired
    public WalletServiceImpl(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Wallet createWallet(User user) {
        if (walletRepository.existsByUser(user)) {
            throw new IllegalStateException("User already has a wallet");
        }
        Wallet wallet = new Wallet(user);
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet createWallet(User user, BigDecimal initialBalance) {
        if (user.getRole().equals(UserRole.ADMIN.getValue())) {
            throw new IllegalArgumentException("Admin users cannot have wallets");
        }

        if (walletRepository.existsByUser(user)) {
            throw new IllegalStateException("User already has a wallet");
        }

        Wallet wallet = new Wallet(user, initialBalance);
        Wallet savedWallet = walletRepository.save(wallet);

        if (initialBalance.compareTo(BigDecimal.ZERO) > 0) {
            Transaction transaction = new Transaction(
                    savedWallet,
                    initialBalance,
                    TransactionType.DEPOSIT,
                    "Initial deposit"
            );
            transactionRepository.save(transaction);
        }

        return savedWallet;
    }

    @Override
    public Optional<Wallet> getWalletById(UUID walletId) {
        return walletRepository.findById(walletId);
    }

    @Override
    public Optional<Wallet> getWalletByUser(User user) {
        return walletRepository.findByUser(user);
    }

    @Override
    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Wallet deposit(UUID walletId, BigDecimal amount, String description) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException(walletNotFoundStr));

        wallet.deposit(amount);

        Transaction transaction = new Transaction(
                wallet,
                amount,
                TransactionType.DEPOSIT,
                description
        );
        transactionRepository.save(transaction);

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet withdraw(UUID walletId, BigDecimal amount, String description) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException(walletNotFoundStr));

        wallet.withdraw(amount);

        Transaction transaction = new Transaction(
                wallet,
                amount,
                TransactionType.WITHDRAWAL,
                description
        );
        transactionRepository.save(transaction);

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public List<Wallet> processServicePayment(UUID customerId, UUID technicianId, BigDecimal amount, UUID serviceRequestId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

        Wallet customerWallet = getWalletByUser(customer)
                .orElseThrow(() -> new IllegalArgumentException("Customer wallet not found"));

        Wallet technicianWallet = getWalletByUser(technician)
                .orElseThrow(() -> new IllegalArgumentException("Technician wallet not found"));

        // Withdraw from customer
        customerWallet.withdraw(amount);

        // Payment transaction for customer
        Transaction customerTransaction = new Transaction(
                customerWallet,
                amount,
                TransactionType.PAYMENT,
                "Payment for service #" + serviceRequestId,
                technicianWallet
        );

        // Deposit to technician
        technicianWallet.deposit(amount);

        // Earning transaction for technician
        Transaction technicianTransaction = new Transaction(
                technicianWallet,
                amount,
                TransactionType.EARNING,
                "Payment received for service #" + serviceRequestId,
                customerWallet
        );

        transactionRepository.save(customerTransaction);
        transactionRepository.save(technicianTransaction);

        // Save both wallets
        customerWallet = walletRepository.save(customerWallet);
        technicianWallet = walletRepository.save(technicianWallet);

        return Arrays.asList(customerWallet, technicianWallet);
    }

    @Override
    public BigDecimal getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException(walletNotFoundStr));
        return wallet.getBalance();
    }

    @Override
    public boolean hasWallet(User user) {
        return walletRepository.existsByUser(user);
    }

    @Override
    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }
}