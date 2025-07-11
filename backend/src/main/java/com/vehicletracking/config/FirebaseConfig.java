package com.vehicletracking.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    @Value("${firebase.database.url:https://vehicle-tracking-dev-default-rtdb.firebaseio.com/}")
    private String databaseUrl;
    
    @Value("${firebase.storage.bucket:vehicle-tracking-dev.appspot.com}")
    private String storageBucket;
    
    @Value("${firebase.project.id:vehicle-tracking-dev}")
    private String projectId;
    
    @Value("${firebase.service.account.key.path:firebase-service-account.json}")
    private String serviceAccountKeyPath;
    
    @PostConstruct
    public void initialize() {
        try {
            // Check if Firebase is already initialized
            if (!FirebaseApp.getApps().isEmpty()) {
                logger.info("Firebase app already initialized");
                return;
            }
            
            // Load service account key
            InputStream serviceAccount = getServiceAccountStream();
            
            // Build Firebase options
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl(databaseUrl)
                .setStorageBucket(storageBucket)
                .setProjectId(projectId)
                .build();
            
            // Initialize Firebase
            FirebaseApp.initializeApp(options);
            logger.info("Firebase initialized successfully for project: {}", projectId);
            
        } catch (Exception e) {
            logger.error("Firebase initialization failed: {}", e.getMessage(), e);
            // Note: Application continues without Firebase for graceful degradation
        }
    }
    
    private InputStream getServiceAccountStream() throws IOException {
        try {
            // Try to load from classpath first
            ClassPathResource resource = new ClassPathResource(serviceAccountKeyPath);
            if (resource.exists() && resource.isReadable()) {
                logger.info("Loading Firebase service account from classpath: {}", serviceAccountKeyPath);
                return resource.getInputStream();
            }
        } catch (Exception e) {
            logger.warn("Could not load service account from classpath: {}", e.getMessage());
        }
        
        // Fallback: Try to load from environment variables
        String privateKeyId = System.getenv("FIREBASE_PRIVATE_KEY_ID");
        String privateKey = System.getenv("FIREBASE_PRIVATE_KEY");
        String clientEmail = System.getenv("FIREBASE_CLIENT_EMAIL");
        String clientId = System.getenv("FIREBASE_CLIENT_ID");
        
        if (privateKey != null && clientEmail != null) {
            // Create service account JSON from environment variables
            String serviceAccountJson = String.format(
                "{" +
                "\"type\": \"service_account\"," +
                "\"project_id\": \"%s\"," +
                "\"private_key_id\": \"%s\"," +
                "\"private_key\": \"%s\"," +
                "\"client_email\": \"%s\"," +
                "\"client_id\": \"%s\"," +
                "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\"," +
                "\"token_uri\": \"https://oauth2.googleapis.com/token\"" +
                "}",
                projectId, privateKeyId, privateKey.replace("\\n", "\n"), 
                clientEmail, clientId
            );
            
            return new java.io.ByteArrayInputStream(serviceAccountJson.getBytes());
        }
        
        throw new IOException("Firebase service account configuration not found. " +
            "Please provide firebase-service-account.json or set environment variables.");
    }
    
    @Bean
    public FirebaseAuth firebaseAuth() {
        try {
            return FirebaseAuth.getInstance();
        } catch (Exception e) {
            logger.error("Firebase Auth not available: {}", e.getMessage());
            return null;
        }
    }
    
    @Bean
    public FirebaseDatabase firebaseDatabase() {
        try {
            return FirebaseDatabase.getInstance();
        } catch (Exception e) {
            logger.error("Firebase Database not available: {}", e.getMessage());
            return null;
        }
    }
    
    @Bean
    public FirebaseMessaging firebaseMessaging() {
        try {
            return FirebaseMessaging.getInstance();
        } catch (Exception e) {
            logger.error("Firebase Messaging not available: {}", e.getMessage());
            return null;
        }
    }
    
    @Bean
    public Storage firebaseStorage() {
        try {
            return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
        } catch (Exception e) {
            logger.error("Firebase Storage not available: {}", e.getMessage());
            return null;
        }
    }
} 