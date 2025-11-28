package com.flightreservation.controller;

import com.flightreservation.dao.BookingDAO;
import com.flightreservation.dao.FlightDAO;
import com.flightreservation.dao.UserDAO;
import com.flightreservation.dao.impl.BookingDAOImpl;
import com.flightreservation.dao.impl.FlightDAOImpl;
import com.flightreservation.dao.impl.UserDAOImpl;
import com.flightreservation.model.entity.Booking;
import com.flightreservation.model.entity.Customer;
import com.flightreservation.model.entity.Flight;
import com.flightreservation.model.entity.Passenger;
import com.flightreservation.model.enums.BookingStatus;
import com.flightreservation.service.NotificationManager;

import java.sql.SQLException;
import java.util.List;

public class BookingController {
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    private UserDAO userDAO;
    private FlightController flightController;
    private NotificationManager notificationManager;

    public BookingController() {
        this.bookingDAO = new BookingDAOImpl();
        this.flightDAO = new FlightDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.flightController = new FlightController();
        this.notificationManager = NotificationManager.getInstance();
    }

    public Booking createBooking(int customerId, int flightId, List<Passenger> passengers) {
        try {
            // Verify flight exists and has available seats
            Flight flight = flightDAO.findById(flightId);
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
            booking = bookingDAO.create(booking);

            // Add passengers
            for (Passenger passenger : passengers) {
                passenger.setBookingId(booking.getBookingId());
                bookingDAO.addPassenger(passenger);
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
            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null) {
                System.out.println("✗ Booking not found.");
                return false;
            }

            booking.setStatus(BookingStatus.CONFIRMED);
            boolean success = bookingDAO.update(booking);

            if (success) {
                System.out.println("✓ Booking confirmed!");

                // Send notifications to customer
                Customer customer = (Customer) userDAO.findById(booking.getCustomerId());
                if (customer != null) {
                    Flight flight = flightDAO.findById(booking.getFlightId());
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
            Booking booking = bookingDAO.findById(bookingId);
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
            boolean success = bookingDAO.cancel(bookingId);

            if (success) {
                System.out.println("✓ Booking cancelled successfully!");

                // Send cancellation notifications to customer
                Customer customer = (Customer) userDAO.findById(booking.getCustomerId());
                if (customer != null) {
                    Flight flight = flightDAO.findById(booking.getFlightId());
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
            List<Booking> bookings = bookingDAO.findByCustomerId(customerId);

            // Populate flight details for each booking
            for (Booking booking : bookings) {
                Flight flight = flightDAO.findById(booking.getFlightId());
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
            return bookingDAO.findByFlightId(flightId);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving flight bookings: " + e.getMessage());
            return List.of();
        }
    }

    public Booking getBookingById(int bookingId) {
        try {
            Booking booking = bookingDAO.findById(bookingId);
            if (booking != null) {
                Flight flight = flightDAO.findById(booking.getFlightId());
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
            List<Booking> bookings = bookingDAO.findAll();

            // Populate flight details
            for (Booking booking : bookings) {
                Flight flight = flightDAO.findById(booking.getFlightId());
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
            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null) {
                return "Booking not found.";
            }

            Flight flight = flightDAO.findById(booking.getFlightId());
            List<Passenger> passengers = bookingDAO.getPassengersByBookingId(bookingId);

            StringBuilder confirmation = new StringBuilder();
            confirmation.append("\n╔════════════════════════════════════════════════════════════╗\n");
            confirmation.append("║           BOOKING CONFIRMATION                             ║\n");
            confirmation.append("╠════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("║ Booking Reference: %-39s ║\n", booking.getBookingReference()));
            confirmation.append(String.format("║ Status: %-50s ║\n", booking.getStatus()));
            confirmation.append("╠════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("║ Flight: %-50s ║\n", flight.getFlightNumber()));
            confirmation.append(String.format("║ Airline: %-49s ║\n", flight.getAirline()));
            confirmation
                    .append(String.format("║ Route: %-51s ║\n", flight.getOrigin() + " → " + flight.getDestination()));
            confirmation.append(String.format("║ Departure: %-47s ║\n", flight.getFormattedDepartureTime()));
            confirmation.append(String.format("║ Arrival: %-49s ║\n", flight.getFormattedArrivalTime()));
            confirmation.append("╠════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("║ Passengers: %-46d ║\n", passengers.size()));

            for (int i = 0; i < passengers.size(); i++) {
                Passenger p = passengers.get(i);
                confirmation.append(String.format("║   %d. %-54s ║\n", (i + 1), p.getFullName()));
            }

            confirmation.append("╠════════════════════════════════════════════════════════════╣\n");
            confirmation.append(String.format("║ Total Amount: $%-44.2f ║\n", booking.getTotalAmount()));
            confirmation.append("╚════════════════════════════════════════════════════════════╝\n");

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
            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null) {
                System.out.println("✗ Booking not found.");
                return false;
            }

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                System.out.println("✗ Cannot modify a cancelled booking.");
                return false;
            }

            Flight oldFlight = flightDAO.findById(booking.getFlightId());
            Flight newFlight = flightDAO.findById(newFlightId);

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
            boolean success = bookingDAO.update(booking);

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
                Customer customer = (Customer) userDAO.findById(booking.getCustomerId());
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

            boolean success = bookingDAO.updatePassenger(passenger);

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
            return bookingDAO.getPassengersByBookingId(bookingId);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving passengers: " + e.getMessage());
            return List.of();
        }
    }
}
