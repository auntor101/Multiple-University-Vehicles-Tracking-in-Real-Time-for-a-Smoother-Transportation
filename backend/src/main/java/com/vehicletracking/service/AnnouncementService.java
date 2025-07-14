package com.vehicletracking.service;

import com.vehicletracking.model.Announcement;
import com.vehicletracking.model.Role;

import java.util.List;
import java.util.Optional;

public interface AnnouncementService {
    
    /**
     * Create a new announcement
     */
    Announcement createAnnouncement(Announcement announcement);
    
    /**
     * Get announcement by ID
     */
    Optional<Announcement> getAnnouncementById(String id);
    
    /**
     * Get all announcements
     */
    List<Announcement> getAllAnnouncements();
    
    /**
     * Get announcements visible to a specific role
     */
    List<Announcement> getAnnouncementsForRole(Role role);
    
    /**
     * Get pinned announcements
     */
    List<Announcement> getPinnedAnnouncements();
    
    /**
     * Get announcements by priority
     */
    List<Announcement> getAnnouncementsByPriority(String priority);
    
    /**
     * Get active announcements (not expired)
     */
    List<Announcement> getActiveAnnouncements();
    
    /**
     * Update announcement
     */
    Announcement updateAnnouncement(String id, Announcement announcement);
    
    /**
     * Delete announcement
     */
    void deleteAnnouncement(String id);
    
    /**
     * Pin/Unpin announcement
     */
    Announcement togglePinAnnouncement(String id);
    
    /**
     * Mark announcement as read by a user
     */
    void markAsRead(String announcementId, String userId);
    
    /**
     * Get unread announcements count for a user
     */
    Long getUnreadCount(String userId, Role userRole);
    
    /**
     * Search announcements by content
     */
    List<Announcement> searchAnnouncements(String searchTerm, Role userRole);
    
    /**
     * Archive expired announcements
     */
    void archiveExpiredAnnouncements();
    
    /**
     * Get announcements statistics
     */
    com.vehicletracking.dto.AnnouncementStats getAnnouncementStats();
}

class AnnouncementStats {
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
