package com.vehicletracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicletracking.dto.UserLoginDto;
import com.vehicletracking.dto.UserRegistrationDto;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import com.vehicletracking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(Role.STUDENT);
        testUser.setUniversity("Test University");
        testUser.setIsActive(true);
        testUser.setIsEmailVerified(true);
        testUser = userRepository.save(testUser);
    }

    @Test
    void signin_ValidCredentials_ReturnsJwtToken() throws Exception {
        // Given
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("testuser");
        loginDto.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role", is("STUDENT")));
    }

    @Test
    void signin_ValidEmail_ReturnsJwtToken() throws Exception {
        // Given
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("test@example.com");
        loginDto.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void signin_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("testuser");
        loginDto.setPassword("wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Authentication failed")))
                .andExpect(jsonPath("$.message", is("Invalid username or password")));
    }

    @Test
    void signin_NonExistentUser_ReturnsUnauthorized() throws Exception {
        // Given
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("nonexistent");
        loginDto.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signin_EmptyCredentials_ReturnsBadRequest() throws Exception {
        // Given
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("");
        loginDto.setPassword("");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.usernameOrEmail", is(notNullValue())))
                .andExpect(jsonPath("$.validationErrors.password", is(notNullValue())));
    }

    @Test
    void signup_ValidData_ReturnsSuccess() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newuser");
        registrationDto.setEmail("newuser@example.com");
        registrationDto.setPassword("newpassword123");
        registrationDto.setFirstName("New");
        registrationDto.setLastName("User");
        registrationDto.setRole(Role.STUDENT);
        registrationDto.setUniversity("Test University");
        registrationDto.setPhoneNumber("+1234567890");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully!")));
    }

    @Test
    void signup_DuplicateUsername_ReturnsBadRequest() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser"); // Same as existing user
        registrationDto.setEmail("different@example.com");
        registrationDto.setPassword("newpassword123");
        registrationDto.setFirstName("New");
        registrationDto.setLastName("User");
        registrationDto.setRole(Role.STUDENT);
        registrationDto.setUniversity("Test University");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpected(status().isBadRequest())
                .andExpected(jsonPath("$.message", is("Error: Username is already taken!")));
    }

    @Test
    void signup_DuplicateEmail_ReturnsBadRequest() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("differentuser");
        registrationDto.setEmail("test@example.com"); // Same as existing user
        registrationDto.setPassword("newpassword123");
        registrationDto.setFirstName("New");
        registrationDto.setLastName("User");
        registrationDto.setRole(Role.STUDENT);
        registrationDto.setUniversity("Test University");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpected(status().isBadRequest())
                .andExpected(jsonPath("$.message", is("Error: Email is already in use!")));
    }

    @Test
    void signup_InvalidData_ReturnsBadRequest() throws Exception {
        // Given - Missing required fields
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(""); // Empty username
        registrationDto.setEmail("invalid-email"); // Invalid email format
        registrationDto.setPassword("123"); // Too short password
        // Missing first name, last name, role, university

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpected(status().isBadRequest())
                .andExpected(jsonPath("$.validationErrors", is(notNullValue())))
                .andExpected(jsonPath("$.validationErrors.username", is(notNullValue())))
                .andExpected(jsonPath("$.validationErrors.email", is(notNullValue())))
                .andExpected(jsonPath("$.validationErrors.password", is(notNullValue())))
                .andExpected(jsonPath("$.validationErrors.firstName", is(notNullValue())))
                .andExpected(jsonPath("$.validationErrors.lastName", is(notNullValue())))
                .andExpected(jsonPath("$.validationErrors.role", is(notNullValue())))
                .andExpected(jsonPath("$.validationErrors.university", is(notNullValue())));
    }

    @Test
    void signup_DriverWithoutLicenseNumber_ReturnsSuccess() throws Exception {
        // Given - Driver registration should be allowed without license initially
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newdriver");
        registrationDto.setEmail("driver@example.com");
        registrationDto.setPassword("driverpassword123");
        registrationDto.setFirstName("New");
        registrationDto.setLastName("Driver");
        registrationDto.setRole(Role.DRIVER);
        registrationDto.setUniversity("Test University");
        // No license number provided

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.message", is("User registered successfully!")));
    }

    @Test
    void signup_StudentWithStudentId_ReturnsSuccess() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("student123");
        registrationDto.setEmail("student@example.com");
        registrationDto.setPassword("studentpass123");
        registrationDto.setFirstName("Student");
        registrationDto.setLastName("Test");
        registrationDto.setRole(Role.STUDENT);
        registrationDto.setUniversity("Test University");
        registrationDto.setDepartment("Computer Science");
        registrationDto.setStudentId("CS2024001");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.message", is("User registered successfully!")));
    }

    @Test
    void signup_TeacherWithEmployeeId_ReturnsSuccess() throws Exception {
        // Given
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("teacher123");
        registrationDto.setEmail("teacher@example.com");
        registrationDto.setPassword("teacherpass123");
        registrationDto.setFirstName("Teacher");
        registrationDto.setLastName("Test");
        registrationDto.setRole(Role.TEACHER);
        registrationDto.setUniversity("Test University");
        registrationDto.setDepartment("Mathematics");
        registrationDto.setEmployeeId("MATH001");

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.message", is("User registered successfully!")));
    }

    @Test
    void signup_MalformedJson_ReturnsBadRequest() throws Exception {
        // Given - Malformed JSON
        String malformedJson = "{ \"username\": \"test\", \"email\": }"; // Missing value

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpected(status().isBadRequest());
    }
} 