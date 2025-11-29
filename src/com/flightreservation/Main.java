package com.flightreservation;

import com.flightreservation.dao.DatabaseConnection;
import com.flightreservation.view.gui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize database connection
        System.out.println("Initializing Flight Reservation System...");
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        if (dbConnection.getConnection() == null) {
            System.err.println("\n✗ Failed to connect to database!");
            System.err.println("Please ensure MySQL is running and the database 'flight_reservation_db' exists.");
            System.err.println("Run the schema.sql file to create the database and tables.");

            // Show error dialog
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to database!\n\n" +
                            "Please ensure MySQL is running and the database exists.\n" +
                            "Run the schema.sql file to create the database and tables.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        System.out.println("System initialized successfully!");
        System.out.println("Launching GUI...\n");

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });

        // Add shutdown hook for cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            dbConnection.closeConnection();
        }));
    }
}
