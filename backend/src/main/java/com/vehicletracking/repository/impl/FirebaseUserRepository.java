package com.vehicletracking.repository.impl;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import com.vehicletracking.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class FirebaseUserRepository implements UserRepository {

    private static final String COLLECTION_NAME = "users";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("username", username)
                    .get()
                    .get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                User user = documentToUser(document);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get()
                    .get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                User user = documentToUser(document);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    @Override
    public Boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public Boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public List<User> findByRole(Role role) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("role", role.name())
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByUniversity(String university) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("university", university)
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByUniversityAndRole(String university, Role role) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("university", university)
                    .whereEqualTo("role", role.name())
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByIsActive(Boolean isActive) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("isActive", isActive)
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public User save(User user) {
        try {
            Map<String, Object> userData = userToMap(user);
            
            if (user.getId() == null || user.getId().isEmpty()) {
                // Create new user
                DocumentReference docRef = getFirestore().collection(COLLECTION_NAME).document();
                user.setId(docRef.getId());
                userData.put("id", user.getId());
                docRef.set(userData).get();
            } else {
                // Update existing user
                getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(user.getId())
                    .set(userData)
                    .get();
            }
            
            return user;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            DocumentSnapshot document = getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(id)
                    .get()
                    .get();

            if (document.exists()) {
                User user = documentToUser(document);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            getFirestore()
                    .collection(COLLECTION_NAME)
                    .document(id)
                    .delete()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByRoleAndIsActive(Role role, Boolean isActive) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("role", role.name())
                    .whereEqualTo("isActive", isActive)
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findByRoles(List<Role> roles) {
        try {
            List<String> roleNames = roles.stream()
                    .map(Role::name)
                    .collect(Collectors.toList());

            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereIn("role", roleNames)
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> findActiveUsersByUniversityAndRoles(String university, List<Role> roles) {
        try {
            List<String> roleNames = roles.stream()
                    .map(Role::name)
                    .collect(Collectors.toList());

            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("university", university)
                    .whereIn("role", roleNames)
                    .whereEqualTo("isActive", true)
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        try {
            // Firebase doesn't support complex text search, so we'll do basic filtering
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .get()
                    .get();

            return querySnapshot.getDocuments().stream()
                    .map(this::documentToUser)
                    .filter(user -> 
                        user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                        (user.getLastName() != null && user.getLastName().toLowerCase().contains(searchTerm.toLowerCase()))
                    )
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<User> findByStudentId(String studentId) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("studentId", studentId)
                    .get()
                    .get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                User user = documentToUser(document);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmployeeId(String employeeId) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("employeeId", employeeId)
                    .get()
                    .get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                User user = documentToUser(document);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByLicenseNumber(String licenseNumber) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("licenseNumber", licenseNumber)
                    .get()
                    .get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                User user = documentToUser(document);
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    @Override
    public Long countUsersByUniversityAndRole(String university, Role role) {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .whereEqualTo("university", university)
                    .whereEqualTo("role", role.name())
                    .get()
                    .get();

            return (long) querySnapshot.size();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return 0L;
        }
    }

    @Override
    public long count() {
        try {
            QuerySnapshot querySnapshot = getFirestore()
                    .collection(COLLECTION_NAME)
                    .get()
                    .get();

            return querySnapshot.size();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return 0L;
        }
    }

    private User documentToUser(DocumentSnapshot document) {
        User user = new User();
        user.setId(document.getId());
        user.setUsername(document.getString("username"));
        user.setEmail(document.getString("email"));
        user.setPassword(document.getString("password"));
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setPhoneNumber(document.getString("phoneNumber"));
        user.setUniversity(document.getString("university"));
        user.setStudentId(document.getString("studentId"));
        user.setEmployeeId(document.getString("employeeId"));
        user.setLicenseNumber(document.getString("licenseNumber"));
        
        String roleStr = document.getString("role");
        if (roleStr != null) {
            user.setRole(Role.valueOf(roleStr));
        }
        
        Boolean isActive = document.getBoolean("isActive");
        user.setIsActive(isActive != null ? isActive : false);
        
        com.google.cloud.Timestamp createdAt = document.getTimestamp("createdAt");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        
        com.google.cloud.Timestamp updatedAt = document.getTimestamp("updatedAt");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        
        return user;
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("password", user.getPassword());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("phoneNumber", user.getPhoneNumber());
        map.put("university", user.getUniversity());
        map.put("studentId", user.getStudentId());
        map.put("employeeId", user.getEmployeeId());
        map.put("licenseNumber", user.getLicenseNumber());
        map.put("role", user.getRole() != null ? user.getRole().name() : null);
        map.put("isActive", user.getIsActive());
        
        if (user.getCreatedAt() != null) {
            map.put("createdAt", com.google.cloud.Timestamp.of(java.sql.Timestamp.valueOf(user.getCreatedAt())));
        }
        
        if (user.getUpdatedAt() != null) {
            map.put("updatedAt", com.google.cloud.Timestamp.of(java.sql.Timestamp.valueOf(user.getUpdatedAt())));
        }
        
        return map;
    }
}
