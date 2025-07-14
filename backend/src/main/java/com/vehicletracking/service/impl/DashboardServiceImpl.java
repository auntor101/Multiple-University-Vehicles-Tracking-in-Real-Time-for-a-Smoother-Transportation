package com.vehicletracking.service.impl;

import com.vehicletracking.model.Role;
import com.vehicletracking.service.DashboardService;
import com.vehicletracking.service.DashboardService.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public AdminDashboardStats getAdminDashboardStats() {
        AdminDashboardStats stats = new AdminDashboardStats();
        
        // Mock data - in production, fetch from database
        stats.setTotalUsers(150L);
        stats.setTotalVehicles(25L);
        stats.setActiveTrips(8L);
        stats.setCompletedTripsToday(12L);
        stats.setTotalDrivers(15L);
        stats.setAvailableVehicles(17L);
        stats.setTotalDistanceTraveled(2500.0);
        
        // Users by role
        Map<String, Long> usersByRole = new HashMap<>();
        usersByRole.put("ADMIN", 5L);
        usersByRole.put("DRIVER", 15L);
        usersByRole.put("TEACHER", 50L);
        usersByRole.put("STUDENT", 80L);
        stats.setUsersByRole(usersByRole);
        
        // Vehicles by status
        Map<String, Long> vehiclesByStatus = new HashMap<>();
        vehiclesByStatus.put("ACTIVE", 17L);
        vehiclesByStatus.put("IN_TRANSIT", 8L);
        vehiclesByStatus.put("MAINTENANCE", 0L);
        stats.setVehiclesByStatus(vehiclesByStatus);
        
        // Recent activities
        List<RecentActivity> activities = new ArrayList<>();
        activities.add(new RecentActivity("Trip Completed", "John Driver", "5 minutes ago", "Route A completed successfully"));
        activities.add(new RecentActivity("New User Registration", "Jane Student", "10 minutes ago", "New student registered"));
        activities.add(new RecentActivity("Vehicle Maintenance", "Admin", "1 hour ago", "Vehicle #15 maintenance scheduled"));
        stats.setRecentActivities(activities);
        
        return stats;
    }

    @Override
    public DriverDashboardStats getDriverDashboardStats(String driverId) {
        DriverDashboardStats stats = new DriverDashboardStats();
        
        // Mock data - in production, fetch from database based on driverId
        stats.setTotalTripsCompleted(45L);
        stats.setTripsToday(3L);
        stats.setTotalDistanceDriven(1250.0);
        stats.setAverageTripDuration(25.5);
        stats.setCurrentVehicle("Bus #7");
        stats.setNextScheduledTrip("Route B - 2:30 PM");
        
        // Recent trips
        List<RecentTrip> recentTrips = new ArrayList<>();
        RecentTrip trip1 = new RecentTrip();
        trip1.setTripId("TRIP001");
        trip1.setRoute("Campus A to Campus B");
        trip1.setDate("2024-01-15");
        trip1.setDuration("25 minutes");
        trip1.setStatus("COMPLETED");
        recentTrips.add(trip1);
        
        RecentTrip trip2 = new RecentTrip();
        trip2.setTripId("TRIP002");
        trip2.setRoute("Campus B to Downtown");
        trip2.setDate("2024-01-15");
        trip2.setDuration("30 minutes");
        trip2.setStatus("COMPLETED");
        recentTrips.add(trip2);
        
        stats.setRecentTrips(recentTrips);
        
        // Performance metrics
        PerformanceMetrics performance = new PerformanceMetrics();
        performance.setOnTimePercentage(94.5);
        performance.setAverageRating(4.7);
        performance.setTotalPassengers(280L);
        performance.setSafetyScore(98.2);
        stats.setPerformanceMetrics(performance);
        
        return stats;
    }

    @Override
    public VehicleUsageStats getVehicleUsageStats(String vehicleId, LocalDate startDate, LocalDate endDate) {
        VehicleUsageStats stats = new VehicleUsageStats();
        
        // Mock data - in production, calculate from actual data
        stats.setVehicleId(vehicleId);
        stats.setVehicleName("University Bus #" + vehicleId);
        stats.setTotalTrips(28L);
        stats.setTotalDistance(850.0);
        stats.setTotalFuelConsumption(120.5);
        stats.setTotalPassengers(420L);
        stats.setUtilizationRate(76.5);
        
        // Daily trips map
        Map<LocalDate, Long> dailyTrips = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            dailyTrips.put(date, (long) (Math.random() * 10 + 1));
        }
        stats.setDailyTrips(dailyTrips);
        
        return stats;
    }

    @Override
    public SystemOverview getSystemOverview() {
        SystemOverview overview = new SystemOverview();
        
        overview.setSystemStatus("HEALTHY");
        overview.setActiveConnections(142L);
        overview.setServerUptime(99.8);
        overview.setTotalDataProcessed(15000L);
        
        Map<String, Object> systemHealth = new HashMap<>();
        systemHealth.put("database", "HEALTHY");
        systemHealth.put("api", "HEALTHY");
        systemHealth.put("messaging", "HEALTHY");
        systemHealth.put("storage", "HEALTHY");
        overview.setSystemHealth(systemHealth);
        
        return overview;
    }

    @Override
    public FleetPerformanceMetrics getFleetPerformanceMetrics() {
        FleetPerformanceMetrics metrics = new FleetPerformanceMetrics();
        
        metrics.setAverageSpeed(35.2);
        metrics.setFuelEfficiency(8.5);
        metrics.setOnTimePerformance(92.3);
        metrics.setMaintenanceAlerts(2L);
        
        Map<String, Double> performanceByVehicle = new HashMap<>();
        performanceByVehicle.put("Bus #1", 94.5);
        performanceByVehicle.put("Bus #2", 91.2);
        performanceByVehicle.put("Bus #3", 96.8);
        performanceByVehicle.put("Bus #4", 89.7);
        performanceByVehicle.put("Bus #5", 93.1);
        metrics.setPerformanceByVehicle(performanceByVehicle);
        
        return metrics;
    }

    @Override
    public UserActivityStats getUserActivityStats(Role role) {
        UserActivityStats stats = new UserActivityStats();
        
        stats.setActiveUsers(85L);
        stats.setNewRegistrations(12L);
        
        Map<String, Long> activityByHour = new HashMap<>();
        for (int i = 6; i <= 22; i++) {
            activityByHour.put(String.valueOf(i), (long) (Math.random() * 20 + 5));
        }
        stats.setActivityByHour(activityByHour);
        
        List<String> mostActiveUsers = Arrays.asList("john.doe", "jane.smith", "mike.wilson", "sarah.johnson", "david.brown");
        stats.setMostActiveUsers(mostActiveUsers);
        
        return stats;
    }

    @Override
    public TripAnalytics getTripAnalytics(LocalDate startDate, LocalDate endDate) {
        TripAnalytics analytics = new TripAnalytics();
        
        analytics.setTotalTrips(156L);
        analytics.setAverageDistance(12.5);
        analytics.setAverageDuration(22.3);
        
        Map<String, Long> tripsByStatus = new HashMap<>();
        tripsByStatus.put("COMPLETED", 145L);
        tripsByStatus.put("CANCELLED", 8L);
        tripsByStatus.put("IN_PROGRESS", 3L);
        analytics.setTripsByStatus(tripsByStatus);
        
        Map<LocalDate, Long> tripsOverTime = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            tripsOverTime.put(date, (long) (Math.random() * 15 + 5));
        }
        analytics.setTripsOverTime(tripsOverTime);
        
        List<String> popularRoutes = Arrays.asList(
            "Campus A to Campus B",
            "Downtown to University",
            "Dormitory to Main Campus",
            "Library to Sports Complex",
            "Medical Center to Campus"
        );
        analytics.setPopularRoutes(popularRoutes);
        
        return analytics;
    }
}
