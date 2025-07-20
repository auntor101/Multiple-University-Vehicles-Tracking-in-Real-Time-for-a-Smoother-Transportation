package com.vehicletracking.controller;

import com.vehicletracking.dto.*;
import com.vehicletracking.model.VehicleType;
import com.vehicletracking.model.VehicleStatus;
import com.vehicletracking.model.Vehicle;
import com.vehicletracking.service.FirebaseDataService;
import com.vehicletracking.service.FirebaseMessagingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = {"${app.cors.allowed-origins}"}, maxAge = 3600)
@RestController
@RequestMapping("/api/firebase/vehicles")
public class FirebaseVehicleController {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseVehicleController.class);
    
    @Autowired
    private FirebaseDataService firebaseDataService;
    
    @Autowired
    private FirebaseMessagingService messagingService;
    
    // Create Vehicle (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<Object>> createVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
        return firebaseDataService.createVehicle(vehicleDto)
            .thenApply(vehicle -> {
                logger.info("Vehicle created successfully: {}", vehicle.getVehicleNumber());
                
                // Send notification to admins
                Map<String, String> data = new HashMap<>();
                data.put("vehicleId", vehicle.getId().toString());
                data.put("vehicleNumber", vehicle.getVehicleNumber());
                
                messagingService.sendToRole(
                    com.vehicletracking.model.Role.ADMIN,
                    FirebaseMessagingService.NotificationType.GENERAL_ANNOUNCEMENT,
                    "New vehicle " + vehicle.getVehicleNumber() + " has been added to the fleet",
                    data
                );
                
                return ResponseEntity.<Object>ok(vehicle);
            })
            .exceptionally(throwable -> {
                logger.error("Failed to create vehicle: {}", throwable.getMessage());
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to create vehicle: " + throwable.getMessage()));
            });
    }
    
    // Get All Active Vehicles
    @GetMapping
    public CompletableFuture<ResponseEntity<List<Vehicle>>> getAllVehicles() {
        return firebaseDataService.getAllActiveVehicles()
            .thenApply(vehicles -> {
                logger.debug("Retrieved {} vehicles", vehicles.size());
                return ResponseEntity.ok(vehicles);
            })
            .exceptionally(throwable -> {
                logger.error("Failed to retrieve vehicles: {}", throwable.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }
    
    // Get Vehicles with Location (for tracking)
    @GetMapping("/with-location")
    public CompletableFuture<ResponseEntity<List<Vehicle>>> getVehiclesWithLocation() {
        return firebaseDataService.getVehiclesWithLocation()
            .thenApply(vehicles -> {
                logger.debug("Retrieved {} vehicles with location", vehicles.size());
                return ResponseEntity.ok(vehicles);
            })
            .exceptionally(throwable -> {
                logger.error("Failed to retrieve vehicles with location: {}", throwable.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }
    
    // Update Vehicle Location (Driver only for their assigned vehicle)
    @PostMapping("/{vehicleId}/location")
    @PreAuthorize("hasRole('DRIVER')")
    public CompletableFuture<ResponseEntity<Object>> updateVehicleLocation(
            @PathVariable String vehicleId,
            @Valid @RequestBody LocationUpdateDto locationUpdate) {
        
        return firebaseDataService.updateVehicleLocation(vehicleId, locationUpdate)
            .thenApply(vehicle -> {
                logger.debug("Updated location for vehicle: {}", vehicleId);
                
                // Send real-time notification for significant updates
                if (locationUpdate.getSpeed() != null && locationUpdate.getSpeed() > 0) {
                    Map<String, String> data = new HashMap<>();
                    data.put("vehicleId", vehicleId);
                    data.put("latitude", locationUpdate.getLatitude().toString());
                    data.put("longitude", locationUpdate.getLongitude().toString());
                    data.put("speed", locationUpdate.getSpeed().toString());
                    
                    // Notify students/staff about vehicle movement
                    messagingService.sendToRole(
                        com.vehicletracking.model.Role.STUDENT,
                        FirebaseMessagingService.NotificationType.VEHICLE_DEPARTED,
                        "Vehicle " + vehicle.getVehicleNumber() + " is on the move",
                        data
                    );
                }
                
                return ResponseEntity.<Object>ok(vehicle);
            })
            .exceptionally(throwable -> {
                logger.error("Failed to update vehicle location: {}", throwable.getMessage());
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to update location: " + throwable.getMessage()));
            });
    }
    
    // Send Emergency Alert
    @PostMapping("/{vehicleId}/emergency")
    @PreAuthorize("hasRole('DRIVER')")
    public CompletableFuture<ResponseEntity<MessageResponse>> sendEmergencyAlert(
            @PathVariable String vehicleId,
            @RequestBody Map<String, String> emergencyData) {
        
        String vehicleNumber = emergencyData.getOrDefault("vehicleNumber", "Unknown");
        String location = emergencyData.getOrDefault("location", "Unknown location");
        String details = emergencyData.getOrDefault("details", "Emergency situation");
        
        return messagingService.sendEmergencyAlert(vehicleNumber, location, details)
            .thenApply(response -> {
                logger.warn("Emergency alert sent for vehicle {}: {}", vehicleNumber, details);
                
                // Store emergency data in Firebase
                Map<String, Object> emergencyRecord = new HashMap<>();
                emergencyRecord.put("vehicleId", vehicleId);
                emergencyRecord.put("vehicleNumber", vehicleNumber);
                emergencyRecord.put("location", location);
                emergencyRecord.put("details", details);
                emergencyRecord.put("timestamp", System.currentTimeMillis());
                emergencyRecord.put("status", "ACTIVE");
                
                return ResponseEntity.ok(new MessageResponse("Emergency alert sent successfully"));
            })
            .exceptionally(throwable -> {
                logger.error("Failed to send emergency alert: {}", throwable.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to send emergency alert"));
            });
    }
    
    // Get Real-time Vehicle Statistics
    @GetMapping("/stats")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getVehicleStats() {
        return firebaseDataService.getAllActiveVehicles()
            .thenApply(vehicles -> {
                Map<String, Object> stats = new HashMap<>();
                
                // Calculate statistics
                long totalVehicles = vehicles.size();
                long activeVehicles = vehicles.stream()
                    .filter(v -> v.getStatus() == VehicleStatus.ACTIVE)
                    .count();
                long vehiclesWithLocation = vehicles.stream()
                    .filter(v -> v.getCurrentLatitude() != null && v.getCurrentLongitude() != null)
                    .count();
                long movingVehicles = vehicles.stream()
                    .filter(v -> v.getCurrentSpeed() != null && v.getCurrentSpeed() > 0)
                    .count();
                
                // Average fuel level
                double avgFuelLevel = vehicles.stream()
                    .filter(v -> v.getFuelLevel() != null)
                    .mapToDouble(Vehicle::getFuelLevel)
                    .average()
                    .orElse(0.0);
                
                stats.put("totalVehicles", totalVehicles);
                stats.put("activeVehicles", activeVehicles);
                stats.put("vehiclesWithLocation", vehiclesWithLocation);
                stats.put("movingVehicles", movingVehicles);
                stats.put("averageFuelLevel", Math.round(avgFuelLevel * 100.0) / 100.0);
                stats.put("lastUpdated", System.currentTimeMillis());
                
                // Vehicle type breakdown
                Map<String, Long> typeBreakdown = vehicles.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                        v -> v.getVehicleType().name(),
                        java.util.stream.Collectors.counting()
                    ));
                stats.put("vehicleTypes", typeBreakdown);
                
                return ResponseEntity.ok(stats);
            })
            .exceptionally(throwable -> {
                logger.error("Failed to calculate vehicle stats: {}", throwable.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }
    
    // Send Vehicle Arrival Notification
    @PostMapping("/{vehicleId}/arrival")
    @PreAuthorize("hasRole('DRIVER')")
    public CompletableFuture<ResponseEntity<MessageResponse>> notifyVehicleArrival(
            @PathVariable String vehicleId,
            @RequestBody Map<String, String> arrivalData) {
        
        String vehicleNumber = arrivalData.getOrDefault("vehicleNumber", "Unknown");
        String stopName = arrivalData.getOrDefault("stopName", "Unknown stop");
        String route = arrivalData.getOrDefault("route", "Unknown route");
        
        return messagingService.sendVehicleArrivalNotification(vehicleNumber, stopName, route)
            .thenApply(response -> {
                logger.info("Arrival notification sent for vehicle {} at {}", vehicleNumber, stopName);
                return ResponseEntity.ok(new MessageResponse("Arrival notification sent"));
            })
            .exceptionally(throwable -> {
                logger.error("Failed to send arrival notification: {}", throwable.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to send notification"));
            });
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Firebase Vehicle Controller");
        health.put("timestamp", System.currentTimeMillis());
        health.put("firebase", "Connected");
        
        return ResponseEntity.ok(health);
    }
} 