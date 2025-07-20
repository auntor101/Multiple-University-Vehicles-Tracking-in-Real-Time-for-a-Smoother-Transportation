package com.vehicletracking.dto;

import java.time.LocalDate;
import java.util.Map;

public class VehicleUsageStats {
    private String vehicleId;
    private String vehicleName;
    private Integer totalTrips;
    private Double totalDistance;
    private Double totalFuelConsumption;
    private Long totalPassengers;
    private Double utilizationRate;
    private Map<LocalDate, Long> dailyTrips;
    
    public VehicleUsageStats() {}
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    
    public Integer getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Integer totalTrips) { this.totalTrips = totalTrips; }
    
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
