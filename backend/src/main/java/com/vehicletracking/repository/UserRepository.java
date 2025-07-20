package com.vehicletracking.repository;

import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByUniversity(String university);
    
    List<User> findByUniversityAndRole(String university, Role role);
    
    List<User> findByIsActive(Boolean isActive);
    
    User save(User user);
    
    Optional<User> findById(String id);
    
    void deleteById(String id);
    
    List<User> findAll();
    
    List<User> findByRoleAndIsActive(Role role, Boolean isActive);
    
    List<User> findByRoles(List<Role> roles);
    
    List<User> findActiveUsersByUniversityAndRoles(String university, List<Role> roles);
    
    List<User> searchUsers(String searchTerm);
    
    Optional<User> findByStudentId(String studentId);
    
    Optional<User> findByEmployeeId(String employeeId);
    
    Optional<User> findByLicenseNumber(String licenseNumber);
    
    Long countUsersByUniversityAndRole(String university, Role role);
    
    long count();
} 