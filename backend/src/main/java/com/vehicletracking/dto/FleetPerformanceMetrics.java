package com.vehicletracking.dto;

import java.util.Map;

public class FleetPerformanceMetrics {
    private Double averageSpeed;
    private Double fuelEfficiency;
    private Double onTimePerformance;
    private Long maintenanceAlerts;
    private Map<String, Double> performanceByVehicle;
    
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
