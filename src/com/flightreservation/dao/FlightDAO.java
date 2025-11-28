package com.flightreservation.dao;

import com.flightreservation.model.entity.Flight;
import com.flightreservation.model.enums.FlightStatus;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface FlightDAO {
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
