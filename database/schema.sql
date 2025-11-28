-- Flight Reservation System Database Schema
-- Drop existing database if exists and create fresh
DROP DATABASE IF EXISTS flight_reservation_db;
CREATE DATABASE flight_reservation_db;
USE flight_reservation_db;

-- Users table (supports all user types)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role ENUM('CUSTOMER', 'FLIGHT_AGENT', 'ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Customer-specific data
CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    loyalty_points INT DEFAULT 0,
    registration_date DATE NOT NULL,
    receive_promotions BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Flight Agent-specific data
CREATE TABLE flight_agents (
    agent_id INT PRIMARY KEY,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(50),
    hire_date DATE NOT NULL,
    FOREIGN KEY (agent_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Admin-specific data
CREATE TABLE admins (
    admin_id INT PRIMARY KEY,
    admin_level INT DEFAULT 1,
    permissions TEXT,
    FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Flights table
CREATE TABLE flights (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    flight_number VARCHAR(20) UNIQUE NOT NULL,
    airline VARCHAR(50) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    status ENUM('SCHEDULED', 'DELAYED', 'CANCELLED', 'COMPLETED') DEFAULT 'SCHEDULED',
    aircraft_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_flight_number (flight_number),
    INDEX idx_route (origin, destination),
    INDEX idx_departure (departure_time),
    INDEX idx_status (status)
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('CONFIRMED', 'CANCELLED', 'PENDING') DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL,
    num_passengers INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id),
    INDEX idx_flight (flight_id),
    INDEX idx_status (status)
);

-- Passengers table
CREATE TABLE passengers (
    passenger_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    passport_number VARCHAR(20) NOT NULL,
    seat_number VARCHAR(10),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_booking (booking_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
    INDEX idx_booking (booking_id),
    INDEX idx_status (status)
);

-- Promotions table
CREATE TABLE promotions (
    promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    discount_percentage DECIMAL(5, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_validity (valid_from, valid_to)
);

-- Customer promotions tracking
CREATE TABLE customer_promotions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    promotion_id INT NOT NULL,
    sent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id),
    INDEX idx_promotion (promotion_id)
);

-- Insert default admin user (password: admin123)
INSERT INTO users (email, password, first_name, last_name, phone, role) 
VALUES ('admin@flight.com', 'admin123', 'System', 'Administrator', '1234567890', 'ADMIN');

INSERT INTO admins (admin_id, admin_level, permissions) 
VALUES (LAST_INSERT_ID(), 3, 'ALL');

-- Insert sample flight agent (password: agent123)
INSERT INTO users (email, password, first_name, last_name, phone, role) 
VALUES ('agent@flight.com', 'agent123', 'John', 'Agent', '1234567891', 'FLIGHT_AGENT');

INSERT INTO flight_agents (agent_id, employee_id, department, hire_date) 
VALUES (LAST_INSERT_ID(), 'EMP001', 'Customer Service', CURDATE());

-- Insert sample flights
INSERT INTO flights (flight_number, airline, origin, destination, departure_time, arrival_time, price, total_seats, available_seats, aircraft_type) VALUES
('AA101', 'American Airlines', 'New York (JFK)', 'Los Angeles (LAX)', '2025-12-01 08:00:00', '2025-12-01 11:30:00', 299.99, 180, 180, 'Boeing 737'),
('UA202', 'United Airlines', 'Chicago (ORD)', 'San Francisco (SFO)', '2025-12-01 10:00:00', '2025-12-01 13:00:00', 349.99, 200, 200, 'Airbus A320'),
('DL303', 'Delta Airlines', 'Atlanta (ATL)', 'Miami (MIA)', '2025-12-02 14:00:00', '2025-12-02 16:30:00', 199.99, 150, 150, 'Boeing 757'),
('SW404', 'Southwest Airlines', 'Dallas (DFW)', 'Denver (DEN)', '2025-12-03 09:00:00', '2025-12-03 10:30:00', 179.99, 175, 175, 'Boeing 737'),
('AA505', 'American Airlines', 'Boston (BOS)', 'Seattle (SEA)', '2025-12-04 07:00:00', '2025-12-04 10:30:00', 399.99, 190, 190, 'Boeing 787'),
('UA606', 'United Airlines', 'Los Angeles (LAX)', 'New York (JFK)', '2025-12-05 15:00:00', '2025-12-05 23:30:00', 319.99, 200, 200, 'Airbus A321');

-- Insert sample promotion
INSERT INTO promotions (title, message, valid_from, valid_to, discount_percentage) VALUES
('December Holiday Special', 'Book now and save 20% on all domestic flights! Use code HOLIDAY20 at checkout.', '2025-12-01', '2025-12-31', 20.00);
