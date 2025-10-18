@echo off
echo ========================================
echo University Vehicle Tracking System
echo Automated Setup Script for Windows
echo ========================================
echo.

REM Check prerequisites
echo Checking prerequisites...
echo.

REM Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java 17+ from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo [OK] Java is installed

REM Check Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo Please install Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo [OK] Maven is installed

REM Check Node.js
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed or not in PATH
    echo Please install Node.js from: https://nodejs.org/
    pause
    exit /b 1
)
echo [OK] Node.js is installed

REM Check npm
npm -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] npm is not installed or not in PATH
    pause
    exit /b 1
)
echo [OK] npm is installed

echo.
echo ========================================
echo All prerequisites satisfied!
echo ========================================
echo.

REM Setup Backend
echo Setting up backend...
cd backend

REM Check for firebase service account
if not exist "src\main\resources\firebase-service-account.json" (
    echo.
    echo [WARNING] firebase-service-account.json not found!
    echo Please download it from Firebase Console and place it in:
    echo   backend\src\main\resources\firebase-service-account.json
    echo.
    echo The file should contain your Firebase project credentials.
    echo.
)

echo Installing backend dependencies...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Backend build failed
    pause
    exit /b 1
)
echo [OK] Backend dependencies installed

cd ..

REM Setup Frontend
echo.
echo Setting up frontend...
cd frontend

REM Check for .env file
if not exist ".env" (
    echo.
    echo [INFO] Creating .env file from template...
    if exist "env.template" (
        copy env.template .env
        echo [OK] .env file created. Please edit it with your Firebase configuration.
    ) else (
        echo [WARNING] env.template not found!
        echo Please create a .env file in frontend\ directory with your Firebase configuration.
    )
    echo.
)

echo Installing frontend dependencies...
call npm install
if %errorlevel% neq 0 (
    echo [ERROR] Frontend dependencies installation failed
    pause
    exit /b 1
)
echo [OK] Frontend dependencies installed

echo.
echo Fixing security vulnerabilities...
call npm audit fix
echo [OK] Security audit completed

cd ..

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Configure Firebase:
echo    - Place firebase-service-account.json in backend\src\main\resources\
echo    - Update frontend\.env with your Firebase configuration
echo.
echo 2. Start the application:
echo    Backend:  cd backend ^&^& start-local.bat
echo    Frontend: cd frontend ^&^& run.bat
echo.
echo 3. Access the application:
echo    Frontend: http://localhost:3000
echo    Backend:  http://localhost:8080/api
echo.
echo For detailed setup instructions, see SETUP_GUIDE.md
echo.
pause



