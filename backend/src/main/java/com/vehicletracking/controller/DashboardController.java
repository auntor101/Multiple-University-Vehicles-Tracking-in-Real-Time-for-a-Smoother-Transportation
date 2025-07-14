package com.vehicletracking.controller;

import com.vehicletracking.dto.MessageResponse;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import com.vehicletracking.service.DashboardService;
import com.vehicletracking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    /**
     * Get dashboard statistics based on user role
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            Role userRole = user.getRole();
            
            switch (userRole) {
                case ADMIN:
                    return ResponseEntity.ok(dashboardService.getAdminDashboardStats());
                case DRIVER:
                    return ResponseEntity.ok(dashboardService.getDriverDashboardStats(user.getId().toString()));
                case TEACHER:
                case STUDENT:
                    // For teachers and students, provide basic dashboard info
                    return ResponseEntity.ok(dashboardService.getSystemOverview());
                default:
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Access denied for role: " + userRole));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching dashboard stats: " + e.getMessage()));
        }
    }

    /**
     * Get user activity statistics for specific role
     */
    @GetMapping("/user-activity")
    public ResponseEntity<?> getUserActivity(@RequestParam(required = false) String role,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            
            // Only admin can view activity for other roles
            if (role != null && !user.getRole().equals(Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only administrators can view activity for other roles"));
            }
            
            Role targetRole = role != null ? Role.valueOf(role.toUpperCase()) : user.getRole();
            return ResponseEntity.ok(dashboardService.getUserActivityStats(targetRole));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching user activity: " + e.getMessage()));
        }
    }

    /**
     * Get trip analytics for date range
     */
    @GetMapping("/trip-analytics")
    public ResponseEntity<?> getTripAnalytics(@RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate,
                                            Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            
            // Only admin and drivers can view trip analytics
            if (!user.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.DRIVER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Access denied for role: " + user.getRole()));
            }
            
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            return ResponseEntity.ok(dashboardService.getTripAnalytics(start, end));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching trip analytics: " + e.getMessage()));
        }
    }

    /**
     * Get fleet performance metrics (Admin only)
     */
    @GetMapping("/fleet-performance")
    public ResponseEntity<?> getFleetPerformance(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            
            if (!user.getRole().equals(Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only administrators can view fleet performance"));
            }
            
            return ResponseEntity.ok(dashboardService.getFleetPerformanceMetrics());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching fleet performance: " + e.getMessage()));
        }
    }

    /**
     * Get vehicle usage statistics for specific vehicle
     */
    @GetMapping("/vehicle-usage/{vehicleId}")
    public ResponseEntity<?> getVehicleUsage(@PathVariable String vehicleId,
                                           @RequestParam(required = false) String startDate,
                                           @RequestParam(required = false) String endDate,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            
            // Only admin and drivers can view vehicle usage
            if (!user.getRole().equals(Role.ADMIN) && !user.getRole().equals(Role.DRIVER)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Access denied for role: " + user.getRole()));
            }
            
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            
            return ResponseEntity.ok(dashboardService.getVehicleUsageStats(vehicleId, start, end));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching vehicle usage: " + e.getMessage()));
        }
    }

    /**
     * Get system overview
     */
    @GetMapping("/system-overview")
    public ResponseEntity<?> getSystemOverview(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            return ResponseEntity.ok(dashboardService.getSystemOverview());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching system overview: " + e.getMessage()));
        }
    }
}
