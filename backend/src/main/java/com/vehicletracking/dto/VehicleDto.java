package com.vehicletracking.dto;

import com.vehicletracking.model.VehicleType;
import com.vehicletracking.model.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

public class VehicleDto {
    
    @NotBlank
    @Size(max = 20)
    private String vehicleNumber;
    
    @NotBlank
    @Size(max = 50)
    private String model;
    
    @NotBlank
    @Size(max = 50)
    private String brand;
    
    @Min(1)
    private Integer capacity;
    
    @NotNull
    private VehicleType vehicleType;
    
    private VehicleStatus status = VehicleStatus.ACTIVE;
    
    @NotBlank
    @Size(max = 100)
    private String university;
    
    private String driverId;
    
    @Size(max = 200)
    private String routeName;
    
    @Size(max = 500)
    private String routeDescription;
    
    private String routeStops; // JSON string
    
    @Size(max = 20)
    private String fuelType;
    
    private Double fuelLevel;
    
    @Size(max = 100)
    private String insuranceNumber;
    
    private LocalDateTime insuranceExpiry;
    
    private LocalDateTime lastMaintenance;
    private LocalDateTime nextMaintenance;
    
    // Constructors
    public VehicleDto() {}
    
    // Getters and Setters
    public String getVehicleNumber() {
        return vehicleNumber;
    }
    
    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public VehicleType getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public VehicleStatus getStatus() {
        return status;
    }
    
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
    
    public String getUniversity() {
        return university;
    }
    
    public void setUniversity(String university) {
        this.university = university;
    }
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public String getRouteName() {
        return routeName;
    }
    
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
    
    public String getRouteDescription() {
        return routeDescription;
    }
    
    public void setRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
    }
    
    public String getRouteStops() {
        return routeStops;
    }
    
    public void setRouteStops(String routeStops) {
        this.routeStops = routeStops;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public Double getFuelLevel() {
        return fuelLevel;
    }
    
    public void setFuelLevel(Double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }
    
    public String getInsuranceNumber() {
        return insuranceNumber;
    }
    
    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
    
    public LocalDateTime getInsuranceExpiry() {
        return insuranceExpiry;
    }
    
    public void setInsuranceExpiry(LocalDateTime insuranceExpiry) {
        this.insuranceExpiry = insuranceExpiry;
    }
    
    public LocalDateTime getLastMaintenance() {
        return lastMaintenance;
    }
    
    public void setLastMaintenance(LocalDateTime lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }
    
    public LocalDateTime getNextMaintenance() {
        return nextMaintenance;
    }
    
    public void setNextMaintenance(LocalDateTime nextMaintenance) {
        this.nextMaintenance = nextMaintenance;
    }
} 