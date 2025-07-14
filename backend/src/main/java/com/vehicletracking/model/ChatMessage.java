package com.vehicletracking.model;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ChatMessage {
    
    private String id;
    
    @NotBlank(message = "Message content is required")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    private String message;
    
    @NotBlank(message = "Sender ID is required")
    private String senderId;
    
    @NotBlank(message = "Sender name is required")
    private String senderName;
    
    private Role senderRole;
    
    private String recipientId;
    
    private String recipientName;
    
    private Role recipientRole;
    
    private ChatMessageType type = ChatMessageType.USER_MESSAGE;
    
    private Boolean isRead = false;
    
    private Boolean isEdited = false;
    
    private LocalDateTime editedAt;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime readAt;
    
    // Constructors
    public ChatMessage() {}
    
    public ChatMessage(String message, String senderId, String senderName, Role senderRole) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.type = ChatMessageType.USER_MESSAGE;
    }
    
    public ChatMessage(String message, String senderId, String senderName, Role senderRole,
                      String recipientId, String recipientName, Role recipientRole) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.recipientRole = recipientRole;
        this.type = ChatMessageType.PRIVATE_MESSAGE;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { 
        this.message = message;
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
    }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public Role getSenderRole() { return senderRole; }
    public void setSenderRole(Role senderRole) { this.senderRole = senderRole; }
    
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    
    public Role getRecipientRole() { return recipientRole; }
    public void setRecipientRole(Role recipientRole) { this.recipientRole = recipientRole; }
    
    public ChatMessageType getType() { return type; }
    public void setType(ChatMessageType type) { this.type = type; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { 
        this.isRead = isRead;
        if (isRead && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
    
    public Boolean getIsEdited() { return isEdited; }
    public void setIsEdited(Boolean isEdited) { this.isEdited = isEdited; }
    
    public LocalDateTime getEditedAt() { return editedAt; }
    public void setEditedAt(LocalDateTime editedAt) { this.editedAt = editedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    // Utility methods
    public boolean isPrivateMessage() {
        return type == ChatMessageType.PRIVATE_MESSAGE;
    }
    
    public boolean isSystemMessage() {
        return type == ChatMessageType.SYSTEM_MESSAGE;
    }
    
    public boolean isUserMessage() {
        return type == ChatMessageType.USER_MESSAGE;
    }
    
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}

enum ChatMessageType {
    USER_MESSAGE,
    PRIVATE_MESSAGE,
    SYSTEM_MESSAGE,
    ANNOUNCEMENT
}
