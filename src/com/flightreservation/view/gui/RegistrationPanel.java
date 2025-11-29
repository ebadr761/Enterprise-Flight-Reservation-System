package com.flightreservation.view.gui;

import com.flightreservation.controller.AuthenticationController;
import com.flightreservation.model.entity.Customer;
import com.flightreservation.view.gui.components.RoundedButton;
import com.flightreservation.view.gui.components.StyledPasswordField;
import com.flightreservation.view.gui.components.StyledTextField;
import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Registration panel for new customers
 */
public class RegistrationPanel extends BasePanel {

    private AuthenticationController authController;
    private StyledTextField emailField;
    private StyledPasswordField passwordField;
    private StyledTextField firstNameField;
    private StyledTextField lastNameField;
    private StyledTextField phoneField;
    private RoundedButton registerButton;
    private JLabel loginLink;

    public RegistrationPanel(MainFrame mainFrame) {
        super(mainFrame);
        this.authController = new AuthenticationController();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new GridBagLayout());

        JPanel registerCard = createRegisterCard();
        add(registerCard);
    }

    private JPanel createRegisterCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(AppTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.DIVIDER, 1),
                BorderFactory.createEmptyBorder(
                        AppTheme.PADDING_XLARGE,
                        AppTheme.PADDING_XLARGE,
                        AppTheme.PADDING_XLARGE,
                        AppTheme.PADDING_XLARGE)));
        card.setPreferredSize(new Dimension(450, 600));
        card.setMaximumSize(new Dimension(450, 600));

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Register as a new customer");
        subtitleLabel.setFont(AppTheme.FONT_BODY);
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_LARGE));

        // First Name
        addFormField(card, "First Name", firstNameField = new StyledTextField("Enter first name", 20));

        // Last Name
        addFormField(card, "Last Name", lastNameField = new StyledTextField("Enter last name", 20));

        // Email
        addFormField(card, "Email", emailField = new StyledTextField("Enter email address", 20));

        // Phone
        addFormField(card, "Phone", phoneField = new StyledTextField("Enter phone number", 20));

        // Password
        addFormField(card, "Password", passwordField = new StyledPasswordField("Minimum 6 characters", 20));

        card.add(Box.createVerticalStrut(AppTheme.PADDING_LARGE));

        // Register button
        registerButton = new RoundedButton("Create Account");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setPreferredSize(new Dimension(380, 45));
        registerButton.setMaximumSize(new Dimension(380, 45));
        registerButton.addActionListener(e -> handleRegistration());
        card.add(registerButton);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_MEDIUM));

        // Login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setBackground(AppTheme.SURFACE);
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginText = new JLabel("Already have an account?");
        loginText.setFont(AppTheme.FONT_BODY);
        loginText.setForeground(AppTheme.TEXT_SECONDARY);
        loginPanel.add(loginText);

        loginLink = new JLabel("Sign In");
        loginLink.setFont(AppTheme.FONT_BODY);
        loginLink.setForeground(AppTheme.PRIMARY);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showPanel(MainFrame.LOGIN_PANEL);
            }
        });
        loginPanel.add(loginLink);

        card.add(loginPanel);

        return card;
    }

    private void addFormField(JPanel parent, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.FONT_BODY);
        label.setForeground(AppTheme.TEXT_PRIMARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        parent.add(label);

        parent.add(Box.createVerticalStrut(AppTheme.PADDING_SMALL));

        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(380, 40));
        parent.add(field);

        parent.add(Box.createVerticalStrut(AppTheme.PADDING_MEDIUM));
    }

    private void handleRegistration() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validation
        if (!validateRequired(email, "Email") ||
                !validateRequired(password, "Password") ||
                !validateRequired(firstName, "First Name") ||
                !validateRequired(lastName, "Last Name") ||
                !validateRequired(phone, "Phone")) {
            return;
        }

        if (!validateEmail(email)) {
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        Customer customer = authController.registerCustomer(email, password, firstName, lastName, phone);

        if (customer != null) {
            showSuccess("Registration successful! You can now sign in.");

            // Clear fields
            emailField.setText("");
            passwordField.setText("");
            firstNameField.setText("");
            lastNameField.setText("");
            phoneField.setText("");

            // Navigate to login
            mainFrame.showPanel(MainFrame.LOGIN_PANEL);
        }
    }
}
