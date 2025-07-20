package com.vehicletracking.service;

import com.vehicletracking.dto.UserRegistrationDto;
import com.vehicletracking.dto.UserResponseDto;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    /**
     * Create a new user
     */
    UserResponseDto createUser(UserRegistrationDto userDto);
    
    /**
     * Update user details
     */
    UserResponseDto updateUser(String id, UserRegistrationDto userDto);
    
    /**
     * Delete user
     */
    void deleteUser(String id);
    
    /**
     * Find user by ID
     */
    Optional<User> findById(String id);
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Get all users
     */
    List<UserResponseDto> getAllUsers();
    
    /**
     * Get users by role
     */
    List<UserResponseDto> getUsersByRole(Role role);
    
    /**
     * Get users by university
     */
    List<UserResponseDto> getUsersByUniversity(String university);
    
    /**
     * Search users
     */
    List<UserResponseDto> searchUsers(String searchTerm);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Get user statistics
     */
    UserStats getUserStats();
    
    /**
     * Update user status
     */
    UserResponseDto updateUserStatus(String userId, boolean isActive);
    
    /**
     * Reset user password
     */
    void resetPassword(String email);
    
    /**
     * Change user password
     */
    void changePassword(String userId, String currentPassword, String newPassword);
}

class UserStats {
    private Long totalUsers;
    private Long activeUsers;
    private Long adminUsers;
    private Long driverUsers;
    private Long teacherUsers;
    private Long studentUsers;
    private Long newUsersThisMonth;
    
    // Constructors
    public UserStats() {}
    
    public UserStats(Long totalUsers, Long activeUsers, Long adminUsers, 
                    Long driverUsers, Long teacherUsers, Long studentUsers, 
                    Long newUsersThisMonth) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.adminUsers = adminUsers;
        this.driverUsers = driverUsers;
        this.teacherUsers = teacherUsers;
        this.studentUsers = studentUsers;
        this.newUsersThisMonth = newUsersThisMonth;
    }
    
    // Getters and Setters
    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
    
    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
    
    public Long getAdminUsers() { return adminUsers; }
    public void setAdminUsers(Long adminUsers) { this.adminUsers = adminUsers; }
    
    public Long getDriverUsers() { return driverUsers; }
    public void setDriverUsers(Long driverUsers) { this.driverUsers = driverUsers; }
    
    public Long getTeacherUsers() { return teacherUsers; }
    public void setTeacherUsers(Long teacherUsers) { this.teacherUsers = teacherUsers; }
    
    public Long getStudentUsers() { return studentUsers; }
    public void setStudentUsers(Long studentUsers) { this.studentUsers = studentUsers; }
    
    public Long getNewUsersThisMonth() { return newUsersThisMonth; }
    public void setNewUsersThisMonth(Long newUsersThisMonth) { this.newUsersThisMonth = newUsersThisMonth; }
}
