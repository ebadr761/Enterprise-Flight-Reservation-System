package com.flightreservation.view.gui;

import com.flightreservation.controller.*;
import com.flightreservation.model.entity.*;
import com.flightreservation.model.enums.FlightStatus;
import com.flightreservation.view.gui.components.RoundedButton;
import com.flightreservation.view.gui.components.StyledTextField;
import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Admin dashboard for flight and system management
 */
public class AdminDashboard extends BasePanel {

    private AuthenticationController authController;
    private FlightController flightController;
    private BookingController bookingController;
    private Admin admin;

    private JPanel contentArea;
    private CardLayout contentLayout;

    private static final String FLIGHTS_PANEL = "flights";
    private static final String STATS_PANEL = "stats";

    public AdminDashboard(MainFrame mainFrame, AuthenticationController authController) {
        super(mainFrame);
        this.authController = authController;
        this.flightController = new FlightController();
        this.bookingController = new BookingController();
        this.admin = (Admin) authController.getCurrentUser();
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

        contentArea.add(createFlightsPanel(), FLIGHTS_PANEL);
        contentArea.add(createStatsPanel(), STATS_PANEL);

        add(contentArea, BorderLayout.CENTER);

        showContent(FLIGHTS_PANEL);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_LARGE,
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_LARGE));

        JLabel titleLabel = new JLabel("⚙ Admin Dashboard");
        titleLabel.setFont(AppTheme.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(AppTheme.PRIMARY);

        JLabel userLabel = new JLabel("Administrator: " + admin.getFirstName() + " " + admin.getLastName());
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

        sidebar.add(createNavButton("✈ Manage Flights", e -> showContent(FLIGHTS_PANEL)));
        sidebar.add(createNavButton("📊 Statistics", e -> showContent(STATS_PANEL)));

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
        if (panelName.equals(FLIGHTS_PANEL)) {
            refreshFlightsPanel();
        }
        contentLayout.show(contentArea, panelName);
    }

    private JPanel createFlightsPanel() {
        JPanel panel = createTitledSection("Flight Management");

        // Add flight button
        RoundedButton addButton = new RoundedButton("+ Add New Flight");
        addButton.setPreferredSize(new Dimension(180, 40));
        addButton.addActionListener(e -> showAddFlightDialog());
        panel.add(addButton);
        panel.add(Box.createVerticalStrut(AppTheme.PADDING_MEDIUM));

        return panel;
    }

    private void refreshFlightsPanel() {
        JPanel newPanel = createTitledSection("Flight Management");

        // Add flight button
        RoundedButton addButton = new RoundedButton("+ Add New Flight");
        addButton.setPreferredSize(new Dimension(180, 40));
        addButton.addActionListener(e -> showAddFlightDialog());
        newPanel.add(addButton);
        newPanel.add(Box.createVerticalStrut(AppTheme.PADDING_MEDIUM));

        // Flights table
        List<Flight> flights = flightController.getAllFlights();

        String[] columnNames = { "Flight #", "Airline", "From", "To", "Departure", "Arrival", "Price", "Seats",
                "Status", "" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };

        for (Flight flight : flights) {
            tableModel.addRow(new Object[] {
                    flight.getFlightNumber(),
                    flight.getAirlineName(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    flight.getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    "$" + String.format("%.2f", flight.getPrice()),
                    flight.getAvailableSeats() + "/" + flight.getTotalSeats(),
                    flight.getStatus(),
                    "Delete"
            });
        }

        JTable table = new JTable(tableModel);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(35);

        // Add delete button renderer
        table.getColumn("").setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            RoundedButton btn = new RoundedButton("Delete");
            btn.setBackground(AppTheme.ERROR);
            btn.setPreferredSize(new Dimension(80, 28));
            return btn;
        });

        table.getColumn("").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private RoundedButton button = new RoundedButton("Delete");

            {
                button.setBackground(AppTheme.ERROR);
                button.addActionListener(e -> {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Flight flight = flights.get(row);
                        if (showConfirmation(
                                "Are you sure you want to delete flight " + flight.getFlightNumber() + "?")) {
                            if (flightController.deleteFlight(flight.getFlightId())) {
                                showSuccess("Flight deleted successfully");
                                refreshFlightsPanel();
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
        scrollPane.setPreferredSize(new Dimension(950, 500));

        newPanel.add(scrollPane);

        // Replace old panel
        contentArea.remove(0);
        contentArea.add(newPanel, FLIGHTS_PANEL, 0);
    }

    private void showAddFlightDialog() {
        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10));

        JTextField flightNumberField = new JTextField();
        JTextField airlineField = new JTextField();
        JTextField originField = new JTextField();
        JTextField destField = new JTextField();
        JTextField departureField = new JTextField();
        JTextField arrivalField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField seatsField = new JTextField();

        panel.add(new JLabel("Flight Number:"));
        panel.add(flightNumberField);
        panel.add(new JLabel("Airline:"));
        panel.add(airlineField);
        panel.add(new JLabel("Origin:"));
        panel.add(originField);
        panel.add(new JLabel("Destination:"));
        panel.add(destField);
        panel.add(new JLabel("Departure (YYYY-MM-DD HH:MM):"));
        panel.add(departureField);
        panel.add(new JLabel("Arrival (YYYY-MM-DD HH:MM):"));
        panel.add(arrivalField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Total Seats:"));
        panel.add(seatsField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Flight", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Flight flight = new Flight();
                flight.setFlightNumber(flightNumberField.getText());
                flight.setAirlineName(airlineField.getText());
                flight.setOrigin(originField.getText());
                flight.setDestination(destField.getText());
                flight.setDepartureTime(
                        LocalDateTime.parse(departureField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                flight.setArrivalTime(
                        LocalDateTime.parse(arrivalField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                flight.setPrice(Double.parseDouble(priceField.getText()));
                flight.setTotalSeats(Integer.parseInt(seatsField.getText()));
                flight.setAvailableSeats(Integer.parseInt(seatsField.getText()));
                flight.setStatus(FlightStatus.SCHEDULED);

                if (flightController.addFlight(flight)) {
                    showSuccess("Flight added successfully");
                    refreshFlightsPanel();
                }
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage());
            }
        }
    }

    private JPanel createStatsPanel() {
        JPanel panel = createTitledSection("System Statistics");

        JPanel statsGrid = new JPanel(new GridLayout(3, 2, 20, 20));
        statsGrid.setBackground(AppTheme.SURFACE);

        List<Flight> flights = flightController.getAllFlights();
        List<Booking> bookings = bookingController.getAllBookings();

        double totalRevenue = bookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        statsGrid.add(createStatCard("Total Flights", String.valueOf(flights.size())));
        statsGrid.add(createStatCard("Total Bookings", String.valueOf(bookings.size())));
        statsGrid.add(createStatCard("Total Revenue", "$" + String.format("%.2f", totalRevenue)));
        statsGrid.add(createStatCard("Active Flights",
                String.valueOf(flights.stream().filter(f -> f.getStatus() == FlightStatus.SCHEDULED).count())));

        panel.add(statsGrid);

        return panel;
    }

    private JPanel createStatCard(String label, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.DIVIDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        valueLabel.setForeground(AppTheme.PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(AppTheme.FONT_BODY);
        labelText.setForeground(AppTheme.TEXT_SECONDARY);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(labelText);

        return card;
    }

    private void handleLogout() {
        if (showConfirmation("Are you sure you want to logout?")) {
            authController.logout();
            mainFrame.removePanel(MainFrame.ADMIN_DASHBOARD);
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        }
    }
}
