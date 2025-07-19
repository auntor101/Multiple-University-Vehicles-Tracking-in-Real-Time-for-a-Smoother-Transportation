package com.vehicletracking.dto;

public class SystemOverview {
    private String systemStatus;
    private Integer totalActiveUsers;
    private Integer totalActiveVehicles;
    private Long uptime;
    private Double systemLoad;
    private String lastUpdate;
    
    // Constructors
    public SystemOverview() {}
    
    public SystemOverview(String systemStatus, Integer totalActiveUsers, Integer totalActiveVehicles,
                         Long uptime, Double systemLoad, String lastUpdate) {
        this.systemStatus = systemStatus;
        this.totalActiveUsers = totalActiveUsers;
        this.totalActiveVehicles = totalActiveVehicles;
        this.uptime = uptime;
        this.systemLoad = systemLoad;
        this.lastUpdate = lastUpdate;
    }
    
    // Getters and Setters
    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }
    
    public Integer getTotalActiveUsers() { return totalActiveUsers; }
    public void setTotalActiveUsers(Integer totalActiveUsers) { this.totalActiveUsers = totalActiveUsers; }
    
    public Integer getTotalActiveVehicles() { return totalActiveVehicles; }
    public void setTotalActiveVehicles(Integer totalActiveVehicles) { this.totalActiveVehicles = totalActiveVehicles; }
    
    public Long getUptime() { return uptime; }
    public void setUptime(Long uptime) { this.uptime = uptime; }
    
    public Double getSystemLoad() { return systemLoad; }
    public void setSystemLoad(Double systemLoad) { this.systemLoad = systemLoad; }
    
    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
}
