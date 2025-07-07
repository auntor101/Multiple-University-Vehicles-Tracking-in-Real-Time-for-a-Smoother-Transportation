package com.vehicletracking.exception;

public class VehicleNotFoundException extends RuntimeException {
    
    public VehicleNotFoundException(String message) {
        super(message);
    }
    
    public VehicleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public VehicleNotFoundException(Long vehicleId) {
        super("Vehicle not found with ID: " + vehicleId);
    }
    
    public VehicleNotFoundException(String field, String value) {
        super("Vehicle not found with " + field + ": " + value);
    }
} 