package model.enums;

/**
 * Represents the operational status of a flight.
 * SCHEDULED: Flight is planned and bookable, CANCELLED: Flight cancelled,
 * COMPLETED: All seats booked or flight departed.
 */
public enum FlightStatus {
    SCHEDULED,
    CANCELLED,
    COMPLETED
}
