package com.vehicletracking.controller;

import com.vehicletracking.dto.MessageResponse;
import com.vehicletracking.service.DashboardService;
import com.vehicletracking.service.UserService;
import com.vehicletracking.service.VehicleService;
import com.vehicletracking.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private AnnouncementService announcementService;

    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            return ResponseEntity.ok(dashboardService.getAdminDashboardStats());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching dashboard stats: " + e.getMessage()));
        }
    }

    /**
     * Get system overview
     */
    @GetMapping("/system/overview")
    public ResponseEntity<?> getSystemOverview() {
        try {
            return ResponseEntity.ok(dashboardService.getSystemOverview());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching system overview: " + e.getMessage()));
        }
    }

    /**
     * Get fleet performance metrics
     */
    @GetMapping("/fleet/performance")
    public ResponseEntity<?> getFleetPerformance() {
        try {
            return ResponseEntity.ok(dashboardService.getFleetPerformanceMetrics());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching fleet performance: " + e.getMessage()));
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/users/stats")
    public ResponseEntity<?> getUserStats() {
        try {
            return ResponseEntity.ok(userService.getUserStats());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching user stats: " + e.getMessage()));
        }
    }

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching users: " + e.getMessage()));
        }
    }

    /**
     * Get all vehicles
     */
    @GetMapping("/vehicles")
    public ResponseEntity<?> getAllVehicles() {
        try {
            return ResponseEntity.ok(vehicleService.getAllVehicles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching vehicles: " + e.getMessage()));
        }
    }

    /**
     * Get vehicle usage statistics
     */
    @GetMapping("/vehicles/{vehicleId}/usage")
    public ResponseEntity<?> getVehicleUsage(@PathVariable String vehicleId,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            return ResponseEntity.ok(dashboardService.getVehicleUsageStats(vehicleId, start, end));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching vehicle usage: " + e.getMessage()));
        }
    }

    /**
     * Get trip analytics
     */
    @GetMapping("/trips/analytics")
    public ResponseEntity<?> getTripAnalytics(@RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            return ResponseEntity.ok(dashboardService.getTripAnalytics(start, end));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching trip analytics: " + e.getMessage()));
        }
    }

    /**
     * Update user status
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, 
                                            @RequestParam boolean isActive) {
        try {
            return ResponseEntity.ok(userService.updateUserStatus(userId, isActive));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error updating user status: " + e.getMessage()));
        }
    }

    /**
     * Search users
     */
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        try {
            return ResponseEntity.ok(userService.searchUsers(query));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error searching users: " + e.getMessage()));
        }
    }

    /**
     * Search vehicles
     */
    @GetMapping("/vehicles/search")
    public ResponseEntity<?> searchVehicles(@RequestParam String query) {
        try {
            return ResponseEntity.ok(vehicleService.searchVehicles(query));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error searching vehicles: " + e.getMessage()));
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error deleting user: " + e.getMessage()));
        }
    }

    /**
     * Delete vehicle
     */
    @DeleteMapping("/vehicles/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long vehicleId) {
        try {
            vehicleService.deleteVehicle(vehicleId);
            return ResponseEntity.ok(new MessageResponse("Vehicle deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error deleting vehicle: " + e.getMessage()));
        }
    }
}
