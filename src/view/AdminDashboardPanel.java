//AdminDashboardPanel.java
package view;

import controller.*;
import model.entity.Admin;
import model.entity.Customer;
import model.entity.Flight;
import model.entity.User;
import model.enums.FlightStatus;
import service.NotificationManager;
import util.DateTimeUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private Admin admin;
    private FlightController flightController;
    private BookingController bookingController;
    private AuthenticationController authController;

    private JTable flightsTable;
    private DefaultTableModel flightsTableModel;

    public AdminDashboardPanel(MainFrame mainFrame, User user, FlightController flightController,
                               BookingController bookingController, AuthenticationController authController) {
        this.mainFrame = mainFrame;
        this.admin = (Admin) user;
        this.flightController = flightController;
        this.bookingController = bookingController;
        this.authController = authController;

        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(47, 79, 79)); // Dark Slate Gray
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard - " + admin.getFullName());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Flights", createManageFlightsPanel());
        tabbedPane.addTab("Flight Status", createStatusPanel());
        tabbedPane.addTab("System Stats", createStatsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createManageFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add Flight");
        JButton updateButton = new JButton("Update Flight");
        JButton deleteButton = new JButton("Delete Flight");
        JButton refreshButton = new JButton("Refresh List");
        JButton newsletterButton = new JButton("Send Newsletter");

        toolBar.add(addButton);
        toolBar.add(updateButton);
        toolBar.add(deleteButton);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(newsletterButton);
        toolBar.add(refreshButton);

        panel.add(toolBar, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Flight #", "Airline", "Origin", "Destination", "Departure", "Arrival", "Price",
                "Seats", "Status" };
        flightsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(flightsTableModel);
        panel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);

        // Actions
        refreshButton.addActionListener(e -> refreshFlightsTable());
        addButton.addActionListener(e -> showAddFlightDialog());
        updateButton.addActionListener(e -> showUpdateFlightDialog());
        deleteButton.addActionListener(e -> showDeleteFlightDialog());
        newsletterButton.addActionListener(e -> sendNewsletter());

        // Initial Load
        refreshFlightsTable();

        return panel;
    }

    private void refreshFlightsTable() {
        flightsTableModel.setRowCount(0);
        List<Flight> flights = flightController.getAllFlights();
        for (Flight f : flights) {
            Object[] row = {
                    f.getFlightId(),
                    f.getFlightNumber(),
                    f.getAirline(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    f.getPrice(),
                    f.getTotalSeats(),
                    f.getStatus()
            };
            flightsTableModel.addRow(row);
        }
    }

    private void showAddFlightDialog() {
        // Simple dialog for adding flight
        JDialog dialog = new JDialog(mainFrame, "Add New Flight", true);
        dialog.setLayout(new GridLayout(0, 2));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JTextField numField = new JTextField();
        JTextField airlineField = new JTextField();
        JTextField originField = new JTextField();
        JTextField destField = new JTextField();
        JTextField depField = new JTextField("YYYY-MM-DD HH:MM");
        JTextField arrField = new JTextField("YYYY-MM-DD HH:MM");
        JTextField priceField = new JTextField();
        JTextField seatsField = new JTextField();
        JTextField typeField = new JTextField();

        dialog.add(new JLabel("Flight Number:"));
        dialog.add(numField);
        dialog.add(new JLabel("Airline:"));
        dialog.add(airlineField);
        dialog.add(new JLabel("Origin:"));
        dialog.add(originField);
        dialog.add(new JLabel("Destination:"));
        dialog.add(destField);
        dialog.add(new JLabel("Departure:"));
        dialog.add(depField);
        dialog.add(new JLabel("Arrival:"));
        dialog.add(arrField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Seats:"));
        dialog.add(seatsField);
        dialog.add(new JLabel("Aircraft Type:"));
        dialog.add(typeField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                LocalDateTime dep = DateTimeUtil.parseDateTime(depField.getText());
                LocalDateTime arr = DateTimeUtil.parseDateTime(arrField.getText());
                double price = Double.parseDouble(priceField.getText());
                int seats = Integer.parseInt(seatsField.getText());

                if (dep == null || arr == null)
                    throw new Exception("Invalid Date");

                flightController.createFlight(numField.getText(), airlineField.getText(), originField.getText(),
                        destField.getText(), dep, arr, price, seats, typeField.getText());

                refreshFlightsTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Input: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private void showUpdateFlightDialog() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to update.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (Integer) flightsTableModel.getValueAt(selectedRow, 0);
        Flight flight = flightController.getFlightById(flightId);

        if (flight == null) {
            JOptionPane.showMessageDialog(this, "Flight not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create update dialog
        JDialog dialog = new JDialog(mainFrame, "Update Flight", true);
        dialog.setLayout(new GridLayout(0, 2));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JTextField numField = new JTextField(flight.getFlightNumber());
        JTextField airlineField = new JTextField(flight.getAirline());
        JTextField originField = new JTextField(flight.getOrigin());
        JTextField destField = new JTextField(flight.getDestination());
        JTextField depField = new JTextField(flight.getDepartureTime().toString().replace("T", " "));
        JTextField arrField = new JTextField(flight.getArrivalTime().toString().replace("T", " "));
        JTextField priceField = new JTextField(String.valueOf(flight.getPrice()));
        JTextField seatsField = new JTextField(String.valueOf(flight.getTotalSeats()));
        JTextField typeField = new JTextField(flight.getAircraftType());

        dialog.add(new JLabel("Flight Number:"));
        dialog.add(numField);
        dialog.add(new JLabel("Airline:"));
        dialog.add(airlineField);
        dialog.add(new JLabel("Origin:"));
        dialog.add(originField);
        dialog.add(new JLabel("Destination:"));
        dialog.add(destField);
        dialog.add(new JLabel("Departure:"));
        dialog.add(depField);
        dialog.add(new JLabel("Arrival:"));
        dialog.add(arrField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Total Seats:"));
        dialog.add(seatsField);
        dialog.add(new JLabel("Aircraft Type:"));
        dialog.add(typeField);

        JButton saveButton = new JButton("Update");
        saveButton.addActionListener(e -> {
            try {
                LocalDateTime dep = DateTimeUtil.parseDateTime(depField.getText());
                LocalDateTime arr = DateTimeUtil.parseDateTime(arrField.getText());
                double price = Double.parseDouble(priceField.getText());
                int newTotalSeats = Integer.parseInt(seatsField.getText());

                if (dep == null || arr == null)
                    throw new Exception("Invalid Date Format");

                // Calculate reserved seats and adjust available seats proportionally
                int oldTotalSeats = flight.getTotalSeats();
                int oldAvailableSeats = flight.getAvailableSeats();
                int reservedSeats = oldTotalSeats - oldAvailableSeats;
                int newAvailableSeats = Math.max(0, newTotalSeats - reservedSeats);

                flight.setFlightNumber(numField.getText());
                flight.setAirline(airlineField.getText());
                flight.setOrigin(originField.getText());
                flight.setDestination(destField.getText());
                flight.setDepartureTime(dep);
                flight.setArrivalTime(arr);
                flight.setPrice(price);
                flight.setTotalSeats(newTotalSeats);
                flight.setAvailableSeats(newAvailableSeats); // Update available seats proportionally
                flight.setAircraftType(typeField.getText());

                if (flightController.updateFlight(flight)) {
                    JOptionPane.showMessageDialog(dialog, "Flight updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshFlightsTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update flight.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Input: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(saveButton);
        dialog.setVisible(true);
    }

    private void showDeleteFlightDialog() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (Integer) flightsTableModel.getValueAt(selectedRow, 0);
        String flightNumber = (String) flightsTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete flight " + flightNumber + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (flightController.deleteFlight(flightId)) {
                JOptionPane.showMessageDialog(this, "Flight deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshFlightsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete flight.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendNewsletter() {
        List<Flight> flights = flightController.getAllFlights();
        StringBuilder content = new StringBuilder("Check out our available flights:\n\n");

        for (Flight f : flights) {
            if (f.getStatus() == FlightStatus.SCHEDULED) {
                content.append(String.format("✈ %s: %s to %s at $%.2f\n",
                        f.getAirline(), f.getOrigin(), f.getDestination(), f.getPrice()));
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Send newsletter with the following content?\n\n" + content.toString(),
                "Confirm Newsletter", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Newsletter sent to all subscribed customers!");
            System.out.println("Newsletter Content:\n" + content);
        }
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Flight Number:"));
        JTextField flightNumField = new JTextField(10);
        inputPanel.add(flightNumField);

        inputPanel.add(new JLabel("New Status:"));
        JComboBox<FlightStatus> statusCombo = new JComboBox<>(FlightStatus.values());
        inputPanel.add(statusCombo);

        JButton updateButton = new JButton("Update Status");
        inputPanel.add(updateButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Recent Updates Log (Mock)
        JTextArea logArea = new JTextArea("System Log:\n");
        logArea.setEditable(false);
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        updateButton.addActionListener(e -> {
            String flightNum = flightNumField.getText().trim();
            FlightStatus status = (FlightStatus) statusCombo.getSelectedItem();

            if (flightNum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a flight number.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Flight flight = flightController.getFlightByNumber(flightNum);
            if (flight != null) {

                flight.setStatus(status);
                if (flightController.updateFlight(flight)) {
                    logArea.append(String.format("Updated %s to %s at %s\n", flightNum, status, LocalDateTime.now()));
                    JOptionPane.showMessageDialog(this, "Status updated successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Flight not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Metrics
        List<model.entity.Booking> allBookings = bookingController.getAllBookings();
        int totalFlights = flightController.getAllFlights().size();
        int totalBookings = allBookings.size();

        // Filter confirmed bookings for accurate revenue calculation
        int confirmedBookings = (int) allBookings.stream()
                .filter(b -> b.getStatus() == model.enums.BookingStatus.CONFIRMED)
                .count();

        double totalRevenue = allBookings.stream()
                .filter(b -> b.getStatus() == model.enums.BookingStatus.CONFIRMED)
                .mapToDouble(model.entity.Booking::getTotalAmount)
                .sum();

        JLabel flightsLabel = new JLabel("Total Flights: " + totalFlights, SwingConstants.CENTER);
        flightsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        flightsLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel bookingsLabel = new JLabel("Total Bookings: " + totalBookings, SwingConstants.CENTER);
        bookingsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        bookingsLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel confirmedLabel = new JLabel("Confirmed Bookings: " + confirmedBookings, SwingConstants.CENTER);
        confirmedLabel.setFont(new Font("Arial", Font.BOLD, 24));
        confirmedLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        confirmedLabel.setForeground(new Color(0, 100, 0));

        JLabel revenueLabel = new JLabel(String.format("Total Revenue: $%.2f", totalRevenue), SwingConstants.CENTER);
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        revenueLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        revenueLabel.setForeground(new Color(0, 100, 0));

        panel.add(flightsLabel);
        panel.add(bookingsLabel);
        panel.add(confirmedLabel);
        panel.add(revenueLabel);

        return panel;
    }
}
