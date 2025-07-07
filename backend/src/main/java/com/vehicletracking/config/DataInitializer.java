package com.vehicletracking.config;

import com.vehicletracking.model.*;
import com.vehicletracking.repository.UserRepository;
import com.vehicletracking.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void initializeData() {
        if (userRepository.count() == 0) {
            createUsers();
        }
        if (vehicleRepository.count() == 0) {
            createVehicles();
        }
    }
    
    private void createUsers() {
        logger.info("Initializing demo users...");
        
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@university.edu");
        admin.setPassword(passwordEncoder.encode("SecureAdmin@123"));
        admin.setFirstName("John");
        admin.setLastName("Anderson");
        admin.setRole(Role.ADMIN);
        admin.setUniversity("Metropolitan University");
        admin.setPhoneNumber("+1-555-0101");
        admin.setIsActive(true);
        admin.setIsEmailVerified(true);
        userRepository.save(admin);
        
        User driver1 = new User();
        driver1.setUsername("driver1");
        driver1.setEmail("driver1@university.edu");
        driver1.setPassword(passwordEncoder.encode("SecureDriver@123"));
        driver1.setFirstName("Michael");
        driver1.setLastName("Thompson");
        driver1.setRole(Role.DRIVER);
        driver1.setUniversity("Metropolitan University");
        driver1.setLicenseNumber("DL847392056");
        driver1.setPhoneNumber("+1-555-0201");
        driver1.setIsActive(true);
        driver1.setIsEmailVerified(true);
        userRepository.save(driver1);
        
        User driver2 = new User();
        driver2.setUsername("driver2");
        driver2.setEmail("driver2@university.edu");
        driver2.setPassword(passwordEncoder.encode("SecureDriver@124"));
        driver2.setFirstName("Sarah");
        driver2.setLastName("Wilson");
        driver2.setRole(Role.DRIVER);
        driver2.setUniversity("Metropolitan University");
        driver2.setLicenseNumber("DL593847261");
        driver2.setPhoneNumber("+1-555-0202");
        driver2.setIsActive(true);
        driver2.setIsEmailVerified(true);
        userRepository.save(driver2);
        
        User student = new User();
        student.setUsername("student1");
        student.setEmail("student1@university.edu");
        student.setPassword(passwordEncoder.encode("SecureStudent@123"));
        student.setFirstName("Alice");
        student.setLastName("Johnson");
        student.setRole(Role.STUDENT);
        student.setUniversity("Metropolitan University");
        student.setDepartment("Computer Science");
        student.setStudentId("CS2024001");
        student.setPhoneNumber("+1-555-0301");
        student.setIsActive(true);
        student.setIsEmailVerified(true);
        userRepository.save(student);
        
        User teacher = new User();
        teacher.setUsername("teacher1");
        teacher.setEmail("teacher1@university.edu");
        teacher.setPassword(passwordEncoder.encode("SecureTeacher@123"));
        teacher.setFirstName("Dr. Michael");
        teacher.setLastName("Brown");
        teacher.setRole(Role.TEACHER);
        teacher.setUniversity("Metropolitan University");
        teacher.setDepartment("Computer Science");
        teacher.setEmployeeId("FAC2019045");
        teacher.setPhoneNumber("+1-555-0401");
        teacher.setIsActive(true);
        teacher.setIsEmailVerified(true);
        userRepository.save(teacher);
        
        User officeAdmin = new User();
        officeAdmin.setUsername("office_admin");
        officeAdmin.setEmail("office_admin@university.edu");
        officeAdmin.setPassword(passwordEncoder.encode("SecureOffice@123"));
        officeAdmin.setFirstName("Emily");
        officeAdmin.setLastName("Davis");
        officeAdmin.setRole(Role.OFFICE_ADMIN);
        officeAdmin.setUniversity("Metropolitan University");
        officeAdmin.setDepartment("Administration");
        officeAdmin.setEmployeeId("ADM2020123");
        officeAdmin.setPhoneNumber("+1-555-0501");
        officeAdmin.setIsActive(true);
        officeAdmin.setIsEmailVerified(true);
        userRepository.save(officeAdmin);
        
        logger.info("Demo users created successfully");
    }
    
    private void createVehicles() {
        logger.info("Initializing demo vehicles...");
        
        List<User> drivers = userRepository.findByRole(Role.DRIVER);
        
        Vehicle studentBus1 = new Vehicle();
        studentBus1.setVehicleNumber("STU-001");
        studentBus1.setModel("Blue Bird Vision");
        studentBus1.setBrand("Blue Bird");
        studentBus1.setCapacity(48);
        studentBus1.setVehicleType(VehicleType.STUDENT_BUS);
        studentBus1.setStatus(VehicleStatus.ACTIVE);
        studentBus1.setUniversity("Metropolitan University");
        studentBus1.setRouteName("Campus Main Route");
        studentBus1.setRouteDescription("Main campus to dormitories and parking areas");
        if (!drivers.isEmpty()) {
            studentBus1.setDriver(drivers.get(0));
        }
        studentBus1.setCurrentLatitude(40.7829);
        studentBus1.setCurrentLongitude(-73.9654);
        studentBus1.setCurrentSpeed(15.0);
        studentBus1.setDirection("Northeast");
        studentBus1.setFuelLevel(78.0);
        studentBus1.setLastLocationUpdate(LocalDateTime.now().minusMinutes(2));
        studentBus1.setIsActive(true);
        vehicleRepository.save(studentBus1);
        
        Vehicle teacherVehicle = new Vehicle();
        teacherVehicle.setVehicleNumber("TCH-001");
        teacherVehicle.setModel("Ford Transit 350");
        teacherVehicle.setBrand("Ford");
        teacherVehicle.setCapacity(15);
        teacherVehicle.setVehicleType(VehicleType.TEACHER_BUS);
        teacherVehicle.setStatus(VehicleStatus.ACTIVE);
        teacherVehicle.setUniversity("Metropolitan University");
        teacherVehicle.setRouteName("Faculty Express");
        teacherVehicle.setRouteDescription("Faculty housing to academic buildings");
        if (drivers.size() > 1) {
            teacherVehicle.setDriver(drivers.get(1));
        }
        teacherVehicle.setCurrentLatitude(40.7580);
        teacherVehicle.setCurrentLongitude(-73.9855);
        teacherVehicle.setCurrentSpeed(25.0);
        teacherVehicle.setDirection("South");
        teacherVehicle.setFuelLevel(65.0);
        teacherVehicle.setLastLocationUpdate(LocalDateTime.now().minusMinutes(5));
        teacherVehicle.setIsActive(true);
        vehicleRepository.save(teacherVehicle);
        
        Vehicle officeVehicle = new Vehicle();
        officeVehicle.setVehicleNumber("OFC-001");
        officeVehicle.setModel("Toyota Hiace");
        officeVehicle.setBrand("Toyota");
        officeVehicle.setCapacity(12);
        officeVehicle.setVehicleType(VehicleType.OFFICE_ADMIN_VEHICLE);
        officeVehicle.setStatus(VehicleStatus.ACTIVE);
        officeVehicle.setUniversity("Metropolitan University");
        officeVehicle.setRouteName("Administrative Route");
        officeVehicle.setRouteDescription("Inter-campus administrative travel");
        officeVehicle.setCurrentLatitude(40.7061);
        officeVehicle.setCurrentLongitude(-73.9969);
        officeVehicle.setCurrentSpeed(0.0);
        officeVehicle.setDirection("Parked");
        officeVehicle.setFuelLevel(92.0);
        officeVehicle.setLastLocationUpdate(LocalDateTime.now().minusMinutes(15));
        officeVehicle.setIsActive(true);
        vehicleRepository.save(officeVehicle);
        
        Vehicle studentBus2 = new Vehicle();
        studentBus2.setVehicleNumber("STU-002");
        studentBus2.setModel("Thomas Built Saf-T-Liner");
        studentBus2.setBrand("Thomas Built");
        studentBus2.setCapacity(54);
        studentBus2.setVehicleType(VehicleType.STUDENT_BUS);
        studentBus2.setStatus(VehicleStatus.ACTIVE);
        studentBus2.setUniversity("Metropolitan University");
        studentBus2.setRouteName("Campus Express");
        studentBus2.setRouteDescription("Express route during peak hours");
        studentBus2.setCurrentLatitude(40.7484);
        studentBus2.setCurrentLongitude(-73.9857);
        studentBus2.setCurrentSpeed(20.0);
        studentBus2.setDirection("West");
        studentBus2.setFuelLevel(45.0);
        studentBus2.setLastLocationUpdate(LocalDateTime.now().minusMinutes(1));
        studentBus2.setIsActive(true);
        vehicleRepository.save(studentBus2);
        
        Vehicle generalVehicle = new Vehicle();
        generalVehicle.setVehicleNumber("GEN-001");
        generalVehicle.setModel("Mercedes Sprinter");
        generalVehicle.setBrand("Mercedes-Benz");
        generalVehicle.setCapacity(20);
        generalVehicle.setVehicleType(VehicleType.GENERAL_TRANSPORT);
        generalVehicle.setStatus(VehicleStatus.MAINTENANCE);
        generalVehicle.setUniversity("Metropolitan University");
        generalVehicle.setRouteName("General Purpose");
        generalVehicle.setRouteDescription("Multi-purpose university transport");
        generalVehicle.setCurrentLatitude(40.7074);
        generalVehicle.setCurrentLongitude(-74.0113);
        generalVehicle.setCurrentSpeed(0.0);
        generalVehicle.setDirection("Maintenance Bay");
        generalVehicle.setFuelLevel(30.0);
        generalVehicle.setLastLocationUpdate(LocalDateTime.now().minusHours(2));
        generalVehicle.setIsActive(true);
        vehicleRepository.save(generalVehicle);
        
        logger.info("Demo vehicles created successfully");
    }
} 