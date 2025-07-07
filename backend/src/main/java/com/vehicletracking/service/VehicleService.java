package com.vehicletracking.service;

import com.vehicletracking.dto.VehicleDto;
import com.vehicletracking.dto.VehicleResponseDto;
import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.model.VehicleType;
import com.vehicletracking.model.VehicleStatus;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    
    // CRUD Operations
    VehicleResponseDto createVehicle(VehicleDto vehicleDto);
    
    VehicleResponseDto updateVehicle(Long id, VehicleDto vehicleDto);
    
    void deleteVehicle(Long id);
    
    Optional<VehicleResponseDto> getVehicleById(Long id);
    
    Optional<VehicleResponseDto> getVehicleByNumber(String vehicleNumber);
    
    List<VehicleResponseDto> getAllVehicles();
    
    // Filtering and Search
    List<VehicleResponseDto> getVehiclesByUniversity(String university);
    
    List<VehicleResponseDto> getVehiclesByType(VehicleType vehicleType);
    
    List<VehicleResponseDto> getVehiclesByStatus(VehicleStatus status);
    
    List<VehicleResponseDto> getVehiclesByUniversityAndType(String university, VehicleType vehicleType);
    
    List<VehicleResponseDto> searchVehicles(String searchTerm);
    
    // Driver Assignment
    VehicleResponseDto assignDriver(Long vehicleId, Long driverId);
    
    VehicleResponseDto unassignDriver(Long vehicleId);
    
    Optional<VehicleResponseDto> getVehicleByDriverId(Long driverId);
    
    // Location and Tracking
    List<VehicleResponseDto> getVehiclesWithLocation();
    
    List<VehicleResponseDto> getActiveVehiclesWithLocationByUniversity(String university);
    
    VehicleResponseDto updateVehicleLocation(Long vehicleId, LocationUpdateDto locationUpdate);
    
    // Status Management
    VehicleResponseDto updateVehicleStatus(Long vehicleId, VehicleStatus status);
    
    List<VehicleResponseDto> getActiveVehicles();
    
    // Statistics
    Long countVehiclesByUniversityAndStatus(String university, VehicleStatus status);
    
    // Utility
    boolean isVehicleNumberExists(String vehicleNumber);
} 