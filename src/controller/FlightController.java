package controller;

import adapter.interfaces.FlightDataAdapter;
import adapter.database.FlightDatabaseAdapter;
import model.entity.Flight;
import model.enums.FlightStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Manages flight operations including creation, search, updates, and seat
 * reservations.
 * Handles flight status management and automatically marks flights as completed
 * when fully booked.
 */
public class FlightController {
    private FlightDataAdapter flightDataAdapter;

    public FlightController() {
        this.flightDataAdapter = new FlightDatabaseAdapter();
    }

    public Flight createFlight(String flightNumber, String airline, String origin, String destination,
                               LocalDateTime departureTime, LocalDateTime arrivalTime, double price,
                               int totalSeats, String aircraftType) {
        try {
            Flight flight = new Flight(flightNumber, airline, origin, destination, departureTime,
                    arrivalTime, price, totalSeats, aircraftType);
            flight = flightDataAdapter.create(flight);
            System.out.println("✓ Flight " + flightNumber + " created successfully!");
            return flight;
        } catch (SQLException e) {
            System.err.println("✗ Error creating flight: " + e.getMessage());
            return null;
        }
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDate date, String airline) {
        try {
            return flightDataAdapter.search(origin, destination, date, airline);
        } catch (SQLException e) {
            System.err.println("✗ Error searching flights: " + e.getMessage());
            return List.of();
        }
    }

    public List<Flight> getAllFlights() {
        try {
            return flightDataAdapter.findAll();
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving flights: " + e.getMessage());
            return List.of();
        }
    }

    public Flight getFlightById(int flightId) {
        try {
            return flightDataAdapter.findById(flightId);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving flight: " + e.getMessage());
            return null;
        }
    }

    public Flight getFlightByNumber(String flightNumber) {
        try {
            return flightDataAdapter.findByFlightNumber(flightNumber);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving flight: " + e.getMessage());
            return null;
        }
    }

    public boolean updateFlight(Flight flight) {
        try {
            boolean success = flightDataAdapter.update(flight);
            if (success) {
                System.out.println("✓ Flight updated successfully!");
            } else {
                System.out.println("✗ Failed to update flight.");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error updating flight: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFlight(int flightId) {
        try {
            boolean success = flightDataAdapter.delete(flightId);
            if (success) {
                System.out.println("✓ Flight deleted successfully!");
            } else {
                System.out.println("✗ Failed to delete flight.");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error deleting flight: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFlightStatus(int flightId, FlightStatus status) {
        try {
            boolean success = flightDataAdapter.updateStatus(flightId, status);
            if (success) {
                System.out.println("✓ Flight status updated to " + status);
            }
            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error updating flight status: " + e.getMessage());
            return false;
        }
    }

    public boolean reserveSeats(int flightId, int numSeats) {
        try {
            Flight flight = flightDataAdapter.findById(flightId);
            if (flight == null) {
                System.out.println("✗ Flight not found.");
                return false;
            }

            if (!flight.hasAvailableSeats(numSeats)) {
                System.out.println("✗ Not enough available seats.");
                return false;
            }

            flight.reserveSeats(numSeats);
            boolean updated = flightDataAdapter.updateAvailableSeats(flightId, flight.getAvailableSeats());

            // Auto-complete flight when all seats are booked
            if (updated && flight.getAvailableSeats() == 0) {
                updateFlightStatus(flightId, FlightStatus.COMPLETED);
                System.out.println("✓ Flight is now fully booked and marked as COMPLETED.");
            }

            return updated;
        } catch (SQLException e) {
            System.err.println("✗ Error reserving seats: " + e.getMessage());
            return false;
        }
    }

    public boolean releaseSeats(int flightId, int numSeats) {
        try {
            Flight flight = flightDataAdapter.findById(flightId);
            if (flight == null) {
                return false;
            }

            flight.releaseSeats(numSeats);
            return flightDataAdapter.updateAvailableSeats(flightId, flight.getAvailableSeats());
        } catch (SQLException e) {
            System.err.println("✗ Error releasing seats: " + e.getMessage());
            return false;
        }
    }
}
