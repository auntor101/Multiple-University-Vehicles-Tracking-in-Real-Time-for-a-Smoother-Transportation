package com.vehicletracking.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleNumberValidatorTest {
    
    private VehicleNumberValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;
    
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
    
    @BeforeEach
    void setUp() {
        validator = new VehicleNumberValidator();
        
        // Setup mock behavior for constraint violation context
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);
    }
    
    @Test
    void isValid_ValidVehicleNumbers_ReturnsTrue() {
        // Test valid vehicle numbers
        assertTrue(validator.isValid("STU-001", context));
        assertTrue(validator.isValid("TCH-999", context));
        assertTrue(validator.isValid("OFC-123", context));
        assertTrue(validator.isValid("GEN-456", context));
        assertTrue(validator.isValid("EMG-789", context));
        assertTrue(validator.isValid("MNT-002", context));
        
        // Verify no constraint violations were built
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
    
    @Test
    void isValid_NullVehicleNumber_ReturnsFalse() {
        // Given
        String vehicleNumber = null;
        
        // When
        boolean result = validator.isValid(vehicleNumber, context);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void isValid_EmptyVehicleNumber_ReturnsFalse() {
        // Given
        String vehicleNumber = "";
        
        // When
        boolean result = validator.isValid(vehicleNumber, context);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void isValid_WhitespaceOnlyVehicleNumber_ReturnsFalse() {
        // Given
        String vehicleNumber = "   ";
        
        // When
        boolean result = validator.isValid(vehicleNumber, context);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    void isValid_InvalidPattern_ReturnsFalse() {
        // Test various invalid patterns
        assertFalse(validator.isValid("STU001", context)); // Missing hyphen
        assertFalse(validator.isValid("STU-01", context)); // Too few digits
        assertFalse(validator.isValid("STU-0001", context)); // Too many digits
        assertFalse(validator.isValid("S-001", context)); // Too few letters
        assertFalse(validator.isValid("STUD-001", context)); // Too many letters
        assertFalse(validator.isValid("123-001", context)); // Numbers instead of letters
        assertFalse(validator.isValid("STU-ABC", context)); // Letters instead of numbers
        
        // Verify constraint violations were built for each invalid case
        verify(context, atLeast(7)).disableDefaultConstraintViolation();
        verify(context, atLeast(7)).buildConstraintViolationWithTemplate(anyString());
    }
    
    @Test
    void isValid_InvalidPrefix_ReturnsFalse() {
        // Test invalid prefixes
        assertFalse(validator.isValid("XXX-001", context));
        assertFalse(validator.isValid("ABC-001", context));
        assertFalse(validator.isValid("ZZZ-001", context));
        
        // Verify constraint violations were built
        verify(context, times(3)).disableDefaultConstraintViolation();
        verify(context, times(3)).buildConstraintViolationWithTemplate(
            "Invalid vehicle prefix. Use: STU (Student), TCH (Teacher), OFC (Office), GEN (General), EMG (Emergency)"
        );
    }
    
    @Test
    void isValid_InvalidPatterns_ReturnsFalse() {
        // Test patterns that should be rejected
        assertFalse(validator.isValid("TEST-001", context));
        assertFalse(validator.isValid("STU-TEST", context));
        assertFalse(validator.isValid("DEMO-001", context));
        assertFalse(validator.isValid("SAMPLE-001", context));
        assertFalse(validator.isValid("EXAMPLE-001", context));
        
        // Verify constraint violations were built
        verify(context, atLeast(5)).disableDefaultConstraintViolation();
    }
    
    @Test
    void isValid_NumberOutOfRange_ReturnsFalse() {
        // Test numbers out of valid range (001-999)
        assertFalse(validator.isValid("STU-000", context));
        
        // Verify constraint violation was built
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(
            "Vehicle number must be between 001 and 999"
        );
    }
    
    @Test
    void isValid_CaseInsensitive_ReturnsTrue() {
        // Test that validator handles different cases correctly
        assertTrue(validator.isValid("stu-001", context));
        assertTrue(validator.isValid("Stu-001", context));
        assertTrue(validator.isValid("STU-001", context));
        
        // Verify no constraint violations were built
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
    
    @Test
    void isValid_WithWhitespace_ReturnsTrue() {
        // Test that validator trims whitespace
        assertTrue(validator.isValid(" STU-001 ", context));
        assertTrue(validator.isValid("STU-001\t", context));
        assertTrue(validator.isValid("\nSTU-001", context));
        
        // Verify no constraint violations were built
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
    
    @Test
    void isValid_EdgeCaseNumbers_ReturnsTrue() {
        // Test edge cases for numbers
        assertTrue(validator.isValid("STU-001", context)); // Minimum valid
        assertTrue(validator.isValid("STU-999", context)); // Maximum valid
        assertTrue(validator.isValid("STU-100", context)); // Middle range
        
        // Verify no constraint violations were built
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
    
    @Test
    void isValid_AllValidPrefixes_ReturnsTrue() {
        // Test all valid prefixes
        assertTrue(validator.isValid("STU-001", context)); // Student
        assertTrue(validator.isValid("TCH-001", context)); // Teacher
        assertTrue(validator.isValid("OFC-001", context)); // Office
        assertTrue(validator.isValid("GEN-001", context)); // General
        assertTrue(validator.isValid("EMG-001", context)); // Emergency
        assertTrue(validator.isValid("MNT-001", context)); // Maintenance
        
        // Verify no constraint violations were built
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }
} 