package com.vehicletracking.controller;

import com.vehicletracking.dto.MessageResponse;
import com.vehicletracking.model.Announcement;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import com.vehicletracking.service.AnnouncementService;
import com.vehicletracking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    /**
     * Create a new announcement (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody Announcement announcement, 
                                              Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            announcement.setAuthorId(user.getId().toString());
            announcement.setAuthorName(user.getFirstName() + " " + user.getLastName());
            announcement.setAuthorRole(user.getRole());
            
            Announcement createdAnnouncement = announcementService.createAnnouncement(announcement);
            return ResponseEntity.ok(createdAnnouncement);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error creating announcement: " + e.getMessage()));
        }
    }

    /**
     * Get all announcements for current user's role
     */
    @GetMapping
    public ResponseEntity<?> getAnnouncements(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            Role userRole = userOpt.get().getRole();
            List<Announcement> announcements = announcementService.getAnnouncementsForRole(userRole);
            return ResponseEntity.ok(announcements);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching announcements: " + e.getMessage()));
        }
    }

    /**
     * Get announcement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAnnouncementById(@PathVariable String id, Authentication authentication) {
        try {
            Optional<Announcement> announcementOpt = announcementService.getAnnouncementById(id);
            
            if (announcementOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Announcement not found"));
            }
            
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isPresent()) {
                // Mark as read for the user
                announcementService.markAsRead(id, userOpt.get().getId().toString());
            }
            
            return ResponseEntity.ok(announcementOpt.get());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching announcement: " + e.getMessage()));
        }
    }

    /**
     * Get pinned announcements
     */
    @GetMapping("/pinned")
    public ResponseEntity<?> getPinnedAnnouncements() {
        try {
            List<Announcement> pinnedAnnouncements = announcementService.getPinnedAnnouncements();
            return ResponseEntity.ok(pinnedAnnouncements);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching pinned announcements: " + e.getMessage()));
        }
    }

    /**
     * Search announcements
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchAnnouncements(@RequestParam String query, Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            Role userRole = userOpt.get().getRole();
            List<Announcement> announcements = announcementService.searchAnnouncements(query, userRole);
            return ResponseEntity.ok(announcements);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error searching announcements: " + e.getMessage()));
        }
    }

    /**
     * Update announcement (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAnnouncement(@PathVariable String id, 
                                              @Valid @RequestBody Announcement announcement) {
        try {
            Announcement updatedAnnouncement = announcementService.updateAnnouncement(id, announcement);
            return ResponseEntity.ok(updatedAnnouncement);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error updating announcement: " + e.getMessage()));
        }
    }

    /**
     * Delete announcement (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable String id) {
        try {
            announcementService.deleteAnnouncement(id);
            return ResponseEntity.ok(new MessageResponse("Announcement deleted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error deleting announcement: " + e.getMessage()));
        }
    }

    /**
     * Toggle pin status (Admin only)
     */
    @PostMapping("/{id}/toggle-pin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> togglePinAnnouncement(@PathVariable String id) {
        try {
            Announcement announcement = announcementService.togglePinAnnouncement(id);
            return ResponseEntity.ok(announcement);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error toggling pin status: " + e.getMessage()));
        }
    }

    /**
     * Get unread announcements count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            Long unreadCount = announcementService.getUnreadCount(user.getId().toString(), user.getRole());
            return ResponseEntity.ok(unreadCount);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching unread count: " + e.getMessage()));
        }
    }

    /**
     * Get announcement statistics (Admin only)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAnnouncementStats() {
        try {
            return ResponseEntity.ok(announcementService.getAnnouncementStats());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching announcement stats: " + e.getMessage()));
        }
    }
}
