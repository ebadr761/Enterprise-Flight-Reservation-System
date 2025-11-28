package com.flightreservation.model.entity;

import com.flightreservation.model.enums.UserRole;
import java.time.LocalDate;

public class Customer extends User {
    private int loyaltyPoints;
    private LocalDate registrationDate;
    private boolean receivePromotions;

    // Constructors
    public Customer() {
        super();
        this.setRole(UserRole.CUSTOMER);
        this.receivePromotions = true;
    }

    public Customer(String email, String password, String firstName, String lastName, String phone) {
        super(email, password, firstName, lastName, phone, UserRole.CUSTOMER);
        this.loyaltyPoints = 0;
        this.registrationDate = LocalDate.now();
        this.receivePromotions = true;
    }

    // Getters and Setters
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isReceivePromotions() {
        return receivePromotions;
    }

    public void setReceivePromotions(boolean receivePromotions) {
        this.receivePromotions = receivePromotions;
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "userId=" + getUserId() +
                ", name='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
