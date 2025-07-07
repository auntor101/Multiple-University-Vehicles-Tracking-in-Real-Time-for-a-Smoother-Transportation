package com.vehicletracking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import com.vehicletracking.validation.ValidVehicleNumber;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ValidVehicleNumber
    @Column(unique = true, nullable = false, length = 10)
    private String vehicleNumber;
    
    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 100, message = "Model must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String model;
    
    @NotBlank(message = "Brand is required")
    @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String brand;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity cannot exceed 100")
    @Column(nullable = false)
    private Integer capacity;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VehicleType vehicleType;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VehicleStatus status = VehicleStatus.ACTIVE;
    
    @Size(max = 100)
    private String university;
    
    // Driver assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver;
    
    // Current location
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime lastLocationUpdate;
    
    // Route information
    @Size(max = 200)
    private String routeName;
    
    @Size(max = 500)
    private String routeDescription;
    
    @Column(columnDefinition = "TEXT")
    private String routeStops; // JSON string of stops
    
    // Speed and other tracking info
    private Double currentSpeed; // km/h
    private String direction;
    
    // Vehicle details
    @Size(max = 20)
    private String fuelType;
    
    private Double fuelLevel; // percentage
    
    @Size(max = 100)
    private String insuranceNumber;
    
    private LocalDateTime insuranceExpiry;
    
    private LocalDateTime lastMaintenance;
    private LocalDateTime nextMaintenance;
    
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastLocationUpdate = LocalDateTime.now();
    }
    
    // Constructors
    public Vehicle() {}
    
    public Vehicle(String vehicleNumber, String model, String brand, 
                   VehicleType vehicleType, String university) {
        this.vehicleNumber = vehicleNumber;
        this.model = model;
        this.brand = brand;
        this.vehicleType = vehicleType;
        this.university = university;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public User getDriver() {
        return driver;
    }
    
    public void setDriver(User driver) {
        this.driver = driver;
    }
    
    public Double getCurrentLatitude() {
        return currentLatitude;
    }
    
    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }
    
    public Double getCurrentLongitude() {
        return currentLongitude;
    }
    
    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
    
    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }
    
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
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
    
    public Double getCurrentSpeed() {
        return currentSpeed;
    }
    
    public void setCurrentSpeed(Double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 