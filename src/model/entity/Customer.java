package model.entity;

import model.enums.UserRole;
import java.time.LocalDate;

/**
 * Customer entity representing registered customers in the system.
 * Customers can search and book flights, manage their bookings, and receive
 * promotional notifications. Loyalty points are tracked for future rewards.
 */
public class Customer extends User {
    private LocalDate registrationDate;
    private boolean receiveNewsletter;

    public Customer() {
        super();
        this.setRole(UserRole.CUSTOMER);
        this.receiveNewsletter = true;
    }

    public Customer(String email, String password, String firstName, String lastName, String phone) {
        super(email, password, firstName, lastName, phone, UserRole.CUSTOMER);
        this.registrationDate = LocalDate.now();
        this.receiveNewsletter = true;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isReceiveNewsletter() {
        return receiveNewsletter;
    }

    public void setReceiveNewsletter(boolean receiveNewsletter) {
        this.receiveNewsletter = receiveNewsletter;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "userId=" + getUserId() +
                ", name='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
