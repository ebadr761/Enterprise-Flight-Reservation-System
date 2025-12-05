package model.observer;
import model.entity.Customer;
/**
 * Concrete observer for Email notifications.
 */
public class EmailNotification implements NotificationObserver {

    @Override
    public void update(String message, Customer customer) {
        sendEmail(customer.getEmail(), message);
    }

    /**
     * Simulate sending an email notification.
     */
    private void sendEmail(String emailAddress, String message) {
        System.out.println("\n📧 Email Notification Sent:");
        System.out.println("   To: " + emailAddress);
        System.out.println("   Subject: Flight Reservation Update");
        System.out.println("   Message: " + message);
        System.out.println("   Status: ✓ Sent");
    }

    @Override
    public String getChannelName() {
        return "Email";
    }
}
