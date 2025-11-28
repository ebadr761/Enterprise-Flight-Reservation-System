package com.flightreservation.model.observer;

import com.flightreservation.model.entity.Customer;

/**
 * Observer interface for the notification system.
 * Concrete observers will implement this to receive notifications.
 */
public interface NotificationObserver {
    /**
     * Update method called when a notification is sent.
     * 
     * @param message  The notification message
     * @param customer The customer to notify
     */
    void update(String message, Customer customer);

    /**
     * Get the name of the notification channel.
     * 
     * @return The channel name
     */
    String getChannelName();
}
