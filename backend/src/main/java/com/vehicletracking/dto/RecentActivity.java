package com.vehicletracking.dto;

public class RecentActivity {
    private String action;
    private String user;
    private String timestamp;
    private String description;
    
    public RecentActivity() {}
    
    public RecentActivity(String action, String user, String timestamp, String description) {
        this.action = action;
        this.user = user;
        this.timestamp = timestamp;
        this.description = description;
    }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
