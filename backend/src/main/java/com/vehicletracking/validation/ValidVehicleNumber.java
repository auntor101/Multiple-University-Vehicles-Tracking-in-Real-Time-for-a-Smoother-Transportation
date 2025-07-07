package com.vehicletracking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VehicleNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVehicleNumber {
    
    String message() default "Invalid vehicle number format";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
} 