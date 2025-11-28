package com.flightreservation;

import com.flightreservation.dao.DatabaseConnection;
import com.flightreservation.view.MainMenuUI;

public class Main {
    public static void main(String[] args) {
        // Print welcome banner
        printBanner();

        // Initialize database connection
        System.out.println("Initializing Flight Reservation System...");
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();

        if (dbConnection.getConnection() == null) {
            System.err.println("\n✗ Failed to connect to database!");
            System.err.println("Please ensure MySQL is running and the database 'flight_reservation_db' exists.");
            System.err.println("Run the schema.sql file to create the database and tables.");
            System.exit(1);
        }

        // Start the application
        System.out.println("System initialized successfully!\n");

        MainMenuUI mainMenu = new MainMenuUI();
        mainMenu.display();

        // Cleanup on exit
        dbConnection.closeConnection();
    }

    private static void printBanner() {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                   ║");
        System.out.println("║     ✈✈✈  FLIGHT RESERVATION SYSTEM  ✈✈✈                         ║");
        System.out.println("║                                                                   ║");
        System.out.println("║     A Comprehensive Flight Booking Management System             ║");
        System.out.println("║                                                                   ║");
        System.out.println("║     Features:                                                     ║");
        System.out.println("║     • Multi-role access (Customer, Agent, Admin)                 ║");
        System.out.println("║     • Flight search and booking                                  ║");
        System.out.println("║     • Payment processing simulation                              ║");
        System.out.println("║     • Booking management and cancellation                        ║");
        System.out.println("║     • Flight schedule management                                 ║");
        System.out.println("║                                                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}
