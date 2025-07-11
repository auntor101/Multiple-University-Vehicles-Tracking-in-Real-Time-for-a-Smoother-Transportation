package com.vehicletracking.service;

import com.google.firebase.messaging.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vehicletracking.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseMessagingService {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseMessagingService.class);
    
    @Autowired
    private FirebaseMessaging firebaseMessaging;
    
    @Autowired
    private FirebaseDatabase firebaseDatabase;
    
    // Notification Types
    public enum NotificationType {
        VEHICLE_ARRIVED("Vehicle Arrived", "🚌"),
        VEHICLE_DEPARTED("Vehicle Departed", "🚀"),
        ROUTE_CHANGED("Route Changed", "🛣️"),
        EMERGENCY_ALERT("Emergency Alert", "🚨"),
        MAINTENANCE_REMINDER("Maintenance Due", "🔧"),
        FUEL_LOW("Low Fuel", "⛽"),
        GENERAL_ANNOUNCEMENT("Announcement", "📢");
        
        private final String title;
        private final String emoji;
        
        NotificationType(String title, String emoji) {
            this.title = title;
            this.emoji = emoji;
        }
        
        public String getTitle() { return title; }
        public String getEmoji() { return emoji; }
    }
    
    // Send notification to specific user
    public CompletableFuture<String> sendToUser(String userToken, NotificationType type, 
                                               String message, Map<String, String> data) {
        if (firebaseMessaging == null) {
            return CompletableFuture.completedFuture("Firebase Messaging not available");
        }
        
        try {
            Message fcmMessage = Message.builder()
                .setToken(userToken)
                .setNotification(Notification.builder()
                    .setTitle(type.getEmoji() + " " + type.getTitle())
                    .setBody(message)
                    .build())
                .putAllData(data != null ? data : new HashMap<>())
                .setAndroidConfig(AndroidConfig.builder()
                    .setTtl(3600 * 1000) // 1 hour
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                        .setIcon("ic_notification")
                        .setColor("#1976d2")
                        .setSound("default")
                        .build())
                    .build())
                .setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setAlert(ApsAlert.builder()
                            .setTitle(type.getEmoji() + " " + type.getTitle())
                            .setBody(message)
                            .build())
                        .setBadge(1)
                        .setSound("default")
                        .build())
                    .build())
                .build();
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    String response = firebaseMessaging.send(fcmMessage);
                    logger.info("Successfully sent message to user: {}", response);
                    
                    // Store notification in database
                    storeNotification(userToken, type, message, data);
                    
                    return response;
                } catch (Exception e) {
                    logger.error("Failed to send message to user: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error building FCM message: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
    
    // Send notification to multiple users
    public CompletableFuture<BatchResponse> sendToMultipleUsers(List<String> userTokens, 
                                                               NotificationType type, String message, 
                                                               Map<String, String> data) {
        if (firebaseMessaging == null || userTokens.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        
        try {
            MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(userTokens)
                .setNotification(Notification.builder()
                    .setTitle(type.getEmoji() + " " + type.getTitle())
                    .setBody(message)
                    .build())
                .putAllData(data != null ? data : new HashMap<>())
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                        .setIcon("ic_notification")
                        .setColor("#1976d2")
                        .setSound("default")
                        .build())
                    .build())
                .build();
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    BatchResponse response = firebaseMessaging.sendMulticast(multicastMessage);
                    logger.info("Successfully sent multicast message. Success: {}, Failure: {}", 
                               response.getSuccessCount(), response.getFailureCount());
                    
                    // Store notifications for all users
                    for (String token : userTokens) {
                        storeNotification(token, type, message, data);
                    }
                    
                    return response;
                } catch (Exception e) {
                    logger.error("Failed to send multicast message: {}", e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error building multicast FCM message: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
    
    // Send notification to topic (role-based)
    public CompletableFuture<String> sendToRole(Role role, NotificationType type, 
                                               String message, Map<String, String> data) {
        if (firebaseMessaging == null) {
            return CompletableFuture.completedFuture("Firebase Messaging not available");
        }
        
        String topic = "role_" + role.name().toLowerCase();
        
        try {
            Message topicMessage = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                    .setTitle(type.getEmoji() + " " + type.getTitle())
                    .setBody(message)
                    .build())
                .putAllData(data != null ? data : new HashMap<>())
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                        .setIcon("ic_notification")
                        .setColor("#1976d2")
                        .setSound("default")
                        .build())
                    .build())
                .build();
            
            return CompletableFuture.supplyAsync(() -> {
                try {
                    String response = firebaseMessaging.send(topicMessage);
                    logger.info("Successfully sent message to topic {}: {}", topic, response);
                    
                    // Store notification in database
                    storeNotification(topic, type, message, data);
                    
                    return response;
                } catch (Exception e) {
                    logger.error("Failed to send message to topic {}: {}", topic, e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error building topic FCM message: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
    
    // Subscribe user to role-based topic
    public CompletableFuture<TopicManagementResponse> subscribeToRole(String userToken, Role role) {
        if (firebaseMessaging == null) {
            return CompletableFuture.completedFuture(null);
        }
        
        String topic = "role_" + role.name().toLowerCase();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                TopicManagementResponse response = firebaseMessaging.subscribeToTopic(
                    Collections.singletonList(userToken), topic);
                logger.info("Successfully subscribed user to topic {}: {} success, {} failures", 
                           topic, response.getSuccessCount(), response.getFailureCount());
                return response;
            } catch (Exception e) {
                logger.error("Failed to subscribe to topic {}: {}", topic, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
    
    // Send emergency alert to all users
    public CompletableFuture<String> sendEmergencyAlert(String vehicleNumber, String location, 
                                                       String emergencyDetails) {
        String message = String.format("Emergency alert for vehicle %s at %s: %s", 
                                      vehicleNumber, location, emergencyDetails);
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "emergency");
        data.put("vehicleNumber", vehicleNumber);
        data.put("location", location);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return sendToRole(Role.ADMIN, NotificationType.EMERGENCY_ALERT, message, data);
    }
    
    // Send vehicle arrival notification
    public CompletableFuture<String> sendVehicleArrivalNotification(String vehicleNumber, 
                                                                   String stopName, String universityRoute) {
        String message = String.format("Vehicle %s has arrived at %s", vehicleNumber, stopName);
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "arrival");
        data.put("vehicleNumber", vehicleNumber);
        data.put("stopName", stopName);
        data.put("route", universityRoute);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        // Send to students and staff who might be waiting
        return sendToRole(Role.STUDENT, NotificationType.VEHICLE_ARRIVED, message, data);
    }
    
    // Send maintenance reminder
    public CompletableFuture<String> sendMaintenanceReminder(String vehicleNumber, String dueDate) {
        String message = String.format("Vehicle %s maintenance is due on %s", vehicleNumber, dueDate);
        
        Map<String, String> data = new HashMap<>();
        data.put("type", "maintenance");
        data.put("vehicleNumber", vehicleNumber);
        data.put("dueDate", dueDate);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return sendToRole(Role.ADMIN, NotificationType.MAINTENANCE_REMINDER, message, data);
    }
    
    // Store notification in Firebase Database for history
    private void storeNotification(String recipient, NotificationType type, String message, 
                                 Map<String, String> data) {
        try {
            DatabaseReference notificationsRef = firebaseDatabase.getReference("notifications");
            String notificationId = notificationsRef.push().getKey();
            
            Map<String, Object> notification = new HashMap<>();
            notification.put("recipient", recipient);
            notification.put("type", type.name());
            notification.put("title", type.getEmoji() + " " + type.getTitle());
            notification.put("message", message);
            notification.put("data", data);
            notification.put("timestamp", System.currentTimeMillis());
            notification.put("read", false);
            
            notificationsRef.child(notificationId).setValueAsync(notification);
            
        } catch (Exception e) {
            logger.error("Failed to store notification: {}", e.getMessage());
        }
    }
    
    // Get notification history for user
    public CompletableFuture<List<Map<String, Object>>> getNotificationHistory(String userToken) {
        CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
        
        try {
            DatabaseReference notificationsRef = firebaseDatabase.getReference("notifications");
            notificationsRef.orderByChild("recipient").equalTo(userToken)
                .limitToLast(50) // Get last 50 notifications
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        List<Map<String, Object>> notifications = new ArrayList<>();
                        for (com.google.firebase.database.DataSnapshot child : dataSnapshot.getChildren()) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> notification = (Map<String, Object>) child.getValue();
                            notifications.add(notification);
                        }
                        future.complete(notifications);
                    }
                    
                    @Override
                    public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                        future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
                    }
                });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
} 