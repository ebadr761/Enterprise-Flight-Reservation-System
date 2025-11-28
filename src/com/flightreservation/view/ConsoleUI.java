package com.flightreservation.view;

import java.util.Scanner;

public abstract class ConsoleUI {
    protected Scanner scanner;
    protected static final String SEPARATOR = "═══════════════════════════════════════════════════════════════";
    protected static final String LINE = "───────────────────────────────────────────────────────────────";

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    protected void clearScreen() {
        // Print multiple newlines to simulate clearing (works in most terminals)
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    protected void printHeader(String title) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  " + title);
        System.out.println(SEPARATOR);
    }

    protected void printSubHeader(String subtitle) {
        System.out.println("\n" + LINE);
        System.out.println("  " + subtitle);
        System.out.println(LINE);
    }

    protected String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    protected int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a number.");
            }
        }
    }

    protected double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid number.");
            }
        }
    }

    protected boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("✗ Please enter 'y' or 'n'.");
            }
        }
    }

    protected void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    protected void showMessage(String message) {
        System.out.println("\n" + message);
    }

    protected void showError(String error) {
        System.out.println("\n✗ Error: " + error);
    }

    protected void showSuccess(String message) {
        System.out.println("\n✓ " + message);
    }

    public abstract void display();
}
