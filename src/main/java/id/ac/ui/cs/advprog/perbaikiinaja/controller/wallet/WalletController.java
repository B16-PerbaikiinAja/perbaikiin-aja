package id.ac.ui.cs.advprog.perbaikiinaja.controller.wallet;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Customer;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.Technician;
import id.ac.ui.cs.advprog.perbaikiinaja.model.auth.User;
import id.ac.ui.cs.advprog.perbaikiinaja.model.wallet.Wallet;
import id.ac.ui.cs.advprog.perbaikiinaja.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * REST controller for wallet-related operations.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;
    private final static String messageStr = "message";
    private final static String walletNotFoundStr = "Wallet not found. Create a wallet first.";
    private final static String cstTechStr = "Only customers and technicians have wallets";

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Get wallet information for the authenticated user.
     *
     * @param authentication The authentication object containing the user
     * @return The wallet information
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyWallet(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Check if user is Customer or Technician
        if (!(user instanceof Customer) && !(user instanceof Technician)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(messageStr, cstTechStr));
        }

        // Only call this after checking user type
        Optional<Wallet> wallet = walletService.getWalletByUser(user);

        if (wallet.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, walletNotFoundStr);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(createWalletResponse(wallet.get()));
    }

    /**
     * Create a wallet for the authenticated user.
     *
     * @param authentication The authentication object containing the user
     * @return The created wallet
     */
    @PostMapping("/me")
    public ResponseEntity<Map<String, Object>> createMyWallet(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        if (user.getRole().equals(UserRole.ADMIN.getValue())) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, "Admin users cannot have wallets");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        if (walletService.hasWallet(user)) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, "You already have a wallet");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        Wallet wallet = walletService.createWallet(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createWalletResponse(wallet));
    }

    /**
     * Deposit money into the authenticated user's wallet.
     *
     * @param authentication The authentication object containing the user
     * @param requestBody Map containing amount and description
     * @return The updated wallet
     */
    @PostMapping("/me/deposit")
    public ResponseEntity<Map<String, Object>> deposit(
            Authentication authentication,
            @RequestBody Map<String, Object> requestBody) {
        User user = (User) authentication.getPrincipal();
        Optional<Wallet> walletOpt = walletService.getWalletByUser(user);

        if (!(user instanceof Customer) && !(user instanceof Technician)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(messageStr, cstTechStr));
        }

        if (walletOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, walletNotFoundStr);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(requestBody.get("amount").toString());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, "Invalid amount");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String description = (String) requestBody.getOrDefault("description", "Deposit");

        try {
            Wallet wallet = walletService.deposit(walletOpt.get().getId(), amount, description);
            return ResponseEntity.ok(createWalletResponse(wallet));
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Withdraw money from the authenticated user's wallet.
     *
     * @param authentication The authentication object containing the user
     * @param requestBody Map containing amount and description
     * @return The updated wallet
     */
    @PostMapping("/me/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(
            Authentication authentication,
            @RequestBody Map<String, Object> requestBody) {
        User user = (User) authentication.getPrincipal();
        Optional<Wallet> walletOpt = walletService.getWalletByUser(user);

        if (!(user instanceof Customer) && !(user instanceof Technician)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(messageStr, cstTechStr));
        }

        if (walletOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, walletNotFoundStr);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(requestBody.get("amount").toString());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, "Invalid amount");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String description = (String) requestBody.getOrDefault("description", "Withdrawal");

        try {
            Wallet wallet = walletService.withdraw(walletOpt.get().getId(), amount, description);
            return ResponseEntity.ok(createWalletResponse(wallet));
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get a wallet by its ID (admin only).
     *
     * @param walletId The ID of the wallet
     * @return The wallet information
     */
    @GetMapping("/{walletId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getWallet(@PathVariable UUID walletId) {
        Optional<Wallet> wallet = walletService.getWalletById(walletId);

        if (wallet.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put(messageStr, "Wallet not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(createWalletResponse(wallet.get()));
    }

    /**
     * Create a wallet for a user (admin only).
     *
     * @param userId The ID of the user
     * @param requestBody Map containing initialBalance (optional)
     * @return The created wallet
     */
    @PostMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createWalletForUser(
            @PathVariable UUID userId,
            @RequestBody(required = false) Map<String, Object> requestBody) {
        // Implementation relies on additional methods not included here
        // This would involve looking up the user by ID and creating a wallet for them
        Map<String, Object> response = new HashMap<>();
        response.put(messageStr, "This endpoint is not fully implemented");
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
    }

    /**
     * Create a response map from a wallet.
     *
     * @param wallet The wallet
     * @return A map containing wallet information
     */
    private Map<String, Object> createWalletResponse(Wallet wallet) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", wallet.getId());
        response.put("userId", wallet.getUser().getId());
        response.put("balance", wallet.getBalance());
        response.put("createdAt", wallet.getCreatedAt());
        response.put("updatedAt", wallet.getUpdatedAt());
        return response;
    }
}