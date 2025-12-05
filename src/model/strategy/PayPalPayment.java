package model.strategy;
/**
 * Concrete strategy for PayPal payment processing.
 */
public class PayPalPayment implements PaymentStrategy {

    @Override
    public boolean processPayment(double amount, String transactionId, String cardNumber, String expiryDate,
                                  String cvv) {
        // Card details not needed for PayPal
        System.out.println("\n💰 Processing PayPal Payment...");
        System.out.println("   Transaction ID: " + transactionId);
        System.out.println("   Amount: $" + String.format("%.2f", amount));

        // Simulate PayPal authentication
        try {
            System.out.print("   Authenticating with PayPal");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.println();

            // Simulate payment authorization
            System.out.println("   Authorizing payment...");
            Thread.sleep(500);

            // Simulate successful processing
            System.out.println("   ✓ PayPal payment authorized!");
            System.out.println("   ✓ Payment completed via PayPal!");

            return true;
        } catch (InterruptedException e) {
            System.err.println("   ✗ Payment processing interrupted.");
            return false;
        }
    }

    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
}
