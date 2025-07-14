package com.vehicletracking.service;

import com.vehicletracking.model.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    
    /**
     * Get dashboard statistics for admin
     */
    AdminDashboardStats getAdminDashboardStats();
    
    /**
     * Get dashboard statistics for driver
     */
    DriverDashboardStats getDriverDashboardStats(String driverId);
    
    /**
     * Get vehicle usage statistics
     */
    VehicleUsageStats getVehicleUsageStats(String vehicleId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get system overview for admin
     */
    SystemOverview getSystemOverview();
    
    /**
     * Get fleet performance metrics
     */
    FleetPerformanceMetrics getFleetPerformanceMetrics();
    
    /**
     * Get user activity statistics
     */
    UserActivityStats getUserActivityStats(Role role);
    
    /**
     * Get trip analytics
     */
    TripAnalytics getTripAnalytics(LocalDate startDate, LocalDate endDate);
}

class AdminDashboardStats {
    private Long totalUsers;
    private Long totalVehicles;
    private Long activeTrips;
    private Long completedTripsToday;
    private Long totalDrivers;
    private Long availableVehicles;
    private Double totalDistanceTraveled;
    private Map<String, Long> usersByRole;
    private Map<String, Long> vehiclesByStatus;
    private List<RecentActivity> recentActivities;
    
    // Constructors
    public AdminDashboardStats() {}
    
    // Getters and Setters
    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
    
    public Long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(Long totalVehicles) { this.totalVehicles = totalVehicles; }
    
    public Long getActiveTrips() { return activeTrips; }
    public void setActiveTrips(Long activeTrips) { this.activeTrips = activeTrips; }
    
    public Long getCompletedTripsToday() { return completedTripsToday; }
    public void setCompletedTripsToday(Long completedTripsToday) { this.completedTripsToday = completedTripsToday; }
    
    public Long getTotalDrivers() { return totalDrivers; }
    public void setTotalDrivers(Long totalDrivers) { this.totalDrivers = totalDrivers; }
    
    public Long getAvailableVehicles() { return availableVehicles; }
    public void setAvailableVehicles(Long availableVehicles) { this.availableVehicles = availableVehicles; }
    
    public Double getTotalDistanceTraveled() { return totalDistanceTraveled; }
    public void setTotalDistanceTraveled(Double totalDistanceTraveled) { this.totalDistanceTraveled = totalDistanceTraveled; }
    
    public Map<String, Long> getUsersByRole() { return usersByRole; }
    public void setUsersByRole(Map<String, Long> usersByRole) { this.usersByRole = usersByRole; }
    
    public Map<String, Long> getVehiclesByStatus() { return vehiclesByStatus; }
    public void setVehiclesByStatus(Map<String, Long> vehiclesByStatus) { this.vehiclesByStatus = vehiclesByStatus; }
    
    public List<RecentActivity> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<RecentActivity> recentActivities) { this.recentActivities = recentActivities; }
}

class DriverDashboardStats {
    private Long totalTripsCompleted;
    private Long tripsToday;
    private Double totalDistanceDriven;
    private Double averageTripDuration;
    private String currentVehicle;
    private String nextScheduledTrip;
    private List<RecentTrip> recentTrips;
    private PerformanceMetrics performanceMetrics;
    
    // Constructors
    public DriverDashboardStats() {}
    
    // Getters and Setters
    public Long getTotalTripsCompleted() { return totalTripsCompleted; }
    public void setTotalTripsCompleted(Long totalTripsCompleted) { this.totalTripsCompleted = totalTripsCompleted; }
    
    public Long getTripsToday() { return tripsToday; }
    public void setTripsToday(Long tripsToday) { this.tripsToday = tripsToday; }
    
    public Double getTotalDistanceDriven() { return totalDistanceDriven; }
    public void setTotalDistanceDriven(Double totalDistanceDriven) { this.totalDistanceDriven = totalDistanceDriven; }
    
    public Double getAverageTripDuration() { return averageTripDuration; }
    public void setAverageTripDuration(Double averageTripDuration) { this.averageTripDuration = averageTripDuration; }
    
    public String getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(String currentVehicle) { this.currentVehicle = currentVehicle; }
    
    public String getNextScheduledTrip() { return nextScheduledTrip; }
    public void setNextScheduledTrip(String nextScheduledTrip) { this.nextScheduledTrip = nextScheduledTrip; }
    
    public List<RecentTrip> getRecentTrips() { return recentTrips; }
    public void setRecentTrips(List<RecentTrip> recentTrips) { this.recentTrips = recentTrips; }
    
    public PerformanceMetrics getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) { this.performanceMetrics = performanceMetrics; }
}

class VehicleUsageStats {
    private String vehicleId;
    private String vehicleName;
    private Long totalTrips;
    private Double totalDistance;
    private Double totalFuelConsumption;
    private Long totalPassengers;
    private Double utilizationRate;
    private Map<LocalDate, Long> dailyTrips;
    
    // Constructors
    public VehicleUsageStats() {}
    
    // Getters and Setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    
    public Long getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Long totalTrips) { this.totalTrips = totalTrips; }
    
    public Double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(Double totalDistance) { this.totalDistance = totalDistance; }
    
    public Double getTotalFuelConsumption() { return totalFuelConsumption; }
    public void setTotalFuelConsumption(Double totalFuelConsumption) { this.totalFuelConsumption = totalFuelConsumption; }
    
    public Long getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(Long totalPassengers) { this.totalPassengers = totalPassengers; }
    
    public Double getUtilizationRate() { return utilizationRate; }
    public void setUtilizationRate(Double utilizationRate) { this.utilizationRate = utilizationRate; }
    
    public Map<LocalDate, Long> getDailyTrips() { return dailyTrips; }
    public void setDailyTrips(Map<LocalDate, Long> dailyTrips) { this.dailyTrips = dailyTrips; }
}

class SystemOverview {
    private String systemStatus;
    private Long activeConnections;
    private Double serverUptime;
    private Long totalDataProcessed;
    private Map<String, Object> systemHealth;
    
    // Constructors and getters/setters...
    public SystemOverview() {}
    
    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }
    
    public Long getActiveConnections() { return activeConnections; }
    public void setActiveConnections(Long activeConnections) { this.activeConnections = activeConnections; }
    
    public Double getServerUptime() { return serverUptime; }
    public void setServerUptime(Double serverUptime) { this.serverUptime = serverUptime; }
    
    public Long getTotalDataProcessed() { return totalDataProcessed; }
    public void setTotalDataProcessed(Long totalDataProcessed) { this.totalDataProcessed = totalDataProcessed; }
    
    public Map<String, Object> getSystemHealth() { return systemHealth; }
    public void setSystemHealth(Map<String, Object> systemHealth) { this.systemHealth = systemHealth; }
}

class FleetPerformanceMetrics {
    private Double averageSpeed;
    private Double fuelEfficiency;
    private Double onTimePerformance;
    private Long maintenanceAlerts;
    private Map<String, Double> performanceByVehicle;
    
    // Constructors and getters/setters...
    public FleetPerformanceMetrics() {}
    
    public Double getAverageSpeed() { return averageSpeed; }
    public void setAverageSpeed(Double averageSpeed) { this.averageSpeed = averageSpeed; }
    
    public Double getFuelEfficiency() { return fuelEfficiency; }
    public void setFuelEfficiency(Double fuelEfficiency) { this.fuelEfficiency = fuelEfficiency; }
    
    public Double getOnTimePerformance() { return onTimePerformance; }
    public void setOnTimePerformance(Double onTimePerformance) { this.onTimePerformance = onTimePerformance; }
    
    public Long getMaintenanceAlerts() { return maintenanceAlerts; }
    public void setMaintenanceAlerts(Long maintenanceAlerts) { this.maintenanceAlerts = maintenanceAlerts; }
    
    public Map<String, Double> getPerformanceByVehicle() { return performanceByVehicle; }
    public void setPerformanceByVehicle(Map<String, Double> performanceByVehicle) { this.performanceByVehicle = performanceByVehicle; }
}

class UserActivityStats {
    private Long activeUsers;
    private Long newRegistrations;
    private Map<String, Long> activityByHour;
    private List<String> mostActiveUsers;
    
    // Constructors and getters/setters...
    public UserActivityStats() {}
    
    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
    
    public Long getNewRegistrations() { return newRegistrations; }
    public void setNewRegistrations(Long newRegistrations) { this.newRegistrations = newRegistrations; }
    
    public Map<String, Long> getActivityByHour() { return activityByHour; }
    public void setActivityByHour(Map<String, Long> activityByHour) { this.activityByHour = activityByHour; }
    
    public List<String> getMostActiveUsers() { return mostActiveUsers; }
    public void setMostActiveUsers(List<String> mostActiveUsers) { this.mostActiveUsers = mostActiveUsers; }
}

class TripAnalytics {
    private Long totalTrips;
    private Double averageDistance;
    private Double averageDuration;
    private Map<String, Long> tripsByStatus;
    private Map<LocalDate, Long> tripsOverTime;
    private List<String> popularRoutes;
    
    // Constructors and getters/setters...
    public TripAnalytics() {}
    
    public Long getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Long totalTrips) { this.totalTrips = totalTrips; }
    
    public Double getAverageDistance() { return averageDistance; }
    public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
    
    public Double getAverageDuration() { return averageDuration; }
    public void setAverageDuration(Double averageDuration) { this.averageDuration = averageDuration; }
    
    public Map<String, Long> getTripsByStatus() { return tripsByStatus; }
    public void setTripsByStatus(Map<String, Long> tripsByStatus) { this.tripsByStatus = tripsByStatus; }
    
    public Map<LocalDate, Long> getTripsOverTime() { return tripsOverTime; }
    public void setTripsOverTime(Map<LocalDate, Long> tripsOverTime) { this.tripsOverTime = tripsOverTime; }
    
    public List<String> getPopularRoutes() { return popularRoutes; }
    public void setPopularRoutes(List<String> popularRoutes) { this.popularRoutes = popularRoutes; }
}

class RecentActivity {
    private String action;
    private String user;
    private String timestamp;
    private String details;
    
    public RecentActivity() {}
    
    public RecentActivity(String action, String user, String timestamp, String details) {
        this.action = action;
        this.user = user;
        this.timestamp = timestamp;
        this.details = details;
    }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}

class RecentTrip {
    private String tripId;
    private String route;
    private String date;
    private String duration;
    private String status;
    
    public RecentTrip() {}
    
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

class PerformanceMetrics {
    private Double onTimePercentage;
    private Double averageRating;
    private Long totalPassengers;
    private Double safetyScore;
    
    public PerformanceMetrics() {}
    
    public Double getOnTimePercentage() { return onTimePercentage; }
    public void setOnTimePercentage(Double onTimePercentage) { this.onTimePercentage = onTimePercentage; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Long getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(Long totalPassengers) { this.totalPassengers = totalPassengers; }
    
    public Double getSafetyScore() { return safetyScore; }
    public void setSafetyScore(Double safetyScore) { this.safetyScore = safetyScore; }
}
