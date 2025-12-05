package view;

import controller.*;
import model.entity.User;
import model.enums.UserRole;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window managing view navigation and user sessions.
 * Uses CardLayout to switch between login and role-specific dashboards.
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Controllers
    private AuthenticationController authController;
    private FlightController flightController;
    private BookingController bookingController;
    private PaymentController paymentController;

    // View Names
    public static final String LOGIN_VIEW = "LOGIN";
    public static final String CUSTOMER_VIEW = "CUSTOMER";
    public static final String ADMIN_VIEW = "ADMIN";
    public static final String AGENT_VIEW = "AGENT";

    public MainFrame() {
        authController = new AuthenticationController();
        flightController = new FlightController();
        bookingController = new BookingController();
        paymentController = new PaymentController();

        setTitle("Flight Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        initializeViews();

        showView(LOGIN_VIEW);
    }

    private void initializeViews() {
        LoginPanel loginPanel = new LoginPanel(this, authController);
        mainPanel.add(loginPanel, LOGIN_VIEW);
    }

    public void showView(String viewName) {
        cardLayout.show(mainPanel, viewName);
    }

    /**
     * Handles successful login by creating and displaying role-specific dashboard.
     *
     * @param user Authenticated user
     */
    public void loginSuccess(User user) {
        if (user.getRole() == UserRole.CUSTOMER) {
            CustomerDashboardPanel customerPanel = new CustomerDashboardPanel(this, user, flightController,
                    bookingController, paymentController, authController);
            mainPanel.add(customerPanel, CUSTOMER_VIEW);
            showView(CUSTOMER_VIEW);
        } else if (user.getRole() == UserRole.ADMIN) {
            AdminDashboardPanel adminPanel = new AdminDashboardPanel(this, user, flightController, bookingController,
                    authController);
            mainPanel.add(adminPanel, ADMIN_VIEW);
            showView(ADMIN_VIEW);
        } else if (user.getRole() == UserRole.FLIGHT_AGENT) {
            AgentDashboardPanel agentPanel = new AgentDashboardPanel(this, user, flightController, bookingController,
                    authController);
            mainPanel.add(agentPanel, AGENT_VIEW);
            showView(AGENT_VIEW);
        }
    }

    /**
     * Logs out the current user and returns to login screen.
     * Clears all dashboard panels to reset state.
     */
    public void logout() {
        authController.logout();
        Component[] components = mainPanel.getComponents();
        for (Component c : components) {
            if (!(c instanceof LoginPanel)) {
                mainPanel.remove(c);
            }
        }
        showView(LOGIN_VIEW);
    }
}
