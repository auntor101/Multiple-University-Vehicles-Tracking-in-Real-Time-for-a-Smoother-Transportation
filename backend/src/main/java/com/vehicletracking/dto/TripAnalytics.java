package com.vehicletracking.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TripAnalytics {
    private Integer totalTrips;
    private Double averageDistance;
    private Double averageDuration;
    private Map<String, Long> tripsByStatus;
    private Map<LocalDate, Long> tripsOverTime;
    private List<String> popularRoutes;
    
    public TripAnalytics() {}
    
    public Integer getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Integer totalTrips) { this.totalTrips = totalTrips; }
    
    public Double getAverageDistance() { return averageDistance; }
    public void setAverageDistance(Double averageDistance) { this.averageDistance = averageDistance; }
    
    public Double getAverageDuration() { return averageDuration; }
    public void setAverageDuration(Double averageDuration) { this.averageDuration = averageDuration; }
    
    public Map<String, Long> getTripsByStatus() { return tripsByStatus; }
    public void setTripsByStatus(Map<String, Long> tripsByStatus) { this.tripsByStatus = tripsByStatus; }
    
    public Map<LocalDate, Long> getTripsOverTime() { return tripsOverTime; }
    public void setTripsOverTime(Map<LocalDate, Long> tripsOverTime) { this.tripsOverTime = tripsOverTime; }
    
    public List<String> getPopularRoutes() { return popularRoutes; }
    public void setPopularRoutes(List<String> popularRoutes) { this.popularRoutes = popularRoutes; }
}
