package com.vehicletracking.service.impl;

import com.vehicletracking.dto.AnnouncementStats;
import com.vehicletracking.model.Announcement;
import com.vehicletracking.model.AnnouncementPriority;
import com.vehicletracking.model.Role;
import com.vehicletracking.service.AnnouncementService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    // In-memory storage (in production, use a database)
    private final Map<String, Announcement> announcements = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userReadAnnouncements = new ConcurrentHashMap<>();

    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        String id = UUID.randomUUID().toString();
        announcement.setId(id);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcements.put(id, announcement);
        return announcement;
    }

    @Override
    public Optional<Announcement> getAnnouncementById(String id) {
        return Optional.ofNullable(announcements.get(id));
    }

    @Override
    public List<Announcement> getAllAnnouncements() {
        return new ArrayList<>(announcements.values())
                .stream()
                .filter(a -> a.getIsActive())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> getAnnouncementsForRole(Role role) {
        return announcements.values().stream()
                .filter(a -> a.getIsActive())
                .filter(a -> !a.isExpired())
                .filter(a -> a.isVisibleToRole(role))
                .sorted((a, b) -> {
                    // Pinned announcements first
                    if (a.getIsPinned() && !b.getIsPinned()) return -1;
                    if (!a.getIsPinned() && b.getIsPinned()) return 1;
                    // Then by priority
                    int priorityCompare = b.getPriority().compareTo(a.getPriority());
                    if (priorityCompare != 0) return priorityCompare;
                    // Finally by creation date
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> getPinnedAnnouncements() {
        return announcements.values().stream()
                .filter(a -> a.getIsActive())
                .filter(a -> !a.isExpired())
                .filter(Announcement::getIsPinned)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Announcement> getAnnouncementsByPriority(String priority) {
        try {
            AnnouncementPriority priorityEnum = 
                AnnouncementPriority.valueOf(priority.toUpperCase());
            
            return announcements.values().stream()
                    .filter(a -> a.getIsActive())
                    .filter(a -> !a.isExpired())
                    .filter(a -> a.getPriority().equals(priorityEnum))
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Announcement> getActiveAnnouncements() {
        return announcements.values().stream()
                .filter(a -> a.getIsActive())
                .filter(a -> !a.isExpired())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public Announcement updateAnnouncement(String id, Announcement announcement) {
        Announcement existing = announcements.get(id);
        if (existing == null) {
            throw new RuntimeException("Announcement not found with id: " + id);
        }

        announcement.setId(id);
        announcement.setCreatedAt(existing.getCreatedAt());
        announcement.setUpdatedAt(LocalDateTime.now());
        announcement.setViews(existing.getViews());
        
        announcements.put(id, announcement);
        return announcement;
    }

    @Override
    public void deleteAnnouncement(String id) {
        announcements.remove(id);
        // Also remove from read tracking
        userReadAnnouncements.values().forEach(set -> set.remove(id));
    }

    @Override
    public Announcement togglePinAnnouncement(String id) {
        Announcement announcement = announcements.get(id);
        if (announcement == null) {
            throw new RuntimeException("Announcement not found with id: " + id);
        }

        announcement.setIsPinned(!announcement.getIsPinned());
        announcement.setUpdatedAt(LocalDateTime.now());
        return announcement;
    }

    @Override
    public void markAsRead(String announcementId, String userId) {
        userReadAnnouncements.computeIfAbsent(userId, k -> new HashSet<>()).add(announcementId);
        
        // Increment view count
        Announcement announcement = announcements.get(announcementId);
        if (announcement != null) {
            announcement.incrementViews();
        }
    }

    @Override
    public Long getUnreadCount(String userId, Role userRole) {
        Set<String> readAnnouncements = userReadAnnouncements.getOrDefault(userId, new HashSet<>());
        
        return getAnnouncementsForRole(userRole).stream()
                .filter(a -> !readAnnouncements.contains(a.getId()))
                .count();
    }

    @Override
    public List<Announcement> searchAnnouncements(String searchTerm, Role userRole) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        
        return getAnnouncementsForRole(userRole).stream()
                .filter(a -> 
                    a.getTitle().toLowerCase().contains(lowerSearchTerm) ||
                    a.getContent().toLowerCase().contains(lowerSearchTerm) ||
                    a.getAuthorName().toLowerCase().contains(lowerSearchTerm)
                )
                .collect(Collectors.toList());
    }

    @Override
    public void archiveExpiredAnnouncements() {
        announcements.values().stream()
                .filter(Announcement::isExpired)
                .forEach(a -> a.setIsActive(false));
    }

    @Override
    public AnnouncementStats getAnnouncementStats() {
        long total = announcements.size();
        long active = announcements.values().stream()
                .filter(a -> a.getIsActive() && !a.isExpired())
                .count();
        long pinned = announcements.values().stream()
                .filter(a -> a.getIsActive() && a.getIsPinned())
                .count();
        long expired = announcements.values().stream()
                .filter(Announcement::isExpired)
                .count();
        long urgent = announcements.values().stream()
                .filter(a -> a.getIsActive() && 
                    a.getPriority() == AnnouncementPriority.URGENT)
                .count();

        return new AnnouncementStats(total, active, pinned, expired, urgent);
    }
}
