package com.vehicletracking.dto;

public class DriverDashboardStats {
    private String driverName;
    private String assignedVehicle;
    private String currentLocation;
    private Double totalKmToday;
    private Integer tripsToday;
    private Integer passengersToday;
    private String lastTrip;
    private String nextScheduledTrip;
    
    // Constructors
    public DriverDashboardStats() {}
    
    public DriverDashboardStats(String driverName, String assignedVehicle, String currentLocation,
                               Double totalKmToday, Integer tripsToday, Integer passengersToday,
                               String lastTrip, String nextScheduledTrip) {
        this.driverName = driverName;
        this.assignedVehicle = assignedVehicle;
        this.currentLocation = currentLocation;
        this.totalKmToday = totalKmToday;
        this.tripsToday = tripsToday;
        this.passengersToday = passengersToday;
        this.lastTrip = lastTrip;
        this.nextScheduledTrip = nextScheduledTrip;
    }
    
    // Getters and Setters
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    
    public String getAssignedVehicle() { return assignedVehicle; }
    public void setAssignedVehicle(String assignedVehicle) { this.assignedVehicle = assignedVehicle; }
    
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    
    public Double getTotalKmToday() { return totalKmToday; }
    public void setTotalKmToday(Double totalKmToday) { this.totalKmToday = totalKmToday; }
    
    public Integer getTripsToday() { return tripsToday; }
    public void setTripsToday(Integer tripsToday) { this.tripsToday = tripsToday; }
    
    public Integer getPassengersToday() { return passengersToday; }
    public void setPassengersToday(Integer passengersToday) { this.passengersToday = passengersToday; }
    
    public String getLastTrip() { return lastTrip; }
    public void setLastTrip(String lastTrip) { this.lastTrip = lastTrip; }
    
    public String getNextScheduledTrip() { return nextScheduledTrip; }
    public void setNextScheduledTrip(String nextScheduledTrip) { this.nextScheduledTrip = nextScheduledTrip; }
}
