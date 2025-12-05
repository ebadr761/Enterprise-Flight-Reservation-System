package model.entity;

import model.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Payment entity representing payment transactions for bookings.
 * Tracks payment status, method, and generates unique transaction IDs.
 * Supports multiple payment methods through the Strategy pattern.
 */
public class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private String transactionId;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public Payment(int bookingId, double amount, String paymentMethod) {
        this();
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = generateTransactionId();
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Generates a unique transaction ID based on current timestamp.
     *
     * @return Transaction ID in format "TXN[timestamp]"
     */
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    /**
     * Returns formatted payment date for display purposes.
     *
     * @return Payment date in "yyyy-MM-dd HH:mm:ss" format
     */
    public String getFormattedPaymentDate() {
        return paymentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("Payment{id=%d, amount=$%.2f, method='%s', status=%s, txnId='%s'}",
                paymentId, amount, paymentMethod, status, transactionId);
    }
}
