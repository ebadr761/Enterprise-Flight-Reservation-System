package com.flightreservation.model.entity;

import java.time.LocalDate;

public class Promotion {
    private int promotionId;
    private String title;
    private String message;
    private LocalDate validFrom;
    private LocalDate validTo;
    private double discountPercentage;

    // Constructors
    public Promotion() {
    }

    public Promotion(String title, String message, LocalDate validFrom, LocalDate validTo, double discountPercentage) {
        this.title = title;
        this.message = message;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.discountPercentage = discountPercentage;
    }

    // Getters and Setters
    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(validFrom) && !today.isAfter(validTo);
    }

    @Override
    public String toString() {
        return String.format("Promotion: %s (%.0f%% off) | Valid: %s to %s\n%s",
                title, discountPercentage, validFrom, validTo, message);
    }
}
