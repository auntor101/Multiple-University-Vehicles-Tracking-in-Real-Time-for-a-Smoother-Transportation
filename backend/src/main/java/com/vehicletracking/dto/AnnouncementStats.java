package com.vehicletracking.dto;

public class AnnouncementStats {
    private Long totalAnnouncements;
    private Long activeAnnouncements;
    private Long pinnedAnnouncements;
    private Long expiredAnnouncements;
    private Long urgentAnnouncements;
    
    // Constructors
    public AnnouncementStats() {}
    
    public AnnouncementStats(Long totalAnnouncements, Long activeAnnouncements, 
                           Long pinnedAnnouncements, Long expiredAnnouncements, 
                           Long urgentAnnouncements) {
        this.totalAnnouncements = totalAnnouncements;
        this.activeAnnouncements = activeAnnouncements;
        this.pinnedAnnouncements = pinnedAnnouncements;
        this.expiredAnnouncements = expiredAnnouncements;
        this.urgentAnnouncements = urgentAnnouncements;
    }
    
    // Getters and Setters
    public Long getTotalAnnouncements() { return totalAnnouncements; }
    public void setTotalAnnouncements(Long totalAnnouncements) { this.totalAnnouncements = totalAnnouncements; }
    
    public Long getActiveAnnouncements() { return activeAnnouncements; }
    public void setActiveAnnouncements(Long activeAnnouncements) { this.activeAnnouncements = activeAnnouncements; }
    
    public Long getPinnedAnnouncements() { return pinnedAnnouncements; }
    public void setPinnedAnnouncements(Long pinnedAnnouncements) { this.pinnedAnnouncements = pinnedAnnouncements; }
    
    public Long getExpiredAnnouncements() { return expiredAnnouncements; }
    public void setExpiredAnnouncements(Long expiredAnnouncements) { this.expiredAnnouncements = expiredAnnouncements; }
    
    public Long getUrgentAnnouncements() { return urgentAnnouncements; }
    public void setUrgentAnnouncements(Long urgentAnnouncements) { this.urgentAnnouncements = urgentAnnouncements; }
}
