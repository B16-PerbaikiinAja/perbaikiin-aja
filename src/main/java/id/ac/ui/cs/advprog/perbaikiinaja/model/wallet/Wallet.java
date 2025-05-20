package id.ac.ui.cs.advprog.perbaikiinaja.model.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Creates a new wallet for a user with zero balance.
     *
     * @param user The user who owns this wallet
     */
    public Wallet(User user) {
        this.user = user;
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Creates a new wallet for a user with an initial balance.
     *
     * @param user The user who owns this wallet
     * @param initialBalance The initial balance for the wallet
     */
    public Wallet(User user, BigDecimal initialBalance) {
        this.user = user;
        this.balance = initialBalance;
    }

    /**
     * Adds the specified amount to the balance.
     * This should be used with transactions to maintain consistency.
     *
     * @param amount The amount to add
     * @return The new balance
     */
    public BigDecimal deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    /**
     * Subtracts the specified amount from the balance.
     * This should be used with transactions to maintain consistency.
     *
     * @param amount The amount to withdraw
     * @return The new balance
     * @throws IllegalArgumentException if amount is negative or exceeds balance
     */
    public BigDecimal withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }

    /**
     * Adds a transaction to this wallet's history.
     *
     * @param transaction The transaction to add
     */
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setWallet(this);
    }
}