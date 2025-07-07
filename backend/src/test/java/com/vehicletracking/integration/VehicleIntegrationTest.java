package com.vehicletracking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicletracking.dto.LocationUpdateDto;
import com.vehicletracking.dto.VehicleDto;
import com.vehicletracking.model.*;
import com.vehicletracking.repository.UserRepository;
import com.vehicletracking.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class VehicleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testDriver;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        // Create test admin user
        testAdmin = new User();
        testAdmin.setUsername("testadmin");
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword(passwordEncoder.encode("admin123"));
        testAdmin.setFirstName("Test");
        testAdmin.setLastName("Admin");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setUniversity("Test University");
        testAdmin.setIsActive(true);
        testAdmin = userRepository.save(testAdmin);

        // Create test driver user
        testDriver = new User();
        testDriver.setUsername("testdriver");
        testDriver.setEmail("driver@test.com");
        testDriver.setPassword(passwordEncoder.encode("driver123"));
        testDriver.setFirstName("Test");
        testDriver.setLastName("Driver");
        testDriver.setRole(Role.DRIVER);
        testDriver.setUniversity("Test University");
        testDriver.setLicenseNumber("DL123456789");
        testDriver.setIsActive(true);
        testDriver = userRepository.save(testDriver);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVehicle_Success() throws Exception {
        // Given
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleNumber("STU-001");
        vehicleDto.setModel("Test Bus");
        vehicleDto.setBrand("TestBrand");
        vehicleDto.setCapacity(45);
        vehicleDto.setVehicleType(VehicleType.STUDENT_BUS);
        vehicleDto.setStatus(VehicleStatus.ACTIVE);
        vehicleDto.setUniversity("Test University");
        vehicleDto.setDriverId(testDriver.getId());

        // When & Then
        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleNumber", is("STU-001")))
                .andExpect(jsonPath("$.model", is("Test Bus")))
                .andExpect(jsonPath("$.brand", is("TestBrand")))
                .andExpect(jsonPath("$.capacity", is(45)))
                .andExpect(jsonPath("$.vehicleType", is("STUDENT_BUS")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.driverName", is("Test Driver")))
                .andExpect(jsonPath("$.isActive", is(true)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createVehicle_DuplicateVehicleNumber_Returns400() throws Exception {
        // Given - Create first vehicle
        Vehicle existingVehicle = new Vehicle();
        existingVehicle.setVehicleNumber("STU-001");
        existingVehicle.setModel("Existing Bus");
        existingVehicle.setBrand("ExistingBrand");
        existingVehicle.setCapacity(40);
        existingVehicle.setVehicleType(VehicleType.STUDENT_BUS);
        existingVehicle.setStatus(VehicleStatus.ACTIVE);
        existingVehicle.setUniversity("Test University");
        existingVehicle.setIsActive(true);
        vehicleRepository.save(existingVehicle);

        // Try to create vehicle with same number
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleNumber("STU-001");
        vehicleDto.setModel("Test Bus");
        vehicleDto.setBrand("TestBrand");
        vehicleDto.setCapacity(45);
        vehicleDto.setVehicleType(VehicleType.STUDENT_BUS);
        vehicleDto.setStatus(VehicleStatus.ACTIVE);
        vehicleDto.setUniversity("Test University");

        // When & Then
        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Vehicle number already exists")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createVehicle_InsufficientPermissions_Returns403() throws Exception {
        // Given
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleNumber("STU-001");
        vehicleDto.setModel("Test Bus");
        vehicleDto.setBrand("TestBrand");

        // When & Then
        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void updateVehicleLocation_Success() throws Exception {
        // Given - Create vehicle with driver
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("STU-001");
        vehicle.setModel("Test Bus");
        vehicle.setBrand("TestBrand");
        vehicle.setCapacity(45);
        vehicle.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle.setStatus(VehicleStatus.ACTIVE);
        vehicle.setUniversity("Test University");
        vehicle.setDriver(testDriver);
        vehicle.setIsActive(true);
        vehicle = vehicleRepository.save(vehicle);

        LocationUpdateDto locationUpdate = new LocationUpdateDto();
        locationUpdate.setLatitude(40.7589);
        locationUpdate.setLongitude(-73.9851);
        locationUpdate.setSpeed(25.0);
        locationUpdate.setDirection("North");
        locationUpdate.setFuelLevel(80.0);

        // When & Then
        mockMvc.perform(post("/api/tracking/location/{vehicleId}", vehicle.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentLatitude", is(40.7589)))
                .andExpect(jsonPath("$.currentLongitude", is(-73.9851)))
                .andExpect(jsonPath("$.currentSpeed", is(25.0)))
                .andExpect(jsonPath("$.direction", is("North")))
                .andExpect(jsonPath("$.fuelLevel", is(80.0)))
                .andExpect(jsonPath("$.lastLocationUpdate", is(notNullValue())));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void updateMyVehicleLocation_Success() throws Exception {
        // Given - Create vehicle with driver
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("STU-001");
        vehicle.setModel("Test Bus");
        vehicle.setBrand("TestBrand");
        vehicle.setCapacity(45);
        vehicle.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle.setStatus(VehicleStatus.ACTIVE);
        vehicle.setUniversity("Test University");
        vehicle.setDriver(testDriver);
        vehicle.setIsActive(true);
        vehicle = vehicleRepository.save(vehicle);

        LocationUpdateDto locationUpdate = new LocationUpdateDto();
        locationUpdate.setLatitude(40.7128);
        locationUpdate.setLongitude(-74.0060);
        locationUpdate.setSpeed(30.0);

        // When & Then
        mockMvc.perform(post("/api/tracking/my-vehicle/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentLatitude", is(40.7128)))
                .andExpect(jsonPath("$.currentLongitude", is(-74.0060)))
                .andExpect(jsonPath("$.currentSpeed", is(30.0)));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void updateMyVehicleLocation_NoVehicleAssigned_Returns400() throws Exception {
        // Given - Driver has no vehicle assigned
        LocationUpdateDto locationUpdate = new LocationUpdateDto();
        locationUpdate.setLatitude(40.7128);
        locationUpdate.setLongitude(-74.0060);

        // When & Then
        mockMvc.perform(post("/api/tracking/my-vehicle/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(locationUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("No vehicle assigned to this driver")));
    }

    @Test
    void getVehiclesWithLocation_Success() throws Exception {
        // Given - Create vehicles with location data
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleNumber("STU-001");
        vehicle1.setModel("Bus 1");
        vehicle1.setBrand("Brand1");
        vehicle1.setCapacity(45);
        vehicle1.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle1.setStatus(VehicleStatus.ACTIVE);
        vehicle1.setUniversity("Test University");
        vehicle1.setCurrentLatitude(40.7128);
        vehicle1.setCurrentLongitude(-74.0060);
        vehicle1.setIsActive(true);
        vehicleRepository.save(vehicle1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleNumber("TCH-001");
        vehicle2.setModel("Van 1");
        vehicle2.setBrand("Brand2");
        vehicle2.setCapacity(12);
        vehicle2.setVehicleType(VehicleType.TEACHER_BUS);
        vehicle2.setStatus(VehicleStatus.ACTIVE);
        vehicle2.setUniversity("Test University");
        vehicle2.setCurrentLatitude(40.7589);
        vehicle2.setCurrentLongitude(-73.9851);
        vehicle2.setIsActive(true);
        vehicleRepository.save(vehicle2);

        // When & Then
        mockMvc.perform(get("/api/tracking/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].vehicleNumber", is(oneOf("STU-001", "TCH-001"))))
                .andExpect(jsonPath("$[0].currentLatitude", is(notNullValue())))
                .andExpect(jsonPath("$[0].currentLongitude", is(notNullValue())))
                .andExpect(jsonPath("$[1].vehicleNumber", is(oneOf("STU-001", "TCH-001"))))
                .andExpect(jsonPath("$[1].currentLatitude", is(notNullValue())))
                .andExpect(jsonPath("$[1].currentLongitude", is(notNullValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignDriverToVehicle_Success() throws Exception {
        // Given - Create vehicle without driver
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("STU-001");
        vehicle.setModel("Test Bus");
        vehicle.setBrand("TestBrand");
        vehicle.setCapacity(45);
        vehicle.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle.setStatus(VehicleStatus.ACTIVE);
        vehicle.setUniversity("Test University");
        vehicle.setIsActive(true);
        vehicle = vehicleRepository.save(vehicle);

        // When & Then
        mockMvc.perform(post("/api/vehicles/{vehicleId}/assign-driver/{driverId}", 
                vehicle.getId(), testDriver.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverName", is("Test Driver")))
                .andExpect(jsonPath("$.driverId", is(testDriver.getId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignDriverToVehicle_DriverAlreadyAssigned_Returns400() throws Exception {
        // Given - Create vehicle with driver already assigned
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleNumber("STU-001");
        vehicle1.setModel("Bus 1");
        vehicle1.setBrand("Brand1");
        vehicle1.setCapacity(45);
        vehicle1.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle1.setStatus(VehicleStatus.ACTIVE);
        vehicle1.setUniversity("Test University");
        vehicle1.setDriver(testDriver);
        vehicle1.setIsActive(true);
        vehicleRepository.save(vehicle1);

        // Create another vehicle
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleNumber("STU-002");
        vehicle2.setModel("Bus 2");
        vehicle2.setBrand("Brand2");
        vehicle2.setCapacity(40);
        vehicle2.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle2.setStatus(VehicleStatus.ACTIVE);
        vehicle2.setUniversity("Test University");
        vehicle2.setIsActive(true);
        vehicle2 = vehicleRepository.save(vehicle2);

        // When & Then - Try to assign same driver to second vehicle
        mockMvc.perform(post("/api/vehicles/{vehicleId}/assign-driver/{driverId}", 
                vehicle2.getId(), testDriver.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Driver is already assigned to another vehicle")));
    }

    @Test
    void getVehiclesByUniversity_Success() throws Exception {
        // Given - Create vehicles for different universities
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleNumber("STU-001");
        vehicle1.setModel("Bus 1");
        vehicle1.setBrand("Brand1");
        vehicle1.setCapacity(45);
        vehicle1.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle1.setStatus(VehicleStatus.ACTIVE);
        vehicle1.setUniversity("Test University");
        vehicle1.setIsActive(true);
        vehicleRepository.save(vehicle1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleNumber("STU-002");
        vehicle2.setModel("Bus 2");
        vehicle2.setBrand("Brand2");
        vehicle2.setCapacity(40);
        vehicle2.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle2.setStatus(VehicleStatus.ACTIVE);
        vehicle2.setUniversity("Other University");
        vehicle2.setIsActive(true);
        vehicleRepository.save(vehicle2);

        // When & Then
        mockMvc.perform(get("/api/vehicles/university/{university}", "Test University"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vehicleNumber", is("STU-001")))
                .andExpect(jsonPath("$[0].university", is("Test University")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateVehicleStatus_Success() throws Exception {
        // Given
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("STU-001");
        vehicle.setModel("Test Bus");
        vehicle.setBrand("TestBrand");
        vehicle.setCapacity(45);
        vehicle.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle.setStatus(VehicleStatus.ACTIVE);
        vehicle.setUniversity("Test University");
        vehicle.setIsActive(true);
        vehicle = vehicleRepository.save(vehicle);

        // When & Then
        mockMvc.perform(put("/api/vehicles/{vehicleId}/status", vehicle.getId())
                .content("MAINTENANCE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("MAINTENANCE")));
    }

    @Test
    void searchVehicles_Success() throws Exception {
        // Given
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setVehicleNumber("STU-001");
        vehicle1.setModel("Transit Bus");
        vehicle1.setBrand("MegaBus");
        vehicle1.setCapacity(45);
        vehicle1.setVehicleType(VehicleType.STUDENT_BUS);
        vehicle1.setStatus(VehicleStatus.ACTIVE);
        vehicle1.setUniversity("Test University");
        vehicle1.setRouteName("Campus Route A");
        vehicle1.setIsActive(true);
        vehicleRepository.save(vehicle1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleNumber("TCH-001");
        vehicle2.setModel("Faculty Van");
        vehicle2.setBrand("ComfortRide");
        vehicle2.setCapacity(12);
        vehicle2.setVehicleType(VehicleType.TEACHER_BUS);
        vehicle2.setStatus(VehicleStatus.ACTIVE);
        vehicle2.setUniversity("Test University");
        vehicle2.setRouteName("Faculty Route");
        vehicle2.setIsActive(true);
        vehicleRepository.save(vehicle2);

        // When & Then - Search by model
        mockMvc.perform(get("/api/vehicles/search?q=Transit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vehicleNumber", is("STU-001")))
                .andExpect(jsonPath("$[0].model", is("Transit Bus")));

        // Search by vehicle number
        mockMvc.perform(get("/api/vehicles/search?q=TCH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].vehicleNumber", is("TCH-001")));
    }
} 