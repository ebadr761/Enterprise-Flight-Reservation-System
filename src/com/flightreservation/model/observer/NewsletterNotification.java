package com.flightreservation.model.observer;

import com.flightreservation.model.entity.Customer;

/**
 * Concrete observer for monthly newsletter notifications.
 * Only sends to customers who have opted in to receive promotions.
 */
public class NewsletterNotification implements NotificationObserver {

    @Override
    public void update(String message, Customer customer) {
        // Only send newsletter to customers who opted in
        if (customer.isReceivePromotions()) {
            sendNewsletter(customer.getEmail(), customer.getFullName(), message);
        }
    }

    /**
     * Simulate sending a newsletter.
     */
    private void sendNewsletter(String emailAddress, String customerName, String message) {
        System.out.println("\n📰 Monthly Newsletter Sent:");
        System.out.println("   To: " + emailAddress);
        System.out.println("   Recipient: " + customerName);
        System.out.println("   Subject: Monthly Flight Deals & Updates");
        System.out.println("   Content: " + message);
        System.out.println("   Status: ✓ Sent");
    }

    @Override
    public String getChannelName() {
        return "Newsletter";
    }
}
