package com.vehicletracking.model;

public enum VehicleType {
    STUDENT_BUS("STUDENT_BUS"),
    TEACHER_BUS("TEACHER_BUS"),
    OFFICE_ADMIN_VEHICLE("OFFICE_ADMIN_VEHICLE"),
    GENERAL_TRANSPORT("GENERAL_TRANSPORT");

    private final String value;

    VehicleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
} 