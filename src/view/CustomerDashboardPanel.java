//CustomerDashboardPanel.java
package view;

import controller.*;
import model.entity.Flight;
import model.entity.User;
import model.entity.Customer;
import util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class CustomerDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private Customer customer;
    private FlightController flightController;
    private BookingController bookingController;
    private PaymentController paymentController;
    private AuthenticationController authController;

    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;
    private JTextField originField;
    private JTextField destinationField;
    private JTextField dateField;

    public CustomerDashboardPanel(MainFrame mainFrame, User user, FlightController flightController,
                                  BookingController bookingController, PaymentController paymentController,
                                  AuthenticationController authController) {
        this.mainFrame = mainFrame;
        this.customer = (Customer) user;
        this.flightController = flightController;
        this.bookingController = bookingController;
        this.paymentController = paymentController;
        this.authController = authController;

        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome, " + customer.getFullName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Search Flights", createSearchPanel());
        tabbedPane.addTab("My Bookings", createBookingsPanel());
        tabbedPane.addTab("Profile", createProfilePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search Inputs
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Origin:"));
        originField = new JTextField(10);
        inputPanel.add(originField);

        inputPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField(10);
        inputPanel.add(destinationField);

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(10);
        inputPanel.add(dateField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchFlights());
        inputPanel.add(searchButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Results Table
        String[] columns = { "Flight #", "Airline", "Origin", "Destination", "Departure", "Arrival", "Price", "Seats" };
        flightsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsTableModel);
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Book Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Book Selected Flight");
        bookButton.addActionListener(e -> showBookingDialog());
        bottomPanel.add(bookButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void searchFlights() {
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();
        String dateStr = dateField.getText().trim();

        LocalDate date = null;
        if (!dateStr.isEmpty()) {
            date = DateTimeUtil.parseDate(dateStr);
            if (date == null) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        List<Flight> flights = flightController.searchFlights(
                origin.isEmpty() ? null : origin,
                destination.isEmpty() ? null : destination,
                date,
                null);

        // Filter out flights with 0 available seats or COMPLETED status
        flights = flights.stream()
                .filter(f -> f.getAvailableSeats() > 0 &&
                        f.getStatus() != model.enums.FlightStatus.COMPLETED)
                .collect(java.util.stream.Collectors.toList());

        flightsTableModel.setRowCount(0); // Clear table
        for (Flight f : flights) {
            Object[] row = {
                    f.getFlightNumber(),
                    f.getAirline(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    String.format("$%.2f", f.getPrice()),
                    f.getAvailableSeats()
            };
            flightsTableModel.addRow(row);
        }

        if (flights.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No flights found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showBookingDialog() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to book.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String flightNum = (String) flightsTableModel.getValueAt(selectedRow, 0);
        Flight selectedFlight = flightController.getFlightByNumber(flightNum);

        if (selectedFlight != null) {
            // Open Booking Dialog
            BookingDialog dialog = new BookingDialog(mainFrame, customer, selectedFlight, bookingController,
                    paymentController);
            dialog.setVisible(true);
        }
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton refreshButton = new JButton("Refresh");
        JButton cancelButton = new JButton("Cancel Booking");

        toolBar.add(refreshButton);
        toolBar.add(cancelButton);
        panel.add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columns = { "Booking ID", "Flight", "Origin", "Destination", "Date", "Passengers", "Amount",
                "Status" };
        DefaultTableModel bookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable bookingsTable = new JTable(bookingsModel);
        panel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        // Actions
        ActionListener refreshAction = e -> {
            bookingsModel.setRowCount(0);
            List<model.entity.Booking> bookings = bookingController
                    .getCustomerBookings(customer.getUserId());
            for (model.entity.Booking b : bookings) {
                Flight f = b.getFlight();
                Object[] row = {
                        b.getBookingId(),
                        f != null ? f.getFlightNumber() : "N/A",
                        f != null ? f.getOrigin() : "N/A",
                        f != null ? f.getDestination() : "N/A",
                        b.getBookingDate(),
                        b.getPassengers().size(),
                        String.format("$%.2f", b.getTotalAmount()),
                        b.getStatus()
                };
                bookingsModel.addRow(row);
            }
        };

        refreshButton.addActionListener(refreshAction);

        cancelButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int bookingId = (Integer) bookingsModel.getValueAt(selectedRow, 0);
            String status = (String) bookingsModel.getValueAt(selectedRow, 7).toString();

            if ("CANCELLED".equals(status)) {
                JOptionPane.showMessageDialog(this, "Booking is already cancelled.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this booking?",
                    "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (bookingController.cancelBooking(bookingId)) {
                    // Refund payment
                    model.entity.Payment payment = paymentController
                            .getPaymentByBookingId(bookingId);
                    if (payment != null) {
                        paymentController.refundPayment(payment.getPaymentId());
                    }
                    JOptionPane.showMessageDialog(this, "Booking cancelled successfully. Refund processed.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshAction.actionPerformed(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel booking.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Initial Load
        refreshAction.actionPerformed(null);

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("My Profile"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels to hold profile data so they can be updated
        JLabel nameLabel = new JLabel(customer.getFullName());
        JLabel emailLabel = new JLabel(customer.getEmail());
        JLabel phoneLabel = new JLabel(customer.getPhone());
        JLabel memberSinceLabel = new JLabel(String.valueOf(customer.getRegistrationDate()));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Member Since:"), gbc);
        gbc.gridx = 1;
        panel.add(memberSinceLabel, gbc);

        // Edit Profile Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton editButton = new JButton("Edit Profile");
        editButton.addActionListener(e -> {
            EditProfileDialog dialog = new EditProfileDialog(mainFrame, customer, authController);
            dialog.setVisible(true);

            if (dialog.isProfileUpdated()) {
                // Refresh labels
                nameLabel.setText(customer.getFullName());
                phoneLabel.setText(customer.getPhone());
                // Update welcome label in header if possible, or just refresh panel
                revalidate();
                repaint();
            }
        });
        panel.add(editButton, gbc);

        // Wrapper to center the form
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.add(panel);
        return wrapper;
    }
}
