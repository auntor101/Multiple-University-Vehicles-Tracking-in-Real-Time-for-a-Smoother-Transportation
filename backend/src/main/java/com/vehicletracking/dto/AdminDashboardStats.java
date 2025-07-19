package com.vehicletracking.dto;

public class AdminDashboardStats {
    private Long totalUsers;
    private Long totalVehicles;
    private Long activeVehicles;
    private Long totalDrivers;
    private Long activeDrivers;
    private Long totalTrips;
    private Long pendingApprovals;
    
    // Constructors
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
    
    // Getters and Setters
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
}
