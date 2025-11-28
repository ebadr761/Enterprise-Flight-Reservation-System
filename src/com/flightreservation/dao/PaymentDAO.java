package com.flightreservation.dao;

import com.flightreservation.model.entity.Payment;
import com.flightreservation.model.enums.PaymentStatus;
import java.sql.SQLException;
import java.util.List;

public interface PaymentDAO {
    Payment create(Payment payment) throws SQLException;

    Payment findById(int paymentId) throws SQLException;

    Payment findByBookingId(int bookingId) throws SQLException;

    List<Payment> findAll() throws SQLException;

    boolean updateStatus(int paymentId, PaymentStatus status) throws SQLException;
}
