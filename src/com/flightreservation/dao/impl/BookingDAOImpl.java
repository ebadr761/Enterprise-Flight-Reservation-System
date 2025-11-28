package com.flightreservation.dao.impl;

import com.flightreservation.dao.DatabaseConnection;
import com.flightreservation.dao.BookingDAO;
import com.flightreservation.model.entity.Booking;
import com.flightreservation.model.entity.Passenger;
import com.flightreservation.model.enums.BookingStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {
    private Connection connection;

    public BookingDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Booking create(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (customer_id, flight_id, status, total_amount, num_passengers) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getCustomerId());
            stmt.setInt(2, booking.getFlightId());
            stmt.setString(3, booking.getStatus().name());
            stmt.setDouble(4, booking.getTotalAmount());
            stmt.setInt(5, booking.getNumPassengers());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                booking.setBookingId(rs.getInt(1));
            }
        }
        return booking;
    }

    @Override
    public Booking findById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setPassengers(getPassengersByBookingId(bookingId));
                return booking;
            }
        }
        return null;
    }

    @Override
    public List<Booking> findByCustomerId(int customerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY booking_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setPassengers(getPassengersByBookingId(booking.getBookingId()));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findByFlightId(int flightId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE flight_id = ? ORDER BY booking_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setPassengers(getPassengersByBookingId(booking.getBookingId()));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_date DESC";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setPassengers(getPassengersByBookingId(booking.getBookingId()));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public boolean update(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET status = ?, total_amount = ?, num_passengers = ? WHERE booking_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, booking.getStatus().name());
            stmt.setDouble(2, booking.getTotalAmount());
            stmt.setInt(3, booking.getNumPassengers());
            stmt.setInt(4, booking.getBookingId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cancel(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean addPassenger(Passenger passenger) throws SQLException {
        String sql = "INSERT INTO passengers (booking_id, first_name, last_name, passport_number, seat_number) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, passenger.getBookingId());
            stmt.setString(2, passenger.getFirstName());
            stmt.setString(3, passenger.getLastName());
            stmt.setString(4, passenger.getPassportNumber());
            stmt.setString(5, passenger.getSeatNumber());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                passenger.setPassengerId(rs.getInt(1));
            }
            return true;
        }
    }

    @Override
    public boolean updatePassenger(Passenger passenger) throws SQLException {
        String sql = "UPDATE passengers SET first_name = ?, last_name = ?, passport_number = ?, seat_number = ? " +
                "WHERE passenger_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, passenger.getFirstName());
            stmt.setString(2, passenger.getLastName());
            stmt.setString(3, passenger.getPassportNumber());
            stmt.setString(4, passenger.getSeatNumber());
            stmt.setInt(5, passenger.getPassengerId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Passenger> getPassengersByBookingId(int bookingId) throws SQLException {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM passengers WHERE booking_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                passengers.add(extractPassengerFromResultSet(rs));
            }
        }
        return passengers;
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setCustomerId(rs.getInt("customer_id"));
        booking.setFlightId(rs.getInt("flight_id"));
        booking.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setTotalAmount(rs.getDouble("total_amount"));
        booking.setNumPassengers(rs.getInt("num_passengers"));
        return booking;
    }

    private Passenger extractPassengerFromResultSet(ResultSet rs) throws SQLException {
        Passenger passenger = new Passenger();
        passenger.setPassengerId(rs.getInt("passenger_id"));
        passenger.setBookingId(rs.getInt("booking_id"));
        passenger.setFirstName(rs.getString("first_name"));
        passenger.setLastName(rs.getString("last_name"));
        passenger.setPassportNumber(rs.getString("passport_number"));
        passenger.setSeatNumber(rs.getString("seat_number"));
        return passenger;
    }
}
