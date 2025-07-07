package com.vehicletracking.controller;

import com.vehicletracking.dto.*;
import com.vehicletracking.model.VehicleType;
import com.vehicletracking.model.VehicleStatus;
import com.vehicletracking.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    
    @Autowired
    private VehicleService vehicleService;
    
    // Create Vehicle (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
        try {
            VehicleResponseDto response = vehicleService.createVehicle(vehicleDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Update Vehicle (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleDto vehicleDto) {
        try {
            VehicleResponseDto response = vehicleService.updateVehicle(id, vehicleDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Delete Vehicle (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok(new MessageResponse("Vehicle deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get Vehicle by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        Optional<VehicleResponseDto> vehicle = vehicleService.getVehicleById(id);
        if (vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get Vehicle by Number
    @GetMapping("/number/{vehicleNumber}")
    public ResponseEntity<?> getVehicleByNumber(@PathVariable String vehicleNumber) {
        Optional<VehicleResponseDto> vehicle = vehicleService.getVehicleByNumber(vehicleNumber);
        if (vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get All Vehicles
    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> getAllVehicles() {
        List<VehicleResponseDto> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Vehicles by University
    @GetMapping("/university/{university}")
    public ResponseEntity<List<VehicleResponseDto>> getVehiclesByUniversity(@PathVariable String university) {
        List<VehicleResponseDto> vehicles = vehicleService.getVehiclesByUniversity(university);
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Vehicles by Type
    @GetMapping("/type/{vehicleType}")
    public ResponseEntity<List<VehicleResponseDto>> getVehiclesByType(@PathVariable VehicleType vehicleType) {
        List<VehicleResponseDto> vehicles = vehicleService.getVehiclesByType(vehicleType);
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Vehicles by Status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<VehicleResponseDto>> getVehiclesByStatus(@PathVariable VehicleStatus status) {
        List<VehicleResponseDto> vehicles = vehicleService.getVehiclesByStatus(status);
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Vehicles by University and Type
    @GetMapping("/university/{university}/type/{vehicleType}")
    public ResponseEntity<List<VehicleResponseDto>> getVehiclesByUniversityAndType(
            @PathVariable String university, @PathVariable VehicleType vehicleType) {
        List<VehicleResponseDto> vehicles = vehicleService.getVehiclesByUniversityAndType(university, vehicleType);
        return ResponseEntity.ok(vehicles);
    }
    
    // Search Vehicles
    @GetMapping("/search")
    public ResponseEntity<List<VehicleResponseDto>> searchVehicles(@RequestParam String q) {
        List<VehicleResponseDto> vehicles = vehicleService.searchVehicles(q);
        return ResponseEntity.ok(vehicles);
    }
    
    // Assign Driver (Admin only)
    @PostMapping("/{vehicleId}/assign-driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignDriver(@PathVariable Long vehicleId, @PathVariable Long driverId) {
        try {
            VehicleResponseDto response = vehicleService.assignDriver(vehicleId, driverId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Unassign Driver (Admin only)
    @PostMapping("/{vehicleId}/unassign-driver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unassignDriver(@PathVariable Long vehicleId) {
        try {
            VehicleResponseDto response = vehicleService.unassignDriver(vehicleId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get Vehicle by Driver ID
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<?> getVehicleByDriverId(@PathVariable Long driverId) {
        Optional<VehicleResponseDto> vehicle = vehicleService.getVehicleByDriverId(driverId);
        if (vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get Vehicles with Location (for tracking)
    @GetMapping("/with-location")
    public ResponseEntity<List<VehicleResponseDto>> getVehiclesWithLocation() {
        List<VehicleResponseDto> vehicles = vehicleService.getVehiclesWithLocation();
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Active Vehicles with Location by University
    @GetMapping("/university/{university}/with-location")
    public ResponseEntity<List<VehicleResponseDto>> getActiveVehiclesWithLocationByUniversity(@PathVariable String university) {
        List<VehicleResponseDto> vehicles = vehicleService.getActiveVehiclesWithLocationByUniversity(university);
        return ResponseEntity.ok(vehicles);
    }
    
    // Update Vehicle Status (Admin and Driver for their own vehicle)
    @PutMapping("/{vehicleId}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DRIVER') and @vehicleService.getVehicleByDriverId(authentication.principal.id).isPresent() and @vehicleService.getVehicleByDriverId(authentication.principal.id).get().id == #vehicleId)")
    public ResponseEntity<?> updateVehicleStatus(@PathVariable Long vehicleId, @RequestBody VehicleStatus status) {
        try {
            VehicleResponseDto response = vehicleService.updateVehicleStatus(vehicleId, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get Active Vehicles
    @GetMapping("/active")
    public ResponseEntity<List<VehicleResponseDto>> getActiveVehicles() {
        List<VehicleResponseDto> vehicles = vehicleService.getActiveVehicles();
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Vehicle Count by University and Status
    @GetMapping("/count/university/{university}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countVehiclesByUniversityAndStatus(
            @PathVariable String university, @PathVariable VehicleStatus status) {
        Long count = vehicleService.countVehiclesByUniversityAndStatus(university, status);
        return ResponseEntity.ok(count);
    }
    
    // Check if Vehicle Number Exists
    @GetMapping("/exists/{vehicleNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> checkVehicleNumberExists(@PathVariable String vehicleNumber) {
        boolean exists = vehicleService.isVehicleNumberExists(vehicleNumber);
        return ResponseEntity.ok(exists);
    }
} 