@echo off
echo ========================================
echo University Vehicle Tracking System
echo Starting ALL Services
echo ========================================
echo.

REM Check if already running
echo Checking for running services...
netstat -ano | findstr :8080 >nul
if %errorlevel% equ 0 (
    echo [WARNING] Port 8080 is already in use. Backend may already be running.
    echo.
)

netstat -ano | findstr :3000 >nul
if %errorlevel% equ 0 (
    echo [WARNING] Port 3000 is already in use. Frontend may already be running.
    echo.
)

REM Start Backend in new window
echo Starting Backend Server...
start "Vehicle Tracking Backend" cmd /k "cd backend && mvn spring-boot:run"
echo [OK] Backend starting in new window on port 8080

REM Wait for backend to initialize
echo.
echo Waiting for backend to initialize (10 seconds)...
timeout /t 10 /nobreak >nul

REM Start Frontend in new window
echo.
echo Starting Frontend Application...
start "Vehicle Tracking Frontend" cmd /k "cd frontend && npm start"
echo [OK] Frontend starting in new window on port 3000

echo.
echo ========================================
echo All Services Started!
echo ========================================
echo.
echo Services are starting in separate windows:
echo - Backend:  http://localhost:8080/api
echo - Frontend: http://localhost:3000
echo.
echo The frontend should open automatically in your browser.
echo If not, please navigate to: http://localhost:3000
echo.
echo To stop the services:
echo 1. Close the backend window (Spring Boot server)
echo 2. Close the frontend window (React development server)
echo.
echo Or press Ctrl+C in each window
echo.
echo ========================================
echo.
pause



