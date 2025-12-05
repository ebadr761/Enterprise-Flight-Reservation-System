package adapter.interfaces;

import model.entity.Booking;
import model.entity.Passenger;
import java.sql.SQLException;
import java.util.List;

/**
 * Adapter interface for booking data access operations.
 * Adapts application booking management needs to the database interface.
 */
public interface BookingDataAdapter {
    Booking create(Booking booking) throws SQLException;

    Booking findById(int bookingId) throws SQLException;

    List<Booking> findByCustomerId(int customerId) throws SQLException;

    List<Booking> findByFlightId(int flightId) throws SQLException;

    List<Booking> findAll() throws SQLException;

    boolean update(Booking booking) throws SQLException;

    boolean cancel(int bookingId) throws SQLException;

    boolean addPassenger(Passenger passenger) throws SQLException;

    boolean updatePassenger(Passenger passenger) throws SQLException;

    List<Passenger> getPassengersByBookingId(int bookingId) throws SQLException;

    boolean removePassenger(int passengerId) throws SQLException;
}
