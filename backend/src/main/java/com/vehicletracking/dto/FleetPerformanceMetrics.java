package com.vehicletracking.dto;

import java.util.Map;

public class FleetPerformanceMetrics {
    private Double averageFuelEfficiency;
    private Map<String, Double> maintenanceCosts;
    private Double totalOperatingCost;
    private Integer vehiclesInMaintenance;
    private Double fleetUtilization;
    
    // Constructors
    public FleetPerformanceMetrics() {}
    
    public FleetPerformanceMetrics(Double averageFuelEfficiency, Map<String, Double> maintenanceCosts,
                                  Double totalOperatingCost, Integer vehiclesInMaintenance, Double fleetUtilization) {
        this.averageFuelEfficiency = averageFuelEfficiency;
        this.maintenanceCosts = maintenanceCosts;
        this.totalOperatingCost = totalOperatingCost;
        this.vehiclesInMaintenance = vehiclesInMaintenance;
        this.fleetUtilization = fleetUtilization;
    }
    
    // Getters and Setters
    public Double getAverageFuelEfficiency() { return averageFuelEfficiency; }
    public void setAverageFuelEfficiency(Double averageFuelEfficiency) { this.averageFuelEfficiency = averageFuelEfficiency; }
    
    public Map<String, Double> getMaintenanceCosts() { return maintenanceCosts; }
    public void setMaintenanceCosts(Map<String, Double> maintenanceCosts) { this.maintenanceCosts = maintenanceCosts; }
    
    public Double getTotalOperatingCost() { return totalOperatingCost; }
    public void setTotalOperatingCost(Double totalOperatingCost) { this.totalOperatingCost = totalOperatingCost; }
    
    public Integer getVehiclesInMaintenance() { return vehiclesInMaintenance; }
    public void setVehiclesInMaintenance(Integer vehiclesInMaintenance) { this.vehiclesInMaintenance = vehiclesInMaintenance; }
    
    public Double getFleetUtilization() { return fleetUtilization; }
    public void setFleetUtilization(Double fleetUtilization) { this.fleetUtilization = fleetUtilization; }
}
