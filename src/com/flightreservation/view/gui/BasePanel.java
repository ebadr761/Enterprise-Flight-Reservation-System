package com.flightreservation.view.gui;

import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Base panel providing common functionality for all GUI panels
 */
public abstract class BasePanel extends JPanel {

    protected MainFrame mainFrame;

    public BasePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(AppTheme.BACKGROUND);
        setLayout(new BorderLayout());
        initializeComponents();
    }

    /**
     * Initialize panel components - to be implemented by subclasses
     */
    protected abstract void initializeComponents();

    /**
     * Show error message dialog
     */
    protected void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show success message dialog
     */
    protected void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show warning message dialog
     */
    protected void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show confirmation dialog
     */
    protected boolean showConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm",
                JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Create a titled panel with padding
     */
    protected JPanel createTitledSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_MEDIUM,
                AppTheme.PADDING_MEDIUM));

        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(AppTheme.FONT_HEADING);
            titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(titleLabel);
            panel.add(Box.createVerticalStrut(AppTheme.PADDING_MEDIUM));
        }

        return panel;
    }

    /**
     * Validate required field
     */
    protected boolean validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            showError(fieldName + " is required");
            return false;
        }
        return true;
    }

    /**
     * Validate email format
     */
    protected boolean validateEmail(String email) {
        if (!email.contains("@")) {
            showError("Please enter a valid email address");
            return false;
        }
        return true;
    }
}
