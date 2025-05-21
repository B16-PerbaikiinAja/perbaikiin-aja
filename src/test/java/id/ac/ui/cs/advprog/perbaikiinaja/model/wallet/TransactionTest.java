package id.ac.ui.cs.advprog.perbaikiinaja.model.wallet;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class TransactionTest {

    private Wallet mockWallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        mockWallet = Mockito.mock(Wallet.class);
        Mockito.when(mockWallet.getId()).thenReturn(UUID.randomUUID());
    }

    @Test
    void testConstructorBasic() {
        BigDecimal amount = new BigDecimal("75.50");
        String description = "Test transaction";

        transaction = new Transaction(mockWallet, amount, TransactionType.DEPOSIT, description);

        assertNotNull(transaction);
        assertEquals(mockWallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(description, transaction.getDescription());
        assertNotNull(transaction.getTimestamp());
        assertNull(transaction.getRelatedWallet());
    }

    @Test
    void testConstructorWithRelatedWallet() {
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Transfer transaction";
        Wallet relatedWallet = Mockito.mock(Wallet.class);

        transaction = new Transaction(mockWallet, amount, TransactionType.PAYMENT, description, relatedWallet);

        assertNotNull(transaction);
        assertEquals(mockWallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
        assertEquals(TransactionType.PAYMENT, transaction.getType());
        assertEquals(description, transaction.getDescription());
        assertNotNull(transaction.getTimestamp());
        assertEquals(relatedWallet, transaction.getRelatedWallet());
    }

    @Test
    void testPrePersist() {
        transaction = new Transaction();

        // Access private method via reflection
        try {
            java.lang.reflect.Method onCreate = Transaction.class.getDeclaredMethod("onCreate");
            onCreate.setAccessible(true);
            onCreate.invoke(transaction);

            assertNotNull(transaction.getCreatedAt());
            assertTrue(transaction.getCreatedAt() instanceof LocalDateTime);
        } catch (Exception e) {
            fail("Failed to invoke onCreate method: " + e.getMessage());
        }
    }

    @Test
    void testSettersAndGetters() {
        transaction = new Transaction();

        UUID id = UUID.randomUUID();
        Wallet wallet = Mockito.mock(Wallet.class);
        BigDecimal amount = new BigDecimal("150.25");
        TransactionType type = TransactionType.EARNING;
        LocalDateTime timestamp = LocalDateTime.now();
        String description = "Test description";
        Wallet relatedWallet = Mockito.mock(Wallet.class);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        transaction.setId(id);
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(timestamp);
        transaction.setDescription(description);
        transaction.setRelatedWallet(relatedWallet);
        transaction.setCreatedAt(createdAt);

        assertEquals(id, transaction.getId());
        assertEquals(wallet, transaction.getWallet());
        assertEquals(amount, transaction.getAmount());
        assertEquals(type, transaction.getType());
        assertEquals(timestamp, transaction.getTimestamp());
        assertEquals(description, transaction.getDescription());
        assertEquals(relatedWallet, transaction.getRelatedWallet());
        assertEquals(createdAt, transaction.getCreatedAt());
    }
}