package com.flightreservation.view.gui;

import com.flightreservation.controller.*;
import com.flightreservation.model.entity.*;
import com.flightreservation.view.gui.components.RoundedButton;
import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Flight Agent dashboard for managing bookings
 */
public class FlightAgentDashboard extends BasePanel {

    private AuthenticationController authController;
    private FlightController flightController;
    private BookingController bookingController;
    private FlightAgent agent;

    private JPanel contentArea;

    public FlightAgentDashboard(MainFrame mainFrame, AuthenticationController authController) {
        super(mainFrame);
        this.authController = authController;
        this.flightController = new FlightController();
        this.bookingController = new BookingController();
        this.agent = (FlightAgent) authController.getCurrentUser();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout());

        // Add header
        add(createHeader(), BorderLayout.NORTH);

        // Add main content
        contentArea = createBookingsPanel();
        add(contentArea, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_LARGE,
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_LARGE));

        JLabel titleLabel = new JLabel("✈ Flight Agent Dashboard");
        titleLabel.setFont(AppTheme.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(AppTheme.PRIMARY);

        JLabel userLabel = new JLabel("Agent: " + agent.getFirstName() + " " + agent.getLastName());
        userLabel.setFont(AppTheme.FONT_BODY);
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);

        RoundedButton logoutButton = new RoundedButton("Logout");
        logoutButton.setBackground(AppTheme.ACCENT);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.addActionListener(e -> handleLogout());
        userPanel.add(logoutButton);

        header.add(userPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = createTitledSection("All Bookings");

        List<Booking> bookings = bookingController.getAllBookings();

        String[] columnNames = { "Booking Ref", "Customer", "Flight", "Date", "Passengers", "Amount", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Booking booking : bookings) {
            tableModel.addRow(new Object[] {
                    booking.getBookingReference(),
                    booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName(),
                    booking.getFlight().getFlightNumber() + " (" + booking.getFlight().getOrigin() + " → "
                            + booking.getFlight().getDestination() + ")",
                    booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    booking.getPassengers().size(),
                    "$" + String.format("%.2f", booking.getTotalAmount()),
                    booking.getStatus()
            });
        }

        JTable table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(35);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 600));

        panel.add(scrollPane);

        return panel;
    }

    private void handleLogout() {
        if (showConfirmation("Are you sure you want to logout?")) {
            authController.logout();
            mainFrame.removePanel(MainFrame.AGENT_DASHBOARD);
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        }
    }
}
