package com.flightreservation.view.gui;

import com.flightreservation.controller.AuthenticationController;
import com.flightreservation.model.entity.User;
import com.flightreservation.view.gui.components.RoundedButton;
import com.flightreservation.view.gui.components.StyledPasswordField;
import com.flightreservation.view.gui.components.StyledTextField;
import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Login panel for user authentication
 */
public class LoginPanel extends BasePanel {

    private AuthenticationController authController;
    private StyledTextField emailField;
    private StyledPasswordField passwordField;
    private RoundedButton loginButton;
    private JLabel registerLink;

    public LoginPanel(MainFrame mainFrame) {
        super(mainFrame);
        this.authController = new AuthenticationController();
    }

    @Override
    protected void initializeComponents() {
        setLayout(new GridBagLayout());

        JPanel loginCard = createLoginCard();
        add(loginCard);
    }

    private JPanel createLoginCard() {
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
        card.setPreferredSize(new Dimension(400, 500));
        card.setMaximumSize(new Dimension(400, 500));

        // Logo/Title
        JLabel titleLabel = new JLabel("✈ Flight Reservation");
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(AppTheme.FONT_BODY);
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_XLARGE));

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(AppTheme.FONT_BODY);
        emailLabel.setForeground(AppTheme.TEXT_PRIMARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(emailLabel);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_SMALL));

        emailField = new StyledTextField("Enter your email", 20);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(350, 40));
        card.add(emailField);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_MEDIUM));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(AppTheme.FONT_BODY);
        passwordLabel.setForeground(AppTheme.TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(passwordLabel);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_SMALL));

        passwordField = new StyledPasswordField("Enter your password", 20);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(350, 40));
        card.add(passwordField);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_XLARGE));

        // Login button
        loginButton = new RoundedButton("Sign In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(350, 45));
        loginButton.setMaximumSize(new Dimension(350, 45));
        loginButton.addActionListener(e -> handleLogin());
        card.add(loginButton);

        card.add(Box.createVerticalStrut(AppTheme.PADDING_LARGE));

        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setBackground(AppTheme.SURFACE);
        registerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel registerText = new JLabel("Don't have an account?");
        registerText.setFont(AppTheme.FONT_BODY);
        registerText.setForeground(AppTheme.TEXT_SECONDARY);
        registerPanel.add(registerText);

        registerLink = new JLabel("Register");
        registerLink.setFont(AppTheme.FONT_BODY);
        registerLink.setForeground(AppTheme.PRIMARY);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showPanel(MainFrame.REGISTER_PANEL);
            }
        });
        registerPanel.add(registerLink);

        card.add(registerPanel);

        return card;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!validateRequired(email, "Email") || !validateRequired(password, "Password")) {
            return;
        }

        if (!validateEmail(email)) {
            return;
        }

        User user = authController.login(email, password);

        if (user != null) {
            // Route to appropriate dashboard based on role
            switch (user.getRole()) {
                case CUSTOMER:
                    CustomerDashboard customerDashboard = new CustomerDashboard(mainFrame, authController);
                    mainFrame.addPanel(customerDashboard, MainFrame.CUSTOMER_DASHBOARD);
                    mainFrame.showPanel(MainFrame.CUSTOMER_DASHBOARD);
                    break;
                case FLIGHT_AGENT:
                    FlightAgentDashboard agentDashboard = new FlightAgentDashboard(mainFrame, authController);
                    mainFrame.addPanel(agentDashboard, MainFrame.AGENT_DASHBOARD);
                    mainFrame.showPanel(MainFrame.AGENT_DASHBOARD);
                    break;
                case ADMIN:
                    AdminDashboard adminDashboard = new AdminDashboard(mainFrame, authController);
                    mainFrame.addPanel(adminDashboard, MainFrame.ADMIN_DASHBOARD);
                    mainFrame.showPanel(MainFrame.ADMIN_DASHBOARD);
                    break;
            }

            // Clear fields
            emailField.setText("");
            passwordField.setText("");
        }
    }
}
