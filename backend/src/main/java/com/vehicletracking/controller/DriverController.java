package com.vehicletracking.controller;

import com.vehicletracking.dto.MessageResponse;
import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.model.User;
import com.vehicletracking.service.DashboardService;
import com.vehicletracking.service.UserService;
import com.vehicletracking.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/driver")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('DRIVER')")
public class DriverController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    /**
     * Get driver dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDriverDashboardStats(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            String driverId = userOpt.get().getId().toString();
            return ResponseEntity.ok(dashboardService.getDriverDashboardStats(driverId));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching driver dashboard stats: " + e.getMessage()));
        }
    }

    /**
     * Get assigned vehicle for current driver
     */
    @GetMapping("/vehicle")
    public ResponseEntity<?> getAssignedVehicle(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            String driverId = userOpt.get().getId();
            return ResponseEntity.ok(vehicleService.getVehicleByDriverId(driverId));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching assigned vehicle: " + e.getMessage()));
        }
    }

    /**
     * Update vehicle location
     */
    @PostMapping("/vehicle/{vehicleId}/location")
    public ResponseEntity<?> updateVehicleLocation(@PathVariable String vehicleId,
                                                  @Valid @RequestBody LocationUpdateDto locationUpdate,
                                                  Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            // Verify driver is assigned to this vehicle
            String driverId = userOpt.get().getId();
            Optional<com.vehicletracking.dto.VehicleResponseDto> vehicleOpt = vehicleService.getVehicleByDriverId(driverId);
            
            if (vehicleOpt.isEmpty() || !vehicleOpt.get().getId().equals(vehicleId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You are not authorized to update this vehicle's location"));
            }
            
            return ResponseEntity.ok(vehicleService.updateVehicleLocation(vehicleId, locationUpdate));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error updating vehicle location: " + e.getMessage()));
        }
    }

    /**
     * Get vehicle status
     */
    @GetMapping("/vehicle/{vehicleId}/status")
    public ResponseEntity<?> getVehicleStatus(@PathVariable String vehicleId,
                                            Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            // Verify driver is assigned to this vehicle
            String driverId = userOpt.get().getId();
            Optional<com.vehicletracking.dto.VehicleResponseDto> vehicleOpt = vehicleService.getVehicleByDriverId(driverId);
            
            if (vehicleOpt.isEmpty() || !vehicleOpt.get().getId().equals(vehicleId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("You are not authorized to view this vehicle's status"));
            }
            
            return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching vehicle status: " + e.getMessage()));
        }
    }

    /**
     * Get driver profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getDriverProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching driver profile: " + e.getMessage()));
        }
    }

    /**
     * Update driver profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateDriverProfile(@Valid @RequestBody com.vehicletracking.dto.UserRegistrationDto userDto,
                                                Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            String userId = userOpt.get().getId();
            return ResponseEntity.ok(userService.updateUser(userId, userDto));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error updating driver profile: " + e.getMessage()));
        }
    }
}
