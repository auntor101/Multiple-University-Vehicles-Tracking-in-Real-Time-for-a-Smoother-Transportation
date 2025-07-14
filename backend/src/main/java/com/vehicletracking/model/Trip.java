package com.vehicletracking.model;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public class Trip {
    
    private String id;
    
    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;
    
    @NotBlank(message = "Driver ID is required")
    private String driverId;
    
    private String driverName;
    
    @NotBlank(message = "Route name is required")
    @Size(min = 1, max = 100, message = "Route name must be between 1 and 100 characters")
    private String routeName;
    
    @NotBlank(message = "Start location is required")
    private String startLocation;
    
    @NotBlank(message = "End location is required")
    private String endLocation;
    
    private Double startLatitude;
    
    private Double startLongitude;
    
    private Double endLatitude;
    
    private Double endLongitude;
    
    private LocalDateTime scheduledStartTime;
    
    private LocalDateTime scheduledEndTime;
    
    private LocalDateTime actualStartTime;
    
    private LocalDateTime actualEndTime;
    
    private TripStatus status = TripStatus.SCHEDULED;
    
    private Integer passengerCount = 0;
    
    private Integer maxPassengers;
    
    private Double estimatedDistance;
    
    private Double actualDistance;
    
    private Double estimatedDuration; // in minutes
    
    private Double actualDuration; // in minutes
    
    private List<String> passengerIds;
    
    private List<TripWaypoint> waypoints;
    
    private String notes;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Trip() {}
    
    public Trip(String vehicleId, String driverId, String routeName, 
               String startLocation, String endLocation, 
               LocalDateTime scheduledStartTime, LocalDateTime scheduledEndTime) {
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.routeName = routeName;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.scheduledStartTime = scheduledStartTime;
        this.scheduledEndTime = scheduledEndTime;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { 
        this.vehicleId = vehicleId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { 
        this.driverId = driverId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { 
        this.routeName = routeName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { 
        this.startLocation = startLocation;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { 
        this.endLocation = endLocation;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getStartLatitude() { return startLatitude; }
    public void setStartLatitude(Double startLatitude) { this.startLatitude = startLatitude; }
    
    public Double getStartLongitude() { return startLongitude; }
    public void setStartLongitude(Double startLongitude) { this.startLongitude = startLongitude; }
    
    public Double getEndLatitude() { return endLatitude; }
    public void setEndLatitude(Double endLatitude) { this.endLatitude = endLatitude; }
    
    public Double getEndLongitude() { return endLongitude; }
    public void setEndLongitude(Double endLongitude) { this.endLongitude = endLongitude; }
    
    public LocalDateTime getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(LocalDateTime scheduledStartTime) { 
        this.scheduledStartTime = scheduledStartTime;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getScheduledEndTime() { return scheduledEndTime; }
    public void setScheduledEndTime(LocalDateTime scheduledEndTime) { 
        this.scheduledEndTime = scheduledEndTime;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getActualStartTime() { return actualStartTime; }
    public void setActualStartTime(LocalDateTime actualStartTime) { 
        this.actualStartTime = actualStartTime;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getActualEndTime() { return actualEndTime; }
    public void setActualEndTime(LocalDateTime actualEndTime) { 
        this.actualEndTime = actualEndTime;
        this.updatedAt = LocalDateTime.now();
    }
    
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getPassengerCount() { return passengerCount; }
    public void setPassengerCount(Integer passengerCount) { 
        this.passengerCount = passengerCount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getMaxPassengers() { return maxPassengers; }
    public void setMaxPassengers(Integer maxPassengers) { this.maxPassengers = maxPassengers; }
    
    public Double getEstimatedDistance() { return estimatedDistance; }
    public void setEstimatedDistance(Double estimatedDistance) { this.estimatedDistance = estimatedDistance; }
    
    public Double getActualDistance() { return actualDistance; }
    public void setActualDistance(Double actualDistance) { this.actualDistance = actualDistance; }
    
    public Double getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(Double estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public Double getActualDuration() { return actualDuration; }
    public void setActualDuration(Double actualDuration) { this.actualDuration = actualDuration; }
    
    public List<String> getPassengerIds() { return passengerIds; }
    public void setPassengerIds(List<String> passengerIds) { 
        this.passengerIds = passengerIds;
        this.passengerCount = passengerIds != null ? passengerIds.size() : 0;
        this.updatedAt = LocalDateTime.now();
    }
    
    public List<TripWaypoint> getWaypoints() { return waypoints; }
    public void setWaypoints(List<TripWaypoint> waypoints) { this.waypoints = waypoints; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { 
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public boolean isInProgress() {
        return status == TripStatus.IN_PROGRESS;
    }
    
    public boolean isCompleted() {
        return status == TripStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == TripStatus.CANCELLED;
    }
    
    public boolean hasStarted() {
        return actualStartTime != null;
    }
    
    public boolean hasEnded() {
        return actualEndTime != null;
    }
    
    public void startTrip() {
        this.status = TripStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void completeTrip() {
        this.status = TripStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        if (this.actualStartTime != null) {
            this.actualDuration = (double) java.time.Duration.between(this.actualStartTime, this.actualEndTime).toMinutes();
        }
    }
    
    public void cancelTrip() {
        this.status = TripStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
}

enum TripStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    DELAYED
}

class TripWaypoint {
    private String name;
    private Double latitude;
    private Double longitude;
    private LocalDateTime estimatedArrival;
    private LocalDateTime actualArrival;
    private Integer order;
    
    // Constructors
    public TripWaypoint() {}
    
    public TripWaypoint(String name, Double latitude, Double longitude, Integer order) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.order = order;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public LocalDateTime getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(LocalDateTime estimatedArrival) { this.estimatedArrival = estimatedArrival; }
    
    public LocalDateTime getActualArrival() { return actualArrival; }
    public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }
    
    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }
}
