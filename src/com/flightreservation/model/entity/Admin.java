package com.flightreservation.model.entity;

import com.flightreservation.model.enums.UserRole;

public class Admin extends User {
    private int adminLevel;
    private String permissions;

    // Constructors
    public Admin() {
        super();
        this.setRole(UserRole.ADMIN);
    }

    public Admin(String email, String password, String firstName, String lastName, String phone, int adminLevel) {
        super(email, password, firstName, lastName, phone, UserRole.ADMIN);
        this.adminLevel = adminLevel;
        this.permissions = "ALL";
    }

    // Getters and Setters
    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userId=" + getUserId() +
                ", name='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", adminLevel=" + adminLevel +
                '}';
    }
}
