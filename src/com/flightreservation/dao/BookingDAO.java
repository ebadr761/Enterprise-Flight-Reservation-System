package com.flightreservation.dao;

import com.flightreservation.model.entity.Booking;
import com.flightreservation.model.entity.Passenger;
import com.flightreservation.model.enums.BookingStatus;
import java.sql.SQLException;
import java.util.List;

public interface BookingDAO {
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
}
