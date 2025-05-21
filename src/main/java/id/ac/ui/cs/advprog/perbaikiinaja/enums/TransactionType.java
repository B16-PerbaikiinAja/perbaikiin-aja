package id.ac.ui.cs.advprog.perbaikiinaja.enums;

/**
 * Enumeration of transaction types for financial operations in the system.
 */
public enum TransactionType {
    /**
     * Money added to wallet (e.g., top-up)
     */
    DEPOSIT,

    /**
     * Money removed from wallet (e.g., withdrawal)
     */
    WITHDRAWAL,

    /**
     * Payment for a service
     */
    PAYMENT,

    /**
     * Receipt of payment for a service
     */
    EARNING,
}