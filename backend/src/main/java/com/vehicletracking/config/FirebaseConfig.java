package com.vehicletracking.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    
    @Value("${firebase.database.url}")
    private String databaseUrl;
    
    @Value("${firebase.storage.bucket}")
    private String storageBucket;
    
    @PostConstruct
    public void initialize() {
        try {
            // For development, you can use a service account key file
            // In production, use environment variables or other secure methods
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setDatabaseUrl(databaseUrl)
                .setStorageBucket(storageBucket)
                // For development, you might want to use service account key
                // .setCredentials(GoogleCredentials.fromStream(new FileInputStream("path/to/serviceAccountKey.json")))
                .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully");
            }
        } catch (Exception e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
            // Don't fail the application startup if Firebase is not configured
            // This allows the app to run without Firebase for basic functionality
        }
    }
    
    @Bean
    public FirebaseDatabase firebaseDatabase() {
        try {
            return FirebaseDatabase.getInstance();
        } catch (Exception e) {
            System.err.println("Firebase Database not available: " + e.getMessage());
            return null;
        }
    }
} 