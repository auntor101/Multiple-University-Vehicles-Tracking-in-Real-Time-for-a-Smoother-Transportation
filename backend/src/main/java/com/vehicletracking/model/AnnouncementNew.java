package com.vehicletracking.model;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

public class Announcement {
    
    private String id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;
    
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 5000, message = "Content must be between 1 and 5000 characters")
    private String content;
    
    private AnnouncementPriority priority = AnnouncementPriority.NORMAL;
    
    private Set<Role> targetRoles;
    
    private Boolean isPinned = false;
    
    private Boolean isActive = true;
    
    private LocalDateTime expiresAt;
    
    private String authorId;
    
    private String authorName;
    
    private Role authorRole;
    
    private Long views = 0L;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Announcement() {}
    
    public Announcement(String title, String content, AnnouncementPriority priority, 
                       Set<Role> targetRoles, String authorId, String authorName, Role authorRole) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.targetRoles = targetRoles;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    
    public AnnouncementPriority getPriority() { return priority; }
    public void setPriority(AnnouncementPriority priority) { 
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Set<Role> getTargetRoles() { return targetRoles; }
    public void setTargetRoles(Set<Role> targetRoles) { 
        this.targetRoles = targetRoles;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { 
        this.isPinned = isPinned;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { 
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { 
        this.expiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public Role getAuthorRole() { return authorRole; }
    public void setAuthorRole(Role authorRole) { this.authorRole = authorRole; }
    
    public Long getViews() { return views; }
    public void setViews(Long views) { this.views = views; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public void incrementViews() {
        this.views++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isVisibleToRole(Role role) {
        return targetRoles == null || targetRoles.isEmpty() || targetRoles.contains(role);
    }
}

enum AnnouncementPriority {
    LOW, NORMAL, HIGH, URGENT
}
