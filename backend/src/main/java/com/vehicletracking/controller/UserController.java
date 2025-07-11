package com.vehicletracking.controller;

import com.vehicletracking.dto.MessageResponse;
import com.vehicletracking.dto.UserResponseDto;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import com.vehicletracking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"${app.cors.allowed-origins}"}, maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    // Get All Users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userDtos = users.stream()
            .map(this::mapUserToResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }
    
    // Get Users by Role (Admin only)
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userRepository.findByRole(role);
        List<UserResponseDto> userDtos = users.stream()
            .map(this::mapUserToResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }
    
    // Get All Drivers (for vehicle assignment)
    @GetMapping("/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllDrivers() {
        List<User> drivers = userRepository.findByRoleAndIsActive(Role.DRIVER, true);
        List<UserResponseDto> driverDtos = drivers.stream()
            .map(this::mapUserToResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(driverDtos);
    }
    
    // Get Users by University
    @GetMapping("/university/{university}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByUniversity(@PathVariable String university) {
        List<User> users = userRepository.findByUniversity(university);
        List<UserResponseDto> userDtos = users.stream()
            .map(this::mapUserToResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }
    
    // Search Users (Admin only)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestParam String q) {
        List<User> users = userRepository.searchUsers(q);
        List<UserResponseDto> userDtos = users.stream()
            .map(this::mapUserToResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }
    
    // Get User by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #id")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> ResponseEntity.ok(mapUserToResponseDto(user)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Update User Status (Admin only)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        return userRepository.findById(id)
            .map(user -> {
                user.setIsActive(isActive);
                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok(mapUserToResponseDto(updatedUser));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Get User Statistics (Admin only)
    @GetMapping("/stats/university/{university}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserStatsByUniversity(@PathVariable String university) {
        try {
            Long studentCount = userRepository.countUsersByUniversityAndRole(university, Role.STUDENT);
            Long teacherCount = userRepository.countUsersByUniversityAndRole(university, Role.TEACHER);
            Long driverCount = userRepository.countUsersByUniversityAndRole(university, Role.DRIVER);
            Long adminCount = userRepository.countUsersByUniversityAndRole(university, Role.ADMIN);
            Long officeAdminCount = userRepository.countUsersByUniversityAndRole(university, Role.OFFICE_ADMIN);
            
            return ResponseEntity.ok(new UserStats(studentCount, teacherCount, driverCount, adminCount, officeAdminCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error fetching user statistics"));
        }
    }
    
    // Helper method to map User to UserResponseDto
    private UserResponseDto mapUserToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setUniversity(user.getUniversity());
        dto.setDepartment(user.getDepartment());
        dto.setStudentId(user.getStudentId());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setLicenseNumber(user.getLicenseNumber());
        dto.setIsActive(user.getIsActive());
        dto.setIsEmailVerified(user.getIsEmailVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    
    // Inner class for user statistics
    public static class UserStats {
        private Long students;
        private Long teachers;
        private Long drivers;
        private Long admins;
        private Long officeAdmins;
        
        public UserStats(Long students, Long teachers, Long drivers, Long admins, Long officeAdmins) {
            this.students = students;
            this.teachers = teachers;
            this.drivers = drivers;
            this.admins = admins;
            this.officeAdmins = officeAdmins;
        }
        
        // Getters
        public Long getStudents() { return students; }
        public Long getTeachers() { return teachers; }
        public Long getDrivers() { return drivers; }
        public Long getAdmins() { return admins; }
        public Long getOfficeAdmins() { return officeAdmins; }
        public Long getTotal() { return students + teachers + drivers + admins + officeAdmins; }
    }
} 