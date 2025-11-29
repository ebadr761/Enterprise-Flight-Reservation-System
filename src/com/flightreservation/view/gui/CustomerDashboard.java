package com.flightreservation.view.gui;

import com.flightreservation.controller.*;
import com.flightreservation.model.entity.*;
import com.flightreservation.model.enums.BookingStatus;
import com.flightreservation.view.gui.components.RoundedButton;
import com.flightreservation.view.gui.components.StyledTextField;
import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer dashboard with flight search, booking, and management features
 */
public class CustomerDashboard extends BasePanel {

    private AuthenticationController authController;
    private FlightController flightController;
    private BookingController bookingController;
    private PaymentController paymentController;
    private Customer customer;

    private JPanel contentArea;
    private CardLayout contentLayout;

    private static final String SEARCH_PANEL = "search";
    private static final String BOOKINGS_PANEL = "bookings";
    private static final String PROFILE_PANEL = "profile";

    public CustomerDashboard(MainFrame mainFrame, AuthenticationController authController) {
        super(mainFrame);
        this.authController = authController;
        this.flightController = new FlightController();
        this.bookingController = new BookingController();
        this.paymentController = new PaymentController();
        this.customer = (Customer) authController.getCurrentUser();
        initializeComponents();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new BorderLayout());

        // Add header
        add(createHeader(), BorderLayout.NORTH);

        // Add navigation sidebar
        add(createSidebar(), BorderLayout.WEST);

        // Add content area
        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setBackground(AppTheme.BACKGROUND);

        contentArea.add(createSearchPanel(), SEARCH_PANEL);
        contentArea.add(createBookingsPanel(), BOOKINGS_PANEL);
        contentArea.add(createProfilePanel(), PROFILE_PANEL);

        add(contentArea, BorderLayout.CENTER);

        showContent(SEARCH_PANEL);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_LARGE,
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_LARGE));

        JLabel titleLabel = new JLabel("✈ Flight Reservation System");
        titleLabel.setFont(AppTheme.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(AppTheme.PRIMARY);

        JLabel userLabel = new JLabel("Welcome, " + customer.getFirstName() + " " + customer.getLastName());
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

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppTheme.SURFACE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.DIVIDER));

        sidebar.add(Box.createVerticalStrut(AppTheme.PADDING_LARGE));

        sidebar.add(createNavButton("🔍 Search Flights", e -> showContent(SEARCH_PANEL)));
        sidebar.add(createNavButton("📋 My Bookings", e -> showContent(BOOKINGS_PANEL)));
        sidebar.add(createNavButton("👤 My Profile", e -> showContent(PROFILE_PANEL)));

        return sidebar;
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));
        button.setFont(AppTheme.FONT_BODY);
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBackground(AppTheme.SURFACE);
        button.setBorder(BorderFactory.createEmptyBorder(AppTheme.PADDING_MEDIUM, AppTheme.PADDING_LARGE,
                AppTheme.PADDING_MEDIUM, AppTheme.PADDING_LARGE));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppTheme.HOVER);
                button.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setOpaque(false);
            }
        });

        return button;
    }

    private void showContent(String panelName) {
        if (panelName.equals(BOOKINGS_PANEL)) {
            refreshBookingsPanel();
        }
        contentLayout.show(contentArea, panelName);
    }

    private JPanel createSearchPanel() {
        JPanel panel = createTitledSection("Search Flights");

        JPanel searchForm = new JPanel(new GridBagLayout());
        searchForm.setBackground(AppTheme.SURFACE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(AppTheme.PADDING_SMALL, AppTheme.PADDING_SMALL, AppTheme.PADDING_SMALL,
                AppTheme.PADDING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Origin field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        searchForm.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        StyledTextField originField = new StyledTextField("e.g., New York", 15);
        searchForm.add(originField, gbc);

        // Destination field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        searchForm.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        StyledTextField destField = new StyledTextField("e.g., Los Angeles", 15);
        searchForm.add(destField, gbc);

        // Date field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        searchForm.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        StyledTextField dateField = new StyledTextField("e.g., 2025-12-01", 15);
        searchForm.add(dateField, gbc);

        // Search button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        RoundedButton searchButton = new RoundedButton("Search Flights");
        searchButton.setPreferredSize(new Dimension(200, 40));

        // Results table
        String[] columnNames = { "Flight", "Airline", "From", "To", "Departure", "Price", "Seats", "" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only book button column is editable
            }
        };
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFont(AppTheme.FONT_BODY);
        resultsTable.setRowHeight(35);
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));

        searchButton.addActionListener(e -> {
            String origin = originField.getText().trim();
            String dest = destField.getText().trim();
            String dateStr = dateField.getText().trim();

            // Clear previous results
            tableModel.setRowCount(0);

            List<Flight> flights = new ArrayList<>();

            if (!origin.isEmpty() && !dest.isEmpty()) {
                flights = flightController.searchFlights(origin, dest, null, null);
            } else if (!dateStr.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(dateStr);
                    flights = flightController.searchFlights(null, null, date, null);
                } catch (Exception ex) {
                    showError("Invalid date format. Use YYYY-MM-DD");
                    return;
                }
            } else {
                flights = flightController.getAllFlights();
            }

            for (Flight flight : flights) {
                RoundedButton bookBtn = new RoundedButton("Book");
                bookBtn.setPreferredSize(new Dimension(80, 28));
                bookBtn.addActionListener(evt -> bookFlight(flight));

                tableModel.addRow(new Object[] {
                        flight.getFlightNumber(),
                        flight.getAirline(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        "$" + String.format("%.2f", flight.getPrice()),
                        flight.getAvailableSeats() + "/" + flight.getTotalSeats(),
                        "Book"
                });
            }

            if (flights.isEmpty()) {
                showWarning("No flights found matching your search criteria");
            }
        });

        searchForm.add(searchButton, gbc);

        panel.add(searchForm);
        panel.add(Box.createVerticalStrut(AppTheme.PADDING_LARGE));
        panel.add(scrollPane);

        return panel;
    }

    private void bookFlight(Flight flight) {
        if (flight.getAvailableSeats() <= 0) {
            showError("No seats available on this flight");
            return;
        }

        String numPassengersStr = JOptionPane.showInputDialog(this, "Number of passengers (1-10):");
        if (numPassengersStr == null)
            return;

        try {
            int numPassengers = Integer.parseInt(numPassengersStr);
            if (numPassengers < 1 || numPassengers > 10) {
                showError("Please enter a number between 1 and 10");
                return;
            }

            if (numPassengers > flight.getAvailableSeats()) {
                showError("Not enough seats available. Only " + flight.getAvailableSeats() + " seats left");
                return;
            }

            // Collect passenger information
            List<Passenger> passengers = new ArrayList<>();
            for (int i = 0; i < numPassengers; i++) {
                JPanel passengerPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField nameField = new JTextField();
                JTextField passportField = new JTextField();
                JTextField ageField = new JTextField();

                passengerPanel.add(new JLabel("Name:"));
                passengerPanel.add(nameField);
                passengerPanel.add(new JLabel("Passport:"));
                passengerPanel.add(passportField);
                passengerPanel.add(new JLabel("Age:"));
                passengerPanel.add(ageField);

                int result = JOptionPane.showConfirmDialog(this, passengerPanel,
                        "Passenger " + (i + 1) + " Information", JOptionPane.OK_CANCEL_OPTION);

                if (result != JOptionPane.OK_OPTION)
                    return;

                String fullName = nameField.getText();
                String[] nameParts = fullName.split(" ", 2);

                Passenger passenger = new Passenger();
                passenger.setFirstName(nameParts.length > 0 ? nameParts[0] : fullName);
                passenger.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                passenger.setPassportNumber(passportField.getText());
                passengers.add(passenger);
            }

            // Create booking
            Booking booking = bookingController.createBooking(customer.getUserId(), flight.getFlightId(), passengers);

            if (booking != null) {
                // Process payment
                String[] paymentMethods = { "Credit Card", "Debit Card", "PayPal" };
                String selectedMethod = (String) JOptionPane.showInputDialog(this,
                        "Select payment method:", "Payment",
                        JOptionPane.QUESTION_MESSAGE, null, paymentMethods, paymentMethods[0]);

                if (selectedMethod != null) {
                    Payment payment = paymentController.processPayment(booking.getBookingId(), booking.getTotalAmount(),
                            selectedMethod);
                    if (payment != null) {
                        showSuccess("Booking confirmed! Booking reference: " + booking.getBookingReference());
                    }
                }
            }
        } catch (NumberFormatException ex) {
            showError("Invalid input");
        }
    }

    private JPanel createBookingsPanel() {
        JPanel panel = createTitledSection("My Bookings");

        return panel;
    }

    private void refreshBookingsPanel() {
        // This will be called when switching to bookings panel
        JPanel newPanel = createTitledSection("My Bookings");

        List<Booking> bookings = bookingController.getCustomerBookings(customer.getUserId());

        String[] columnNames = { "Booking Ref", "Flight", "Date", "Passengers", "Amount", "Status", "" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        for (Booking booking : bookings) {
            tableModel.addRow(new Object[] {
                    booking.getBookingReference(),
                    booking.getFlight().getFlightNumber(),
                    booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    booking.getPassengers().size(),
                    "$" + String.format("%.2f", booking.getTotalAmount()),
                    booking.getStatus(),
                    "Cancel"
            });
        }

        JTable table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(35);

        // Add cancel button renderer and editor
        table.getColumn("").setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            RoundedButton btn = new RoundedButton("Cancel");
            btn.setBackground(AppTheme.ERROR);
            btn.setPreferredSize(new Dimension(80, 28));
            return btn;
        });

        table.getColumn("").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private RoundedButton button = new RoundedButton("Cancel");

            {
                button.setBackground(AppTheme.ERROR);
                button.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Booking booking = bookings.get(row);
                        if (showConfirmation("Are you sure you want to cancel this booking?")) {
                            if (bookingController.cancelBooking(booking.getBookingId())) {
                                showSuccess("Booking cancelled successfully");
                                refreshBookingsPanel();
                            }
                        }
                    }
                    fireEditingStopped();
                });
            }

            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                return button;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 500));

        newPanel.add(scrollPane);

        // Replace old panel
        contentArea.remove(contentArea.getComponent(1)); // Remove old bookings panel
        contentArea.add(newPanel, BOOKINGS_PANEL, 1);
    }

    private JPanel createProfilePanel() {
        JPanel panel = createTitledSection("My Profile");

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBackground(AppTheme.SURFACE);

        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(customer.getFirstName() + " " + customer.getLastName()));

        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(customer.getEmail()));

        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(customer.getPhone()));

        infoPanel.add(new JLabel("Member Since:"));
        infoPanel.add(new JLabel(customer.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        panel.add(infoPanel);

        return panel;
    }

    private void handleLogout() {
        if (showConfirmation("Are you sure you want to logout?")) {
            authController.logout();
            mainFrame.removePanel(MainFrame.CUSTOMER_DASHBOARD);
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        }
    }
}
