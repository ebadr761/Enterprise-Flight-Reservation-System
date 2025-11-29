package com.flightreservation.view.gui;

import com.flightreservation.view.gui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame that manages panel switching
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Panel names
    public static final String LOGIN_PANEL = "login";
    public static final String REGISTER_PANEL = "register";
    public static final String CUSTOMER_DASHBOARD = "customer";
    public static final String AGENT_DASHBOARD = "agent";
    public static final String ADMIN_DASHBOARD = "admin";

    public MainFrame() {
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Flight Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppTheme.BACKGROUND);

        // Add panels
        contentPanel.add(new LoginPanel(this), LOGIN_PANEL);
        contentPanel.add(new RegistrationPanel(this), REGISTER_PANEL);

        add(contentPanel);

        // Show login panel initially
        showPanel(LOGIN_PANEL);
    }

    /**
     * Switch to specified panel
     */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    /**
     * Add a new panel to the card layout
     */
    public void addPanel(JPanel panel, String panelName) {
        contentPanel.add(panel, panelName);
    }

    /**
     * Remove a panel from the card layout
     */
    public void removePanel(String panelName) {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(panelName)) {
                contentPanel.remove(comp);
                break;
            }
        }
    }
}
