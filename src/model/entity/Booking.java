package model.entity;
import model.enums.BookingStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Booking entity representing flight reservations in the system.
 * Supports multi-passenger bookings with individual passenger details.
 * Tracks booking status, payment, and maintains references to flight and
 * customer.
 */
public class Booking {
    private int bookingId;
    private int customerId;
    private int flightId;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private double totalAmount;
    private int numPassengers;
    private List<Passenger> passengers;

    // For display purposes
    private Flight flight;
    private Customer customer;

    public Booking() {
        this.passengers = new ArrayList<>();
        this.status = BookingStatus.PENDING;
        this.bookingDate = LocalDateTime.now();
    }

    public Booking(int customerId, int flightId, double totalAmount, int numPassengers) {
        this();
        this.customerId = customerId;
        this.flightId = flightId;
        this.totalAmount = totalAmount;
        this.numPassengers = numPassengers;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getNumPassengers() {
        return numPassengers;
    }

    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    /**
     * Adds a passenger to this booking.
     *
     * @param passenger Passenger to add
     */
    public void addPassenger(Passenger passenger) {
        this.passengers.add(passenger);
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Returns formatted booking date for display purposes.
     *
     * @return Booking date in "yyyy-MM-dd HH:mm" format
     */
    public String getFormattedBookingDate() {
        return bookingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Generates a formatted booking reference number.
     *
     * @return Booking reference in format "BK######"
     */
    public String getBookingReference() {
        return String.format("BK%06d", bookingId);
    }

    @Override
    public String toString() {
        return String.format("Booking #%s | Status: %s | Passengers: %d | Amount: $%.2f | Date: %s",
                getBookingReference(), status, numPassengers, totalAmount, getFormattedBookingDate());
    }
}
