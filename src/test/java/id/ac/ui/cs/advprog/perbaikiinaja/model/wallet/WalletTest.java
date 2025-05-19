package id.ac.ui.cs.advprog.perbaikiinaja.model.wallet;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class WalletTest {

    private User mockUser;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        mockUser = Mockito.mock(Customer.class);
        Mockito.when(mockUser.getId()).thenReturn(UUID.randomUUID());
        wallet = new Wallet(mockUser);
    }

    @Test
    void testConstructorWithUser() {
        assertEquals(mockUser, wallet.getUser());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
        assertTrue(wallet.getTransactions().isEmpty());
        assertEquals(mockUser, wallet.getUser());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
        assertTrue(wallet.getTransactions().isEmpty());
    }

    @Test
    void testConstructorWithInitialBalance() {
        BigDecimal initialBalance = new BigDecimal("100.50");
        Wallet walletWithBalance = new Wallet(mockUser, initialBalance);

        assertEquals(mockUser, walletWithBalance.getUser());
        assertEquals(initialBalance, walletWithBalance.getBalance());
        assertTrue(walletWithBalance.getTransactions().isEmpty());
    }

    @Test
    void testDeposit() {
        BigDecimal amount = new BigDecimal("50.75");
        wallet.deposit(amount);
        assertEquals(new BigDecimal("50.75"), wallet.getBalance());
    }

    @Test
    void testDepositNegativeAmount() {
        BigDecimal negativeAmount = new BigDecimal("-10.00");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            wallet.deposit(negativeAmount);
        });
        assertEquals("Deposit amount must be positive", exception.getMessage());
    }

    @Test
    void testWithdraw() {
        // Set up initial balance
        wallet.deposit(new BigDecimal("100.00"));

        // Withdraw
        BigDecimal amount = new BigDecimal("50.00");
        wallet.withdraw(amount);
        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
    }

    @Test
    void testWithdrawNegativeAmount() {
        wallet.deposit(new BigDecimal("100.00"));

        BigDecimal negativeAmount = new BigDecimal("-10.00");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            wallet.withdraw(negativeAmount);
        });
        assertEquals("Withdrawal amount must be positive", exception.getMessage());
    }

    @Test
    void testWithdrawInsufficientFunds() {
        wallet.deposit(new BigDecimal("50.00"));

        BigDecimal excessiveAmount = new BigDecimal("100.00");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            wallet.withdraw(excessiveAmount);
        });
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    void testAddTransaction() {
        Transaction mockTransaction = Mockito.mock(Transaction.class);
        wallet.addTransaction(mockTransaction);

        assertEquals(1, wallet.getTransactions().size());
        assertTrue(wallet.getTransactions().contains(mockTransaction));
        Mockito.verify(mockTransaction).setWallet(wallet);
    }

    @Test
    void testPrePersist() {
        // Create a new wallet to trigger onCreate
        Wallet freshWallet = new Wallet();

        // Access private method via reflection
        try {
            java.lang.reflect.Method onCreate = Wallet.class.getDeclaredMethod("onCreate");
            onCreate.setAccessible(true);
            onCreate.invoke(freshWallet);

            assertNotNull(freshWallet.getCreatedAt());
            assertNotNull(freshWallet.getUpdatedAt());
            assertEquals(freshWallet.getCreatedAt(), freshWallet.getUpdatedAt());
        } catch (Exception e) {
            fail("Failed to invoke onCreate method: " + e.getMessage());
        }
    }

    @Test
    void testPreUpdate() {
        // Setup initial timestamps
        LocalDateTime initialTime = LocalDateTime.now().minusHours(1);
        wallet.setCreatedAt(initialTime);
        wallet.setUpdatedAt(initialTime);

        // Access private method via reflection
        try {
            java.lang.reflect.Method onUpdate = Wallet.class.getDeclaredMethod("onUpdate");
            onUpdate.setAccessible(true);
            onUpdate.invoke(wallet);

            assertEquals(initialTime, wallet.getCreatedAt());
            assertNotEquals(initialTime, wallet.getUpdatedAt());
            assertTrue(wallet.getUpdatedAt().isAfter(initialTime));
        } catch (Exception e) {
            fail("Failed to invoke onUpdate method: " + e.getMessage());
        }
    }
}