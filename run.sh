#!/bin/bash

# Flight Reservation System - Run Script

# Compile if needed
if [ ! -d "bin" ] || [ -z "$(ls -A bin)" ]; then
    echo "Compiling project..."
    javac -d bin -cp lib/mysql-connector-j-8.2.0.jar $(find src -name "*.java")
    
    if [ $? -ne 0 ]; then
        echo "✗ Compilation failed!"
        exit 1
    fi
    echo "✓ Compilation successful!"
fi

# Run the application
java -cp bin:lib/mysql-connector-j-8.2.0.jar com.flightreservation.Main
