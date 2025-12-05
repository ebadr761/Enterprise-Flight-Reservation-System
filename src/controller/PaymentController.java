package controller;

import adapter.interfaces.PaymentDataAdapter;
import adapter.database.PaymentDatabaseAdapter;
import model.entity.Payment;
import model.enums.PaymentStatus;
import model.strategy.*;

import java.sql.SQLException;

/**
 * Handles payment processing using the Strategy pattern.
 * Supports multiple payment methods (Credit Card, Debit Card, PayPal) and
 * manages payment status.
 */
public class PaymentController {
    private PaymentDataAdapter paymentDataAdapter;

    public PaymentController() {
        this.paymentDataAdapter = new PaymentDatabaseAdapter();
    }

    /**
     * Process payment using the Strategy pattern.
     * Selects appropriate payment strategy based on payment method.
     */
    public Payment processPayment(int bookingId, double amount, String paymentMethod,
                                  String cardNumber, String expiryDate, String cvv) {
        try {
            Payment payment = new Payment(bookingId, amount, paymentMethod);
            payment.setStatus(PaymentStatus.PENDING);
            payment = paymentDataAdapter.create(payment);

            // Select payment strategy based on payment method
            PaymentStrategy strategy = getPaymentStrategy(paymentMethod);

            if (strategy == null) {
                System.err.println("✗ Invalid payment method: " + paymentMethod);
                payment.setStatus(PaymentStatus.FAILED);
                paymentDataAdapter.updateStatus(payment.getPaymentId(), PaymentStatus.FAILED);
                return null;
            }

            // Process payment using the selected strategy
            boolean success = strategy.processPayment(amount, payment.getTransactionId(),
                    cardNumber, expiryDate, cvv);

            if (success) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentDataAdapter.updateStatus(payment.getPaymentId(), PaymentStatus.COMPLETED);
                System.out.println("✓ Payment successful using " + strategy.getPaymentMethodName() + "!");
                return payment;
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentDataAdapter.updateStatus(payment.getPaymentId(), PaymentStatus.FAILED);
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
            return paymentDataAdapter.findByBookingId(bookingId);
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving payment: " + e.getMessage());
            return null;
        }
    }

    public boolean refundPayment(int paymentId) {
        try {
            Payment payment = paymentDataAdapter.findById(paymentId);
            if (payment == null) {
                System.out.println("✗ Payment not found.");
                return false;
            }

            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                System.out.println("✗ Cannot refund payment that is not completed.");
                return false;
            }

            boolean success = paymentDataAdapter.updateStatus(paymentId, PaymentStatus.REFUNDED);
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
