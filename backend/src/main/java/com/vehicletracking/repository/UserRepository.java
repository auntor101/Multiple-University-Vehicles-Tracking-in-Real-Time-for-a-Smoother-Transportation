package com.vehicletracking.repository;

import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByUniversity(String university);
    
    List<User> findByUniversityAndRole(String university, Role role);
    
    List<User> findByIsActive(Boolean isActive);
    
    List<User> findByRoleAndIsActive(Role role, Boolean isActive);
    
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoles(@Param("roles") List<Role> roles);
    
    @Query("SELECT u FROM User u WHERE u.university = :university AND u.role IN :roles AND u.isActive = true")
    List<User> findActiveUsersByUniversityAndRoles(@Param("university") String university, @Param("roles") List<Role> roles);
    
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm% OR u.email LIKE %:searchTerm% OR u.username LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
    
    Optional<User> findByStudentId(String studentId);
    
    Optional<User> findByEmployeeId(String employeeId);
    
    Optional<User> findByLicenseNumber(String licenseNumber);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.university = :university AND u.role = :role")
    Long countUsersByUniversityAndRole(@Param("university") String university, @Param("role") Role role);
} 