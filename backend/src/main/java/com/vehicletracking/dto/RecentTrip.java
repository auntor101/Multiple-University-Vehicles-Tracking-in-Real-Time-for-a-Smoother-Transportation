package com.vehicletracking.dto;

public class RecentTrip {
    private String tripId;
    private String route;
    private String date;
    private String duration;
    private String status;
    
    public RecentTrip() {}
    
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
