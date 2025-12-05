// FlightDatabaseAdapter.java
package adapter.database;

import adapter.database.DatabaseConnection;
import adapter.interfaces.FlightDataAdapter;
import model.entity.Flight;
import model.enums.FlightStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightDatabaseAdapter implements FlightDataAdapter {
    private Connection connection;

    public FlightDatabaseAdapter() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Flight create(Flight flight) throws SQLException {
        String sql = "INSERT INTO flights (flight_number, airline, origin, destination, departure_time, " +
                "arrival_time, price, total_seats, available_seats, status, aircraft_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, flight.getFlightNumber());
            stmt.setString(2, flight.getAirline());
            stmt.setString(3, flight.getOrigin());
            stmt.setString(4, flight.getDestination());
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setDouble(7, flight.getPrice());
            stmt.setInt(8, flight.getTotalSeats());
            stmt.setInt(9, flight.getAvailableSeats());
            stmt.setString(10, flight.getStatus().name());
            stmt.setString(11, flight.getAircraftType());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                flight.setFlightId(rs.getInt(1));
            }
        }
        return flight;
    }

    @Override
    public Flight findById(int flightId) throws SQLException {
        String sql = "SELECT * FROM flights WHERE flight_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractFlightFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public Flight findByFlightNumber(String flightNumber) throws SQLException {
        String sql = "SELECT * FROM flights WHERE flight_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, flightNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractFlightFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Flight> findAll() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights ORDER BY departure_time";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                flights.add(extractFlightFromResultSet(rs));
            }
        }
        return flights;
    }

    @Override
    public List<Flight> search(String origin, String destination, LocalDate date, String airline) throws SQLException {
        List<Flight> flights = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM flights WHERE 1=1");

        if (origin != null && !origin.isEmpty()) {
            sql.append(" AND origin LIKE ?");
        }
        if (destination != null && !destination.isEmpty()) {
            sql.append(" AND destination LIKE ?");
        }
        if (date != null) {
            sql.append(" AND DATE(departure_time) = ?");
        }
        if (airline != null && !airline.isEmpty()) {
            sql.append(" AND airline LIKE ?");
        }
        sql.append(" AND status = 'SCHEDULED' ORDER BY departure_time");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (origin != null && !origin.isEmpty()) {
                stmt.setString(paramIndex++, "%" + origin + "%");
            }
            if (destination != null && !destination.isEmpty()) {
                stmt.setString(paramIndex++, "%" + destination + "%");
            }
            if (date != null) {
                stmt.setDate(paramIndex++, Date.valueOf(date));
            }
            if (airline != null && !airline.isEmpty()) {
                stmt.setString(paramIndex++, "%" + airline + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                flights.add(extractFlightFromResultSet(rs));
            }
        }
        return flights;
    }

    @Override
    public boolean update(Flight flight) throws SQLException {
        String sql = "UPDATE flights SET flight_number = ?, airline = ?, origin = ?, destination = ?, " +
                "departure_time = ?, arrival_time = ?, price = ?, total_seats = ?, available_seats = ?, " +
                "status = ?, aircraft_type = ? WHERE flight_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, flight.getFlightNumber());
            stmt.setString(2, flight.getAirline());
            stmt.setString(3, flight.getOrigin());
            stmt.setString(4, flight.getDestination());
            stmt.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));
            stmt.setDouble(7, flight.getPrice());
            stmt.setInt(8, flight.getTotalSeats());
            stmt.setInt(9, flight.getAvailableSeats());
            stmt.setString(10, flight.getStatus().name());
            stmt.setString(11, flight.getAircraftType());
            stmt.setInt(12, flight.getFlightId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int flightId) throws SQLException {
        String sql = "DELETE FROM flights WHERE flight_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateAvailableSeats(int flightId, int seats) throws SQLException {
        String sql = "UPDATE flights SET available_seats = ? WHERE flight_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, seats);
            stmt.setInt(2, flightId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int flightId, FlightStatus status) throws SQLException {
        String sql = "UPDATE flights SET status = ? WHERE flight_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, flightId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Flight extractFlightFromResultSet(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setFlightId(rs.getInt("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setAirline(rs.getString("airline"));
        flight.setOrigin(rs.getString("origin"));
        flight.setDestination(rs.getString("destination"));
        flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        flight.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
        flight.setPrice(rs.getDouble("price"));
        flight.setTotalSeats(rs.getInt("total_seats"));
        flight.setAvailableSeats(rs.getInt("available_seats"));
        flight.setStatus(FlightStatus.valueOf(rs.getString("status")));
        flight.setAircraftType(rs.getString("aircraft_type"));
        return flight;
    }
}
