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
    
    VehicleResponseDto updateVehicle(String id, VehicleDto vehicleDto);
    
    void deleteVehicle(String id);
    
    Optional<VehicleResponseDto> getVehicleById(String id);
    
    Optional<VehicleResponseDto> getVehicleByNumber(String vehicleNumber);
    
    List<VehicleResponseDto> getAllVehicles();
    
    // Filtering and Search
    List<VehicleResponseDto> getVehiclesByUniversity(String university);
    
    List<VehicleResponseDto> getVehiclesByType(VehicleType vehicleType);
    
    List<VehicleResponseDto> getVehiclesByStatus(VehicleStatus status);
    
    List<VehicleResponseDto> getVehiclesByUniversityAndType(String university, VehicleType vehicleType);
    
    List<VehicleResponseDto> searchVehicles(String searchTerm);
    
    // Driver Assignment
    VehicleResponseDto assignDriver(String vehicleId, String driverId);
    
    VehicleResponseDto unassignDriver(String vehicleId);
    
    Optional<VehicleResponseDto> getVehicleByDriverId(String driverId);
    
    // Location and Tracking
    List<VehicleResponseDto> getVehiclesWithLocation();
    
    List<VehicleResponseDto> getActiveVehiclesWithLocationByUniversity(String university);
    
    VehicleResponseDto updateVehicleLocation(String vehicleId, LocationUpdateDto locationUpdate);
    
    // Status Management
    VehicleResponseDto updateVehicleStatus(String vehicleId, VehicleStatus status);
    
    List<VehicleResponseDto> getActiveVehicles();
    
    // Statistics
    Long countVehiclesByUniversityAndStatus(String university, VehicleStatus status);
    
    // Utility
    boolean isVehicleNumberExists(String vehicleNumber);
} 