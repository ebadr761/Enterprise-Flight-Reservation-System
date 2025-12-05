//AgentDashboardPanel.java
package view;

import controller.*;
import model.entity.FlightAgent;
import model.entity.User;
import model.enums.FlightStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AgentDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private FlightAgent agent;
    private FlightController flightController;
    private BookingController bookingController;
    private AuthenticationController authController;

    public AgentDashboardPanel(MainFrame mainFrame, User user, FlightController flightController,
                               BookingController bookingController, AuthenticationController authController) {
        this.mainFrame = mainFrame;
        this.agent = (FlightAgent) user;
        this.flightController = flightController;
        this.bookingController = bookingController;
        this.authController = authController;

        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Agent Dashboard - " + agent.getFullName());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("View Bookings", createBookingsPanel());
        tabbedPane.addTab("Flight Schedules", createSchedulesPanel());
        tabbedPane.addTab("Manage Passengers", createManagePassengersPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton refreshButton = new JButton("Refresh List");
        JButton cancelButton = new JButton("Cancel Selected Booking");

        toolBar.add(refreshButton);
        toolBar.add(cancelButton);
        panel.add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columns = { "Booking ID", "Customer ID", "Flight #", "Date", "Passengers", "Amount", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            java.util.List<model.entity.Booking> bookings = bookingController.getAllBookings();
            for (model.entity.Booking b : bookings) {
                Object[] row = {
                        b.getBookingId(),
                        b.getCustomerId(),
                        b.getFlight() != null ? b.getFlight().getFlightNumber() : "N/A",
                        b.getBookingDate(),
                        b.getPassengers().size(),
                        String.format("$%.2f", b.getTotalAmount()),
                        b.getStatus()
                };
                model.addRow(row);
            }
        });

        cancelButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int bookingId = (Integer) model.getValueAt(selectedRow, 0);
            String status = (String) model.getValueAt(selectedRow, 6).toString();

            if ("CANCELLED".equals(status)) {
                JOptionPane.showMessageDialog(this, "Booking is already cancelled.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Cancel this booking?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (bookingController.cancelBooking(bookingId)) {
                    JOptionPane.showMessageDialog(this, "Booking cancelled.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshButton.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel booking.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Initial Load
        refreshButton.doClick();

        return panel;
    }

    private JPanel createSchedulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton refreshButton = new JButton("Refresh Schedules");
        JButton viewBookingsButton = new JButton("View Flight Bookings");
        JButton reviveButton = new JButton("Revive Cancelled Flight");

        toolBar.add(refreshButton);
        toolBar.add(viewBookingsButton);
        toolBar.add(reviveButton);
        panel.add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Flight #", "Airline", "Origin", "Destination", "Departure", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        refreshButton.addActionListener(e -> {
            model.setRowCount(0);
            java.util.List<model.entity.Flight> flights = flightController.getAllFlights();
            for (model.entity.Flight f : flights) {
                Object[] row = {
                        f.getFlightId(),
                        f.getFlightNumber(),
                        f.getAirline(),
                        f.getOrigin(),
                        f.getDestination(),
                        f.getDepartureTime(),
                        f.getStatus()
                };
                model.addRow(row);
            }
        });

        viewBookingsButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int flightId = (Integer) model.getValueAt(selectedRow, 0);
            String flightNum = (String) model.getValueAt(selectedRow, 1);

            java.util.List<model.entity.Booking> bookings = bookingController
                    .getFlightBookings(flightId);

            StringBuilder sb = new StringBuilder("Bookings for Flight " + flightNum + ":\n\n");
            if (bookings.isEmpty()) {
                sb.append("No bookings found.");
            } else {
                for (model.entity.Booking b : bookings) {
                    sb.append(String.format("ID: %d | Cust ID: %d | Pass: %d | Status: %s\n",
                            b.getBookingId(), b.getCustomerId(), b.getPassengers().size(), b.getStatus()));
                }
            }

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Flight Bookings",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        reviveButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to revive.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int flightId = (Integer) model.getValueAt(selectedRow, 0);
            String flightNum = (String) model.getValueAt(selectedRow, 1);
            String currentStatus = (String) model.getValueAt(selectedRow, 6).toString();

            if (!"CANCELLED".equals(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Only CANCELLED flights can be revived.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Revive flight " + flightNum + " and change status to SCHEDULED?",
                    "Confirm Revival", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (flightController.updateFlightStatus(flightId,
                        FlightStatus.SCHEDULED)) {
                    JOptionPane.showMessageDialog(this, "Flight " + flightNum + " has been revived!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshButton.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to revive flight.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshButton.doClick();
        return panel;
    }

    private JPanel createManagePassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Booking ID:"));
        JTextField bookingIdField = new JTextField(10);
        inputPanel.add(bookingIdField);
        JButton searchButton = new JButton("Search Booking");
        inputPanel.add(searchButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Passengers List
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> passengerList = new JList<>(listModel);
        panel.add(new JScrollPane(passengerList), BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Remove Selected Passenger");
        actionPanel.add(removeButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        // Logic
        searchButton.addActionListener(e -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText().trim());
                model.entity.Booking booking = bookingController.getBookingById(bookingId);

                listModel.clear();
                if (booking != null) {
                    for (model.entity.Passenger p : booking.getPassengers()) {
                        listModel.addElement(p.getPassengerId() + ": " + p.getFirstName() + " " + p.getLastName());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Booking not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Booking ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeButton.addActionListener(e -> {
            String selected = passengerList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a passenger.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int passengerId = Integer.parseInt(selected.split(":")[0]);
            int bookingId = Integer.parseInt(bookingIdField.getText().trim());

            int confirm = JOptionPane.showConfirmDialog(this, "Remove passenger?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (bookingController.removePassenger(bookingId, passengerId)) {
                    JOptionPane.showMessageDialog(this, "Passenger removed.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    searchButton.doClick(); // Refresh
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove passenger.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }
}
