package id.ac.ui.cs.advprog.perbaikiinaja.repository.wallet;

import static org.junit.jupiter.api.Assertions.*;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    private Customer mockCustomer;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Create real customer instead of mock
        mockCustomer = new Customer();
        mockCustomer.setEmail("test@example.com");
        mockCustomer.setFullName("Test Customer");
        mockCustomer.setPassword("password");
        mockCustomer.setPhoneNumber("1234567890");
        mockCustomer.setRole(UserRole.CUSTOMER.getValue());
        // Save the customer
        mockCustomer = userRepository.save(mockCustomer);
    }

    @Test
    void testFindByUser() {
        // Prepare test data

        Wallet wallet = new Wallet(mockCustomer);
        walletRepository.save(wallet);

        // Test findByUser
        Optional<Wallet> foundWallet = walletRepository.findByUser(mockCustomer);

        assertTrue(foundWallet.isPresent());
        assertEquals(wallet.getId(), foundWallet.get().getId());
        assertEquals(mockCustomer, foundWallet.get().getUser());
    }

    @Test
    void testFindByUserId() {
        // Prepare test data
        UUID userId = mockCustomer.getId();

        Wallet wallet = new Wallet(mockCustomer);
        walletRepository.save(wallet);

        // Test findByUserId
        Optional<Wallet> foundWallet = walletRepository.findByUserId(userId);

        assertTrue(foundWallet.isPresent());
        assertEquals(wallet.getId(), foundWallet.get().getId());
        assertEquals(mockCustomer, foundWallet.get().getUser());
    }

    @Test
    void testExistsByUser() {
        // Prepare test data

        Wallet wallet = new Wallet(mockCustomer);
        walletRepository.save(wallet);

        // Test existsByUser - should return true
        boolean exists = walletRepository.existsByUser(mockCustomer);
        assertTrue(exists);

        // Test with different user - should return false
        Customer differentUser = new Customer();
        differentUser.setEmail("different@example.com");
        differentUser.setFullName("Different User");
        differentUser.setPassword("password");
        differentUser.setPhoneNumber("9876543210");
        differentUser.setRole(UserRole.CUSTOMER.getValue());
        differentUser = userRepository.save(differentUser);

        boolean notExists = walletRepository.existsByUser(differentUser);
        assertFalse(notExists);
    }

    @Test
    void testSaveWallet() {
        // Prepare test data
        BigDecimal initialBalance = new BigDecimal("200.00");

        Wallet wallet = new Wallet(mockCustomer, initialBalance);

        // Save wallet
        Wallet savedWallet = walletRepository.save(wallet);

        assertNotNull(savedWallet);
        assertNotNull(savedWallet.getId());
        assertEquals(mockCustomer, savedWallet.getUser());
        assertEquals(initialBalance, savedWallet.getBalance());

        // Check if wallet exists in repository
        Optional<Wallet> retrievedWallet = walletRepository.findById(savedWallet.getId());
        assertTrue(retrievedWallet.isPresent());
        assertEquals(savedWallet.getId(), retrievedWallet.get().getId());
    }

    @Test
    void testWalletNotFound() {
        // Generate random UUID that shouldn't exist in the database
        UUID nonExistentId = UUID.randomUUID();

        // Test finding wallet by ID
        Optional<Wallet> wallet = walletRepository.findById(nonExistentId);
        assertFalse(wallet.isPresent());

        // Test finding wallet by user ID
        Optional<Wallet> walletByUserId = walletRepository.findByUserId(nonExistentId);
        assertFalse(walletByUserId.isPresent());

        // Test existsByUser with a user that doesn't have a wallet
        Customer userWithoutWallet = new Customer();
        userWithoutWallet.setEmail("nowallet@example.com");
        userWithoutWallet.setFullName("No Wallet User");
        userWithoutWallet.setPassword("password");
        userWithoutWallet.setPhoneNumber("5555555555");
        userWithoutWallet.setRole(UserRole.CUSTOMER.getValue());
        userWithoutWallet = userRepository.save(userWithoutWallet);

        boolean exists = walletRepository.existsByUser(userWithoutWallet);
        assertFalse(exists);
    }
}