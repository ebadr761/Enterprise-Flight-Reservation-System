-- Flight Reservation System Schema

DROP DATABASE IF EXISTS flight_reservation_db;
CREATE DATABASE flight_reservation_db;
USE flight_reservation_db;

-- Users Table
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       phone VARCHAR(20),
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customers Table
CREATE TABLE customers (
                           customer_id INT PRIMARY KEY,
                           registration_date DATE,
                           FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Flight Agents Table
CREATE TABLE flight_agents (
                               agent_id INT PRIMARY KEY,
                               FOREIGN KEY (agent_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Admins Table
CREATE TABLE admins (
                        admin_id INT PRIMARY KEY,
                        FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Flights Table
CREATE TABLE flights (
                         flight_id INT AUTO_INCREMENT PRIMARY KEY,
                         flight_number VARCHAR(20) UNIQUE NOT NULL,
                         airline VARCHAR(50) NOT NULL,
                         origin VARCHAR(50) NOT NULL,
                         destination VARCHAR(50) NOT NULL,
                         departure_time DATETIME NOT NULL,
                         arrival_time DATETIME NOT NULL,
                         price DECIMAL(10, 2) NOT NULL,
                         total_seats INT NOT NULL,
                         available_seats INT NOT NULL,
                         status VARCHAR(20) NOT NULL,
                         aircraft_type VARCHAR(50),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bookings Table
CREATE TABLE bookings (
                          booking_id INT AUTO_INCREMENT PRIMARY KEY,
                          customer_id INT NOT NULL,
                          flight_id INT NOT NULL,
                          booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          status VARCHAR(20) NOT NULL,
                          total_amount DECIMAL(10, 2) NOT NULL,
                          num_passengers INT NOT NULL,
                          FOREIGN KEY (customer_id) REFERENCES users(user_id),
                          FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);

-- Passengers Table
CREATE TABLE passengers (
                            passenger_id INT AUTO_INCREMENT PRIMARY KEY,
                            booking_id INT NOT NULL,
                            first_name VARCHAR(50) NOT NULL,
                            last_name VARCHAR(50) NOT NULL,
                            passport_number VARCHAR(50),
                            FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- Payments Table
CREATE TABLE payments (
                          payment_id INT AUTO_INCREMENT PRIMARY KEY,
                          booking_id INT NOT NULL,
                          amount DECIMAL(10, 2) NOT NULL,
                          payment_method VARCHAR(50) NOT NULL,
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          status VARCHAR(20) NOT NULL,
                          transaction_id VARCHAR(50) UNIQUE,
                          FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- Insert Default Admin User
INSERT INTO users (email, password, first_name, last_name, phone, role)
VALUES ('admin@flight.com', 'admin123', 'System', 'Admin', '1234567890', 'ADMIN');
INSERT INTO admins (admin_id) VALUES (LAST_INSERT_ID());

-- Insert Default Flight Agent
INSERT INTO users (email, password, first_name, last_name, phone, role)
VALUES ('agent@flight.com', 'agent123', 'Flight', 'Agent', '0987654321', 'FLIGHT_AGENT');
INSERT INTO flight_agents (agent_id) VALUES (LAST_INSERT_ID());

-- Insert Default Customer
INSERT INTO users (email, password, first_name, last_name, phone, role)
VALUES ('customer@flight.com', 'customer123', 'John', 'Doe', '5551234567', 'CUSTOMER');
INSERT INTO customers (customer_id, registration_date) VALUES (LAST_INSERT_ID(), CURDATE());

-- Insert Sample Flights
INSERT INTO flights (
    flight_number, airline, origin, destination,
    departure_time, arrival_time,
    price, total_seats, available_seats, status, aircraft_type
)
VALUES
    (
        'FL001', 'SkyHigh Air', 'New York', 'London',
        DATE_ADD(NOW(), INTERVAL 1 DAY),
        DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 7 HOUR),
        500.00, 150, 150, 'SCHEDULED', 'Boeing 737'
    ),
    (
        'FL002', 'Oceanic', 'Los Angeles', 'Tokyo',
        DATE_ADD(NOW(), INTERVAL 2 DAY),
        DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 11 HOUR),
        800.00, 200, 200, 'SCHEDULED', 'Airbus A380'
    ),
    (
        'FL003', 'Global Air', 'Paris', 'Dubai',
        DATE_ADD(NOW(), INTERVAL 3 DAY),
        DATE_ADD(DATE_ADD(NOW(), INTERVAL 3 DAY), INTERVAL 6 HOUR),
        450.00, 180, 180, 'SCHEDULED', 'Boeing 777'
    );
