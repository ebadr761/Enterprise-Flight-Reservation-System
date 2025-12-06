# ✈️ Flight Reservation System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Design Patterns](https://img.shields.io/badge/Design%20Patterns-Implemented-green?style=for-the-badge)

A comprehensive, role-based Flight Reservation System built with **Java Swing** and **MySQL**. This project demonstrates advanced Object-Oriented Programming (OOP) principles and the practical application of multiple standard Design Patterns within a robust Model-View-Controller (MVC) architecture.

---

## 📖 Table of Contents
- [Features](#-features)
- [System Architecture](#-system-architecture)
- [Design Patterns](#-design-patterns)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Usage](#-usage)

---

## The Main Features:

### 👤 Role-Based Access
*   **Customer Dashboard**: Search for flights, book tickets using various payment methods, view booking history, and manage personal profile settings.
*   **Flight Agent Dashboard**: Assist customers with bookings, view assigned flights, and manage reservations on behalf of passengers.
*   **Admin Dashboard**: Complete system control including Flight CRUD operations (Create, Read, Update, Delete), user management, and system monitoring.

### 🛠 Core Functionality
*   **Flight Management**: Real-time flight searching, filtering, and scheduling capabilities.
*   **Secure Booking System**: Seat reservation logic with instant ticket generation.
*   **Flexible Payments**: Multiple payment gateways (Credit Card, Debit Card, PayPal) supported via the Strategy pattern.
*   **Smart Notifications**: Automated booking confirmations and updates via Email, SMS, or Newsletter (Observer pattern).

---

## 🏗 System Architecture

The application adheres to the **MVC (Model-View-Controller)** architectural pattern, ensuring a clean separation of concerns:

*   **Model (`src/model`)**: Encapsulates business logic and data entities (e.g., `User`, `Flight`, `Booking`).
*   **View (`src/view`)**: Interactive Java Swing UI components (Dashboards, Dialogs) providing a responsive user experience.
*   **Controller (`src/controller`)**: Orchestrates data flow between the Model and View, handling user interactions and business rules.

---

## 🎨 Design Patterns

This project serves as a detailed case study in applying GoF (Gang of Four) Design Patterns to solve real-world software design problems:

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Singleton** | `DatabaseConnection` | Guarantees a single, efficient, thread-safe database connection instance across the entire application lifecycle. |
| **Strategy** | `PaymentStrategy` (Credit, Debit, PayPal) | Allows the payment algorithm to be selected dynamically at runtime, making the payment system highly extensible. |
| **Observer** | `NotificationObserver` | Decouples the notification service from the core logic, enabling multiple notification types (SMS, Email) to react to events like "Booking Confirmed". |
| **Factory Method** | `PaymentController` | Encapsulates the object creation logic for selecting the appropriate payment strategy based on user input. |
| **Adapter** | `UserDatabaseAdapter` | Bridges the gap between the application's domain objects and the MySQL relational database schema. |
| **DAO** | `adapter.database` Package | Abstracts the data persistence layer, allowing the business logic to function independently of the underlying database technology. |

---

## 💻 Tech Stack

*   **Language**: Java (JDK 21 Recommended)
*   **GUI Framework**: Java Swing (javax.swing)
*   **Database**: MySQL 8.0+
*   **Connectivity**: JDBC (Java Database Connectivity)
*   **IDE**: IntelliJ IDEA

---

## 🚀 Getting Started

### Prerequisites
*   **Java Development Kit (JDK)**: Version 8 or higher.
*   **MySQL Server**: Locally installed and running.

### Installation & Setup

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/ebadr761/Flight-Reservation-Application.git
    cd flight-reservation-application
    ```

2.  **Database Configuration**
    *   Locate the `database/schema.sql` file.
    *   Execute the script in your MySQL interface to create the database and tables:
        ```sql
        CREATE DATABASE flight_reservation_db;
        USE flight_reservation_db;
        SOURCE database/schema.sql;
        ```

3.  **Application Config**
    *   Open `config/config.properties`.
    *   Update the database credentials to match your local MySQL setup: (Obviously use YOUR mySQL username and pass)
        ```properties
        db.url=jdbc:mysql://localhost:3306/flight_reservation_db
        db.username=your_root_username
        db.password=your_root_password
        ```

4.  **Run the Application**
    *   Open the project in your preferred IDE.
    *   Locate and run the main entry point: `src/Main.java`.

5. **Troubleshooting**
   * If you want a quick demo or some help troubleshooting the process of getting your JBDC file recognized by the program you can watch this video: 
   * https://youtu.be/-MewEcLvZEc


---

## 📱 Usage

1.  **Launch**: Run the application to see the Login Screen.
2.  **Register/Login**: create a new Customer account or log in with existing credentials.
3.  **Book**: Navigate to "Search Flights", select your destination, choose a seat, and pay using one of the "Mock" payment methods (e.g., enter dummy card details).
4.  **Verify**: Check your "My Bookings" tab to see the confirmed reservation.

---

*Built by Ebad Rehman, Manmohit Singh, Mazin Taher, Anhad Wander*
