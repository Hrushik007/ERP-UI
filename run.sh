#!/bin/bash

# Build and run script for ERP Application
# This script compiles all Java files and runs the application

echo "=========================================="
echo "  ERP Application Build & Run Script"
echo "=========================================="

# Navigate to project directory
cd "$(dirname "$0")"

# Create output directory if it doesn't exist
mkdir -p out

echo ""
echo "[1/3] Cleaning previous build..."
rm -rf out/*

echo "[2/3] Compiling Java files..."
# Find all Java files and compile them
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "      Compilation successful!"
    rm sources.txt

    echo "[3/3] Running application..."
    echo ""
    echo "=========================================="
    java -cp out com.erp.ERPApplication
else
    echo "      Compilation failed!"
    rm sources.txt
    exit 1
fi
