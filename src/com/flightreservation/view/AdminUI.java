package com.flightreservation.view;

import com.flightreservation.controller.*;
import com.flightreservation.model.entity.*;
import com.flightreservation.model.enums.FlightStatus;
import com.flightreservation.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.List;

public class AdminUI extends ConsoleUI {
    private AuthenticationController authController;
    private FlightController flightController;
    private BookingController bookingController;
    private Admin admin;

    public AdminUI(AuthenticationController authController) {
        super();
        this.authController = authController;
        this.flightController = new FlightController();
        this.bookingController = new BookingController();
        this.admin = (Admin) authController.getCurrentUser();
    }

    @Override
    public void display() {
        while (true) {
            clearScreen();
            printHeader("ADMIN DASHBOARD - " + admin.getFullName());
            printMenu();

            int choice = readInt("\nEnter your choice: ");

            switch (choice) {
                case 1:
                    addNewFlight();
                    break;
                case 2:
                    updateFlight();
                    break;
                case 3:
                    deleteFlight();
                    break;
                case 4:
                    viewAllFlights();
                    break;
                case 5:
                    updateFlightStatus();
                    break;
                case 6:
                    viewAllBookings();
                    break;
                case 7:
                    viewSystemStatistics();
                    break;
                case 8:
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
        System.out.println("│  1. Add New Flight                                            │");
        System.out.println("│  2. Update Flight Details                                     │");
        System.out.println("│  3. Delete Flight                                             │");
        System.out.println("│  4. View All Flights                                          │");
        System.out.println("│  5. Update Flight Status                                      │");
        System.out.println("│  6. View All Bookings                                         │");
        System.out.println("│  7. View System Statistics                                    │");
        System.out.println("│  8. Logout                                                    │");
        System.out.println("└───────────────────────────────────────────────────────────────┘");
    }

    private void addNewFlight() {
        printHeader("ADD NEW FLIGHT");

        String flightNumber = readString("Flight Number: ");
        String airline = readString("Airline: ");
        String origin = readString("Origin: ");
        String destination = readString("Destination: ");

        String departureStr = readString("Departure Date & Time (YYYY-MM-DD HH:MM): ");
        LocalDateTime departureTime = DateTimeUtil.parseDateTime(departureStr);
        if (departureTime == null) {
            showError("Invalid date/time format.");
            pause();
            return;
        }

        String arrivalStr = readString("Arrival Date & Time (YYYY-MM-DD HH:MM): ");
        LocalDateTime arrivalTime = DateTimeUtil.parseDateTime(arrivalStr);
        if (arrivalTime == null) {
            showError("Invalid date/time format.");
            pause();
            return;
        }

        double price = readDouble("Price per seat: $");
        int totalSeats = readInt("Total seats: ");
        String aircraftType = readString("Aircraft Type: ");

        Flight flight = flightController.createFlight(flightNumber, airline, origin, destination,
                departureTime, arrivalTime, price, totalSeats, aircraftType);

        if (flight != null) {
            showSuccess("Flight added successfully!");
        }

        pause();
    }

    private void updateFlight() {
        printHeader("UPDATE FLIGHT");

        String flightNumber = readString("Enter Flight Number to update: ");
        Flight flight = flightController.getFlightByNumber(flightNumber);

        if (flight == null) {
            showError("Flight not found.");
            pause();
            return;
        }

        System.out.println("\nCurrent Flight Details:");
        System.out.println(flight.toString());

        System.out.println("\nEnter new details (press Enter to keep current value):");

        String newAirline = readString("Airline [" + flight.getAirline() + "]: ");
        if (!newAirline.isEmpty())
            flight.setAirline(newAirline);

        String newOrigin = readString("Origin [" + flight.getOrigin() + "]: ");
        if (!newOrigin.isEmpty())
            flight.setOrigin(newOrigin);

        String newDestination = readString("Destination [" + flight.getDestination() + "]: ");
        if (!newDestination.isEmpty())
            flight.setDestination(newDestination);

        String priceStr = readString("Price [" + flight.getPrice() + "]: ");
        if (!priceStr.isEmpty()) {
            try {
                flight.setPrice(Double.parseDouble(priceStr));
            } catch (NumberFormatException e) {
                showError("Invalid price format.");
            }
        }

        if (flightController.updateFlight(flight)) {
            showSuccess("Flight updated successfully!");
        }

        pause();
    }

    private void deleteFlight() {
        printHeader("DELETE FLIGHT");

        String flightNumber = readString("Enter Flight Number to delete: ");
        Flight flight = flightController.getFlightByNumber(flightNumber);

        if (flight == null) {
            showError("Flight not found.");
            pause();
            return;
        }

        System.out.println("\nFlight to delete:");
        System.out.println(flight.toString());

        if (readBoolean("Are you sure you want to delete this flight?")) {
            if (flightController.deleteFlight(flight.getFlightId())) {
                showSuccess("Flight deleted successfully!");
            }
        }

        pause();
    }

    private void viewAllFlights() {
        printHeader("ALL FLIGHTS");

        List<Flight> flights = flightController.getAllFlights();

        if (flights.isEmpty()) {
            showMessage("No flights in the system.");
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

    private void updateFlightStatus() {
        printHeader("UPDATE FLIGHT STATUS");

        String flightNumber = readString("Enter Flight Number: ");
        Flight flight = flightController.getFlightByNumber(flightNumber);

        if (flight == null) {
            showError("Flight not found.");
            pause();
            return;
        }

        System.out.println("\nCurrent Status: " + flight.getStatus());
        System.out.println("\nAvailable Statuses:");
        System.out.println("1. SCHEDULED");
        System.out.println("2. DELAYED");
        System.out.println("3. CANCELLED");
        System.out.println("4. COMPLETED");

        int choice = readInt("\nSelect new status (1-4): ");

        FlightStatus newStatus;
        switch (choice) {
            case 1:
                newStatus = FlightStatus.SCHEDULED;
                break;
            case 2:
                newStatus = FlightStatus.DELAYED;
                break;
            case 3:
                newStatus = FlightStatus.CANCELLED;
                break;
            case 4:
                newStatus = FlightStatus.COMPLETED;
                break;
            default:
                showError("Invalid choice.");
                pause();
                return;
        }

        if (flightController.updateFlightStatus(flight.getFlightId(), newStatus)) {
            showSuccess("Flight status updated successfully!");
        }

        pause();
    }

    private void viewAllBookings() {
        printHeader("ALL BOOKINGS");

        List<Booking> bookings = bookingController.getAllBookings();

        if (bookings.isEmpty()) {
            showMessage("No bookings in the system.");
            pause();
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("\n" + booking.toString());
            if (booking.getFlight() != null) {
                System.out.println("   Flight: " + booking.getFlight().getFlightNumber());
            }
        }

        pause();
    }

    private void viewSystemStatistics() {
        printHeader("SYSTEM STATISTICS");

        List<Flight> flights = flightController.getAllFlights();
        List<Booking> bookings = bookingController.getAllBookings();

        int totalFlights = flights.size();
        int totalBookings = bookings.size();
        int confirmedBookings = (int) bookings.stream()
                .filter(b -> b.getStatus() == com.flightreservation.model.enums.BookingStatus.CONFIRMED)
                .count();

        double totalRevenue = bookings.stream()
                .filter(b -> b.getStatus() == com.flightreservation.model.enums.BookingStatus.CONFIRMED)
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        System.out.println("\n📊 System Overview:");
        System.out.println("   Total Flights: " + totalFlights);
        System.out.println("   Total Bookings: " + totalBookings);
        System.out.println("   Confirmed Bookings: " + confirmedBookings);
        System.out.println("   Total Revenue: $" + String.format("%.2f", totalRevenue));

        pause();
    }
}
