package com.flightreservation.view;

import com.flightreservation.controller.*;
import com.flightreservation.model.entity.*;
import com.flightreservation.model.enums.BookingStatus;
import com.flightreservation.util.DateTimeUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerUI extends ConsoleUI {
    private AuthenticationController authController;
    private FlightController flightController;
    private BookingController bookingController;
    private PaymentController paymentController;
    private Customer customer;

    public CustomerUI(AuthenticationController authController) {
        super();
        this.authController = authController;
        this.flightController = new FlightController();
        this.bookingController = new BookingController();
        this.paymentController = new PaymentController();
        this.customer = (Customer) authController.getCurrentUser();
    }

    @Override
    public void display() {
        while (true) {
            clearScreen();
            printHeader("CUSTOMER DASHBOARD - Welcome, " + customer.getFullName());
            printMenu();

            int choice = readInt("\nEnter your choice: ");

            switch (choice) {
                case 1:
                    searchAndBookFlights();
                    break;
                case 2:
                    viewMyBookings();
                    break;
                case 3:
                    cancelBooking();
                    break;
                case 4:
                    viewProfile();
                    break;
                case 5:
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
        System.out.println("│  1. Search & Book Flights                                     │");
        System.out.println("│  2. View My Bookings                                          │");
        System.out.println("│  3. Cancel Booking                                            │");
        System.out.println("│  4. View Profile                                              │");
        System.out.println("│  5. Logout                                                    │");
        System.out.println("└───────────────────────────────────────────────────────────────┘");
    }

    private void searchAndBookFlights() {
        printHeader("FLIGHT SEARCH");

        String origin = readString("Origin (or press Enter to skip): ");
        String destination = readString("Destination (or press Enter to skip): ");
        String dateStr = readString("Date (YYYY-MM-DD, or press Enter to skip): ");
        String airline = readString("Airline (or press Enter to skip): ");

        LocalDate date = null;
        if (!dateStr.isEmpty()) {
            date = DateTimeUtil.parseDate(dateStr);
            if (date == null) {
                showError("Invalid date format.");
                pause();
                return;
            }
        }

        List<Flight> flights = flightController.searchFlights(
                origin.isEmpty() ? null : origin,
                destination.isEmpty() ? null : destination,
                date,
                airline.isEmpty() ? null : airline);

        if (flights.isEmpty()) {
            showMessage("No flights found matching your criteria.");
            pause();
            return;
        }

        printSubHeader("Available Flights");
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            System.out.printf("\n%d. %s\n", (i + 1), f.toString());
        }

        if (readBoolean("\nWould you like to book a flight?")) {
            int flightChoice = readInt("Enter flight number (1-" + flights.size() + "): ");
            if (flightChoice < 1 || flightChoice > flights.size()) {
                showError("Invalid flight selection.");
                pause();
                return;
            }

            Flight selectedFlight = flights.get(flightChoice - 1);
            bookFlight(selectedFlight);
        }
    }

    private void bookFlight(Flight flight) {
        printSubHeader("BOOKING FLIGHT: " + flight.getFlightNumber());

        int numPassengers = readInt("Number of passengers (1-10): ");
        if (numPassengers < 1 || numPassengers > 10) {
            showError("Invalid number of passengers.");
            pause();
            return;
        }

        if (!flight.hasAvailableSeats(numPassengers)) {
            showError("Not enough available seats on this flight.");
            pause();
            return;
        }

        List<Passenger> passengers = new ArrayList<>();
        for (int i = 1; i <= numPassengers; i++) {
            System.out.println("\n--- Passenger " + i + " ---");
            String firstName = readString("First Name: ");
            String lastName = readString("Last Name: ");
            String passport = readString("Passport Number: ");

            passengers.add(new Passenger(firstName, lastName, passport));
        }

        // Create booking
        Booking booking = bookingController.createBooking(customer.getUserId(), flight.getFlightId(), passengers);

        if (booking == null) {
            showError("Failed to create booking.");
            pause();
            return;
        }

        // Process payment
        printSubHeader("PAYMENT");
        System.out.printf("Total Amount: $%.2f\n", booking.getTotalAmount());

        String paymentMethod = readString("Payment Method (Credit Card/Debit Card/PayPal): ");

        Payment payment = paymentController.processPayment(booking.getBookingId(), booking.getTotalAmount(),
                paymentMethod);

        if (payment != null) {
            bookingController.confirmBooking(booking.getBookingId());
            showSuccess("Booking completed successfully!");
            System.out.println(bookingController.generateBookingConfirmation(booking.getBookingId()));
        }

        pause();
    }

    private void viewMyBookings() {
        printHeader("MY BOOKINGS");

        List<Booking> bookings = bookingController.getCustomerBookings(customer.getUserId());

        if (bookings.isEmpty()) {
            showMessage("You have no bookings.");
            pause();
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("\n" + booking.toString());
            if (booking.getFlight() != null) {
                System.out.println("   Flight: " + booking.getFlight().toString());
            }
            System.out.println("   Passengers: " + booking.getPassengers().size());
        }

        pause();
    }

    private void cancelBooking() {
        printHeader("CANCEL BOOKING");

        List<Booking> bookings = bookingController.getCustomerBookings(customer.getUserId());

        if (bookings.isEmpty()) {
            showMessage("You have no bookings to cancel.");
            pause();
            return;
        }

        // Show only confirmed bookings
        List<Booking> cancellableBookings = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING) {
                cancellableBookings.add(b);
            }
        }

        if (cancellableBookings.isEmpty()) {
            showMessage("You have no active bookings to cancel.");
            pause();
            return;
        }

        System.out.println("\nYour Active Bookings:");
        for (int i = 0; i < cancellableBookings.size(); i++) {
            Booking b = cancellableBookings.get(i);
            System.out.printf("\n%d. %s\n", (i + 1), b.toString());
            if (b.getFlight() != null) {
                System.out.println("   Flight: " + b.getFlight().getFlightNumber() + " - " +
                        b.getFlight().getOrigin() + " to " + b.getFlight().getDestination());
            }
        }

        int choice = readInt("\nEnter booking number to cancel (0 to go back): ");
        if (choice == 0)
            return;

        if (choice < 1 || choice > cancellableBookings.size()) {
            showError("Invalid selection.");
            pause();
            return;
        }

        Booking selectedBooking = cancellableBookings.get(choice - 1);

        if (readBoolean("Are you sure you want to cancel this booking?")) {
            if (bookingController.cancelBooking(selectedBooking.getBookingId())) {
                // Process refund
                Payment payment = paymentController.getPaymentByBookingId(selectedBooking.getBookingId());
                if (payment != null) {
                    paymentController.refundPayment(payment.getPaymentId());
                }
            }
        }

        pause();
    }

    private void viewProfile() {
        printHeader("MY PROFILE");

        System.out.println("\nName: " + customer.getFullName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Loyalty Points: " + customer.getLoyaltyPoints());
        System.out.println("Member Since: " + customer.getRegistrationDate());

        pause();
    }
}
