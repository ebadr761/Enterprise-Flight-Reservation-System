package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date and time formatting and parsing operations.
 * Provides consistent date/time handling across the application.
 */
public class DateTimeUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    /**
     * Parses a date string in yyyy-MM-dd format.
     *
     * @param dateStr Date string to parse
     * @return LocalDate object or null if parsing fails
     */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DISPLAY_FORMATTER) : "";
    }

    public static boolean isValidDate(String dateStr) {
        return parseDate(dateStr) != null;
    }

    public static boolean isValidDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr) != null;
    }
}
