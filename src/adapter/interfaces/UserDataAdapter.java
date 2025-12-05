package adapter.interfaces;

import model.entity.User;
import model.entity.Customer;
import model.entity.FlightAgent;
import model.entity.Admin;
import java.sql.SQLException;

/**
 * Adapter interface for user data access operations.
 * Adapts application user management needs to the database interface.
 */
public interface UserDataAdapter {
    User create(User user) throws SQLException;

    User findByEmail(String email) throws SQLException;

    User findById(int userId) throws SQLException;

    boolean update(User user) throws SQLException;

    boolean delete(int userId) throws SQLException;

    User authenticate(String email, String password) throws SQLException;

    Customer createCustomer(Customer customer) throws SQLException;

    FlightAgent createFlightAgent(FlightAgent agent) throws SQLException;

    Admin createAdmin(Admin admin) throws SQLException;
}
