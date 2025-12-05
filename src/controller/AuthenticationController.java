package controller;

import adapter.interfaces.UserDataAdapter;
import adapter.database.UserDatabaseAdapter;
import model.entity.*;
import model.enums.UserRole;
import util.InputValidator;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Handles user authentication and registration for all user roles.
 * Validates user input, manages login sessions, and creates new user accounts.
 */
public class AuthenticationController {
    private UserDataAdapter userDataAdapter;
    private User currentUser;

    public AuthenticationController() {
        this.userDataAdapter = new UserDatabaseAdapter();
    }

    /**
     * Authenticates a user with email and password.
     *
     * @param email    User's email address
     * @param password User's password
     * @return Authenticated User object if successful, null otherwise
     */
    public User login(String email, String password) {
        try {
            currentUser = userDataAdapter.authenticate(email, password);
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

    /**
     * Registers a new customer account with validation.
     *
     * @param email     Customer email (must be valid format)
     * @param password  Customer password (minimum 6 characters)
     * @param firstName Customer first name
     * @param lastName  Customer last name
     * @param phone     Customer phone number (10-15 digits)
     * @return Created Customer object if successful, null otherwise
     */
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
            if (userDataAdapter.findByEmail(email) != null) {
                System.out.println("✗ Email already registered.");
                return null;
            }

            Customer customer = new Customer(email, password, firstName, lastName, phone);
            customer.setRegistrationDate(LocalDate.now());
            customer = userDataAdapter.createCustomer(customer);

            System.out.println("✓ Registration successful! Welcome, " + customer.getFullName());
            return customer;
        } catch (SQLException e) {
            System.err.println("✗ Registration error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Registers a new flight agent account.
     *
     * @param email      Agent email
     * @param password   Agent password
     * @param firstName  Agent first name
     * @param lastName   Agent last name
     * @param phone      Agent phone number
     * @param employeeId Unique employee identifier
     * @param department Agent's department
     * @return Created FlightAgent object if successful, null otherwise
     */
    public FlightAgent registerFlightAgent(String email, String password, String firstName, String lastName,
                                           String phone, String employeeId, String department) {
        try {
            if (userDataAdapter.findByEmail(email) != null) {
                System.out.println("✗ Email already registered.");
                return null;
            }

            FlightAgent agent = new FlightAgent(email, password, firstName, lastName, phone, employeeId, department);
            agent = userDataAdapter.createFlightAgent(agent);

            System.out.println("✓ Flight Agent registered successfully!");
            return agent;
        } catch (SQLException e) {
            System.err.println("✗ Registration error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Registers a new administrator account.
     *
     * @param email      Admin email
     * @param password   Admin password
     * @param firstName  Admin first name
     * @param lastName   Admin last name
     * @param phone      Admin phone number
     * @param adminLevel Admin privilege level
     * @return Created Admin object if successful, null otherwise
     */
    public Admin registerAdmin(String email, String password, String firstName, String lastName,
                               String phone, int adminLevel) {
        try {
            if (userDataAdapter.findByEmail(email) != null) {
                System.out.println("✗ Email already registered.");
                return null;
            }

            Admin admin = new Admin(email, password, firstName, lastName, phone, adminLevel);
            admin = userDataAdapter.createAdmin(admin);

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

    /**
     * Updates user profile information.
     *
     * @param user      The user to update
     * @param firstName New first name
     * @param lastName  New last name
     * @param phone     New phone number
     * @param password  New password (can be null/empty if not changing)
     * @return true if update successful, false otherwise
     */
    public boolean updateProfile(User user, String firstName, String lastName, String phone, String password) {
        // Validate inputs
        if (!InputValidator.isNotEmpty(firstName) || !InputValidator.isNotEmpty(lastName)) {
            System.out.println("✗ First name and last name cannot be empty.");
            return false;
        }
        if (!InputValidator.isValidPhone(phone)) {
            System.out.println("✗ Invalid phone number.");
            return false;
        }

        try {
            // Update user object
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);

            if (InputValidator.isNotEmpty(password) && password.length() >= 6) {
                user.setPassword(password);
            } else if (InputValidator.isNotEmpty(password)) {
                System.out.println("✗ Password must be at least 6 characters.");
                return false;
            }

            // Persist to database
            boolean success = userDataAdapter.update(user);
            if (success) {
                System.out.println("✓ Profile updated successfully!");
                // Update current user reference if it's the logged-in user
                if (currentUser != null && currentUser.getUserId() == user.getUserId()) {
                    currentUser = user;
                }
            } else {
                System.out.println("✗ Failed to update profile.");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error updating profile: " + e.getMessage());
            return false;
        }
    }
}
