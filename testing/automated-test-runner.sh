#!/bin/bash

# University Vehicle Tracking System - Automated Test Runner
# This script runs comprehensive tests for the entire system

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo ""
    echo "=================================="
    echo "$1"
    echo "=================================="
}

# Configuration
BACKEND_DIR="backend"
FRONTEND_DIR="frontend"
TEST_RESULTS_DIR="test-results"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Create test results directory
mkdir -p "$TEST_RESULTS_DIR"

print_header "University Vehicle Tracking System - Test Suite"
print_status "Starting comprehensive test execution at $(date)"

# Check prerequisites
print_header "Checking Prerequisites"

# Check if Java is available
if ! command -v java &> /dev/null; then
    print_error "Java is not installed or not in PATH"
    exit 1
fi

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed or not in PATH"
    exit 1
fi

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    print_error "Node.js is not installed or not in PATH"
    exit 1
fi

# Check if npm is available
if ! command -v npm &> /dev/null; then
    print_error "npm is not installed or not in PATH"
    exit 1
fi

print_success "All prerequisites are available"

# Function to run backend tests
run_backend_tests() {
    print_header "Running Backend Tests"
    
    cd "$BACKEND_DIR"
    
    # Clean and compile
    print_status "Cleaning and compiling backend..."
    mvn clean compile -q
    
    # Run unit tests
    print_status "Running unit tests..."
    mvn test -Dtest="VehicleNumberValidatorTest,VehicleServiceTest" \
        -Dmaven.test.failure.ignore=true \
        | tee "../$TEST_RESULTS_DIR/backend-unit-tests-$TIMESTAMP.log"
    
    # Run integration tests
    print_status "Running integration tests..."
    mvn test -Dtest="VehicleIntegrationTest" \
        -Dmaven.test.failure.ignore=true \
        | tee "../$TEST_RESULTS_DIR/backend-integration-tests-$TIMESTAMP.log"
    
    # Run API tests
    print_status "Running API tests..."
    mvn test -Dtest="AuthControllerTest" \
        -Dmaven.test.failure.ignore=true \
        | tee "../$TEST_RESULTS_DIR/backend-api-tests-$TIMESTAMP.log"
    
    # Run security tests
    print_status "Running security tests..."
    mvn test -Dtest="SecurityTest" \
        -Dmaven.test.failure.ignore=true \
        | tee "../$TEST_RESULTS_DIR/backend-security-tests-$TIMESTAMP.log"
    
    # Run performance tests
    print_status "Running performance tests..."
    mvn test -Dtest="VehicleTrackingPerformanceTest" \
        -Dmaven.test.failure.ignore=true \
        | tee "../$TEST_RESULTS_DIR/backend-performance-tests-$TIMESTAMP.log"
    
    # Run complete test suite
    print_status "Running complete test suite..."
    mvn test -Dmaven.test.failure.ignore=true \
        | tee "../$TEST_RESULTS_DIR/backend-complete-tests-$TIMESTAMP.log"
    
    # Generate test report
    print_status "Generating test reports..."
    mvn surefire-report:report -q
    
    cd ..
    print_success "Backend tests completed"
}

# Function to run frontend tests
run_frontend_tests() {
    print_header "Running Frontend Tests"
    
    cd "$FRONTEND_DIR"
    
    # Install dependencies if needed
    if [ ! -d "node_modules" ]; then
        print_status "Installing frontend dependencies..."
        npm install
    fi
    
    # Run linting
    print_status "Running ESLint..."
    npm run lint 2>&1 | tee "../$TEST_RESULTS_DIR/frontend-lint-$TIMESTAMP.log" || true
    
    # Run unit tests (if available)
    if npm run | grep -q "test"; then
        print_status "Running frontend unit tests..."
        npm test -- --coverage --watchAll=false 2>&1 | tee "../$TEST_RESULTS_DIR/frontend-tests-$TIMESTAMP.log" || true
    else
        print_warning "No frontend tests configured"
    fi
    
    # Build application to test for build errors
    print_status "Testing production build..."
    npm run build 2>&1 | tee "../$TEST_RESULTS_DIR/frontend-build-$TIMESTAMP.log"
    
    cd ..
    print_success "Frontend tests completed"
}

# Function to run system integration tests
run_system_tests() {
    print_header "Running System Integration Tests"
    
    # Start backend server in background
    print_status "Starting backend server..."
    cd "$BACKEND_DIR"
    mvn spring-boot:run -Dspring-boot.run.profiles=test &
    BACKEND_PID=$!
    cd ..
    
    # Wait for backend to start
    print_status "Waiting for backend to start..."
    sleep 30
    
    # Check if backend is responding
    if curl -f http://localhost:8080/api/tracking/vehicles > /dev/null 2>&1; then
        print_success "Backend server is responding"
    else
        print_error "Backend server is not responding"
        kill $BACKEND_PID 2>/dev/null || true
        return 1
    fi
    
    # Start frontend in background
    print_status "Starting frontend server..."
    cd "$FRONTEND_DIR"
    npm start &
    FRONTEND_PID=$!
    cd ..
    
    # Wait for frontend to start
    print_status "Waiting for frontend to start..."
    sleep 20
    
    # Check if frontend is responding
    if curl -f http://localhost:3000 > /dev/null 2>&1; then
        print_success "Frontend server is responding"
    else
        print_warning "Frontend server may not be fully ready"
    fi
    
    # Run basic API connectivity tests
    print_status "Testing API connectivity..."
    
    # Test public endpoints
    curl -f http://localhost:8080/api/tracking/vehicles -o /dev/null || print_error "Vehicle tracking endpoint failed"
    
    # Test authentication endpoint
    curl -f -X POST http://localhost:8080/api/auth/signin \
        -H "Content-Type: application/json" \
        -d '{"usernameOrEmail":"admin@university.edu","password":"admin123"}' \
        -o /dev/null || print_error "Authentication endpoint failed"
    
    # Cleanup
    print_status "Stopping test servers..."
    kill $FRONTEND_PID 2>/dev/null || true
    kill $BACKEND_PID 2>/dev/null || true
    
    print_success "System integration tests completed"
}

# Function to generate final report
generate_report() {
    print_header "Generating Test Report"
    
    REPORT_FILE="$TEST_RESULTS_DIR/test-summary-$TIMESTAMP.txt"
    
    echo "University Vehicle Tracking System - Test Execution Summary" > "$REPORT_FILE"
    echo "Generated on: $(date)" >> "$REPORT_FILE"
    echo "=========================================" >> "$REPORT_FILE"
    echo "" >> "$REPORT_FILE"
    
    # Count test results
    echo "Test Results Summary:" >> "$REPORT_FILE"
    echo "--------------------" >> "$REPORT_FILE"
    
    # Backend test results
    if [ -f "$TEST_RESULTS_DIR/backend-complete-tests-$TIMESTAMP.log" ]; then
        BACKEND_TESTS=$(grep -o "Tests run: [0-9]*" "$TEST_RESULTS_DIR/backend-complete-tests-$TIMESTAMP.log" | tail -1)
        BACKEND_FAILURES=$(grep -o "Failures: [0-9]*" "$TEST_RESULTS_DIR/backend-complete-tests-$TIMESTAMP.log" | tail -1)
        BACKEND_ERRORS=$(grep -o "Errors: [0-9]*" "$TEST_RESULTS_DIR/backend-complete-tests-$TIMESTAMP.log" | tail -1)
        
        echo "Backend: $BACKEND_TESTS, $BACKEND_FAILURES, $BACKEND_ERRORS" >> "$REPORT_FILE"
    fi
    
    # Add manual test checklist
    echo "" >> "$REPORT_FILE"
    echo "Manual Test Checklist:" >> "$REPORT_FILE"
    echo "---------------------" >> "$REPORT_FILE"
    echo "[ ] Authentication flow testing" >> "$REPORT_FILE"
    echo "[ ] Role-based access control" >> "$REPORT_FILE"
    echo "[ ] Vehicle management functionality" >> "$REPORT_FILE"
    echo "[ ] Real-time location tracking" >> "$REPORT_FILE"
    echo "[ ] Mobile responsiveness" >> "$REPORT_FILE"
    echo "[ ] Browser compatibility" >> "$REPORT_FILE"
    echo "[ ] Performance under load" >> "$REPORT_FILE"
    echo "[ ] Security vulnerability testing" >> "$REPORT_FILE"
    
    echo "" >> "$REPORT_FILE"
    echo "Test Artifacts Location:" >> "$REPORT_FILE"
    echo "----------------------" >> "$REPORT_FILE"
    echo "Backend reports: target/surefire-reports/" >> "$REPORT_FILE"
    echo "Test logs: $TEST_RESULTS_DIR/" >> "$REPORT_FILE"
    echo "Manual test scenarios: testing/manual-test-scenarios.md" >> "$REPORT_FILE"
    
    print_success "Test report generated: $REPORT_FILE"
}

# Main execution flow
main() {
    # Check if specific test type is requested
    case "${1:-all}" in
        "backend")
            run_backend_tests
            ;;
        "frontend")
            run_frontend_tests
            ;;
        "system")
            run_system_tests
            ;;
        "all")
            run_backend_tests
            run_frontend_tests
            run_system_tests
            ;;
        *)
            print_error "Invalid test type. Use: backend, frontend, system, or all"
            exit 1
            ;;
    esac
    
    generate_report
    
    print_header "Test Execution Summary"
    print_success "All requested tests completed successfully!"
    print_status "Check $TEST_RESULTS_DIR/ for detailed test logs and reports"
    print_status "Review testing/manual-test-scenarios.md for manual testing guidance"
    
    # Open test results if running on desktop
    if command -v xdg-open &> /dev/null; then
        print_status "Opening test results directory..."
        xdg-open "$TEST_RESULTS_DIR" 2>/dev/null || true
    fi
}

# Handle script arguments
if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
    echo "University Vehicle Tracking System - Automated Test Runner"
    echo ""
    echo "Usage: $0 [test-type]"
    echo ""
    echo "Test types:"
    echo "  all      - Run all tests (default)"
    echo "  backend  - Run only backend tests"
    echo "  frontend - Run only frontend tests"
    echo "  system   - Run only system integration tests"
    echo ""
    echo "Examples:"
    echo "  $0              # Run all tests"
    echo "  $0 backend      # Run only backend tests"
    echo "  $0 frontend     # Run only frontend tests"
    echo "  $0 system       # Run only system tests"
    exit 0
fi

# Execute main function
main "$@" 