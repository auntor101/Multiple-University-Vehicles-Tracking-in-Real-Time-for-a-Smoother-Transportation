package com.vehicletracking.validation;

import com.vehicletracking.config.SecurityConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class VehicleNumberValidator implements ConstraintValidator<ValidVehicleNumber, String> {
    
    private static final Pattern VEHICLE_NUMBER_PATTERN = Pattern.compile(SecurityConstants.VEHICLE_NUMBER_PATTERN);
    
    // Common invalid patterns to reject
    private static final String[] INVALID_PATTERNS = {
        "TEST", "DEMO", "SAMPLE", "EXAMPLE", "NULL", "UNDEFINED"
    };
    
    @Override
    public void initialize(ValidVehicleNumber constraintAnnotation) {
        // Initialization if needed
    }
    
    @Override
    public boolean isValid(String vehicleNumber, ConstraintValidatorContext context) {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            return false;
        }
        
        String trimmedNumber = vehicleNumber.trim().toUpperCase();
        
        // Check basic pattern (e.g., "STU-001", "TCH-001", "OFC-001")
        if (!VEHICLE_NUMBER_PATTERN.matcher(trimmedNumber).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Vehicle number must follow pattern: 2-3 letters, hyphen, 3 digits (e.g., STU-001)"
            ).addConstraintViolation();
            return false;
        }
        
        // Check for invalid patterns
        for (String invalidPattern : INVALID_PATTERNS) {
            if (trimmedNumber.contains(invalidPattern)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Vehicle number cannot contain test or placeholder values"
                ).addConstraintViolation();
                return false;
            }
        }
        
        // Validate prefix based on vehicle type context
        String prefix = trimmedNumber.split("-")[0];
        if (!isValidPrefix(prefix)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Invalid vehicle prefix. Use: STU (Student), TCH (Teacher), OFC (Office), GEN (General), EMG (Emergency)"
            ).addConstraintViolation();
            return false;
        }
        
        // Validate number range (001-999)
        String numberPart = trimmedNumber.split("-")[1];
        int number = Integer.parseInt(numberPart);
        if (number < 1 || number > 999) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Vehicle number must be between 001 and 999"
            ).addConstraintViolation();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidPrefix(String prefix) {
        return prefix.matches("^(STU|TCH|OFC|GEN|EMG|MNT)$");
    }
} 