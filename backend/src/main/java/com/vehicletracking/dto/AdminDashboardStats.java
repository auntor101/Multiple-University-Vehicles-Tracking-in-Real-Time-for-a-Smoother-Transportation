package com.vehicletracking.dto;

import java.util.List;
import java.util.Map;

public class AdminDashboardStats {
    private Long totalUsers;
    private Long totalVehicles;
    private Long activeVehicles;
    private Long totalDrivers;
    private Long activeDrivers;
    private Long totalTrips;
    private Long pendingApprovals;
    private Long activeTrips;
    private Long completedTripsToday;
    private Long availableVehicles;
    private Double totalDistanceTraveled;
    private Map<String, Long> usersByRole;
    private Map<String, Long> vehiclesByStatus;
    private List<RecentActivity> recentActivities;
    
    public AdminDashboardStats() {}
    
    public AdminDashboardStats(Long totalUsers, Long totalVehicles, Long activeVehicles, 
                              Long totalDrivers, Long activeDrivers, Long totalTrips, Long pendingApprovals) {
        this.totalUsers = totalUsers;
        this.totalVehicles = totalVehicles;
        this.activeVehicles = activeVehicles;
        this.totalDrivers = totalDrivers;
        this.activeDrivers = activeDrivers;
        this.totalTrips = totalTrips;
        this.pendingApprovals = pendingApprovals;
    }
    
    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
    
    public Long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(Long totalVehicles) { this.totalVehicles = totalVehicles; }
    
    public Long getActiveVehicles() { return activeVehicles; }
    public void setActiveVehicles(Long activeVehicles) { this.activeVehicles = activeVehicles; }
    
    public Long getTotalDrivers() { return totalDrivers; }
    public void setTotalDrivers(Long totalDrivers) { this.totalDrivers = totalDrivers; }
    
    public Long getActiveDrivers() { return activeDrivers; }
    public void setActiveDrivers(Long activeDrivers) { this.activeDrivers = activeDrivers; }
    
    public Long getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Long totalTrips) { this.totalTrips = totalTrips; }
    
    public Long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(Long pendingApprovals) { this.pendingApprovals = pendingApprovals; }
    
    public Long getActiveTrips() { return activeTrips; }
    public void setActiveTrips(Long activeTrips) { this.activeTrips = activeTrips; }
    
    public Long getCompletedTripsToday() { return completedTripsToday; }
    public void setCompletedTripsToday(Long completedTripsToday) { this.completedTripsToday = completedTripsToday; }
    
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
