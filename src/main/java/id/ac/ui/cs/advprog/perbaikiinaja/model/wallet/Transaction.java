package id.ac.ui.cs.advprog.perbaikiinaja.model.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "related_wallet_id")
    private Wallet relatedWallet;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Creates a new Transaction.
     *
     * @param wallet The wallet associated with this transaction
     * @param amount The amount of the transaction
     * @param type The type of transaction
     * @param description A description of the transaction
     */
    public Transaction(Wallet wallet, BigDecimal amount, TransactionType type, String description) {
        this.wallet = wallet;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a new Transaction with a related wallet (for transfers).
     *
     * @param wallet The wallet associated with this transaction
     * @param amount The amount of the transaction
     * @param type The type of transaction
     * @param description A description of the transaction
     * @param relatedWallet The related wallet (e.g., recipient of a transfer)
     */
    public Transaction(Wallet wallet, BigDecimal amount, TransactionType type, String description, Wallet relatedWallet) {
        this(wallet, amount, type, description);
        this.relatedWallet = relatedWallet;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}