package com.vehicletracking;

import com.vehicletracking.controller.AuthControllerTest;
import com.vehicletracking.integration.VehicleIntegrationTest;
import com.vehicletracking.performance.VehicleTrackingPerformanceTest;
import com.vehicletracking.security.SecurityTest;
import com.vehicletracking.service.VehicleServiceTest;
import com.vehicletracking.validation.VehicleNumberValidatorTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("University Vehicle Tracking System - Complete Test Suite")
@SelectClasses({
    // Unit Tests - Fast, isolated tests
    VehicleNumberValidatorTest.class,
    VehicleServiceTest.class,
    
    // API Tests - Test REST endpoints
    AuthControllerTest.class,
    
    // Integration Tests - Test complete flows
    VehicleIntegrationTest.class,
    
    // Security Tests - Test authentication, authorization, and data protection
    SecurityTest.class,
    
    // Performance Tests - Test system under load (run last)
    VehicleTrackingPerformanceTest.class
})
public class TestSuite {
    // This class serves as a test suite configuration
    // Tests will be executed in the order specified above
} 