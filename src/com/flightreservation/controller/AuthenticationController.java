package com.flightreservation.controller;

import com.flightreservation.dao.UserDAO;
import com.flightreservation.dao.impl.UserDAOImpl;
import com.flightreservation.model.entity.*;
import com.flightreservation.model.enums.UserRole;
import com.flightreservation.util.InputValidator;

import java.sql.SQLException;
import java.time.LocalDate;

public class AuthenticationController {
    private UserDAO userDAO;
    private User currentUser;

    public AuthenticationController() {
        this.userDAO = new UserDAOImpl();
    }

    public User login(String email, String password) {
        try {
            currentUser = userDAO.authenticate(email, password);
            if (currentUser != null) {
                System.out.println("✓ Login successful! Welcome, " + currentUser.getFullName());
            } else {
                System.out.println("✗ Invalid email or password.");
            }
            return currentUser;
        } catch (SQLException e) {
            System.err.println("✗ Login error: " + e.getMessage());
            return null;
        }
    }

    public Customer registerCustomer(String email, String password, String firstName, String lastName, String phone) {
        // Validate inputs
        if (!InputValidator.isValidEmail(email)) {
            System.out.println("✗ Invalid email format.");
            return null;
        }
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("✗ Invalid phone number. Must be 10-15 digits.");
            return null;
        }
        if (!InputValidator.isNotEmpty(password) || password.length() < 6) {
            System.out.println("✗ Password must be at least 6 characters.");
            return null;
        }

        try {
            // Check if email already exists
            if (userDAO.findByEmail(email) != null) {
                System.out.println("✗ Email already registered.");
                return null;
            }

            Customer customer = new Customer(email, password, firstName, lastName, phone);
            customer.setRegistrationDate(LocalDate.now());
            customer = userDAO.createCustomer(customer);

            System.out.println("✓ Registration successful! Welcome, " + customer.getFullName());
            return customer;
        } catch (SQLException e) {
            System.err.println("✗ Registration error: " + e.getMessage());
            return null;
        }
    }

    public FlightAgent registerFlightAgent(String email, String password, String firstName, String lastName,
            String phone, String employeeId, String department) {
        try {
            if (userDAO.findByEmail(email) != null) {
                System.out.println("✗ Email already registered.");
                return null;
            }

            FlightAgent agent = new FlightAgent(email, password, firstName, lastName, phone, employeeId, department);
            agent = userDAO.createFlightAgent(agent);

            System.out.println("✓ Flight Agent registered successfully!");
            return agent;
        } catch (SQLException e) {
            System.err.println("✗ Registration error: " + e.getMessage());
            return null;
        }
    }

    public Admin registerAdmin(String email, String password, String firstName, String lastName,
            String phone, int adminLevel) {
        try {
            if (userDAO.findByEmail(email) != null) {
                System.out.println("✗ Email already registered.");
                return null;
            }

            Admin admin = new Admin(email, password, firstName, lastName, phone, adminLevel);
            admin = userDAO.createAdmin(admin);

            System.out.println("✓ Admin registered successfully!");
            return admin;
        } catch (SQLException e) {
            System.err.println("✗ Registration error: " + e.getMessage());
            return null;
        }
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getFullName() + "!");
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
}
