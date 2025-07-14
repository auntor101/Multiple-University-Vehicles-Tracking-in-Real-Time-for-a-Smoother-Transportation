package com.vehicletracking.controller;

import com.vehicletracking.dto.MessageResponse;
import com.vehicletracking.model.ChatMessage;
import com.vehicletracking.model.User;
import com.vehicletracking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    // Store chat messages in memory (in production, use a database)
    private static final List<ChatMessage> chatHistory = new ArrayList<>();
    private static final ConcurrentHashMap<String, String> activeUsers = new ConcurrentHashMap<>();

    /**
     * Handle new chat messages
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Set timestamp
            chatMessage.setCreatedAt(LocalDateTime.now());
            
            // Generate ID (in production, use proper ID generation)
            chatMessage.setId(System.currentTimeMillis() + "_" + chatMessage.getSenderId());
            
            // Store message in history
            chatHistory.add(chatMessage);
            
            // Limit chat history to last 100 messages
            if (chatHistory.size() > 100) {
                chatHistory.remove(0);
            }
            
            return chatMessage;
            
        } catch (Exception e) {
            // Handle error
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setMessage("Error sending message: " + e.getMessage());
            errorMessage.setSenderName("System");
            errorMessage.setCreatedAt(LocalDateTime.now());
            return errorMessage;
        }
    }

    /**
     * Handle user joining chat
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Add user to active users
            activeUsers.put(headerAccessor.getSessionId(), chatMessage.getSenderName());
            
            // Set timestamp
            chatMessage.setCreatedAt(LocalDateTime.now());
            chatMessage.setId(System.currentTimeMillis() + "_join");
            chatMessage.setMessage(chatMessage.getSenderName() + " joined the chat!");
            
            return chatMessage;
            
        } catch (Exception e) {
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setMessage("Error adding user: " + e.getMessage());
            errorMessage.setSenderName("System");
            errorMessage.setCreatedAt(LocalDateTime.now());
            return errorMessage;
        }
    }

    /**
     * Send private message
     */
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        try {
            chatMessage.setCreatedAt(LocalDateTime.now());
            chatMessage.setId(System.currentTimeMillis() + "_private");
            
            // Send to specific user
            messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),
                "/queue/private",
                chatMessage
            );
            
            // Store in history
            chatHistory.add(chatMessage);
            
        } catch (Exception e) {
            // Send error message back to sender
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setMessage("Error sending private message: " + e.getMessage());
            errorMessage.setSenderName("System");
            errorMessage.setCreatedAt(LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderId(),
                "/queue/private",
                errorMessage
            );
        }
    }

    /**
     * Get chat history (REST endpoint)
     */
    @GetMapping("/history")
    @ResponseBody
    public ResponseEntity<?> getChatHistory(@RequestParam(defaultValue = "50") int limit) {
        try {
            int startIndex = Math.max(0, chatHistory.size() - limit);
            List<ChatMessage> recentMessages = chatHistory.subList(startIndex, chatHistory.size());
            
            return ResponseEntity.ok(recentMessages);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching chat history: " + e.getMessage()));
        }
    }

    /**
     * Get active users (REST endpoint)
     */
    @GetMapping("/active-users")
    @ResponseBody
    public ResponseEntity<?> getActiveUsers() {
        try {
            return ResponseEntity.ok(activeUsers.values());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching active users: " + e.getMessage()));
        }
    }

    /**
     * Send announcement to all users (Admin only)
     */
    @PostMapping("/broadcast")
    @ResponseBody
    public ResponseEntity<?> broadcastMessage(@Valid @RequestBody ChatMessage message,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            
            // Only admin can broadcast
            if (!user.getRole().name().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only administrators can broadcast messages"));
            }
            
            message.setSenderId(user.getId().toString());
            message.setSenderName(user.getFirstName() + " " + user.getLastName());
            message.setSenderRole(user.getRole());
            message.setCreatedAt(LocalDateTime.now());
            message.setId(System.currentTimeMillis() + "_broadcast");
            
            // Broadcast to all connected users
            messagingTemplate.convertAndSend("/topic/public", message);
            
            // Store in history
            chatHistory.add(message);
            
            return ResponseEntity.ok(new MessageResponse("Message broadcasted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error broadcasting message: " + e.getMessage()));
        }
    }

    /**
     * Clear chat history (Admin only)
     */
    @DeleteMapping("/history")
    @ResponseBody
    public ResponseEntity<?> clearChatHistory(Authentication authentication) {
        try {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("User not found"));
            }
            
            User user = userOpt.get();
            
            // Only admin can clear history
            if (!user.getRole().name().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only administrators can clear chat history"));
            }
            
            chatHistory.clear();
            
            // Notify all users about history clear
            ChatMessage systemMessage = new ChatMessage();
            systemMessage.setMessage("Chat history has been cleared by administrator");
            systemMessage.setSenderName("System");
            systemMessage.setCreatedAt(LocalDateTime.now());
            systemMessage.setId(System.currentTimeMillis() + "_system");
            
            messagingTemplate.convertAndSend("/topic/public", systemMessage);
            
            return ResponseEntity.ok(new MessageResponse("Chat history cleared successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error clearing chat history: " + e.getMessage()));
        }
    }
}
