package com.vehicletracking.exception;

public class DriverAssignmentException extends RuntimeException {
    
    public DriverAssignmentException(String message) {
        super(message);
    }
    
    public DriverAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DriverAssignmentException notADriver(Long userId) {
        return new DriverAssignmentException("User with id " + userId + " is not a driver");
    }
    
    public static DriverAssignmentException alreadyAssigned(Long driverId) {
        return new DriverAssignmentException("Driver with id " + driverId + " is already assigned to another vehicle");
    }
    
    public static DriverAssignmentException noVehicleAssigned(Long driverId) {
        return new DriverAssignmentException("No vehicle assigned to driver with id " + driverId);
    }
} 