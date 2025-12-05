package model.enums;
/**
 * Represents the lifecycle states of a booking.
 * PENDING: Payment not yet completed, CONFIRMED: Payment successful, CANCELLED:
 * Booking cancelled by user or system.
 */
public enum BookingStatus {
    CONFIRMED,
    CANCELLED,
    PENDING
}

