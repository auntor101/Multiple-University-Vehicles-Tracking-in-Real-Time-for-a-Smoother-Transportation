package com.vehicletracking.dto;

import java.util.Map;

public class TripAnalytics {
    private Integer totalTrips;
    private Double averageTripDistance;
    private Double averageTripDuration;
    private Map<String, Integer> tripsByRoute;
    private Map<String, Integer> tripsByTimeOfDay;
    private Integer onTimeTrips;
    private Integer delayedTrips;
    
    // Constructors
    public TripAnalytics() {}
    
    public TripAnalytics(Integer totalTrips, Double averageTripDistance, Double averageTripDuration,
                        Map<String, Integer> tripsByRoute, Map<String, Integer> tripsByTimeOfDay,
                        Integer onTimeTrips, Integer delayedTrips) {
        this.totalTrips = totalTrips;
        this.averageTripDistance = averageTripDistance;
        this.averageTripDuration = averageTripDuration;
        this.tripsByRoute = tripsByRoute;
        this.tripsByTimeOfDay = tripsByTimeOfDay;
        this.onTimeTrips = onTimeTrips;
        this.delayedTrips = delayedTrips;
    }
    
    // Getters and Setters
    public Integer getTotalTrips() { return totalTrips; }
    public void setTotalTrips(Integer totalTrips) { this.totalTrips = totalTrips; }
    
    public Double getAverageTripDistance() { return averageTripDistance; }
    public void setAverageTripDistance(Double averageTripDistance) { this.averageTripDistance = averageTripDistance; }
    
    public Double getAverageTripDuration() { return averageTripDuration; }
    public void setAverageTripDuration(Double averageTripDuration) { this.averageTripDuration = averageTripDuration; }
    
    public Map<String, Integer> getTripsByRoute() { return tripsByRoute; }
    public void setTripsByRoute(Map<String, Integer> tripsByRoute) { this.tripsByRoute = tripsByRoute; }
    
    public Map<String, Integer> getTripsByTimeOfDay() { return tripsByTimeOfDay; }
    public void setTripsByTimeOfDay(Map<String, Integer> tripsByTimeOfDay) { this.tripsByTimeOfDay = tripsByTimeOfDay; }
    
    public Integer getOnTimeTrips() { return onTimeTrips; }
    public void setOnTimeTrips(Integer onTimeTrips) { this.onTimeTrips = onTimeTrips; }
    
    public Integer getDelayedTrips() { return delayedTrips; }
    public void setDelayedTrips(Integer delayedTrips) { this.delayedTrips = delayedTrips; }
}
