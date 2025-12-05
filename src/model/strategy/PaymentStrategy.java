package model.strategy;

/**
 * Strategy interface for payment processing.
 * Defines the contract for different payment methods.
 */
public interface PaymentStrategy {
    /**
     * Process a payment using the specific payment method.
     *
     * @param amount        The amount to be charged
     * @param transactionId The unique transaction identifier
     * @param cardNumber    The card number (for card payments, null for others)
     * @param expiryDate    The card expiry date (for card payments, null for
     *                      others)
     * @param cvv           The card CVV (for card payments, null for others)
     * @return true if payment is successful, false otherwise
     */
    boolean processPayment(double amount, String transactionId, String cardNumber, String expiryDate, String cvv);

    /**
     * Get the name of the payment method.
     *
     * @return The payment method name
     */
    String getPaymentMethodName();
}
