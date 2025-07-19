package com.vehicletracking.model;

import com.vehicletracking.config.SecurityConstants;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class User {
    
    private String id; // Firebase uses String IDs
    
    @NotBlank(message = "Username is required")
    @Size(min = SecurityConstants.MIN_USERNAME_LENGTH, max = SecurityConstants.MAX_USERNAME_LENGTH, 
          message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = SecurityConstants.USERNAME_PATTERN, 
             message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Pattern(regexp = SecurityConstants.EMAIL_PATTERN, message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = SecurityConstants.MIN_PASSWORD_LENGTH, max = SecurityConstants.MAX_PASSWORD_LENGTH)
    @Pattern(regexp = SecurityConstants.PASSWORD_PATTERN, 
             message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;
    
    @Pattern(regexp = SecurityConstants.PHONE_NUMBER_PATTERN, 
             message = "Phone number should be in international format")
    private String phoneNumber;
    
    private Role role;
    
    @Size(max = 100, message = "University name must not exceed 100 characters")
    private String university;
    
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String department;
    
    @Size(max = 50, message = "Student ID must not exceed 50 characters")
    private String studentId;
    
    @Size(max = 50, message = "Employee ID must not exceed 50 characters")
    private String employeeId;
    
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;
    
    private Boolean isActive = true;
    
    private Boolean isEmailVerified = false;
    
    private LocalDateTime lastLogin;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getUniversity() {
        return university;
    }
    
    public void setUniversity(String university) {
        this.university = university;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }
    
    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 