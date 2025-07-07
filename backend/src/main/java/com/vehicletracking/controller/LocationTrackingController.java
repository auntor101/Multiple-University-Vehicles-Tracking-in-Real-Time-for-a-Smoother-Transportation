package com.vehicletracking.controller;

import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.dto.MessageResponse;
import com.vehicletracking.dto.VehicleResponseDto;
import com.vehicletracking.security.UserPrincipal;
import com.vehicletracking.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tracking")
public class LocationTrackingController {
    
    @Autowired
    private VehicleService vehicleService;
    
    // Update Vehicle Location (Driver only for their assigned vehicle)
    @PostMapping("/location/{vehicleId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> updateVehicleLocation(
            @PathVariable Long vehicleId,
            @Valid @RequestBody LocationUpdateDto locationUpdate,
            Authentication authentication) {
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // Check if the driver is assigned to this vehicle
            Optional<VehicleResponseDto> driverVehicle = vehicleService.getVehicleByDriverId(userPrincipal.getId());
            if (driverVehicle.isEmpty() || !driverVehicle.get().getId().equals(vehicleId)) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("You are not authorized to update location for this vehicle"));
            }
            
            VehicleResponseDto response = vehicleService.updateVehicleLocation(vehicleId, locationUpdate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Update Current Driver's Vehicle Location
    @PostMapping("/my-vehicle/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> updateMyVehicleLocation(
            @Valid @RequestBody LocationUpdateDto locationUpdate,
            Authentication authentication) {
        
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // Get driver's assigned vehicle
            Optional<VehicleResponseDto> driverVehicle = vehicleService.getVehicleByDriverId(userPrincipal.getId());
            if (driverVehicle.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("No vehicle assigned to this driver"));
            }
            
            VehicleResponseDto response = vehicleService.updateVehicleLocation(
                driverVehicle.get().getId(), locationUpdate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get All Vehicles with Location (for tracking map)
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponseDto>> getAllVehiclesWithLocation() {
        List<VehicleResponseDto> vehicles = vehicleService.getVehiclesWithLocation();
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Vehicles with Location by University
    @GetMapping("/vehicles/university/{university}")
    public ResponseEntity<List<VehicleResponseDto>> getVehiclesWithLocationByUniversity(@PathVariable String university) {
        List<VehicleResponseDto> vehicles = vehicleService.getActiveVehiclesWithLocationByUniversity(university);
        return ResponseEntity.ok(vehicles);
    }
    
    // Get Driver's Current Vehicle Location
    @GetMapping("/my-vehicle")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> getMyVehicleLocation(Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            Optional<VehicleResponseDto> driverVehicle = vehicleService.getVehicleByDriverId(userPrincipal.getId());
            if (driverVehicle.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("No vehicle assigned to this driver"));
            }
            
            return ResponseEntity.ok(driverVehicle.get());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get Specific Vehicle Location by ID
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getVehicleLocation(@PathVariable Long vehicleId) {
        Optional<VehicleResponseDto> vehicle = vehicleService.getVehicleById(vehicleId);
        if (vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get Vehicle Location by Vehicle Number
    @GetMapping("/vehicle/number/{vehicleNumber}")
    public ResponseEntity<?> getVehicleLocationByNumber(@PathVariable String vehicleNumber) {
        Optional<VehicleResponseDto> vehicle = vehicleService.getVehicleByNumber(vehicleNumber);
        if (vehicle.isPresent()) {
            return ResponseEntity.ok(vehicle.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 