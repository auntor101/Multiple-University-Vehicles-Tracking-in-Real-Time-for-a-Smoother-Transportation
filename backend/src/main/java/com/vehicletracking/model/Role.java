package com.vehicletracking.model;

public enum Role {
    ADMIN("ADMIN"),
    STUDENT("STUDENT"),
    TEACHER("TEACHER"),
    DRIVER("DRIVER"),
    OFFICE_ADMIN("OFFICE_ADMIN");

    private final String value;

    Role(String value) {
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