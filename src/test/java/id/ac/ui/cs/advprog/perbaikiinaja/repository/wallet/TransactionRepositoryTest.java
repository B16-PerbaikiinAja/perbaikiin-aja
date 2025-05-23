package id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Transaction;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private Wallet wallet1;
    private Wallet wallet2;
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        // Create test wallets
        Customer customer1 = new Customer();
        customer1.setEmail("customer1@example.com");
        customer1.setFullName("Customer One");
        customer1.setPhoneNumber("1234567890");
        customer1.setRole(UserRole.CUSTOMER.getValue());
        customer1.setPassword("password");

        Customer customer2 = new Customer();
        customer2.setEmail("customer2@example.com");
        customer2.setFullName("Customer Two");
        customer2.setPhoneNumber("987654321");
        customer2.setRole(UserRole.CUSTOMER.getValue());
        customer2.setPassword("password");

        customer1 = userRepository.save(customer1);
        customer2 = userRepository.save(customer2);

        wallet1 = new Wallet(customer1);
        wallet2 = new Wallet(customer2);

        walletRepository.save(wallet1);
        walletRepository.save(wallet2);

        // Create test transactions
        transaction1 = new Transaction(wallet1, new BigDecimal("100.00"), TransactionType.DEPOSIT, "Initial deposit");
        transaction1.setTimestamp(LocalDateTime.now().minusDays(2));

        transaction2 = new Transaction(wallet1, new BigDecimal("50.00"), TransactionType.WITHDRAWAL, "Withdrawal");
        transaction2.setTimestamp(LocalDateTime.now().minusDays(1));

        transaction3 = new Transaction(wallet1, new BigDecimal("75.00"), TransactionType.PAYMENT, "Payment for service", wallet2);
        transaction3.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
    }

    @Test
    void testFindByWallet() {
        List<Transaction> transactions = transactionRepository.findByWallet(wallet1);

        assertEquals(3, transactions.size());
        assertTrue(transactions.contains(transaction1));
        assertTrue(transactions.contains(transaction2));
        assertTrue(transactions.contains(transaction3));
    }

    @Test
    void testFindByWalletWithPagination() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Transaction> transactionPage = transactionRepository.findByWallet(wallet1, pageable);

        assertEquals(2, transactionPage.getContent().size());
        assertEquals(3, transactionPage.getTotalElements());
        assertEquals(2, transactionPage.getTotalPages());

        // First page should contain the most recent transactions (sorted by timestamp DESC)
        List<Transaction> transactions = transactionPage.getContent();
        assertEquals(transaction3.getId(), transactions.get(0).getId());
        assertEquals(transaction2.getId(), transactions.get(1).getId());

        // Check second page
        pageable = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "timestamp"));
        transactionPage = transactionRepository.findByWallet(wallet1, pageable);

        assertEquals(1, transactionPage.getContent().size());
        assertEquals(transaction1.getId(), transactionPage.getContent().get(0).getId());
    }

    @Test
    void testFindByWalletAndType() {
        List<Transaction> deposits = transactionRepository.findByWalletAndType(wallet1, TransactionType.DEPOSIT);
        assertEquals(1, deposits.size());
        assertEquals(transaction1.getId(), deposits.get(0).getId());

        List<Transaction> withdrawals = transactionRepository.findByWalletAndType(wallet1, TransactionType.WITHDRAWAL);
        assertEquals(1, withdrawals.size());
        assertEquals(transaction2.getId(), withdrawals.get(0).getId());

        List<Transaction> payments = transactionRepository.findByWalletAndType(wallet1, TransactionType.PAYMENT);
        assertEquals(1, payments.size());
        assertEquals(transaction3.getId(), payments.get(0).getId());

        List<Transaction> earnings = transactionRepository.findByWalletAndType(wallet1, TransactionType.EARNING);
        assertEquals(0, earnings.size());
    }

    @Test
    void testFindByWalletAndTimestampBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Transaction> recentTransactions = transactionRepository.findByWalletAndTimestampBetween(
                wallet1, startDate, endDate);

        assertEquals(2, recentTransactions.size());
        assertTrue(recentTransactions.contains(transaction2));
        assertTrue(recentTransactions.contains(transaction3));
        assertFalse(recentTransactions.contains(transaction1)); // This was 2 days ago
    }

    @Test
    void testFindByWalletAndRelatedWallet() {
        List<Transaction> transferTransactions = transactionRepository.findByWalletAndRelatedWallet(wallet1, wallet2);

        assertEquals(1, transferTransactions.size());
        assertEquals(transaction3.getId(), transferTransactions.get(0).getId());
    }

    @Test
    void testSaveTransaction() {
        Transaction newTransaction = new Transaction(wallet2, new BigDecimal("200.00"),
                TransactionType.DEPOSIT, "New deposit");

        Transaction savedTransaction = transactionRepository.save(newTransaction);

        assertNotNull(savedTransaction.getId());
        assertEquals(newTransaction.getAmount(), savedTransaction.getAmount());
        assertEquals(newTransaction.getType(), savedTransaction.getType());
        assertEquals(newTransaction.getDescription(), savedTransaction.getDescription());

        // Verify it can be retrieved
        assertTrue(transactionRepository.findById(savedTransaction.getId()).isPresent());
    }
}