# Flight Reservation System

A comprehensive Java-based Flight Booking Management System.
Supports multiple user roles: Customer, Flight Agent, and Administrator.

---

## Repository Structure

```text
Enterprise-Flight-Reservation-System/
├── LICENSE
├── diagrams/                # UML and process diagrams
├── .idea/                   # IntelliJ project config
├── src/                     # Java source code
├── config/                  # Config files
├── database/                # SQL scripts for database
├── lib/                     # Any external libraries
└── .gitignore
```

## Requirements

- Java 21 (JDK)
- MySQL 8+
- IntelliJ IDEA (or any Java IDE)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/ebadr761/Enterprise-Flight-Reservation-System.git
cd Enterprise-Flight-Reservation-System
```

### 2. Create the Database

Start your MySQL server.
Run the SQL schema file:

```bash
mysql -u <username> -p < database/schema.sql
```

Replace `<username>` with your MySQL username.
This will create the `flight_reservation_db` and populate sample data.

### 3. Configure Database Connection

Open `config/config.properties` (if it exists), or use the defaults in `DatabaseConnection.java`:

```properties
db.url=jdbc:mysql://localhost:3306/flight_reservation_db
db.username=root
db.password=password
```

Make sure your username/password matches your MySQL setup.

### 4. Run the Application

Open the project in IntelliJ IDEA.
Build the project.
Run `Main.java` or use the terminal:

```bash
java -cp "src:lib/*" com.flightreservation.Main
```

The GUI should launch, and you can log in as one of the default users:

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@flight.com | admin123 |
| Flight Agent | agent@flight.com | agent123 |
| Customer | customer@flight.com | customer123 |

## Notes

- **Security**: Do not commit your `config.properties` if it contains real passwords.
- **Build**: `out/` is ignored in Git; compiled files are not included.
- **Usage**: You can add more flights, users, and bookings using the GUI.

## Diagrams

All UML diagrams are in the `diagrams/` folder for reference:

- Use-case diagram
- Sequence diagrams
- Activity diagrams
- State transition diagrams

## License

This project is licensed under the MIT License.
