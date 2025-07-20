package com.vehicletracking.dto;

import java.util.Map;

public class SystemOverview {
    private String systemStatus;
    private Long activeConnections;
    private Double serverUptime;
    private Long totalDataProcessed;
    private Map<String, Object> systemHealth;
    
    public SystemOverview() {}
    
    public String getSystemStatus() { return systemStatus; }
    public void setSystemStatus(String systemStatus) { this.systemStatus = systemStatus; }
    
    public Long getActiveConnections() { return activeConnections; }
    public void setActiveConnections(Long activeConnections) { this.activeConnections = activeConnections; }
    
    public Double getServerUptime() { return serverUptime; }
    public void setServerUptime(Double serverUptime) { this.serverUptime = serverUptime; }
    
    public Long getTotalDataProcessed() { return totalDataProcessed; }
    public void setTotalDataProcessed(Long totalDataProcessed) { this.totalDataProcessed = totalDataProcessed; }
    
    public Map<String, Object> getSystemHealth() { return systemHealth; }
    public void setSystemHealth(Map<String, Object> systemHealth) { this.systemHealth = systemHealth; }
}
