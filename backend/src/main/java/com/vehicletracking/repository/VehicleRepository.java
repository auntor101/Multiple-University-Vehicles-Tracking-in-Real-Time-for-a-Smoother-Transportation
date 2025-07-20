package com.vehicletracking.repository;

import com.vehicletracking.model.Vehicle;
import com.vehicletracking.model.VehicleStatus;
import com.vehicletracking.model.VehicleType;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository {
    
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
    
    Boolean existsByVehicleNumber(String vehicleNumber);
    
    List<Vehicle> findByVehicleType(VehicleType vehicleType);
    
    List<Vehicle> findByStatus(VehicleStatus status);
    
    List<Vehicle> findByUniversity(String university);
    
    List<Vehicle> findByUniversityAndVehicleType(String university, VehicleType vehicleType);
    
    List<Vehicle> findByUniversityAndStatus(String university, VehicleStatus status);
    
    List<Vehicle> findByDriverId(String driverId);
    
    List<Vehicle> findByIsActive(Boolean isActive);
    
    List<Vehicle> findActiveVehiclesByUniversityAndStatus(String university, VehicleStatus status);
    
    List<Vehicle> findVehiclesWithLocation();
    
    List<Vehicle> findActiveVehiclesWithLocationByUniversity(String university);
    
    List<Vehicle> findVehiclesUpdatedSince(LocalDateTime since);
    
    List<Vehicle> findByVehicleTypesAndUniversity(List<VehicleType> types, String university);
    
    Long countVehiclesByUniversityAndStatus(String university, VehicleStatus status);
    
    List<Vehicle> searchVehicles(String searchTerm);
    
    Vehicle save(Vehicle vehicle);
    
    Optional<Vehicle> findById(String id);
    
    void deleteById(String id);
    
    List<Vehicle> findAll();
    
    long count();
} 