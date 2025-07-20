package com.vehicletracking.dto;

import java.util.List;
import java.util.Map;

public class UserActivityStats {
    private Long activeUsers;
    private Integer newRegistrations;
    private Map<String, Long> activityByHour;
    private List<String> mostActiveUsers;
    
    public UserActivityStats() {}
    
    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
    
    public Integer getNewRegistrations() { return newRegistrations; }
    public void setNewRegistrations(Integer newRegistrations) { this.newRegistrations = newRegistrations; }
    
    public Map<String, Long> getActivityByHour() { return activityByHour; }
    public void setActivityByHour(Map<String, Long> activityByHour) { this.activityByHour = activityByHour; }
    
    public List<String> getMostActiveUsers() { return mostActiveUsers; }
    public void setMostActiveUsers(List<String> mostActiveUsers) { this.mostActiveUsers = mostActiveUsers; }
}
