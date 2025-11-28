package com.flightreservation.dao;

import com.flightreservation.model.entity.User;
import com.flightreservation.model.entity.Customer;
import com.flightreservation.model.entity.FlightAgent;
import com.flightreservation.model.entity.Admin;
import java.sql.SQLException;

public interface UserDAO {
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
