package com.flightreservation.model.entity;

import com.flightreservation.model.enums.FlightStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Flight {
    private int flightId;
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private int totalSeats;
    private int availableSeats;
    private FlightStatus status;
    private String aircraftType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Flight() {
        this.status = FlightStatus.SCHEDULED;
    }

    public Flight(String flightNumber, String airline, String origin, String destination,
            LocalDateTime departureTime, LocalDateTime arrivalTime, double price,
            int totalSeats, String aircraftType) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.aircraftType = aircraftType;
        this.status = FlightStatus.SCHEDULED;
    }

    // Getters and Setters
    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean hasAvailableSeats(int numSeats) {
        return availableSeats >= numSeats;
    }

    public void reserveSeats(int numSeats) {
        if (hasAvailableSeats(numSeats)) {
            this.availableSeats -= numSeats;
        }
    }

    public void releaseSeats(int numSeats) {
        this.availableSeats = Math.min(totalSeats, availableSeats + numSeats);
    }

    public String getFormattedDepartureTime() {
        return departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedArrivalTime() {
        return arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("Flight %s | %s | %s -> %s | Departs: %s | Price: $%.2f | Seats: %d/%d",
                flightNumber, airline, origin, destination,
                getFormattedDepartureTime(), price, availableSeats, totalSeats);
    }
}
