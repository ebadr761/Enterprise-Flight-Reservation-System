package view;

import controller.AuthenticationController;
import model.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login panel providing authentication and customer registration interface.
 * Displays login form and handles user authentication.
 */
public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private AuthenticationController authController;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame, AuthenticationController authController) {
        this.mainFrame = mainFrame;
        this.authController = authController;

        setLayout(new GridBagLayout());
        setBackground(new Color(240, 248, 255));

        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Flight Reservation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        JButton registerButton = new JButton("Register New Account");
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Color.BLUE);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistrationDialog();
            }
        });
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authController.login(email, password) != null) {
            User user = authController.getCurrentUser();
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getFullName() + "!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            mainFrame.loginSuccess(user);

            emailField.setText("");
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register New Account", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField phoneField = new JTextField();

        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passField);
        dialog.add(new JLabel("First Name:"));
        dialog.add(firstNameField);
        dialog.add(new JLabel("Last Name:"));
        dialog.add(lastNameField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authController.registerCustomer(email, password, firstName, lastName, phone) != null) {
                JOptionPane.showMessageDialog(dialog, "Registration successful! Please login.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Registration failed. Email might be taken or invalid input.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(registerButton);
        dialog.setVisible(true);
    }
}
