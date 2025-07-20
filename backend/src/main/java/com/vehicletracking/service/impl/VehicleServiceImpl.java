package com.vehicletracking.service.impl;

import com.vehicletracking.dto.VehicleDto;
import com.vehicletracking.dto.VehicleResponseDto;
import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.exception.VehicleNotFoundException;
import com.vehicletracking.exception.UserNotFoundException;
import com.vehicletracking.exception.DriverAssignmentException;
import com.vehicletracking.exception.DuplicateVehicleException;
import com.vehicletracking.model.Vehicle;
import com.vehicletracking.model.User;
import com.vehicletracking.model.VehicleType;
import com.vehicletracking.model.VehicleStatus;
import com.vehicletracking.model.Role;
import com.vehicletracking.repository.VehicleRepository;
import com.vehicletracking.repository.UserRepository;
import com.vehicletracking.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public VehicleResponseDto createVehicle(VehicleDto vehicleDto) {
        if (vehicleRepository.existsByVehicleNumber(vehicleDto.getVehicleNumber())) {
            throw DuplicateVehicleException.withNumber(vehicleDto.getVehicleNumber());
        }
        
        Vehicle vehicle = new Vehicle();
        mapDtoToEntity(vehicleDto, vehicle);
        
        // Assign driver if provided
        if (vehicleDto.getDriverId() != null) {
            User driver = userRepository.findById(vehicleDto.getDriverId())
                .orElseThrow(() -> new UserNotFoundException(vehicleDto.getDriverId()));
            
            if (driver.getRole() != Role.DRIVER) {
                throw new RuntimeException("User is not a driver: " + vehicleDto.getDriverId());
            }
            
            // Check if driver is already assigned to another vehicle
            if (!vehicleRepository.findByDriverId(vehicleDto.getDriverId()).isEmpty()) {
                throw new RuntimeException("Driver is already assigned to another vehicle: " + vehicleDto.getDriverId());
            }
            
            vehicle.setDriver(driver);
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapEntityToResponseDto(savedVehicle);
    }
    
    @Override
    public VehicleResponseDto updateVehicle(String id, VehicleDto vehicleDto) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new VehicleNotFoundException(id));
        
        // Check if vehicle number is being changed and if it already exists
        if (!vehicle.getVehicleNumber().equals(vehicleDto.getVehicleNumber()) &&
            vehicleRepository.existsByVehicleNumber(vehicleDto.getVehicleNumber())) {
            throw DuplicateVehicleException.withNumber(vehicleDto.getVehicleNumber());
        }
        
        mapDtoToEntity(vehicleDto, vehicle);
        
        // Handle driver assignment
        if (vehicleDto.getDriverId() != null) {
            if (vehicle.getDriver() == null || !vehicle.getDriver().getId().equals(vehicleDto.getDriverId())) {
                User driver = userRepository.findById(vehicleDto.getDriverId())
                    .orElseThrow(() -> new UserNotFoundException(vehicleDto.getDriverId()));
                
                if (driver.getRole() != Role.DRIVER) {
                    throw new RuntimeException("User is not a driver: " + vehicleDto.getDriverId());
                }
                
                // Check if driver is already assigned to another vehicle
                List<Vehicle> existingAssignments = vehicleRepository.findByDriverId(vehicleDto.getDriverId());
                if (!existingAssignments.isEmpty() && !existingAssignments.get(0).getId().equals(id)) {
                    throw new RuntimeException("Driver is already assigned to another vehicle: " + vehicleDto.getDriverId());
                }
                
                vehicle.setDriver(driver);
            }
        } else {
            vehicle.setDriver(null);
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapEntityToResponseDto(savedVehicle);
    }
    
    @Override
    public void deleteVehicle(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new VehicleNotFoundException(id));
        
        vehicle.setIsActive(false);
        vehicleRepository.save(vehicle);
    }
    
    @Override
    public Optional<VehicleResponseDto> getVehicleById(String id) {
        return vehicleRepository.findById(id)
            .filter(Vehicle::getIsActive)
            .map(this::mapEntityToResponseDto);
    }
    
    @Override
    public Optional<VehicleResponseDto> getVehicleByNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber)
            .filter(Vehicle::getIsActive)
            .map(this::mapEntityToResponseDto);
    }
    
    @Override
    public List<VehicleResponseDto> getAllVehicles() {
        return vehicleRepository.findByIsActive(true)
            .stream()
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VehicleResponseDto> getVehiclesByUniversity(String university) {
        return vehicleRepository.findByUniversity(university)
            .stream()
            .filter(Vehicle::getIsActive)
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VehicleResponseDto> getVehiclesByType(VehicleType vehicleType) {
        return vehicleRepository.findByVehicleType(vehicleType)
            .stream()
            .filter(Vehicle::getIsActive)
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VehicleResponseDto> getVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status)
            .stream()
            .filter(Vehicle::getIsActive)
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VehicleResponseDto> getVehiclesByUniversityAndType(String university, VehicleType vehicleType) {
        return vehicleRepository.findByUniversityAndVehicleType(university, vehicleType)
            .stream()
            .filter(Vehicle::getIsActive)
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VehicleResponseDto> searchVehicles(String searchTerm) {
        return vehicleRepository.searchVehicles(searchTerm)
            .stream()
            .filter(Vehicle::getIsActive)
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public VehicleResponseDto assignDriver(String vehicleId, String driverId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        
        User driver = userRepository.findById(driverId)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        
        if (driver.getRole() != Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }
        
        // Check if driver is already assigned to another vehicle
        if (!vehicleRepository.findByDriverId(driverId).isEmpty()) {
            throw new RuntimeException("Driver is already assigned to another vehicle");
        }
        
        vehicle.setDriver(driver);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapEntityToResponseDto(savedVehicle);
    }
    
    @Override
    public VehicleResponseDto unassignDriver(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        
        vehicle.setDriver(null);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapEntityToResponseDto(savedVehicle);
    }
    
    @Override
    public Optional<VehicleResponseDto> getVehicleByDriverId(String driverId) {
        return vehicleRepository.findByDriverId(driverId)
            .stream()
            .filter(Vehicle::getIsActive)
            .findFirst()
            .map(this::mapEntityToResponseDto);
    }
    
    @Override
    public List<VehicleResponseDto> getVehiclesWithLocation() {
        return vehicleRepository.findVehiclesWithLocation()
            .stream()
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VehicleResponseDto> getActiveVehiclesWithLocationByUniversity(String university) {
        return vehicleRepository.findActiveVehiclesWithLocationByUniversity(university)
            .stream()
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public VehicleResponseDto updateVehicleLocation(String vehicleId, LocationUpdateDto locationUpdate) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        
        vehicle.setCurrentLatitude(locationUpdate.getLatitude());
        vehicle.setCurrentLongitude(locationUpdate.getLongitude());
        vehicle.setLastLocationUpdate(LocalDateTime.now());
        
        if (locationUpdate.getSpeed() != null) {
            vehicle.setCurrentSpeed(locationUpdate.getSpeed());
        }
        
        if (locationUpdate.getDirection() != null) {
            vehicle.setDirection(locationUpdate.getDirection());
        }
        
        if (locationUpdate.getFuelLevel() != null) {
            vehicle.setFuelLevel(locationUpdate.getFuelLevel());
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapEntityToResponseDto(savedVehicle);
    }
    
    @Override
    public VehicleResponseDto updateVehicleStatus(String vehicleId, VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        
        vehicle.setStatus(status);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapEntityToResponseDto(savedVehicle);
    }
    
    @Override
    public List<VehicleResponseDto> getActiveVehicles() {
        return vehicleRepository.findByIsActive(true)
            .stream()
            .filter(vehicle -> vehicle.getStatus() == VehicleStatus.ACTIVE)
            .map(this::mapEntityToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public Long countVehiclesByUniversityAndStatus(String university, VehicleStatus status) {
        return vehicleRepository.countVehiclesByUniversityAndStatus(university, status);
    }
    
    @Override
    public boolean isVehicleNumberExists(String vehicleNumber) {
        return vehicleRepository.existsByVehicleNumber(vehicleNumber);
    }
    
    // Helper methods for mapping
    private void mapDtoToEntity(VehicleDto dto, Vehicle entity) {
        entity.setVehicleNumber(dto.getVehicleNumber());
        entity.setModel(dto.getModel());
        entity.setBrand(dto.getBrand());
        entity.setCapacity(dto.getCapacity());
        entity.setVehicleType(dto.getVehicleType());
        entity.setStatus(dto.getStatus());
        entity.setUniversity(dto.getUniversity());
        entity.setRouteName(dto.getRouteName());
        entity.setRouteDescription(dto.getRouteDescription());
        entity.setFuelLevel(dto.getFuelLevel());
        entity.setLastMaintenanceDate(dto.getLastMaintenance());
        entity.setNextMaintenanceDate(dto.getNextMaintenance());
    }
    
    private VehicleResponseDto mapEntityToResponseDto(Vehicle entity) {
        VehicleResponseDto dto = new VehicleResponseDto();
        dto.setId(entity.getId());
        dto.setVehicleNumber(entity.getVehicleNumber());
        dto.setModel(entity.getModel());
        dto.setBrand(entity.getBrand());
        dto.setCapacity(entity.getCapacity());
        dto.setVehicleType(entity.getVehicleType());
        dto.setStatus(entity.getStatus());
        dto.setUniversity(entity.getUniversity());
        
        // Driver information
        if (entity.getDriver() != null) {
            dto.setDriverId(entity.getDriver().getId());
            dto.setDriverName(entity.getDriver().getFullName());
            dto.setDriverPhone(entity.getDriver().getPhoneNumber());
        }
        
        // Location information
        dto.setCurrentLatitude(entity.getCurrentLatitude());
        dto.setCurrentLongitude(entity.getCurrentLongitude());
        dto.setLastLocationUpdate(entity.getLastLocationUpdate());
        
        // Route information
        dto.setRouteName(entity.getRouteName());
        dto.setRouteDescription(entity.getRouteDescription());
        
        // Speed and tracking info
        dto.setCurrentSpeed(entity.getCurrentSpeed());
        dto.setDirection(entity.getDirection());
        
        // Vehicle details
        dto.setFuelLevel(entity.getFuelLevel());
        dto.setLastMaintenance(entity.getLastMaintenanceDate());
        dto.setNextMaintenance(entity.getNextMaintenanceDate());
        
        // Status
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }
} 