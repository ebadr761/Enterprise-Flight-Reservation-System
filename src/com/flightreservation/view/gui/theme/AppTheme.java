package com.flightreservation.view.gui.theme;

import java.awt.*;

/**
 * Centralized theme colors and fonts for the Flight Reservation System GUI
 */
public class AppTheme {

    // Primary Colors
    public static final Color PRIMARY = new Color(33, 150, 243); // Blue
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);
    public static final Color PRIMARY_LIGHT = new Color(100, 181, 246);

    // Accent Colors
    public static final Color ACCENT = new Color(255, 87, 34); // Orange
    public static final Color SUCCESS = new Color(76, 175, 80); // Green
    public static final Color WARNING = new Color(255, 152, 0); // Amber
    public static final Color ERROR = new Color(244, 67, 54); // Red

    // Neutral Colors
    public static final Color BACKGROUND = new Color(250, 250, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    public static final Color DIVIDER = new Color(224, 224, 224);

    // Component Colors
    public static final Color HOVER = new Color(245, 245, 245);
    public static final Color SELECTED = new Color(227, 242, 253);
    public static final Color DISABLED = new Color(189, 189, 189);

    // Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);

    // Spacing
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    public static final int PADDING_XLARGE = 32;

    // Border Radius
    public static final int RADIUS_SMALL = 4;
    public static final int RADIUS_MEDIUM = 8;
    public static final int RADIUS_LARGE = 12;

    private AppTheme() {
        // Utility class
    }
}
