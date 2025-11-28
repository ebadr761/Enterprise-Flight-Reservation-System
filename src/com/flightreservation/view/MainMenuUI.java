package com.flightreservation.view;

import com.flightreservation.controller.AuthenticationController;
import com.flightreservation.model.entity.User;
import com.flightreservation.model.enums.UserRole;

public class MainMenuUI extends ConsoleUI {
    private AuthenticationController authController;

    public MainMenuUI() {
        super();
        this.authController = new AuthenticationController();
    }

    @Override
    public void display() {
        while (true) {
            clearScreen();
            printWelcomeBanner();
            printMenu();

            int choice = readInt("\nEnter your choice: ");

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegistration();
                    break;
                case 3:
                    System.out.println("\n✈ Thank you for using Flight Reservation System! Goodbye! ✈");
                    System.exit(0);
                    break;
                default:
                    showError("Invalid choice. Please try again.");
                    pause();
            }
        }
    }

    private void printWelcomeBanner() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║          ✈  FLIGHT RESERVATION SYSTEM  ✈                     ║");
        System.out.println("║                                                               ║");
        System.out.println("║          Your Journey Begins Here                             ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }

    private void printMenu() {
        System.out.println("\n┌───────────────────────────────────────────────────────────────┐");
        System.out.println("│  1. Login                                                     │");
        System.out.println("│  2. Register as Customer                                      │");
        System.out.println("│  3. Exit                                                      │");
        System.out.println("└───────────────────────────────────────────────────────────────┘");
    }

    private void handleLogin() {
        printHeader("LOGIN");

        String email = readString("Email: ");
        String password = readString("Password: ");

        User user = authController.login(email, password);

        if (user != null) {
            pause();
            routeToRoleBasedUI(user);
        } else {
            pause();
        }
    }

    private void handleRegistration() {
        printHeader("CUSTOMER REGISTRATION");

        String email = readString("Email: ");
        String password = readString("Password (min 6 characters): ");
        String firstName = readString("First Name: ");
        String lastName = readString("Last Name: ");
        String phone = readString("Phone Number: ");

        var customer = authController.registerCustomer(email, password, firstName, lastName, phone);

        if (customer != null) {
            showSuccess("Registration successful! You can now login.");
        }
        pause();
    }

    private void routeToRoleBasedUI(User user) {
        switch (user.getRole()) {
            case CUSTOMER:
                CustomerUI customerUI = new CustomerUI(authController);
                customerUI.display();
                break;
            case FLIGHT_AGENT:
                FlightAgentUI agentUI = new FlightAgentUI(authController);
                agentUI.display();
                break;
            case ADMIN:
                AdminUI adminUI = new AdminUI(authController);
                adminUI.display();
                break;
        }
    }
}
