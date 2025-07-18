package com.vehicletracking.config;

public final class SecurityConstants {
    
    // JWT Constants
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";
    public static final long JWT_EXPIRATION_TIME = 86400000; // 24 hours in milliseconds
    
    // Password Validation Constants
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    
    // Username Validation Constants
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]+$";
    
    // Rate Limiting Constants
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final long LOCKOUT_DURATION_MINUTES = 15;
    public static final int REQUESTS_PER_MINUTE = 60;
    public static final int REQUESTS_PER_HOUR = 1000;
    
    // Session Constants
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final boolean SECURE_COOKIES = true;
    public static final boolean HTTP_ONLY_COOKIES = true;
    
    // Security Headers
    public static final String CONTENT_SECURITY_POLICY = 
        "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self'";
    public static final String X_FRAME_OPTIONS = "DENY";
    public static final String X_CONTENT_TYPE_OPTIONS = "nosniff";
    public static final String X_XSS_PROTECTION = "1; mode=block";
    public static final String REFERRER_POLICY = "strict-origin-when-cross-origin";
    
    // API Rate Limits by Role
    public static final int ADMIN_REQUESTS_PER_MINUTE = 120;
    public static final int DRIVER_REQUESTS_PER_MINUTE = 100;
    public static final int USER_REQUESTS_PER_MINUTE = 60;
    
    // Location Update Constraints
    public static final double MAX_SPEED_KMH = 150.0; // Maximum realistic vehicle speed
    public static final double MIN_LOCATION_ACCURACY_METERS = 1000.0; // Minimum GPS accuracy
    public static final long MAX_LOCATION_AGE_MINUTES = 10; // Maximum age of location data
    public static final double MIN_LATITUDE = -90.0;
    public static final double MAX_LATITUDE = 90.0;
    public static final double MIN_LONGITUDE = -180.0;
    public static final double MAX_LONGITUDE = 180.0;
    
    // Vehicle Number Validation
    public static final String VEHICLE_NUMBER_PATTERN = "^[A-Z]{2,3}-\\d{3,4}$";
    public static final int MAX_VEHICLE_NUMBER_LENGTH = 10;
    
    // Phone Number Validation (International format)
    public static final String PHONE_NUMBER_PATTERN = "^\\+[1-9]\\d{1,14}$";
    
    // Email Validation (Enhanced RFC-compliant)
    public static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    
    // File Upload Constraints
    public static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
    
    // Input Sanitization
    public static final String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9\\s]+$";
    public static final String DESCRIPTION_PATTERN = "^[a-zA-Z0-9\\s.,!?-]+$";
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_NAME_LENGTH = 100;
    
    // Private constructor to prevent instantiation
    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }
} 