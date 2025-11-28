package com.flightreservation.dao.impl;

import com.flightreservation.dao.DatabaseConnection;
import com.flightreservation.dao.PaymentDAO;
import com.flightreservation.model.entity.Payment;
import com.flightreservation.model.enums.PaymentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private Connection connection;

    public PaymentDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Payment create(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, status, transaction_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getStatus().name());
            stmt.setString(5, payment.getTransactionId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                payment.setPaymentId(rs.getInt(1));
            }
        }
        return payment;
    }

    @Override
    public Payment findById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public Payment findByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE booking_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
        }
        return null;
    }

    @Override
    public List<Payment> findAll() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        }
        return payments;
    }

    @Override
    public boolean updateStatus(int paymentId, PaymentStatus status) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setBookingId(rs.getInt("booking_id"));
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        payment.setTransactionId(rs.getString("transaction_id"));
        return payment;
    }
}
