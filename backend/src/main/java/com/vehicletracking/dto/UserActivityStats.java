package com.vehicletracking.dto;

import java.util.Map;

public class UserActivityStats {
    private Map<String, Integer> activeUsersByRole;
    private Map<String, Integer> loginsByHour;
    private Integer newRegistrations;
    private Integer totalSessions;
    private Double averageSessionDuration;
    
    // Constructors
    public UserActivityStats() {}
    
    public UserActivityStats(Map<String, Integer> activeUsersByRole, Map<String, Integer> loginsByHour,
                            Integer newRegistrations, Integer totalSessions, Double averageSessionDuration) {
        this.activeUsersByRole = activeUsersByRole;
        this.loginsByHour = loginsByHour;
        this.newRegistrations = newRegistrations;
        this.totalSessions = totalSessions;
        this.averageSessionDuration = averageSessionDuration;
    }
    
    // Getters and Setters
    public Map<String, Integer> getActiveUsersByRole() { return activeUsersByRole; }
    public void setActiveUsersByRole(Map<String, Integer> activeUsersByRole) { this.activeUsersByRole = activeUsersByRole; }
    
    public Map<String, Integer> getLoginsByHour() { return loginsByHour; }
    public void setLoginsByHour(Map<String, Integer> loginsByHour) { this.loginsByHour = loginsByHour; }
    
    public Integer getNewRegistrations() { return newRegistrations; }
    public void setNewRegistrations(Integer newRegistrations) { this.newRegistrations = newRegistrations; }
    
    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }
    
    public Double getAverageSessionDuration() { return averageSessionDuration; }
    public void setAverageSessionDuration(Double averageSessionDuration) { this.averageSessionDuration = averageSessionDuration; }
}
