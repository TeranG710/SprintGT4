#!/bin/bash

# Enhanced run script for Monopoly project

# Print colored output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
RESET='\033[0m'

echo -e "${BLUE}Monopoly Game Launcher${RESET}"
echo -e "${BLUE}====================${RESET}"

# Create directories for compiled classes and resources if they don't exist
echo "Setting up directories..."
mkdir -p classes
mkdir -p resources

# Clean previous compiled files
echo "Cleaning previous build..."
rm -rf classes/*

# Compile source files
echo "Compiling source code..."
find ./src -name "*.java" -print > sources.txt

# Detect platform for class path separator
CLASSPATH_SEPARATOR=":"
if [[ "$OSTYPE" == "cygwin" || "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    CLASSPATH_SEPARATOR=";"
fi

# Compile with Java
javac -d classes @sources.txt
COMPILE_RESULT=$?
rm sources.txt

# Check if compilation was successful
if [ $COMPILE_RESULT -eq 0 ]; then
    echo -e "${GREEN}Compilation successful!${RESET}"
    echo "Launching Monopoly game..."
    
    # Run the application with resources directory in the classpath
    # Detect if we're on Windows or Unix for classpath separator
    if [ "$CLASSPATH_SEPARATOR" = ";" ]; then
        # Windows
        java -cp "classes;resources" Model.Main
    else
        # Unix/Mac
        java -cp classes:resources Model.Main
    fi
    
    echo -e "${GREEN}Game closed.${RESET}"
else
    echo -e "${RED}Compilation failed. Please fix the errors and try again.${RESET}"
    exit 1
fi