package com.flightreservation.model.strategy;

/**
 * Concrete strategy for Debit Card payment processing.
 */
public class DebitCardPayment implements PaymentStrategy {

    @Override
    public boolean processPayment(double amount, String transactionId) {
        System.out.println("\n💳 Processing Debit Card Payment...");
        System.out.println("   Transaction ID: " + transactionId);
        System.out.println("   Amount: $" + String.format("%.2f", amount));

        // Simulate debit card validation
        try {
            System.out.print("   Checking account balance");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.println();

            // Simulate PIN verification
            System.out.println("   Verifying PIN...");
            Thread.sleep(400);

            // Simulate successful processing
            System.out.println("   ✓ Debit Card payment verified!");
            System.out.println("   ✓ Amount debited from account!");

            return true;
        } catch (InterruptedException e) {
            System.err.println("   ✗ Payment processing interrupted.");
            return false;
        }
    }

    @Override
    public String getPaymentMethodName() {
        return "Debit Card";
    }
}
