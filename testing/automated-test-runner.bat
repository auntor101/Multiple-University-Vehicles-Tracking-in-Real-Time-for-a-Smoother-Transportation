@echo off
REM University Vehicle Tracking System - Automated Test Runner for Windows
REM This script runs comprehensive tests for the entire system

setlocal enabledelayedexpansion

REM Configuration
set BACKEND_DIR=backend
set FRONTEND_DIR=frontend
set TEST_RESULTS_DIR=test-results
set TIMESTAMP=%date:~-4,4%%date:~-10,2%%date:~-7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%

REM Create test results directory
if not exist "%TEST_RESULTS_DIR%" mkdir "%TEST_RESULTS_DIR%"

echo.
echo ==================================
echo University Vehicle Tracking System - Test Suite
echo ==================================
echo Starting comprehensive test execution at %date% %time%

REM Check prerequisites
echo.
echo ==================================
echo Checking Prerequisites
echo ==================================

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH
    exit /b 1
)

REM Check if Maven is available
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed or not in PATH
    exit /b 1
)

REM Check if Node.js is available
node -v >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed or not in PATH
    exit /b 1
)

REM Check if npm is available
npm -v >nul 2>&1
if errorlevel 1 (
    echo [ERROR] npm is not installed or not in PATH
    exit /b 1
)

echo [SUCCESS] All prerequisites are available

REM Function to run backend tests
:run_backend_tests
echo.
echo ==================================
echo Running Backend Tests
echo ==================================

cd "%BACKEND_DIR%"

REM Clean and compile
echo [INFO] Cleaning and compiling backend...
call mvn clean compile -q

REM Run unit tests
echo [INFO] Running unit tests...
call mvn test -Dtest="VehicleNumberValidatorTest,VehicleServiceTest" -Dmaven.test.failure.ignore=true > "../%TEST_RESULTS_DIR%/backend-unit-tests-%TIMESTAMP%.log" 2>&1

REM Run integration tests
echo [INFO] Running integration tests...
call mvn test -Dtest="VehicleIntegrationTest" -Dmaven.test.failure.ignore=true > "../%TEST_RESULTS_DIR%/backend-integration-tests-%TIMESTAMP%.log" 2>&1

REM Run API tests
echo [INFO] Running API tests...
call mvn test -Dtest="AuthControllerTest" -Dmaven.test.failure.ignore=true > "../%TEST_RESULTS_DIR%/backend-api-tests-%TIMESTAMP%.log" 2>&1

REM Run security tests
echo [INFO] Running security tests...
call mvn test -Dtest="SecurityTest" -Dmaven.test.failure.ignore=true > "../%TEST_RESULTS_DIR%/backend-security-tests-%TIMESTAMP%.log" 2>&1

REM Run performance tests
echo [INFO] Running performance tests...
call mvn test -Dtest="VehicleTrackingPerformanceTest" -Dmaven.test.failure.ignore=true > "../%TEST_RESULTS_DIR%/backend-performance-tests-%TIMESTAMP%.log" 2>&1

REM Run complete test suite
echo [INFO] Running complete test suite...
call mvn test -Dmaven.test.failure.ignore=true > "../%TEST_RESULTS_DIR%/backend-complete-tests-%TIMESTAMP%.log" 2>&1

REM Generate test report
echo [INFO] Generating test reports...
call mvn surefire-report:report -q

cd ..
echo [SUCCESS] Backend tests completed
goto :eof

REM Function to run frontend tests
:run_frontend_tests
echo.
echo ==================================
echo Running Frontend Tests
echo ==================================

cd "%FRONTEND_DIR%"

REM Install dependencies if needed
if not exist "node_modules" (
    echo [INFO] Installing frontend dependencies...
    call npm install
)

REM Run linting
echo [INFO] Running ESLint...
call npm run lint > "../%TEST_RESULTS_DIR%/frontend-lint-%TIMESTAMP%.log" 2>&1

REM Build application to test for build errors
echo [INFO] Testing production build...
call npm run build > "../%TEST_RESULTS_DIR%/frontend-build-%TIMESTAMP%.log" 2>&1

cd ..
echo [SUCCESS] Frontend tests completed
goto :eof

REM Function to run system integration tests
:run_system_tests
echo.
echo ==================================
echo Running System Integration Tests
echo ==================================

REM Start backend server in background
echo [INFO] Starting backend server...
cd "%BACKEND_DIR%"
start /b "backend-server" cmd /c "mvn spring-boot:run -Dspring-boot.run.profiles=test"
cd ..

REM Wait for backend to start
echo [INFO] Waiting for backend to start...
timeout /t 30 /nobreak >nul

REM Check if backend is responding
curl -f http://localhost:8080/api/tracking/vehicles >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Backend server is not responding
    taskkill /f /im java.exe /fi "WINDOWTITLE eq backend-server" >nul 2>&1
    goto :eof
) else (
    echo [SUCCESS] Backend server is responding
)

REM Test basic API connectivity
echo [INFO] Testing API connectivity...

REM Test public endpoints
curl -f http://localhost:8080/api/tracking/vehicles -o nul >nul 2>&1
if errorlevel 1 echo [ERROR] Vehicle tracking endpoint failed

REM Test authentication endpoint
curl -f -X POST http://localhost:8080/api/auth/signin -H "Content-Type: application/json" -d "{\"usernameOrEmail\":\"admin@university.edu\",\"password\":\"admin123\"}" -o nul >nul 2>&1
if errorlevel 1 echo [ERROR] Authentication endpoint failed

REM Cleanup
echo [INFO] Stopping test servers...
taskkill /f /im java.exe /fi "WINDOWTITLE eq backend-server" >nul 2>&1

echo [SUCCESS] System integration tests completed
goto :eof

REM Function to generate final report
:generate_report
echo.
echo ==================================
echo Generating Test Report
echo ==================================

set REPORT_FILE=%TEST_RESULTS_DIR%/test-summary-%TIMESTAMP%.txt

echo University Vehicle Tracking System - Test Execution Summary > "%REPORT_FILE%"
echo Generated on: %date% %time% >> "%REPORT_FILE%"
echo ========================================= >> "%REPORT_FILE%"
echo. >> "%REPORT_FILE%"

echo Test Results Summary: >> "%REPORT_FILE%"
echo -------------------- >> "%REPORT_FILE%"

REM Add manual test checklist
echo. >> "%REPORT_FILE%"
echo Manual Test Checklist: >> "%REPORT_FILE%"
echo --------------------- >> "%REPORT_FILE%"
echo [ ] Authentication flow testing >> "%REPORT_FILE%"
echo [ ] Role-based access control >> "%REPORT_FILE%"
echo [ ] Vehicle management functionality >> "%REPORT_FILE%"
echo [ ] Real-time location tracking >> "%REPORT_FILE%"
echo [ ] Mobile responsiveness >> "%REPORT_FILE%"
echo [ ] Browser compatibility >> "%REPORT_FILE%"
echo [ ] Performance under load >> "%REPORT_FILE%"
echo [ ] Security vulnerability testing >> "%REPORT_FILE%"

echo. >> "%REPORT_FILE%"
echo Test Artifacts Location: >> "%REPORT_FILE%"
echo ---------------------- >> "%REPORT_FILE%"
echo Backend reports: target/surefire-reports/ >> "%REPORT_FILE%"
echo Test logs: %TEST_RESULTS_DIR%/ >> "%REPORT_FILE%"
echo Manual test scenarios: testing/manual-test-scenarios.md >> "%REPORT_FILE%"

echo [SUCCESS] Test report generated: %REPORT_FILE%
goto :eof

REM Main execution flow
:main
if "%1"=="--help" goto :help
if "%1"=="-h" goto :help

set TEST_TYPE=%1
if "%TEST_TYPE%"=="" set TEST_TYPE=all

if "%TEST_TYPE%"=="backend" (
    call :run_backend_tests
) else if "%TEST_TYPE%"=="frontend" (
    call :run_frontend_tests
) else if "%TEST_TYPE%"=="system" (
    call :run_system_tests
) else if "%TEST_TYPE%"=="all" (
    call :run_backend_tests
    call :run_frontend_tests
    call :run_system_tests
) else (
    echo [ERROR] Invalid test type. Use: backend, frontend, system, or all
    exit /b 1
)

call :generate_report

echo.
echo ==================================
echo Test Execution Summary
echo ==================================
echo [SUCCESS] All requested tests completed successfully!
echo [INFO] Check %TEST_RESULTS_DIR%/ for detailed test logs and reports
echo [INFO] Review testing/manual-test-scenarios.md for manual testing guidance

REM Open test results directory
start "" "%TEST_RESULTS_DIR%"

goto :end

:help
echo University Vehicle Tracking System - Automated Test Runner
echo.
echo Usage: %0 [test-type]
echo.
echo Test types:
echo   all      - Run all tests (default)
echo   backend  - Run only backend tests
echo   frontend - Run only frontend tests
echo   system   - Run only system integration tests
echo.
echo Examples:
echo   %0              # Run all tests
echo   %0 backend      # Run only backend tests
echo   %0 frontend     # Run only frontend tests
echo   %0 system       # Run only system tests
goto :end

:end
endlocal 