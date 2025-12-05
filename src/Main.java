import adapter.database.DatabaseConnection;
import javax.swing.UIManager;

/**
 * Main entry point for the Flight Reservation System application.
 * Initializes the database connection, sets up the GUI, and launches the
 * application.
 * The system supports three user roles: Customer, Flight Agent, and
 * Administrator.
 */
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
        System.out.println("Launching GUI...");

        // Launch GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            view.MainFrame mainFrame = new view.MainFrame();
            mainFrame.setVisible(true);
        });

        // Cleanup on exit
        dbConnection.closeConnection();
    }

    /**
     * Prints a welcome banner to the console displaying system information and
     * features.
     */
    private static void printBanner() {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                   ║");
        System.out.println("║     ✈✈✈  FLIGHT RESERVATION SYSTEM  ✈✈✈                           ║");
        System.out.println("║                                                                   ║");
        System.out.println("║     A Comprehensive Flight Booking Management System              ║");
        System.out.println("║                                                                   ║");
        System.out.println("║     Features:                                                     ║");
        System.out.println("║     • Multi-role access (Customer, Agent, Admin)                  ║");
        System.out.println("║     • Flight search and booking                                   ║");
        System.out.println("║     • Payment processing simulation                               ║");
        System.out.println("║     • Booking management and cancellation                         ║");
        System.out.println("║     • Flight schedule management                                  ║");
        System.out.println("║                                                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}
