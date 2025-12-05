package model.entity;
import model.enums.UserRole;

/**
 * Administrator entity representing system administrators.
 * Admins have full system access to manage flights, view statistics,
 * update flight statuses, and oversee all system operations.
 */
public class Admin extends User {
    private int adminLevel;
    private String permissions;

    public Admin() {
        super();
        this.setRole(UserRole.ADMIN);
    }

    public Admin(String email, String password, String firstName, String lastName, String phone, int adminLevel) {
        super(email, password, firstName, lastName, phone, UserRole.ADMIN);
        this.adminLevel = adminLevel;
        this.permissions = "ALL";
    }

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
