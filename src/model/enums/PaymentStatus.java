package model.enums;

/**
 * Represents the status of a payment transaction.
 * PENDING: Awaiting processing, COMPLETED: Successfully processed, FAILED:
 * Processing failed, REFUNDED: Payment refunded.
 */
public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
