package adapter.interfaces;

import model.entity.Payment;
import model.enums.PaymentStatus;
import java.sql.SQLException;
import java.util.List;

/**
 * Adapter interface for payment data access operations.
 * Adapts application payment processing needs to the database interface.
 */
public interface PaymentDataAdapter {
    Payment create(Payment payment) throws SQLException;

    Payment findById(int paymentId) throws SQLException;

    Payment findByBookingId(int bookingId) throws SQLException;

    List<Payment> findAll() throws SQLException;

    boolean updateStatus(int paymentId, PaymentStatus status) throws SQLException;
}
