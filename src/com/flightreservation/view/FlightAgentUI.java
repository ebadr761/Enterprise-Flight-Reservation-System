package com.flightreservation.view;

import com.flightreservation.controller.*;
import com.flightreservation.model.entity.*;

import java.util.List;

public class FlightAgentUI extends ConsoleUI {
    private AuthenticationController authController;
    private FlightController flightController;
    private BookingController bookingController;
    private FlightAgent agent;

    public FlightAgentUI(AuthenticationController authController) {
        super();
        this.authController = authController;
        this.flightController = new FlightController();
        this.bookingController = new BookingController();
        this.agent = (FlightAgent) authController.getCurrentUser();
    }

    @Override
    public void display() {
        while (true) {
            clearScreen();
            printHeader("FLIGHT AGENT DASHBOARD - " + agent.getFullName());
            printMenu();

            int choice = readInt("\nEnter your choice: ");

            switch (choice) {
                case 1:
                    viewAllBookings();
                    break;
                case 2:
                    viewFlightSchedules();
                    break;
                case 3:
                    modifyBooking();
                    break;
                case 4:
                    cancelCustomerBooking();
                    break;
                case 5:
                    viewFlightBookings();
                    break;
                case 6:
                    authController.logout();
                    return;
                default:
                    showError("Invalid choice. Please try again.");
                    pause();
            }
        }
    }

    private void printMenu() {
        System.out.println("\n┌───────────────────────────────────────────────────────────────┐");
        System.out.println("│  1. View All Bookings                                         │");
        System.out.println("│  2. View Flight Schedules                                     │");
        System.out.println("│  3. Modify Booking                                            │");
        System.out.println("│  4. Cancel Customer Booking                                   │");
        System.out.println("│  5. View Bookings for Specific Flight                         │");
        System.out.println("│  6. Logout                                                    │");
        System.out.println("└───────────────────────────────────────────────────────────────┘");
    }

    private void viewAllBookings() {
        printHeader("ALL BOOKINGS");

        List<Booking> bookings = bookingController.getAllBookings();

        if (bookings.isEmpty()) {
            showMessage("No bookings found.");
            pause();
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("\n" + booking.toString());
            if (booking.getFlight() != null) {
                System.out.println("   Flight: " + booking.getFlight().toString());
            }
            System.out.println("   Passengers: " + booking.getNumPassengers());
        }

        pause();
    }

    private void viewFlightSchedules() {
        printHeader("FLIGHT SCHEDULES");

        List<Flight> flights = flightController.getAllFlights();

        if (flights.isEmpty()) {
            showMessage("No flights scheduled.");
            pause();
            return;
        }

        for (Flight flight : flights) {
            System.out.println("\n" + flight.toString());
            System.out.println("   Status: " + flight.getStatus());
            System.out.println("   Aircraft: " + flight.getAircraftType());
        }

        pause();
    }

    private void modifyBooking() {
        printHeader("MODIFY BOOKING");

        int bookingId = readInt("Enter Booking ID: ");
        Booking booking = bookingController.getBookingById(bookingId);

        if (booking == null) {
            showError("Booking not found.");
            pause();
            return;
        }

        System.out.println("\nCurrent Booking Details:");
        System.out.println(booking.toString());
        if (booking.getFlight() != null) {
            System.out.println("Flight: " + booking.getFlight().toString());
        }

        // Display passengers
        List<Passenger> passengers = bookingController.getPassengersByBookingId(bookingId);
        if (!passengers.isEmpty()) {
            System.out.println("\nPassengers:");
            for (int i = 0; i < passengers.size(); i++) {
                Passenger p = passengers.get(i);
                System.out.println("   " + (i + 1) + ". " + p.getFullName() + " (Passport: " + p.getPassportNumber()
                        + ", Seat: " + (p.getSeatNumber() != null ? p.getSeatNumber() : "Not assigned") + ")");
            }
        }

        // Modification menu
        System.out.println("\n┌───────────────────────────────────────────────────────────────┐");
        System.out.println("│  Modification Options:                                        │");
        System.out.println("│  1. Change Flight                                             │");
        System.out.println("│  2. Update Passenger Details                                  │");
        System.out.println("│  3. Cancel                                                    │");
        System.out.println("└───────────────────────────────────────────────────────────────┘");

        int choice = readInt("\nSelect option: ");

        switch (choice) {
            case 1:
                changeFlight(bookingId);
                break;
            case 2:
                updatePassenger(passengers);
                break;
            case 3:
                System.out.println("Modification cancelled.");
                break;
            default:
                showError("Invalid option.");
        }

        pause();
    }

    private void changeFlight(int bookingId) {
        printSubHeader("CHANGE FLIGHT");

        String newFlightNumber = readString("Enter new Flight Number: ");
        Flight newFlight = flightController.getFlightByNumber(newFlightNumber);

        if (newFlight == null) {
            showError("Flight not found.");
            return;
        }

        System.out.println("\nNew Flight Details:");
        System.out.println(newFlight.toString());

        if (readBoolean("\nConfirm flight change?")) {
            boolean success = bookingController.modifyBookingFlight(bookingId, newFlight.getFlightId());
            if (success) {
                showMessage("Flight changed successfully!");
            }
        } else {
            System.out.println("Flight change cancelled.");
        }
    }

    private void updatePassenger(List<Passenger> passengers) {
        printSubHeader("UPDATE PASSENGER DETAILS");

        if (passengers.isEmpty()) {
            showError("No passengers found.");
            return;
        }

        System.out.println("\nSelect passenger to update:");
        for (int i = 0; i < passengers.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + passengers.get(i).getFullName());
        }

        int passengerIndex = readInt("\nEnter passenger number: ") - 1;

        if (passengerIndex < 0 || passengerIndex >= passengers.size()) {
            showError("Invalid passenger selection.");
            return;
        }

        Passenger passenger = passengers.get(passengerIndex);

        System.out.println("\nCurrent Details:");
        System.out.println("   Name: " + passenger.getFullName());
        System.out.println("   Passport: " + passenger.getPassportNumber());

        System.out.println("\nEnter new details (press Enter to keep current):");
        String firstName = readString("First Name [" + passenger.getFirstName() + "]: ");
        if (firstName.trim().isEmpty()) {
            firstName = passenger.getFirstName();
        }

        String lastName = readString("Last Name [" + passenger.getLastName() + "]: ");
        if (lastName.trim().isEmpty()) {
            lastName = passenger.getLastName();
        }

        String passport = readString("Passport Number [" + passenger.getPassportNumber() + "]: ");
        if (passport.trim().isEmpty()) {
            passport = passenger.getPassportNumber();
        }

        if (readBoolean("\nConfirm update?")) {
            boolean success = bookingController.updatePassengerDetails(
                    passenger.getPassengerId(), firstName, lastName, passport);
            if (success) {
                showMessage("Passenger details updated successfully!");
            }
        } else {
            System.out.println("Update cancelled.");
        }
    }

    private void cancelCustomerBooking() {
        printHeader("CANCEL CUSTOMER BOOKING");

        int bookingId = readInt("Enter Booking ID to cancel: ");

        if (readBoolean("Are you sure you want to cancel this booking?")) {
            bookingController.cancelBooking(bookingId);
        }

        pause();
    }

    private void viewFlightBookings() {
        printHeader("VIEW FLIGHT BOOKINGS");

        String flightNumber = readString("Enter Flight Number: ");
        Flight flight = flightController.getFlightByNumber(flightNumber);

        if (flight == null) {
            showError("Flight not found.");
            pause();
            return;
        }

        List<Booking> bookings = bookingController.getFlightBookings(flight.getFlightId());

        if (bookings.isEmpty()) {
            showMessage("No bookings for this flight.");
            pause();
            return;
        }

        System.out.println("\nBookings for Flight " + flightNumber + ":");
        for (Booking booking : bookings) {
            System.out.println("\n" + booking.toString());
            System.out.println("   Passengers: " + booking.getNumPassengers());
        }

        pause();
    }
}
