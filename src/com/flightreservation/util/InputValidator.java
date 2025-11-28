package com.flightreservation.util;

import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z0-9]{6,12}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches();
    }

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
