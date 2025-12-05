package model.entity;

/**
 * Passenger entity representing individual passengers in a booking.
 * Each booking can have multiple passengers with their own details and seat
 * assignments.
 * Seat selection is optional and can be assigned later for an additional fee.
 */
public class Passenger {
    private int passengerId;
    private int bookingId;
    private String firstName;
    private String lastName;
    private String passportNumber;

    public Passenger() {
    }

    public Passenger(String firstName, String lastName, String passportNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    /**
     * Returns the passenger's full name.
     *
     * @return Full name in "FirstName LastName" format
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "name='" + getFullName() + '\'' +
                ", passport='" + passportNumber + '\'' +
                '}';
    }
}
