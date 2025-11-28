package com.flightreservation.model.observer;

import com.flightreservation.model.entity.Customer;

/**
 * Concrete observer for SMS notifications.
 */
public class SMSNotification implements NotificationObserver {

    @Override
    public void update(String message, Customer customer) {
        sendSMS(customer.getPhone(), message);
    }

    /**
     * Simulate sending an SMS notification.
     */
    private void sendSMS(String phoneNumber, String message) {
        System.out.println("\n📱 SMS Notification Sent:");
        System.out.println("   To: " + phoneNumber);
        System.out.println("   Message: " + message);
        System.out.println("   Status: ✓ Delivered");
    }

    @Override
    public String getChannelName() {
        return "SMS";
    }
}
