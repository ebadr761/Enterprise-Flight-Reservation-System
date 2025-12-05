package adapter.database;

import adapter.interfaces.UserDataAdapter;
import model.entity.*;
import model.enums.UserRole;

import java.sql.*;
import java.time.LocalDate;

/**
 * MySQL database adapter for user data access operations.
 * Implements the Adapter pattern to adapt application user management needs
 * to the MySQL database interface via JDBC.
 */
public class UserDatabaseAdapter implements UserDataAdapter {
    private Connection connection;

    public UserDatabaseAdapter() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password, first_name, last_name, phone, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getRole().name());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setUserId(rs.getInt(1));
            }
        }
        return user;
    }

    @Override
    public Customer createCustomer(Customer customer) throws SQLException {
        // First create the base user
        create(customer);

        // Then create customer-specific data
        String sql = "INSERT INTO customers (customer_id, registration_date) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customer.getUserId());
            stmt.setDate(2, Date.valueOf(customer.getRegistrationDate()));
            stmt.executeUpdate();
        }

        return customer;
    }

    @Override
    public FlightAgent createFlightAgent(FlightAgent agent) throws SQLException {
        // First create the base user
        create(agent);

        // Then create agent-specific data
        String sql = "INSERT INTO flight_agents (agent_id) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, agent.getUserId());
            stmt.executeUpdate();
        }

        return agent;
    }

    @Override
    public Admin createAdmin(Admin admin) throws SQLException {
        // First create the base user
        create(admin);

        // Then create admin-specific data
        String sql = "INSERT INTO admins (admin_id) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, admin.getUserId());
            stmt.executeUpdate();
        }

        return admin;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, password = ?, first_name = ?, last_name = ?, phone = ? WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPhone());
            stmt.setInt(6, user.getUserId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        UserRole role = UserRole.valueOf(rs.getString("role"));
        User user;

        switch (role) {
            case CUSTOMER:
                user = extractCustomer(rs);
                break;
            case FLIGHT_AGENT:
                user = extractFlightAgent(rs);
                break;
            case ADMIN:
                user = extractAdmin(rs);
                break;
            default:
                user = new User();
        }

        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(role);

        return user;
    }

    private Customer extractCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();

        // Get customer-specific data
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, rs.getInt("user_id"));
            ResultSet custRs = stmt.executeQuery();

            if (custRs.next()) {
                customer.setRegistrationDate(custRs.getDate("registration_date").toLocalDate());
            }
        }

        return customer;
    }

    private FlightAgent extractFlightAgent(ResultSet rs) throws SQLException {
        FlightAgent agent = new FlightAgent();
        // No agent-specific data to retrieve
        return agent;
    }

    private Admin extractAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        // No admin-specific data to retrieve
        return admin;
    }
}
