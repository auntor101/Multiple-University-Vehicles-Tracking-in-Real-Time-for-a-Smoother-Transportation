package com.vehicletracking.exception;

import com.vehicletracking.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Validation error [{}]: {}", errorId, ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Validation failed",
            "One or more fields have invalid values",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false),
            errors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Constraint violation [{}]: {}", errorId, ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Constraint violation",
            "Data constraints were violated",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false),
            errors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Authentication failed [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Authentication failed",
            "Invalid username or password",
            HttpStatus.UNAUTHORIZED.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Authentication error [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Authentication required",
            "Please provide valid authentication credentials",
            HttpStatus.UNAUTHORIZED.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Access denied [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Access denied",
            "You don't have permission to access this resource",
            HttpStatus.FORBIDDEN.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotFoundException(
            VehicleNotFoundException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Vehicle not found [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Vehicle not found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("User not found [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "User not found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(
            BusinessLogicException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Business logic error [{}]: {}", errorId, ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Business rule violation",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.warn("Type mismatch [{}]: {}", errorId, ex.getMessage());
        
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s", 
            ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Invalid parameter type",
            message,
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected error [{}]: {}", errorId, ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            errorId,
            "Internal server error",
            "An unexpected error occurred. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false),
            null
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Error Response DTO
    public static class ErrorResponse {
        private String errorId;
        private String error;
        private String message;
        private int status;
        private String path;
        private LocalDateTime timestamp;
        private Map<String, String> validationErrors;
        
        public ErrorResponse(String errorId, String error, String message, int status, 
                           String path, Map<String, String> validationErrors) {
            this.errorId = errorId;
            this.error = error;
            this.message = message;
            this.status = status;
            this.path = path;
            this.timestamp = LocalDateTime.now();
            this.validationErrors = validationErrors;
        }
        
        // Getters and setters
        public String getErrorId() { return errorId; }
        public void setErrorId(String errorId) { this.errorId = errorId; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
    }
} 