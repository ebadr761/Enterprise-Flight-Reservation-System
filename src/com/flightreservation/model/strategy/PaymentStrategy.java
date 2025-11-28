package com.flightreservation.model.strategy;

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
     * @return true if payment is successful, false otherwise
     */
    boolean processPayment(double amount, String transactionId);

    /**
     * Get the name of the payment method.
     * 
     * @return The payment method name
     */
    String getPaymentMethodName();
}
