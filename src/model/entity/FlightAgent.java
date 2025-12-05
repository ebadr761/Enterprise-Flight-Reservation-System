package model.entity;

import model.enums.UserRole;
import java.time.LocalDate;

/**
 * Flight Agent entity representing airline staff members.
 * Flight agents can manage customer bookings, view flight schedules,
 * allocate seats, and assist customers with booking-related operations.
 */
public class FlightAgent extends User {
    private String employeeId;
    private String department;
    private LocalDate hireDate;

    public FlightAgent() {
        super();
        this.setRole(UserRole.FLIGHT_AGENT);
    }

    public FlightAgent(String email, String password, String firstName, String lastName, String phone,
                       String employeeId, String department) {
        super(email, password, firstName, lastName, phone, UserRole.FLIGHT_AGENT);
        this.employeeId = employeeId;
        this.department = department;
        this.hireDate = LocalDate.now();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return "FlightAgent{" +
                "userId=" + getUserId() +
                ", name='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
