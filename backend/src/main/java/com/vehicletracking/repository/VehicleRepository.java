package com.vehicletracking.repository;

import com.vehicletracking.model.Vehicle;
import com.vehicletracking.model.VehicleStatus;
import com.vehicletracking.model.VehicleType;
import com.vehicletracking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
    
    Boolean existsByVehicleNumber(String vehicleNumber);
    
    List<Vehicle> findByVehicleType(VehicleType vehicleType);
    
    List<Vehicle> findByStatus(VehicleStatus status);
    
    List<Vehicle> findByUniversity(String university);
    
    List<Vehicle> findByUniversityAndVehicleType(String university, VehicleType vehicleType);
    
    List<Vehicle> findByUniversityAndStatus(String university, VehicleStatus status);
    
    List<Vehicle> findByDriver(User driver);
    
    Optional<Vehicle> findByDriverId(Long driverId);
    
    List<Vehicle> findByIsActive(Boolean isActive);
    
    @Query("SELECT v FROM Vehicle v WHERE v.university = :university AND v.status = :status AND v.isActive = true")
    List<Vehicle> findActiveVehiclesByUniversityAndStatus(@Param("university") String university, @Param("status") VehicleStatus status);
    
    @Query("SELECT v FROM Vehicle v WHERE v.currentLatitude IS NOT NULL AND v.currentLongitude IS NOT NULL AND v.isActive = true")
    List<Vehicle> findVehiclesWithLocation();
    
    @Query("SELECT v FROM Vehicle v WHERE v.university = :university AND v.currentLatitude IS NOT NULL AND v.currentLongitude IS NOT NULL AND v.isActive = true")
    List<Vehicle> findActiveVehiclesWithLocationByUniversity(@Param("university") String university);
    
    @Query("SELECT v FROM Vehicle v WHERE v.lastLocationUpdate >= :since AND v.isActive = true")
    List<Vehicle> findVehiclesUpdatedSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT v FROM Vehicle v WHERE v.vehicleType IN :types AND v.university = :university AND v.isActive = true")
    List<Vehicle> findByVehicleTypesAndUniversity(@Param("types") List<VehicleType> types, @Param("university") String university);
    
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.university = :university AND v.status = :status")
    Long countVehiclesByUniversityAndStatus(@Param("university") String university, @Param("status") VehicleStatus status);
    
    @Query("SELECT v FROM Vehicle v WHERE v.routeName LIKE %:searchTerm% OR v.vehicleNumber LIKE %:searchTerm% OR v.model LIKE %:searchTerm%")
    List<Vehicle> searchVehicles(@Param("searchTerm") String searchTerm);
} 