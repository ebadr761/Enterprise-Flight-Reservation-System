//BookingDialog.java
package view;

import controller.BookingController;
import controller.PaymentController;
import model.entity.Booking;
import model.entity.Customer;
import model.entity.Flight;
import model.entity.Passenger;
import model.entity.Payment;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDialog extends JDialog {
    private Customer customer;
    private Flight flight;
    private BookingController bookingController;
    private PaymentController paymentController;

    private JSpinner passengersSpinner;
    private JPanel passengersPanel;
    private JComboBox<String> paymentMethodCombo;
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JPanel cardDetailsPanel;

    public BookingDialog(Frame owner, Customer customer, Flight flight, BookingController bookingController,
                         PaymentController paymentController) {
        super(owner, "Book Flight: " + flight.getFlightNumber(), true);
        this.customer = customer;
        this.flight = flight;
        this.bookingController = bookingController;
        this.paymentController = paymentController;

        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Flight Info
        JLabel flightInfo = new JLabel("<html><b>Flight:</b> " + flight.getFlightNumber() +
                "<br><b>From:</b> " + flight.getOrigin() + " <b>To:</b> " + flight.getDestination() +
                "<br><b>Price:</b> $" + String.format("%.2f", flight.getPrice()) + " per person</html>");
        mainPanel.add(flightInfo);
        mainPanel.add(Box.createVerticalStrut(10));

        // Number of Passengers
        JPanel numPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        numPanel.add(new JLabel("Number of Passengers:"));
        passengersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        numPanel.add(passengersSpinner);
        mainPanel.add(numPanel);

        // Passengers Details Panel
        passengersPanel = new JPanel();
        passengersPanel.setLayout(new BoxLayout(passengersPanel, BoxLayout.Y_AXIS));
        passengersSpinner.addChangeListener(e -> updatePassengerFields());
        updatePassengerFields(); // Initial call

        JScrollPane scrollPane = new JScrollPane(passengersPanel);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));

        // Payment Method
        JPanel paymentPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment"));

        paymentPanel.add(new JLabel("Method:"));
        String[] methods = { "Credit Card", "Debit Card", "PayPal" };
        paymentMethodCombo = new JComboBox<>(methods);
        paymentPanel.add(paymentMethodCombo);

        mainPanel.add(paymentPanel);

        // Card Details
        cardDetailsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        cardDetailsPanel.setBorder(BorderFactory.createTitledBorder("Card Details"));

        cardDetailsPanel.add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        cardDetailsPanel.add(cardNumberField);

        cardDetailsPanel.add(new JLabel("Expiry (MM/YY):"));
        expiryField = new JTextField();
        cardDetailsPanel.add(expiryField);

        cardDetailsPanel.add(new JLabel("CVV:"));
        cvvField = new JTextField();
        cardDetailsPanel.add(cvvField);

        mainPanel.add(cardDetailsPanel);

        // Toggle card details visibility
        paymentMethodCombo.addActionListener(e -> {
            String selected = (String) paymentMethodCombo.getSelectedItem();
            boolean isCard = selected.contains("Card");
            cardDetailsPanel.setVisible(isCard);
            revalidate();
        });

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton bookButton = new JButton("Confirm Booking");
        bookButton.addActionListener(e -> processBooking());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updatePassengerFields() {
        passengersPanel.removeAll();
        int count = (Integer) passengersSpinner.getValue();

        for (int i = 1; i <= count; i++) {
            JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
            p.setBorder(BorderFactory.createTitledBorder("Passenger " + i));

            p.add(new JLabel("First Name:"));
            p.add(new JTextField());

            p.add(new JLabel("Last Name:"));
            p.add(new JTextField());

            p.add(new JLabel("Passport:"));
            p.add(new JTextField());

            passengersPanel.add(p);
        }
        passengersPanel.revalidate();
        passengersPanel.repaint();
    }

    private void processBooking() {
        // Collect Passenger Info
        List<Passenger> passengers = new ArrayList<>();
        Component[] comps = passengersPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                JTextField fn = (JTextField) p.getComponent(1);
                JTextField ln = (JTextField) p.getComponent(3);
                JTextField pp = (JTextField) p.getComponent(5);

                if (fn.getText().isEmpty() || ln.getText().isEmpty() || pp.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all passenger details.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                passengers.add(new Passenger(fn.getText(), ln.getText(), pp.getText()));
            }
        }

        // Create Booking
        Booking booking = bookingController.createBooking(customer.getUserId(), flight.getFlightId(), passengers);
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "Failed to create booking.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Process Payment
        String method = (String) paymentMethodCombo.getSelectedItem();
        String cardNum = cardNumberField.getText();
        String expiry = expiryField.getText();
        String cvv = cvvField.getText();

        if (method.contains("Card")) {
            if (cardNum.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter card details.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Payment payment = paymentController.processPayment(booking.getBookingId(), booking.getTotalAmount(), method,
                cardNum, expiry, cvv);

        if (payment != null) {
            bookingController.confirmBooking(booking.getBookingId());

            JTextArea confirmationArea = new JTextArea(
                    bookingController.generateBookingConfirmation(booking.getBookingId()));
            confirmationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            confirmationArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(confirmationArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Booking Successful!", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Payment Failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
