#!/bin/bash

# Enhanced runJar script for Monopoly project

# Print colored output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RESET='\033[0m'

echo -e "${BLUE}Monopoly Game Launcher (JAR version)${RESET}"
echo -e "${BLUE}=================================${RESET}"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Maven not found. Please install Maven to use this script.${RESET}"
    echo -e "${YELLOW}Try using ./run.sh instead, which doesn't require Maven.${RESET}"
    exit 1
fi

# Build the JAR file
echo "Building the JAR file with Maven..."
mvn clean package
MVN_RESULT=$?

# Check if Maven build was successful
if [ $MVN_RESULT -eq 0 ]; then
    echo -e "${GREEN}Build successful!${RESET}"
    echo "Launching Monopoly game from JAR..."
    
    # Run the application from the JAR
    java -jar target/MonopolyProject-1.0-Project.jar
    
    echo -e "${GREEN}Game closed.${RESET}"
else
    echo -e "${RED}Build failed. Please fix the errors and try again.${RESET}"
    exit 1
fi

