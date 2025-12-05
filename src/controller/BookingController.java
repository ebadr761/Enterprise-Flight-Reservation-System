package controller;

import adapter.interfaces.BookingDataAdapter;
import adapter.interfaces.FlightDataAdapter;
import adapter.interfaces.UserDataAdapter;
import adapter.database.BookingDatabaseAdapter;
import adapter.database.FlightDatabaseAdapter;
import adapter.database.UserDatabaseAdapter;
import model.entity.Booking;
import model.entity.Customer;
import model.entity.Flight;
import model.entity.Passenger;
import model.enums.BookingStatus;
import service.NotificationManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Manages booking operations including creation, modification, cancellation,
 * and passenger management.
 * Handles multi-passenger bookings, seat reservations, and sends notifications
 * for booking events.
 */
public class BookingController {
    private BookingDataAdapter bookingDataAdapter;
    private FlightDataAdapter flightDataAdapter;
    private UserDataAdapter userDataAdapter;
    private FlightController flightController;
    private NotificationManager notificationManager;

    public BookingController() {
        this.bookingDataAdapter = new BookingDatabaseAdapter();
        this.flightDataAdapter = new FlightDatabaseAdapter();
        this.userDataAdapter = new UserDatabaseAdapter();
        this.flightController = new FlightController();
        this.notificationManager = NotificationManager.getInstance();
    }

    public Booking createBooking(int customerId, int flightId, List<Passenger> passengers) {
        try {
            // Verify flight exists and has available seats
            Flight flight = flightDataAdapter.findById(flightId);
            if (flight == null) {
                System.out.println("✗ Flight not found.");
                return null;
            }

            int numPassengers = passengers.size();
            if (!flight.hasAvailableSeats(numPassengers)) {
                System.out.println("✗ Not enough available seats on this flight.");
                return null;
            }

            // Calculate total amount
            double totalAmount = flight.getPrice() * numPassengers;

            // Create booking
            Booking booking = new Booking(customerId, flightId, totalAmount, numPassengers);
            booking.setStatus(BookingStatus.PENDING);
            booking = bookingDataAdapter.create(booking);

            // Add passengers
            for (Passenger passenger : passengers) {
                passenger.setBookingId(booking.getBookingId());
                bookingDataAdapter.addPassenger(passenger);
            }

            // Reserve seats on the flight
            if (!flightController.reserveSeats(flightId, numPassengers)) {
                System.out.println("✗ Failed to reserve seats.");
                return null;
            }

            System.out.println("✓ Booking created successfully! Booking ID: " + booking.getBookingReference());
            return booking;
        } catch (SQLException e) {
            System.err.println("✗ Error creating booking: " + e.getMessage());
            return null;
        }
    }

    public boolean confirmBooking(int bookingId) {
        try {
            Booking booking = bookingDataAdapter.findById(bookingId);
            if (booking == null) {
                System.out.println("✗ Booking not found.");
                return false;
            }

            booking.setStatus(BookingStatus.CONFIRMED);
            boolean success = bookingDataAdapter.update(booking);

            if (success) {
                System.out.println("✓ Booking confirmed!");

                // Send notifications to customer
                Customer customer = (Customer) userDataAdapter.findById(booking.getCustomerId());
                if (customer != null) {
                    Flight flight = flightDataAdapter.findById(booking.getFlightId());
                    String message = String.format(
                            "Your booking %s has been confirmed! Flight %s from %s to %s on %s.",
                            booking.getBookingReference(),
                            flight.getFlightNumber(),
                            flight.getOrigin(),
                            flight.getDestination(),
                            flight.getFormattedDepartureTime());
                    notificationManager.notifyObservers(message, customer);
                }
            }
            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error confirming booking: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelBooking(int bookingId) {
        try {
            Booking booking = bookingDataAdapter.findById(bookingId);
            if (booking == null) {
                System.out.println("✗ Booking not found.");
                return false;
            }

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                System.out.println("✗ Booking is already cancelled.");
                return false;
            }

            // Release seats back to the flight
            flightController.releaseSeats(booking.getFlightId(), booking.getNumPassengers());

            // Cancel the booking
            boolean success = bookingDataAdapter.cancel(bookingId);

            if (success) {
                System.out.println("✓ Booking cancelled successfully!");

                // Send cancellation notifications to customer
                Customer customer = (Customer) userDataAdapter.findById(booking.getCustomerId());
                if (customer != null) {
                    Flight flight = flightDataAdapter.findById(booking.getFlightId());
                    String message = String.format(
                            "Your booking %s has been cancelled. Flight %s from %s to %s. Refund will be processed within 5-7 business days.",
                            booking.getBookingReference(),
                            flight.getFlightNumber(),
                            flight.getOrigin(),
                            flight.getDestination());
                    notificationManager.notifyObservers(message, customer);
                }
            }
            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error cancelling booking: " + e.getMessage());
            return false;
        }
    }

    public List<Booking> getCustomerBookings(int customerId) {
        try {
            List<Booking> bookings = bookingDataAdapter.findByCustomerId(customerId);

            // Populate flight details for each booking
            for (Booking booking : bookings) {
                Flight flight = flightDataAdapter.findById(booking.getFlightId());
                booking.setFlight(flight);
            }

            return bookings;
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving bookings: " + e.getMessage());
            return List.of();
        }
    }

    public List<Booking> getFlightBookings(int flightId) {
        try {
            return bookingDataAdapter.findByFlightId(flightId);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving flight bookings: " + e.getMessage());
            return List.of();
        }
    }

    public Booking getBookingById(int bookingId) {
        try {
            Booking booking = bookingDataAdapter.findById(bookingId);
            if (booking != null) {
                Flight flight = flightDataAdapter.findById(booking.getFlightId());
                booking.setFlight(flight);
            }
            return booking;
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving booking: " + e.getMessage());
            return null;
        }
    }

    public List<Booking> getAllBookings() {
        try {
            List<Booking> bookings = bookingDataAdapter.findAll();

            // Populate flight details
            for (Booking booking : bookings) {
                Flight flight = flightDataAdapter.findById(booking.getFlightId());
                booking.setFlight(flight);
            }

            return bookings;
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving all bookings: " + e.getMessage());
            return List.of();
        }
    }

    public String generateBookingConfirmation(int bookingId) {
        try {
            Booking booking = bookingDataAdapter.findById(bookingId);
            if (booking == null) {
                return "Booking not found.";
            }

            Flight flight = flightDataAdapter.findById(booking.getFlightId());
            List<Passenger> passengers = bookingDataAdapter.getPassengersByBookingId(bookingId);

            StringBuilder confirmation = new StringBuilder();
            confirmation.append("\n                ╔══════════════════════════════════════════════════════════════╗\n");
            confirmation.append("                ║           BOOKING CONFIRMATION                               ║\n");
            confirmation.append("                ╠══════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("                ║ Booking Reference: %-42s║\n",
                    booking.getBookingReference()));
            confirmation.append(String.format("                ║ Status: %-52s ║\n",
                    booking.getStatus()));
            confirmation.append("                ╠══════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("                ║ Flight: %-52s ║\n",
                    flight.getFlightNumber()));
            confirmation.append(String.format("                ║ Airline: %-51s ║\n",
                    flight.getAirline()));
            String routeInfo = String.format("%s (%s) → %s (%s)",
                    flight.getOrigin(), flight.getOrigin().substring(0, Math.min(3, flight.getOrigin().length())),
                    flight.getDestination(),
                    flight.getDestination().substring(0, Math.min(3, flight.getDestination().length())));
            confirmation.append(String.format("                ║ Route: %-53s ║\n",
                    routeInfo));
            confirmation.append(String.format("                ║ Departure: %-49s ║\n",
                    flight.getFormattedDepartureTime()));
            confirmation.append(String.format("                ║ Arrival: %-51s ║\n",
                    flight.getFormattedArrivalTime()));
            confirmation.append("                ╠══════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("                ║ Passengers: %-48d ║\n",
                    passengers.size()));

            for (int i = 0; i < passengers.size(); i++) {
                Passenger p = passengers.get(i);
                confirmation.append(String.format("                ║   %d. %-56s║\n",
                        (i + 1), p.getFullName()));
            }

            confirmation.append("                ╠══════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("                ║ Total Amount: $%-46.2f║\n",
                    booking.getTotalAmount()));
            confirmation.append("                ╚══════════════════════════════════════════════════════════════╝\n");

            return confirmation.toString();
        } catch (SQLException e) {
            return "Error generating confirmation: " + e.getMessage();
        }
    }

    /**
     * Modify a booking by changing the flight.
     * Handles seat release/reservation and price adjustments.
     */
    public boolean modifyBookingFlight(int bookingId, int newFlightId) {
        try {
            Booking booking = bookingDataAdapter.findById(bookingId);
            if (booking == null) {
                System.out.println("✗ Booking not found.");
                return false;
            }

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                System.out.println("✗ Cannot modify a cancelled booking.");
                return false;
            }

            Flight oldFlight = flightDataAdapter.findById(booking.getFlightId());
            Flight newFlight = flightDataAdapter.findById(newFlightId);

            if (newFlight == null) {
                System.out.println("✗ New flight not found.");
                return false;
            }

            // Check if new flight has enough seats
            if (!newFlight.hasAvailableSeats(booking.getNumPassengers())) {
                System.out.println("✗ Not enough available seats on the new flight.");
                return false;
            }

            // Release seats from old flight
            flightController.releaseSeats(booking.getFlightId(), booking.getNumPassengers());

            // Reserve seats on new flight
            if (!flightController.reserveSeats(newFlightId, booking.getNumPassengers())) {
                // Rollback: re-reserve seats on old flight
                flightController.reserveSeats(booking.getFlightId(), booking.getNumPassengers());
                System.out.println("✗ Failed to reserve seats on new flight.");
                return false;
            }

            // Calculate price difference
            double oldPrice = booking.getTotalAmount();
            double newPrice = newFlight.getPrice() * booking.getNumPassengers();
            double priceDifference = newPrice - oldPrice;

            // Update booking
            booking.setFlightId(newFlightId);
            booking.setTotalAmount(newPrice);
            boolean success = bookingDataAdapter.update(booking);

            if (success) {
                System.out.println("✓ Flight changed successfully!");
                System.out.println("   Old Flight: " + oldFlight.getFlightNumber() + " (" + oldFlight.getOrigin()
                        + " → " + oldFlight.getDestination() + ")");
                System.out.println("   New Flight: " + newFlight.getFlightNumber() + " (" + newFlight.getOrigin()
                        + " → " + newFlight.getDestination() + ")");

                if (priceDifference > 0) {
                    System.out.println("   Additional charge: $" + String.format("%.2f", priceDifference));
                } else if (priceDifference < 0) {
                    System.out.println("   Refund amount: $" + String.format("%.2f", Math.abs(priceDifference)));
                } else {
                    System.out.println("   No price difference.");
                }

                // Send notification to customer
                Customer customer = (Customer) userDataAdapter.findById(booking.getCustomerId());
                if (customer != null) {
                    String message = String.format(
                            "Your booking %s has been modified. New flight: %s from %s to %s on %s.",
                            booking.getBookingReference(),
                            newFlight.getFlightNumber(),
                            newFlight.getOrigin(),
                            newFlight.getDestination(),
                            newFlight.getFormattedDepartureTime());
                    notificationManager.notifyObservers(message, customer);
                }
            }

            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error modifying booking: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update passenger details for a booking.
     */
    public boolean updatePassengerDetails(int passengerId, String firstName, String lastName, String passportNumber) {
        try {
            // Create passenger object with updated details
            Passenger passenger = new Passenger();
            passenger.setPassengerId(passengerId);
            passenger.setFirstName(firstName);
            passenger.setLastName(lastName);
            passenger.setPassportNumber(passportNumber);

            boolean success = bookingDataAdapter.updatePassenger(passenger);

            if (success) {
                System.out.println("✓ Passenger details updated successfully!");
                System.out.println("   Name: " + firstName + " " + lastName);
                System.out.println("   Passport: " + passportNumber);
            }

            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error updating passenger: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get passengers for a specific booking.
     */
    public List<Passenger> getPassengersByBookingId(int bookingId) {
        try {
            return bookingDataAdapter.getPassengersByBookingId(bookingId);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving passengers: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Remove a passenger from a booking.
     */
    public boolean removePassenger(int bookingId, int passengerId) {
        try {
            Booking booking = bookingDataAdapter.findById(bookingId);
            if (booking == null) {
                System.out.println("✗ Booking not found.");
                return false;
            }

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                System.out.println("✗ Cannot remove passenger from a cancelled booking.");
                return false;
            }

            if (booking.getNumPassengers() <= 1) {
                System.out.println("✗ Cannot remove the last passenger. Cancel the booking instead.");
                return false;
            }

            // Remove passenger
            boolean removed = bookingDataAdapter.removePassenger(passengerId);
            if (!removed) {
                System.out.println("✗ Failed to remove passenger record.");
                return false;
            }

            // Update booking details
            Flight flight = flightDataAdapter.findById(booking.getFlightId());
            int newNumPassengers = booking.getNumPassengers() - 1;
            double newTotalAmount = flight.getPrice() * newNumPassengers;

            booking.setNumPassengers(newNumPassengers);
            booking.setTotalAmount(newTotalAmount);
            bookingDataAdapter.update(booking);

            // Release seat
            flightController.releaseSeats(booking.getFlightId(), 1);

            System.out.println("✓ Passenger removed successfully.");
            return true;

        } catch (SQLException e) {
            System.err.println("✗ Error removing passenger: " + e.getMessage());
            return false;
        }
    }
}
