package com.vehicletracking.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.dto.UserLoginDto;
import com.vehicletracking.model.*;
import com.vehicletracking.repository.UserRepository;
import com.vehicletracking.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class VehicleTrackingPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int CONCURRENT_USERS = 50;
    private static final int VEHICLES_COUNT = 100;
    private static final int LOCATION_UPDATES_PER_VEHICLE = 10;

    @BeforeEach
    void setUp() {
        // Create test data for performance testing
        createTestUsers();
        createTestVehicles();
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testConcurrentLocationUpdates() throws Exception {
        // Performance test: Multiple drivers updating locations simultaneously
        
        List<Vehicle> vehicles = vehicleRepository.findAll();
        assertTrue(vehicles.size() >= 10, "Need at least 10 vehicles for concurrent testing");
        
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // Submit concurrent location update tasks
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int threadIndex = i;
            executorService.submit(() -> {
                try {
                    Vehicle vehicle = vehicles.get(threadIndex % vehicles.size());
                    
                    if (vehicle.getDriver() != null) {
                        // Simulate driver updating location
                        LocationUpdateDto locationUpdate = new LocationUpdateDto();
                        locationUpdate.setLatitude(40.7128 + (Math.random() * 0.1));
                        locationUpdate.setLongitude(-74.0060 + (Math.random() * 0.1));
                        locationUpdate.setSpeed(Math.random() * 60); // Random speed 0-60 km/h
                        locationUpdate.setDirection(getRandomDirection());
                        locationUpdate.setFuelLevel(50.0 + (Math.random() * 50)); // 50-100%
                        
                        // Simulate authenticated request (in real test, would need proper JWT)
                        MvcResult result = mockMvc.perform(post("/api/tracking/location/{vehicleId}", vehicle.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(locationUpdate))
                                .header("Authorization", "Bearer mock-token")) // Mock for performance test
                                .andReturn();
                        
                        if (result.getResponse().getStatus() == 200) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        boolean completed = latch.await(25, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        executorService.shutdown();
        
        // Performance assertions
        assertTrue(completed, "All concurrent requests should complete within timeout");
        
        long totalTime = endTime - startTime;
        double requestsPerSecond = (double) CONCURRENT_USERS / (totalTime / 1000.0);
        
        System.out.printf("Performance Results:%n");
        System.out.printf("Total time: %d ms%n", totalTime);
        System.out.printf("Requests per second: %.2f%n", requestsPerSecond);
        System.out.printf("Success count: %d%n", successCount.get());
        System.out.printf("Error count: %d%n", errorCount.get());
        
        // Assert reasonable performance (should handle at least 10 requests per second)
        assertTrue(requestsPerSecond >= 10.0, "System should handle at least 10 requests per second");
        
        // Assert low error rate (less than 10% errors acceptable under load)
        double errorRate = (double) errorCount.get() / CONCURRENT_USERS;
        assertTrue(errorRate < 0.1, "Error rate should be less than 10%");
    }

    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    void testHighVolumeVehicleRetrieval() throws Exception {
        // Performance test: Retrieving large number of vehicles with location data
        
        long startTime = System.currentTimeMillis();
        
        // Execute multiple concurrent requests for vehicle data
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(100); // 100 requests
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(get("/api/tracking/vehicles"))
                            .andExpect(status().isOk())
                            .andReturn();
                    
                    String responseBody = result.getResponse().getContentAsString();
                    assertNotNull(responseBody);
                    assertFalse(responseBody.isEmpty());
                    
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(15, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        executorService.shutdown();
        
        assertTrue(completed, "All vehicle retrieval requests should complete");
        assertEquals(100, successCount.get(), "All requests should succeed");
        
        long totalTime = endTime - startTime;
        double requestsPerSecond = 100.0 / (totalTime / 1000.0);
        
        System.out.printf("Vehicle Retrieval Performance:%n");
        System.out.printf("Total time: %d ms%n", totalTime);
        System.out.printf("Requests per second: %.2f%n", requestsPerSecond);
        
        // Should handle at least 20 read requests per second
        assertTrue(requestsPerSecond >= 20.0, "System should handle at least 20 read requests per second");
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void testDatabasePerformanceUnderLoad() throws Exception {
        // Test database performance with concurrent read/write operations
        
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(150); // Mix of read and write operations
        AtomicInteger readSuccess = new AtomicInteger(0);
        AtomicInteger writeSuccess = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // Submit mix of read and write operations
        for (int i = 0; i < 150; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    if (index % 3 == 0) {
                        // Write operation: Update vehicle location
                        List<Vehicle> vehicles = vehicleRepository.findAll();
                        if (!vehicles.isEmpty()) {
                            Vehicle vehicle = vehicles.get(index % vehicles.size());
                            vehicle.setCurrentLatitude(40.7128 + Math.random() * 0.1);
                            vehicle.setCurrentLongitude(-74.0060 + Math.random() * 0.1);
                            vehicle.setLastLocationUpdate(LocalDateTime.now());
                            vehicleRepository.save(vehicle);
                            writeSuccess.incrementAndGet();
                        }
                    } else {
                        // Read operation: Query vehicles
                        List<Vehicle> vehicles = vehicleRepository.findVehiclesWithLocation();
                        assertNotNull(vehicles);
                        readSuccess.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.err.println("Database operation failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(12, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        executorService.shutdown();
        
        assertTrue(completed, "All database operations should complete");
        
        long totalTime = endTime - startTime;
        double operationsPerSecond = 150.0 / (totalTime / 1000.0);
        
        System.out.printf("Database Performance Under Load:%n");
        System.out.printf("Total time: %d ms%n", totalTime);
        System.out.printf("Operations per second: %.2f%n", operationsPerSecond);
        System.out.printf("Read operations: %d%n", readSuccess.get());
        System.out.printf("Write operations: %d%n", writeSuccess.get());
        
        // Database should handle reasonable load
        assertTrue(operationsPerSecond >= 50.0, "Database should handle at least 50 operations per second");
        assertTrue(readSuccess.get() > 80, "Most read operations should succeed");
        assertTrue(writeSuccess.get() > 30, "Most write operations should succeed");
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMemoryUsageUnderLoad() throws Exception {
        // Test memory usage during high-load operations
        
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Generate load
        List<Future<String>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        
        for (int i = 0; i < 200; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    // Simulate vehicle data retrieval and processing
                    MvcResult result = mockMvc.perform(get("/api/vehicles"))
                            .andExpect(status().isOk())
                            .andReturn();
                    
                    return result.getResponse().getContentAsString();
                } catch (Exception e) {
                    return "error";
                }
            }));
        }
        
        // Wait for all operations to complete
        for (Future<String> future : futures) {
            future.get(1, TimeUnit.SECONDS);
        }
        
        executorService.shutdown();
        
        // Force garbage collection and measure memory
        System.gc();
        Thread.sleep(100); // Give GC time to run
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        double memoryIncreaseMB = memoryIncrease / (1024.0 * 1024.0);
        
        System.out.printf("Memory Usage Test:%n");
        System.out.printf("Initial memory: %.2f MB%n", initialMemory / (1024.0 * 1024.0));
        System.out.printf("Final memory: %.2f MB%n", finalMemory / (1024.0 * 1024.0));
        System.out.printf("Memory increase: %.2f MB%n", memoryIncreaseMB);
        
        // Memory increase should be reasonable (less than 100MB for this load)
        assertTrue(memoryIncreaseMB < 100.0, "Memory increase should be less than 100MB");
    }

    private void createTestUsers() {
        // Create test drivers
        for (int i = 1; i <= 20; i++) {
            User driver = new User();
            driver.setUsername("driver" + i);
            driver.setEmail("driver" + i + "@test.com");
            driver.setPassword(passwordEncoder.encode("password123"));
            driver.setFirstName("Driver");
            driver.setLastName("" + i);
            driver.setRole(Role.DRIVER);
            driver.setUniversity("Test University");
            driver.setLicenseNumber("DL" + String.format("%09d", i));
            driver.setIsActive(true);
            userRepository.save(driver);
        }
    }

    private void createTestVehicles() {
        List<User> drivers = userRepository.findByRole(Role.DRIVER);
        
        // Create test vehicles
        for (int i = 1; i <= VEHICLES_COUNT; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleNumber(getVehicleNumber(i));
            vehicle.setModel("Test Bus " + i);
            vehicle.setBrand("TestBrand");
            vehicle.setCapacity(40 + (i % 20)); // Vary capacity
            vehicle.setVehicleType(getVehicleType(i));
            vehicle.setStatus(VehicleStatus.ACTIVE);
            vehicle.setUniversity("Test University");
            
            // Assign driver if available
            if (!drivers.isEmpty() && i <= drivers.size()) {
                vehicle.setDriver(drivers.get(i - 1));
            }
            
            // Set random location in NYC area
            vehicle.setCurrentLatitude(40.7128 + (Math.random() * 0.1));
            vehicle.setCurrentLongitude(-74.0060 + (Math.random() * 0.1));
            vehicle.setCurrentSpeed(Math.random() * 50);
            vehicle.setFuelLevel(50.0 + (Math.random() * 50));
            vehicle.setLastLocationUpdate(LocalDateTime.now().minusMinutes((long)(Math.random() * 30)));
            vehicle.setIsActive(true);
            
            vehicleRepository.save(vehicle);
        }
    }

    private String getVehicleNumber(int index) {
        String prefix;
        if (index % 4 == 0) {
            prefix = "STU";
        } else if (index % 4 == 1) {
            prefix = "TCH";
        } else if (index % 4 == 2) {
            prefix = "OFC";
        } else {
            prefix = "GEN";
        }
        return prefix + "-" + String.format("%03d", index);
    }

    private VehicleType getVehicleType(int index) {
        VehicleType[] types = VehicleType.values();
        return types[index % types.length];
    }

    private String getRandomDirection() {
        String[] directions = {"North", "South", "East", "West", "Northeast", "Northwest", "Southeast", "Southwest"};
        return directions[(int)(Math.random() * directions.length)];
    }
} 