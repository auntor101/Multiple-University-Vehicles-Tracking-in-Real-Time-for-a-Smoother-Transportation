#!/bin/bash

echo "========================================"
echo "University Vehicle Tracking System"
echo "Starting ALL Services"
echo "========================================"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if ports are in use
echo "Checking for running services..."
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${YELLOW}[WARNING]${NC} Port 8080 is already in use. Backend may already be running."
    echo ""
fi

if lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${YELLOW}[WARNING]${NC} Port 3000 is already in use. Frontend may already be running."
    echo ""
fi

# Create log directory if it doesn't exist
mkdir -p logs

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "Stopping all services..."
    kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
    echo "Services stopped."
    exit 0
}

# Trap Ctrl+C and call cleanup
trap cleanup INT TERM

# Start Backend
echo "Starting Backend Server..."
cd backend
mvn spring-boot:run > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
cd ..
echo -e "${GREEN}[OK]${NC} Backend starting (PID: $BACKEND_PID) on port 8080"
echo "     Logs: logs/backend.log"

# Wait for backend to initialize
echo ""
echo "Waiting for backend to initialize (15 seconds)..."
sleep 15

# Check if backend is still running
if ! ps -p $BACKEND_PID > /dev/null 2>&1; then
    echo -e "${RED}[ERROR]${NC} Backend failed to start. Check logs/backend.log"
    exit 1
fi

# Start Frontend
echo ""
echo "Starting Frontend Application..."
cd frontend
npm start > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..
echo -e "${GREEN}[OK]${NC} Frontend starting (PID: $FRONTEND_PID) on port 3000"
echo "     Logs: logs/frontend.log"

echo ""
echo "========================================"
echo "All Services Started!"
echo "========================================"
echo ""
echo "Services are running:"
echo "- Backend:  http://localhost:8080/api  (PID: $BACKEND_PID)"
echo "- Frontend: http://localhost:3000      (PID: $FRONTEND_PID)"
echo ""
echo "Logs are available in:"
echo "- logs/backend.log"
echo "- logs/frontend.log"
echo ""
echo "The frontend should open automatically in your browser."
echo "If not, please navigate to: http://localhost:3000"
echo ""
echo "To stop the services:"
echo "Press Ctrl+C in this terminal"
echo ""
echo "Or manually kill the processes:"
echo "kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo "========================================"
echo ""
echo "Waiting... (Press Ctrl+C to stop all services)"

# Wait for user interrupt
wait $BACKEND_PID $FRONTEND_PID



