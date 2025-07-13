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
    
    @Value("${firebase.database.url}")
    private String databaseUrl;
    
    @Value("${firebase.storage.bucket}")
    private String storageBucket;
    
    @Value("${firebase.project.id}")
    private String projectId;
    
    @Value("${firebase.service.account.key.path}")
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
        // Load from classpath for local development
        ClassPathResource resource = new ClassPathResource(serviceAccountKeyPath);
        if (resource.exists() && resource.isReadable()) {
            logger.info("Loading Firebase service account from classpath: {}", serviceAccountKeyPath);
            return resource.getInputStream();
        }
        
        throw new IOException("Firebase service account configuration not found. " +
            "Please provide firebase-service-account.json in src/main/resources/");
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