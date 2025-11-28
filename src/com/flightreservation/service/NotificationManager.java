package com.flightreservation.service;

import com.flightreservation.model.entity.Customer;
import com.flightreservation.model.observer.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Notification Manager - Subject in the Observer pattern.
 * Manages notification observers and sends notifications to customers.
 * Implements Singleton pattern for global access.
 */
public class NotificationManager {
    private static NotificationManager instance;
    private List<NotificationObserver> observers;
    private boolean initialized = false;

    private NotificationManager() {
        this.observers = new ArrayList<>();
    }

    /**
     * Get the singleton instance of NotificationManager.
     */
    public static NotificationManager getInstance() {
        if (instance == null) {
            synchronized (NotificationManager.class) {
                if (instance == null) {
                    instance = new NotificationManager();
                    instance.initializeObservers();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize default observers (called once during singleton creation).
     */
    private void initializeObservers() {
        if (!initialized) {
            attach(new SMSNotification());
            attach(new EmailNotification());
            attach(new NewsletterNotification());
            initialized = true;
        }
    }

    /**
     * Attach an observer to the notification system.
     * Prevents duplicate observers of the same type.
     */
    public void attach(NotificationObserver observer) {
        // Check if an observer of this type already exists
        for (NotificationObserver existing : observers) {
            if (existing.getClass().equals(observer.getClass())) {
                return; // Already registered, skip
            }
        }

        observers.add(observer);
        if (initialized) {
            System.out.println("✓ " + observer.getChannelName() + " notification channel registered.");
        }
    }

    /**
     * Detach an observer from the notification system.
     */
    public void detach(NotificationObserver observer) {
        observers.remove(observer);
        System.out.println("✓ " + observer.getChannelName() + " notification channel unregistered.");
    }

    /**
     * Notify all observers with a message for a specific customer.
     */
    public void notifyObservers(String message, Customer customer) {
        if (customer == null) {
            System.err.println("✗ Cannot send notification: Customer is null.");
            return;
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📢 Sending notifications to " + customer.getFullName() + "...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (NotificationObserver observer : observers) {
            observer.update(message, customer);
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Notify all observers with a message for multiple customers.
     * Useful for promotional campaigns and newsletters.
     */
    public void notifyMultipleCustomers(String message, List<Customer> customers) {
        if (customers == null || customers.isEmpty()) {
            System.err.println("✗ Cannot send notification: No customers provided.");
            return;
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📢 Broadcasting to " + customers.size() + " customers...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (Customer customer : customers) {
            for (NotificationObserver observer : observers) {
                observer.update(message, customer);
            }
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✓ Broadcast complete!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    /**
     * Get the number of registered observers.
     */
    public int getObserverCount() {
        return observers.size();
    }
}
