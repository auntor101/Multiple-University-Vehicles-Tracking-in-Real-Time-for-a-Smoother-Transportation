package com.vehicletracking.service;

import com.google.firebase.database.*;
import com.google.firebase.auth.FirebaseAuth;
import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.dto.VehicleDto;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import com.vehicletracking.model.Vehicle;
import com.vehicletracking.model.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseDataService.class);
    
    @Autowired
    private FirebaseDatabase firebaseDatabase;
    
    @Autowired
    private FirebaseAuth firebaseAuth;
    
    // Database References
    private DatabaseReference getUsersRef() {
        if (firebaseDatabase == null) {
            throw new RuntimeException("Firebase Database not initialized");
        }
        return firebaseDatabase.getReference("users");
    }
    
    private DatabaseReference getVehiclesRef() {
        if (firebaseDatabase == null) {
            throw new RuntimeException("Firebase Database not initialized");
        }
        return firebaseDatabase.getReference("vehicles");
    }
    
    private DatabaseReference getNotificationsRef() {
        if (firebaseDatabase == null) {
            throw new RuntimeException("Firebase Database not initialized");
        }
        return firebaseDatabase.getReference("notifications");
    }
    
    // User Operations
    public CompletableFuture<User> createUser(User user) {
        CompletableFuture<User> future = new CompletableFuture<>();
        
        try {
            String userId = getUsersRef().push().getKey();
            if (userId == null) {
                throw new RuntimeException("Failed to generate Firebase key");
            }
            user.setId(userId); // Use Firebase key directly as String
            
            Map<String, Object> userData = convertUserToMap(user);
            userData.put("createdAt", System.currentTimeMillis());
            userData.put("updatedAt", System.currentTimeMillis());
            
            getUsersRef().child(userId).setValueAsync(userData);
            // Firebase Realtime Database operations complete immediately
            logger.info("User created successfully: {}", user.getEmail());
            future.complete(user);
                
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    public CompletableFuture<Optional<User>> findUserByEmail(String email) {
        CompletableFuture<Optional<User>> future = new CompletableFuture<>();
        
        getUsersRef().orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            User user = convertMapToUser(child.getValue(), child.getKey());
                            future.complete(Optional.of(user));
                            return;
                        }
                    }
                    future.complete(Optional.empty());
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    logger.error("Failed to find user by email: {}", databaseError.getMessage());
                    future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
                }
            });
            
        return future;
    }
    
    public CompletableFuture<List<User>> findUsersByRole(Role role) {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        
        getUsersRef().orderByChild("role").equalTo(role.name())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<User> users = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User user = convertMapToUser(child.getValue(), child.getKey());
                        users.add(user);
                    }
                    future.complete(users);
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
                }
            });
            
        return future;
    }
    
    // Vehicle Operations
    public CompletableFuture<Vehicle> createVehicle(VehicleDto vehicleDto) {
        CompletableFuture<Vehicle> future = new CompletableFuture<>();
        
        try {
            String vehicleId = getVehiclesRef().push().getKey();
            if (vehicleId == null) {
                throw new RuntimeException("Failed to generate Firebase key");
            }
            Vehicle vehicle = convertDtoToVehicle(vehicleDto);
            vehicle.setId(vehicleId); // Use Firebase key directly as String
            
            Map<String, Object> vehicleData = convertVehicleToMap(vehicle);
            vehicleData.put("createdAt", System.currentTimeMillis());
            vehicleData.put("updatedAt", System.currentTimeMillis());
            
            getVehiclesRef().child(vehicleId).setValueAsync(vehicleData);
            future.complete(vehicle);
                
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    public CompletableFuture<List<Vehicle>> getAllActiveVehicles() {
        CompletableFuture<List<Vehicle>> future = new CompletableFuture<>();
        
        getVehiclesRef().orderByChild("isActive").equalTo(true)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Vehicle> vehicles = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Vehicle vehicle = convertMapToVehicle(child.getValue(), child.getKey());
                        vehicles.add(vehicle);
                    }
                    future.complete(vehicles);
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
                }
            });
            
        return future;
    }
    
    public CompletableFuture<List<Vehicle>> getVehiclesWithLocation() {
        CompletableFuture<List<Vehicle>> future = new CompletableFuture<>();
        
        getVehiclesRef().orderByChild("isActive").equalTo(true)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Vehicle> vehicles = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Vehicle vehicle = convertMapToVehicle(child.getValue(), child.getKey());
                        // Only include vehicles with location data
                        if (vehicle.getCurrentLatitude() != null && vehicle.getCurrentLongitude() != null) {
                            vehicles.add(vehicle);
                        }
                    }
                    future.complete(vehicles);
                }
                
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
                }
            });
            
        return future;
    }
    
    public CompletableFuture<Vehicle> updateVehicleLocation(String vehicleId, LocationUpdateDto locationUpdate) {
        CompletableFuture<Vehicle> future = new CompletableFuture<>();
        
        DatabaseReference vehicleRef = getVehiclesRef().child(vehicleId);
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("location/latitude", locationUpdate.getLatitude());
        updates.put("location/longitude", locationUpdate.getLongitude());
        updates.put("location/timestamp", System.currentTimeMillis());
        updates.put("updatedAt", System.currentTimeMillis());
        
        if (locationUpdate.getSpeed() != null) {
            updates.put("location/speed", locationUpdate.getSpeed());
        }
        if (locationUpdate.getDirection() != null) {
            updates.put("location/direction", locationUpdate.getDirection());
        }
        if (locationUpdate.getFuelLevel() != null) {
            updates.put("fuelLevel", locationUpdate.getFuelLevel());
        }
        
        vehicleRef.updateChildrenAsync(updates);
        
        // Fetch updated vehicle
        vehicleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vehicle vehicle = convertMapToVehicle(dataSnapshot.getValue(), vehicleId);
                future.complete(vehicle);
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
            }
        });
            
        return future;
    }
    
    // Real-time Listeners
    public void addVehicleLocationListener(String vehicleId, ValueEventListener listener) {
        getVehiclesRef().child(vehicleId).child("location").addValueEventListener(listener);
    }
    
    public void addAllVehiclesListener(ValueEventListener listener) {
        getVehiclesRef().addValueEventListener(listener);
    }
    
    // Utility Methods
    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        map.put("username", user.getUsername());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("phoneNumber", user.getPhoneNumber());
        map.put("role", user.getRole().name());
        map.put("university", user.getUniversity());
        map.put("department", user.getDepartment());
        map.put("isActive", user.getIsActive());
        map.put("isEmailVerified", user.getIsEmailVerified());
        return map;
    }
    
    private User convertMapToUser(Object data, String firebaseKey) {
        if (!(data instanceof Map)) return null;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        
        User user = new User();
        user.setId(firebaseKey); // Use Firebase key directly as String
        user.setEmail((String) map.get("email"));
        user.setUsername((String) map.get("username"));
        user.setFirstName((String) map.get("firstName"));
        user.setLastName((String) map.get("lastName"));
        user.setPhoneNumber((String) map.get("phoneNumber"));
        user.setRole(Role.valueOf((String) map.get("role")));
        user.setUniversity((String) map.get("university"));
        user.setDepartment((String) map.get("department"));
        user.setIsActive((Boolean) map.getOrDefault("isActive", true));
        user.setIsEmailVerified((Boolean) map.getOrDefault("isEmailVerified", false));
        
        return user;
    }
    
    private Map<String, Object> convertVehicleToMap(Vehicle vehicle) {
        Map<String, Object> map = new HashMap<>();
        map.put("vehicleNumber", vehicle.getVehicleNumber());
        map.put("model", vehicle.getModel());
        map.put("brand", vehicle.getBrand());
        map.put("capacity", vehicle.getCapacity());
        map.put("vehicleType", vehicle.getVehicleType().name());
        map.put("status", vehicle.getStatus().name());
        map.put("university", vehicle.getUniversity());
        map.put("routeName", vehicle.getRouteName());
        map.put("routeDescription", vehicle.getRouteDescription());
        map.put("isActive", vehicle.getIsActive());
        
        if (vehicle.getCurrentLatitude() != null) {
            Map<String, Object> location = new HashMap<>();
            location.put("latitude", vehicle.getCurrentLatitude());
            location.put("longitude", vehicle.getCurrentLongitude());
            location.put("speed", vehicle.getCurrentSpeed());
            location.put("direction", vehicle.getDirection());
            location.put("timestamp", System.currentTimeMillis());
            map.put("location", location);
        }
        
        return map;
    }
    
    private Vehicle convertMapToVehicle(Object data, String firebaseKey) {
        if (!(data instanceof Map)) return null;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) data;
        
        Vehicle vehicle = new Vehicle();
        vehicle.setId(firebaseKey); // Use Firebase key directly as String
        vehicle.setVehicleNumber((String) map.get("vehicleNumber"));
        vehicle.setModel((String) map.get("model"));
        vehicle.setBrand((String) map.get("brand"));
        vehicle.setCapacity(((Number) map.get("capacity")).intValue());
        vehicle.setVehicleType(com.vehicletracking.model.VehicleType.valueOf((String) map.get("vehicleType")));
        vehicle.setStatus(VehicleStatus.valueOf((String) map.get("status")));
        vehicle.setUniversity((String) map.get("university"));
        vehicle.setRouteName((String) map.get("routeName"));
        vehicle.setRouteDescription((String) map.get("routeDescription"));
        vehicle.setIsActive((Boolean) map.getOrDefault("isActive", true));
        
        // Handle location data
        @SuppressWarnings("unchecked")
        Map<String, Object> location = (Map<String, Object>) map.get("location");
        if (location != null) {
            vehicle.setCurrentLatitude(((Number) location.get("latitude")).doubleValue());
            vehicle.setCurrentLongitude(((Number) location.get("longitude")).doubleValue());
            if (location.get("speed") != null) {
                vehicle.setCurrentSpeed(((Number) location.get("speed")).doubleValue());
            }
            vehicle.setDirection((String) location.get("direction"));
        }
        
        return vehicle;
    }
    
    private Vehicle convertDtoToVehicle(VehicleDto dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(dto.getVehicleNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setBrand(dto.getBrand());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setStatus(dto.getStatus());
        vehicle.setUniversity(dto.getUniversity());
        vehicle.setRouteName(dto.getRouteName());
        vehicle.setRouteDescription(dto.getRouteDescription());
        vehicle.setIsActive(true);
        return vehicle;
    }
} 