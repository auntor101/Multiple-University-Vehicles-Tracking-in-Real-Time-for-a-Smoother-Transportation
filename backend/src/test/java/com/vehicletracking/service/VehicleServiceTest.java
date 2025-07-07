package com.vehicletracking.service;

import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.dto.VehicleDto;
import com.vehicletracking.dto.VehicleResponseDto;
import com.vehicletracking.exception.BusinessLogicException;
import com.vehicletracking.exception.VehicleNotFoundException;
import com.vehicletracking.model.*;
import com.vehicletracking.repository.UserRepository;
import com.vehicletracking.repository.VehicleRepository;
import com.vehicletracking.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private VehicleServiceImpl vehicleService;
    
    private Vehicle testVehicle;
    private User testDriver;
    private VehicleDto testVehicleDto;
    
    @BeforeEach
    void setUp() {
        // Setup test driver
        testDriver = new User();
        testDriver.setId(1L);
        testDriver.setUsername("testdriver");
        testDriver.setEmail("driver@test.com");
        testDriver.setFirstName("John");
        testDriver.setLastName("Driver");
        testDriver.setRole(Role.DRIVER);
        testDriver.setLicenseNumber("DL123456789");
        testDriver.setPhoneNumber("+1234567890");
        testDriver.setIsActive(true);
        
        // Setup test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setVehicleNumber("STU-001");
        testVehicle.setModel("Transit Bus 3000");
        testVehicle.setBrand("MegaBus");
        testVehicle.setCapacity(45);
        testVehicle.setVehicleType(VehicleType.STUDENT_BUS);
        testVehicle.setStatus(VehicleStatus.ACTIVE);
        testVehicle.setUniversity("Test University");
        testVehicle.setDriver(testDriver);
        testVehicle.setIsActive(true);
        testVehicle.setCurrentLatitude(40.7128);
        testVehicle.setCurrentLongitude(-74.0060);
        testVehicle.setCurrentSpeed(25.0);
        testVehicle.setFuelLevel(85.0);
        testVehicle.setLastLocationUpdate(LocalDateTime.now());
        
        // Setup test DTO
        testVehicleDto = new VehicleDto();
        testVehicleDto.setVehicleNumber("STU-002");
        testVehicleDto.setModel("City Bus 2000");
        testVehicleDto.setBrand("UrbanTrans");
        testVehicleDto.setCapacity(40);
        testVehicleDto.setVehicleType(VehicleType.STUDENT_BUS);
        testVehicleDto.setStatus(VehicleStatus.ACTIVE);
        testVehicleDto.setUniversity("Test University");
        testVehicleDto.setDriverId(1L);
    }
    
    @Test
    void createVehicle_Success() {
        // Given
        when(vehicleRepository.existsByVehicleNumber(testVehicleDto.getVehicleNumber())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(vehicleRepository.findByDriverId(1L)).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        
        // When
        VehicleResponseDto result = vehicleService.createVehicle(testVehicleDto);
        
        // Then
        assertNotNull(result);
        assertEquals(testVehicle.getVehicleNumber(), result.getVehicleNumber());
        assertEquals(testVehicle.getModel(), result.getModel());
        assertEquals(testVehicle.getBrand(), result.getBrand());
        assertEquals(testDriver.getFullName(), result.getDriverName());
        
        verify(vehicleRepository).existsByVehicleNumber(testVehicleDto.getVehicleNumber());
        verify(userRepository).findById(1L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }
    
    @Test
    void createVehicle_DuplicateVehicleNumber_ThrowsException() {
        // Given
        when(vehicleRepository.existsByVehicleNumber(testVehicleDto.getVehicleNumber())).thenReturn(true);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> vehicleService.createVehicle(testVehicleDto));
        
        assertEquals("Vehicle number already exists: " + testVehicleDto.getVehicleNumber(), 
            exception.getMessage());
        
        verify(vehicleRepository).existsByVehicleNumber(testVehicleDto.getVehicleNumber());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
    
    @Test
    void createVehicle_DriverNotFound_ThrowsException() {
        // Given
        when(vehicleRepository.existsByVehicleNumber(testVehicleDto.getVehicleNumber())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> vehicleService.createVehicle(testVehicleDto));
        
        assertEquals("Driver not found with id: 1", exception.getMessage());
        
        verify(vehicleRepository).existsByVehicleNumber(testVehicleDto.getVehicleNumber());
        verify(userRepository).findById(1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
    
    @Test
    void createVehicle_UserNotDriver_ThrowsException() {
        // Given
        testDriver.setRole(Role.STUDENT); // Change role to non-driver
        when(vehicleRepository.existsByVehicleNumber(testVehicleDto.getVehicleNumber())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> vehicleService.createVehicle(testVehicleDto));
        
        assertEquals("User is not a driver", exception.getMessage());
    }
    
    @Test
    void createVehicle_DriverAlreadyAssigned_ThrowsException() {
        // Given
        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setId(999L);
        existingVehicle.setDriver(testDriver);
        
        when(vehicleRepository.existsByVehicleNumber(testVehicleDto.getVehicleNumber())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(vehicleRepository.findByDriverId(1L)).thenReturn(Optional.of(existingVehicle));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> vehicleService.createVehicle(testVehicleDto));
        
        assertEquals("Driver is already assigned to another vehicle", exception.getMessage());
    }
    
    @Test
    void updateVehicleLocation_Success() {
        // Given
        LocationUpdateDto locationUpdate = new LocationUpdateDto();
        locationUpdate.setLatitude(40.7589);
        locationUpdate.setLongitude(-73.9851);
        locationUpdate.setSpeed(30.0);
        locationUpdate.setDirection("North");
        locationUpdate.setFuelLevel(80.0);
        
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        
        // When
        VehicleResponseDto result = vehicleService.updateVehicleLocation(1L, locationUpdate);
        
        // Then
        assertNotNull(result);
        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository).save(testVehicle);
        
        // Verify that location was updated
        assertEquals(locationUpdate.getLatitude(), testVehicle.getCurrentLatitude());
        assertEquals(locationUpdate.getLongitude(), testVehicle.getCurrentLongitude());
        assertEquals(locationUpdate.getSpeed(), testVehicle.getCurrentSpeed());
        assertEquals(locationUpdate.getDirection(), testVehicle.getDirection());
        assertEquals(locationUpdate.getFuelLevel(), testVehicle.getFuelLevel());
        assertNotNull(testVehicle.getLastLocationUpdate());
    }
    
    @Test
    void updateVehicleLocation_VehicleNotFound_ThrowsException() {
        // Given
        LocationUpdateDto locationUpdate = new LocationUpdateDto();
        locationUpdate.setLatitude(40.7589);
        locationUpdate.setLongitude(-73.9851);
        
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> vehicleService.updateVehicleLocation(1L, locationUpdate));
        
        assertEquals("Vehicle not found with id: 1", exception.getMessage());
        verify(vehicleRepository).findById(1L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
    
    @Test
    void assignDriver_Success() {
        // Given
        testVehicle.setDriver(null); // Remove existing driver
        
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testDriver));
        when(vehicleRepository.findByDriverId(2L)).thenReturn(Optional.empty());
        when(vehicleRepository.save(testVehicle)).thenReturn(testVehicle);
        
        // When
        VehicleResponseDto result = vehicleService.assignDriver(1L, 2L);
        
        // Then
        assertNotNull(result);
        assertEquals(testDriver, testVehicle.getDriver());
        verify(vehicleRepository).save(testVehicle);
    }
    
    @Test
    void assignDriver_DriverAlreadyAssigned_ThrowsException() {
        // Given
        Vehicle anotherVehicle = new Vehicle();
        anotherVehicle.setId(2L);
        
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testDriver));
        when(vehicleRepository.findByDriverId(2L)).thenReturn(Optional.of(anotherVehicle));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> vehicleService.assignDriver(1L, 2L));
        
        assertEquals("Driver is already assigned to another vehicle", exception.getMessage());
    }
    
    @Test
    void getVehiclesWithLocation_Success() {
        // Given
        List<Vehicle> vehiclesWithLocation = Arrays.asList(testVehicle);
        when(vehicleRepository.findVehiclesWithLocation()).thenReturn(vehiclesWithLocation);
        
        // When
        List<VehicleResponseDto> result = vehicleService.getVehiclesWithLocation();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVehicle.getVehicleNumber(), result.get(0).getVehicleNumber());
        
        verify(vehicleRepository).findVehiclesWithLocation();
    }
    
    @Test
    void updateVehicleStatus_Success() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(testVehicle)).thenReturn(testVehicle);
        
        // When
        VehicleResponseDto result = vehicleService.updateVehicleStatus(1L, VehicleStatus.MAINTENANCE);
        
        // Then
        assertNotNull(result);
        assertEquals(VehicleStatus.MAINTENANCE, testVehicle.getStatus());
        verify(vehicleRepository).save(testVehicle);
    }
    
    @Test
    void deleteVehicle_Success() {
        // Given
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(testVehicle)).thenReturn(testVehicle);
        
        // When
        vehicleService.deleteVehicle(1L);
        
        // Then
        assertFalse(testVehicle.getIsActive());
        verify(vehicleRepository).save(testVehicle);
    }
    
    @Test
    void getVehiclesByUniversityAndType_Success() {
        // Given
        List<Vehicle> vehicles = Arrays.asList(testVehicle);
        when(vehicleRepository.findByUniversityAndVehicleType("Test University", VehicleType.STUDENT_BUS))
            .thenReturn(vehicles);
        
        // When
        List<VehicleResponseDto> result = vehicleService.getVehiclesByUniversityAndType(
            "Test University", VehicleType.STUDENT_BUS);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVehicle.getVehicleNumber(), result.get(0).getVehicleNumber());
    }
    
    @Test
    void isVehicleNumberExists_ReturnsTrue() {
        // Given
        when(vehicleRepository.existsByVehicleNumber("STU-001")).thenReturn(true);
        
        // When
        boolean result = vehicleService.isVehicleNumberExists("STU-001");
        
        // Then
        assertTrue(result);
        verify(vehicleRepository).existsByVehicleNumber("STU-001");
    }
    
    @Test
    void isVehicleNumberExists_ReturnsFalse() {
        // Given
        when(vehicleRepository.existsByVehicleNumber("STU-999")).thenReturn(false);
        
        // When
        boolean result = vehicleService.isVehicleNumberExists("STU-999");
        
        // Then
        assertFalse(result);
        verify(vehicleRepository).existsByVehicleNumber("STU-999");
    }
} 