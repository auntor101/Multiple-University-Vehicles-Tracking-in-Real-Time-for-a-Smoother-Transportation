package com.vehicletracking.dto;

import java.util.Map;

public class VehicleUsageStats {
    private Map<String, Integer> usageByType;
    private Map<String, Double> utilizationRates;
    private Double averageSpeed;
    private Double totalDistanceCovered;
    private Integer totalTrips;
    
    // Constructors
    public VehicleUsageStats() {}
    
    public VehicleUsageStats(Map<String, Integer> usageByType, Map<String, Double> utilizationRates,
                            Double averageSpeed, Double totalDistanceCovered, Integer totalTrips) {
        this.usageByType = usageByType;
        this.utilizationRates = utilizationRates;
        this.averageSpeed = averageSpeed;
        this.totalDistanceCovered = totalDistanceCovered;
        this.totalTrips = totalTrips;
    }
    
    // Getters and Setters
    public Map<String, Integer> getUsageByType() { return usageByType; }
    public void setUsageByType(Map<String, Integer> usageByType) { this.usageByType = usageByType; }
    
    public Map<String, Double> getUtilizationRates() { return utilizationRates; }
    public void setUtilizationRates(Map<String, Double> utilizationRates) { this.utilizationRates = utilizationRates; }
    
    public Double getAverageSpeed() { return averageSpeed; }
    public void setAverageSpeed(Double averageSpeed) { this.averageSpeed = averageSpeed; }
    
    public Double getTotalDistanceCovered() { return totalDistanceCovered; }
    public void setTotalDistanceCovered(Double totalDistanceCovered) { this.totalDistanceCovered = totalDistanceCovered; }
    
    public Integer getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Integer totalTrips) { this.totalTrips = totalTrips; }
}
