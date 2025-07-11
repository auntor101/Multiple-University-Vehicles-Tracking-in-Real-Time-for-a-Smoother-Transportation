package com.vehicletracking.exception;

public class DuplicateVehicleException extends RuntimeException {
    
    public DuplicateVehicleException(String message) {
        super(message);
    }
    
    public DuplicateVehicleException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DuplicateVehicleException withNumber(String vehicleNumber) {
        return new DuplicateVehicleException("Vehicle number already exists: " + vehicleNumber);
    }
} 