package view;

import controller.AuthenticationController;
import model.entity.User;

import javax.swing.*;
import java.awt.*;

public class EditProfileDialog extends JDialog {
    private User user;
    private AuthenticationController authController;
    private boolean profileUpdated = false;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JPasswordField passwordField;

    public EditProfileDialog(Frame owner, User user, AuthenticationController authController) {
        super(owner, "Edit Profile", true);
        this.user = user;
        this.authController = authController;

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getOwner());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        firstNameField = new JTextField(user.getFirstName());
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        lastNameField = new JTextField(user.getLastName());
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(user.getPhone());
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("New Password (optional):"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveProfile() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authController.updateProfile(user, firstName, lastName, phone, password)) {
            profileUpdated = true;
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile. Please check your inputs.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isProfileUpdated() {
        return profileUpdated;
    }
}
