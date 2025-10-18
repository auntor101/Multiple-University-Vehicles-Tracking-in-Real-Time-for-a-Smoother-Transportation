#!/bin/bash

echo "========================================"
echo "University Vehicle Tracking System"
echo "Automated Setup Script for Unix/Linux/Mac"
echo "========================================"
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo "Checking prerequisites..."
echo ""

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}[ERROR]${NC} Java is not installed"
    echo "Please install Java 17+ from: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi
echo -e "${GREEN}[OK]${NC} Java is installed"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}[ERROR]${NC} Maven is not installed"
    echo "Please install Maven from: https://maven.apache.org/download.cgi"
    exit 1
fi
echo -e "${GREEN}[OK]${NC} Maven is installed"

# Check Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}[ERROR]${NC} Node.js is not installed"
    echo "Please install Node.js from: https://nodejs.org/"
    exit 1
fi
echo -e "${GREEN}[OK]${NC} Node.js is installed"

# Check npm
if ! command -v npm &> /dev/null; then
    echo -e "${RED}[ERROR]${NC} npm is not installed"
    exit 1
fi
echo -e "${GREEN}[OK]${NC} npm is installed"

echo ""
echo "========================================"
echo "All prerequisites satisfied!"
echo "========================================"
echo ""

# Setup Backend
echo "Setting up backend..."
cd backend

# Check for firebase service account
if [ ! -f "src/main/resources/firebase-service-account.json" ]; then
    echo ""
    echo -e "${YELLOW}[WARNING]${NC} firebase-service-account.json not found!"
    echo "Please download it from Firebase Console and place it in:"
    echo "  backend/src/main/resources/firebase-service-account.json"
    echo ""
    echo "The file should contain your Firebase project credentials."
    echo ""
fi

echo "Installing backend dependencies..."
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}[ERROR]${NC} Backend build failed"
    exit 1
fi
echo -e "${GREEN}[OK]${NC} Backend dependencies installed"

cd ..

# Setup Frontend
echo ""
echo "Setting up frontend..."
cd frontend

# Check for .env file
if [ ! -f ".env" ]; then
    echo ""
    echo -e "${YELLOW}[INFO]${NC} Creating .env file from template..."
    if [ -f "env.template" ]; then
        cp env.template .env
        echo -e "${GREEN}[OK]${NC} .env file created. Please edit it with your Firebase configuration."
    else
        echo -e "${YELLOW}[WARNING]${NC} env.template not found!"
        echo "Please create a .env file in frontend/ directory with your Firebase configuration."
    fi
    echo ""
fi

echo "Installing frontend dependencies..."
npm install
if [ $? -ne 0 ]; then
    echo -e "${RED}[ERROR]${NC} Frontend dependencies installation failed"
    exit 1
fi
echo -e "${GREEN}[OK]${NC} Frontend dependencies installed"

echo ""
echo "Fixing security vulnerabilities..."
npm audit fix
echo -e "${GREEN}[OK]${NC} Security audit completed"

cd ..

# Make scripts executable
echo ""
echo "Making scripts executable..."
chmod +x backend/start-local.sh 2>/dev/null
chmod +x frontend/run.sh 2>/dev/null
echo -e "${GREEN}[OK]${NC} Scripts are executable"

echo ""
echo "========================================"
echo "Setup Complete!"
echo "========================================"
echo ""
echo "Next steps:"
echo "1. Configure Firebase:"
echo "   - Place firebase-service-account.json in backend/src/main/resources/"
echo "   - Update frontend/.env with your Firebase configuration"
echo ""
echo "2. Start the application:"
echo "   Backend:  cd backend && ./start-local.sh"
echo "   Frontend: cd frontend && ./run.sh"
echo ""
echo "3. Access the application:"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080/api"
echo ""
echo "For detailed setup instructions, see SETUP_GUIDE.md"
echo ""



