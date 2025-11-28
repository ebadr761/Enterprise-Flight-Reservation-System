#!/bin/bash

# Flight Reservation System - Quick Start Script

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║     Flight Reservation System - Setup Script                 ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Check if MySQL is running
echo "Checking MySQL connection..."
if ! mysql -u root -p -e "SELECT 1" > /dev/null 2>&1; then
    echo "⚠️  MySQL is not running or credentials are incorrect."
    echo "Please start MySQL and ensure you have the correct credentials."
    exit 1
fi

# Create database
echo "Setting up database..."
mysql -u root -p < database/schema.sql

if [ $? -eq 0 ]; then
    echo "✓ Database setup completed successfully!"
else
    echo "✗ Database setup failed. Please check your MySQL credentials."
    exit 1
fi

echo ""
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║     Setup Complete!                                           ║"
echo "╠═══════════════════════════════════════════════════════════════╣"
echo "║  Default Credentials:                                         ║"
echo "║  Admin:  admin@flight.com / admin123                          ║"
echo "║  Agent:  agent@flight.com / agent123                          ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "To run the application, use:"
echo "  ./run.sh"
echo ""
