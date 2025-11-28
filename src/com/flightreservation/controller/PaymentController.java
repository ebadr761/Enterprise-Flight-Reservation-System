package com.flightreservation.controller;

import com.flightreservation.dao.PaymentDAO;
import com.flightreservation.dao.impl.PaymentDAOImpl;
import com.flightreservation.model.entity.Payment;
import com.flightreservation.model.enums.PaymentStatus;
import com.flightreservation.model.strategy.*;

import java.sql.SQLException;

public class PaymentController {
    private PaymentDAO paymentDAO;

    public PaymentController() {
        this.paymentDAO = new PaymentDAOImpl();
    }

    /**
     * Process payment using the Strategy pattern.
     * Selects appropriate payment strategy based on payment method.
     */
    public Payment processPayment(int bookingId, double amount, String paymentMethod) {
        try {
            Payment payment = new Payment(bookingId, amount, paymentMethod);
            payment.setStatus(PaymentStatus.PENDING);
            payment = paymentDAO.create(payment);

            // Select payment strategy based on payment method
            PaymentStrategy strategy = getPaymentStrategy(paymentMethod);

            if (strategy == null) {
                System.err.println("✗ Invalid payment method: " + paymentMethod);
                payment.setStatus(PaymentStatus.FAILED);
                paymentDAO.updateStatus(payment.getPaymentId(), PaymentStatus.FAILED);
                return null;
            }

            // Process payment using the selected strategy
            boolean success = strategy.processPayment(amount, payment.getTransactionId());

            if (success) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentDAO.updateStatus(payment.getPaymentId(), PaymentStatus.COMPLETED);
                System.out.println("✓ Payment successful using " + strategy.getPaymentMethodName() + "!");
                return payment;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentDAO.updateStatus(payment.getPaymentId(), PaymentStatus.FAILED);
                System.err.println("✗ Payment failed.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("✗ Payment error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Factory method to get the appropriate payment strategy.
     */
    private PaymentStrategy getPaymentStrategy(String paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }

        String method = paymentMethod.toLowerCase().trim();

        if (method.contains("credit")) {
            return new CreditCardPayment();
        } else if (method.contains("debit")) {
            return new DebitCardPayment();
        } else if (method.contains("paypal")) {
            return new PayPalPayment();
        }

        return null;
    }

    public Payment getPaymentByBookingId(int bookingId) {
        try {
            return paymentDAO.findByBookingId(bookingId);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving payment: " + e.getMessage());
            return null;
        }
    }

    public boolean refundPayment(int paymentId) {
        try {
            Payment payment = paymentDAO.findById(paymentId);
            if (payment == null) {
                System.out.println("✗ Payment not found.");
                return false;
            }

            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                System.out.println("✗ Cannot refund payment that is not completed.");
                return false;
            }

            boolean success = paymentDAO.updateStatus(paymentId, PaymentStatus.REFUNDED);
            if (success) {
                System.out.println("✓ Payment refunded successfully!");
            }
            return success;
        } catch (SQLException e) {
            System.err.println("✗ Error refunding payment: " + e.getMessage());
            return false;
        }
    }
}
