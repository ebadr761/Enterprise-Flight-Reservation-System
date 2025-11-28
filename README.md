# Flight Reservation System

A comprehensive CLI-based Flight Reservation System built with Java and MySQL, featuring role-based access control and complete booking management capabilities.

## Features

### User Roles
- **Customer**: Search flights, make bookings, view reservations, cancel bookings
- **Flight Agent**: Manage customer bookings, view schedules, modify reservations
- **System Administrator**: Add/remove flights, manage routes, update schedules, view system statistics

### Core Functionality
- ✈️ **Flight Search**: Search by origin, destination, date, and airline
- 📅 **Booking Management**: Create, view, modify, and cancel reservations
- 💳 **Payment Simulation**: Simulated payment processing with multiple payment methods
- 👥 **Multi-Passenger Booking**: Book for up to 10 passengers per reservation
- 📊 **System Statistics**: Real-time analytics for administrators
- 🎫 **Booking Confirmation**: Detailed booking confirmations with all flight and passenger details

## Technology Stack

- **Language**: Java
- **Database**: MySQL
- **Architecture**: MVC (Model-View-Controller)
- **Design Patterns**: Singleton (Database Connection), DAO Pattern

## Project Structure

```
Term_Project/
├── src/com/flightreservation/
│   ├── Main.java                          # Application entry point
│   ├── model/
│   │   ├── entity/                        # Entity classes
│   │   │   ├── User.java
│   │   │   ├── Customer.java
│   │   │   ├── FlightAgent.java
│   │   │   ├── Admin.java
│   │   │   ├── Flight.java
│   │   │   ├── Booking.java
│   │   │   ├── Passenger.java
│   │   │   ├── Payment.java
│   │   │   └── Promotion.java
│   │   └── enums/                         # Enumerations
│   │       ├── UserRole.java
│   │       ├── FlightStatus.java
│   │       ├── BookingStatus.java
│   │       └── PaymentStatus.java
│   ├── dao/                               # Data Access Layer
│   │   ├── DatabaseConnection.java
│   │   ├── UserDAO.java
│   │   ├── FlightDAO.java
│   │   ├── BookingDAO.java
│   │   ├── PaymentDAO.java
│   │   └── impl/                          # DAO Implementations
│   │       ├── UserDAOImpl.java
│   │       ├── FlightDAOImpl.java
│   │       ├── BookingDAOImpl.java
│   │       └── PaymentDAOImpl.java
│   ├── controller/                        # Business Logic Layer
│   │   ├── AuthenticationController.java
│   │   ├── FlightController.java
│   │   ├── BookingController.java
│   │   └── PaymentController.java
│   ├── view/                              # CLI User Interface
│   │   ├── ConsoleUI.java
│   │   ├── MainMenuUI.java
│   │   ├── CustomerUI.java
│   │   ├── FlightAgentUI.java
│   │   └── AdminUI.java
│   └── util/                              # Utility Classes
│       ├── InputValidator.java
│       └── DateTimeUtil.java
├── database/
│   └── schema.sql                         # Database schema
├── config/
│   └── config.properties                  # Configuration file
├── lib/
│   └── mysql-connector-j-8.2.0.jar       # MySQL JDBC driver
└── bin/                                   # Compiled classes

```

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- MySQL Server 8.0 or higher
- MySQL JDBC Driver (included in lib/)

## Setup Instructions

### 1. Database Setup

```bash
# Start MySQL server
mysql.server start

# Create database and tables
mysql -u root -p < database/schema.sql
```

The schema includes:
- Sample admin user (email: admin@flight.com, password: admin123)
- Sample flight agent (email: agent@flight.com, password: agent123)
- 6 sample flights for testing

### 2. Configure Database Connection

Edit `config/config.properties` if needed:

```properties
db.url=jdbc:mysql://localhost:3306/flight_reservation_db
db.username=root
db.password=your_password
```

### 3. Download MySQL Connector

If not already present, download the MySQL Connector/J:

```bash
# Download to lib/ directory
cd lib
curl -O https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.2.0/mysql-connector-j-8.2.0.jar
```

### 4. Compile the Project

```bash
# Compile all Java files
javac -d bin -cp lib/mysql-connector-j-8.2.0.jar src/com/flightreservation/**/*.java
```

### 5. Run the Application

```bash
# Run the main application
java -cp bin:lib/mysql-connector-j-8.2.0.jar com.flightreservation.Main
```

## Usage Guide

### Customer Workflow

1. **Register**: Create a new customer account
2. **Login**: Access the customer dashboard
3. **Search Flights**: Find flights by origin, destination, date, or airline
4. **Book Flight**: Select flight, add passenger details, and complete payment
5. **View Bookings**: See all your reservations
6. **Cancel Booking**: Cancel and get refund for unwanted bookings

### Flight Agent Workflow

1. **Login**: Use agent credentials (agent@flight.com / agent123)
2. **View Bookings**: See all customer bookings
3. **View Schedules**: Check flight schedules
4. **Manage Bookings**: Modify or cancel customer bookings
5. **Flight Bookings**: View bookings for specific flights

### Administrator Workflow

1. **Login**: Use admin credentials (admin@flight.com / admin123)
2. **Add Flights**: Create new flight schedules
3. **Update Flights**: Modify flight details
4. **Delete Flights**: Remove outdated flights
5. **Update Status**: Change flight status (Scheduled, Delayed, Cancelled, Completed)
6. **View Statistics**: Monitor system performance and revenue

## Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@flight.com | admin123 |
| Flight Agent | agent@flight.com | agent123 |

## Sample Flights

The system comes pre-loaded with 6 sample flights:
- AA101: New York → Los Angeles
- UA202: Chicago → San Francisco
- DL303: Atlanta → Miami
- SW404: Dallas → Denver
- AA505: Boston → Seattle
- UA606: Los Angeles → New York

## Input Validation

The system validates:
- ✅ Email format (must contain @)
- ✅ Phone numbers (10-15 digits)
- ✅ Passport numbers (6-12 alphanumeric characters)
- ✅ Date/time formats (YYYY-MM-DD HH:MM)
- ✅ Required fields

## Error Handling

- Database connection failures
- Invalid user input
- Booking conflicts (insufficient seats)
- Payment processing errors
- Data validation errors

## Future Enhancements

- Monthly promotion system with automated notifications
- Seat selection with visual seat map
- Loyalty points rewards program
- Email notifications for bookings
- Advanced reporting and analytics
- Multi-currency support
- Flight delay notifications

## Troubleshooting

### Database Connection Issues

```bash
# Verify MySQL is running
mysql.server status

# Test connection
mysql -u root -p flight_reservation_db
```

### Compilation Errors

```bash
# Clean and recompile
rm -rf bin/*
javac -d bin -cp lib/mysql-connector-j-8.2.0.jar src/com/flightreservation/**/*.java
```

### Runtime Errors

- Ensure MySQL server is running
- Verify database exists and schema is loaded
- Check config.properties for correct credentials
- Ensure MySQL connector JAR is in lib/ directory

## License

This project is created for educational purposes.

## Author

Flight Reservation System - Term Project
