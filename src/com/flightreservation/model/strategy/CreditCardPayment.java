package com.flightreservation.model.strategy;

/**
 * Concrete strategy for Credit Card payment processing.
 */
public class CreditCardPayment implements PaymentStrategy {

    @Override
    public boolean processPayment(double amount, String transactionId) {
        System.out.println("\n💳 Processing Credit Card Payment...");
        System.out.println("   Transaction ID: " + transactionId);
        System.out.println("   Amount: $" + String.format("%.2f", amount));

        // Simulate credit card validation
        try {
            System.out.print("   Validating card details");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.println();

            // Simulate authorization
            System.out.println("   Authorizing with card issuer...");
            Thread.sleep(500);

            // Simulate successful processing
            System.out.println("   ✓ Credit Card payment authorized!");
            System.out.println("   ✓ Funds captured successfully!");

            return true;
        } catch (InterruptedException e) {
            System.err.println("   ✗ Payment processing interrupted.");
            return false;
        }
    }

    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
}
