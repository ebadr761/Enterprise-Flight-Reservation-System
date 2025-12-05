package adapter.interfaces;

import model.entity.Flight;
import model.enums.FlightStatus;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Adapter interface for flight data access operations.
 * Adapts application flight management needs to the database interface.
 */
public interface FlightDataAdapter {
    Flight create(Flight flight) throws SQLException;

    Flight findById(int flightId) throws SQLException;

    Flight findByFlightNumber(String flightNumber) throws SQLException;

    List<Flight> findAll() throws SQLException;

    List<Flight> search(String origin, String destination, LocalDate date, String airline) throws SQLException;

    boolean update(Flight flight) throws SQLException;

    boolean delete(int flightId) throws SQLException;

    boolean updateAvailableSeats(int flightId, int seats) throws SQLException;

    boolean updateStatus(int flightId, FlightStatus status) throws SQLException;
}
