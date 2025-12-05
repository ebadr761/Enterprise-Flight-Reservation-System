package util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation across the application.
 * Provides validation for email, phone, passport, and general input fields.
 */
public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z0-9]{6,12}$");

    /**
     * Validates email address format.
     *
     * @param email Email address to validate
     * @return true if email matches standard format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates phone number format (10-15 digits).
     *
     * @param phone Phone number to validate
     * @return true if phone number is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches();
    }

    /**
     * Validates passport number format (6-12 alphanumeric characters).
     *
     * @param passport Passport number to validate
     * @return true if passport format is valid, false otherwise
     */
    public static boolean isValidPassport(String passport) {
        return passport != null && PASSPORT_PATTERN.matcher(passport.toUpperCase()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isPositiveInteger(int value) {
        return value > 0;
    }
}
