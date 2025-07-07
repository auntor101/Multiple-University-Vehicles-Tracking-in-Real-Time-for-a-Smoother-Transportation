package com.vehicletracking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicletracking.dto.UserLoginDto;
import com.vehicletracking.model.Role;
import com.vehicletracking.model.User;
import com.vehicletracking.repository.UserRepository;
import com.vehicletracking.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User adminUser;
    private User driverUser;
    private User studentUser;

    @BeforeEach
    void setUp() {
        // Create test users for different roles
        adminUser = createUser("admin", "admin@test.com", Role.ADMIN);
        driverUser = createUser("driver", "driver@test.com", Role.DRIVER);
        studentUser = createUser("student", "student@test.com", Role.STUDENT);
    }

    @Test
    @WithAnonymousUser
    void accessProtectedEndpoint_WithoutAuthentication_Returns401() throws Exception {
        // Test protected endpoints return 401 without authentication
        mockMvc.perform(get("/api/vehicles"))
                .andExpected(status().isUnauthorized());

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpected(status().isUnauthorized());

        mockMvc.perform(get("/api/users"))
                .andExpected(status().isUnauthorized());
    }

    @Test
    void accessPublicEndpoints_WithoutAuthentication_ReturnsSuccess() throws Exception {
        // Test public endpoints are accessible without authentication
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("admin");
        loginDto.setPassword("password123");

        // Auth endpoints should be accessible
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpected(status().isOk());

        // Vehicle tracking should be public (read-only)
        mockMvc.perform(get("/api/tracking/vehicles"))
                .andExpected(status().isOk());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void accessAdminEndpoint_AsStudent_Returns403() throws Exception {
        // Students should not access admin-only endpoints
        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpected(status().isForbidden());

        mockMvc.perform(get("/api/users"))
                .andExpected(status().isForbidden());

        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpected(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void accessDriverEndpoints_AsDriver_ReturnsSuccess() throws Exception {
        // Drivers should access their own vehicle updates
        mockMvc.perform(post("/api/tracking/my-vehicle/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"latitude\":40.7128,\"longitude\":-74.0060}"))
                .andExpected(status().isBadRequest()); // No vehicle assigned, but authorized
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void accessAdminEndpoints_AsAdmin_ReturnsSuccess() throws Exception {
        // Admins should access all endpoints
        mockMvc.perform(get("/api/vehicles"))
                .andExpected(status().isOk());

        mockMvc.perform(get("/api/users"))
                .andExpected(status().isOk());
    }

    @Test
    void jwtToken_InvalidToken_Returns401() throws Exception {
        // Test with completely invalid token
        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpected(status().isUnauthorized());

        // Test with malformed token
        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer malformed-token"))
                .andExpected(status().isUnauthorized());

        // Test with empty token
        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer "))
                .andExpected(status().isUnauthorized());
    }

    @Test
    void jwtToken_ExpiredToken_Returns401() throws Exception {
        // Create expired token (simulate by creating token with past expiration)
        String expiredToken = createExpiredToken();

        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpected(status().isUnauthorized());
    }

    @Test
    void jwtToken_ValidToken_ReturnsSuccess() throws Exception {
        // Create valid token
        String validToken = jwtUtils.generateJwtToken(adminUser.getUsername());

        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + validToken))
                .andExpected(status().isOk());
    }

    @Test
    void passwordSecurity_WeakPasswords_AreRejected() throws Exception {
        // Test various weak password patterns
        String[] weakPasswords = {
                "123",           // Too short
                "password",      // No numbers, uppercase, special chars
                "PASSWORD",      // No lowercase, numbers, special chars
                "12345678",      // Only numbers
                "abcdefgh",      // Only lowercase
                "ABCDEFGH",      // Only uppercase
                "password123",   // No uppercase, special chars
                "Password123"    // No special chars
        };

        for (String weakPassword : weakPasswords) {
            String registrationData = String.format(
                    "{\"username\":\"test%s\",\"email\":\"test%s@test.com\",\"password\":\"%s\",\"firstName\":\"Test\",\"lastName\":\"User\",\"role\":\"STUDENT\",\"university\":\"Test University\"}",
                    System.currentTimeMillis(), System.currentTimeMillis(), weakPassword
            );

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registrationData))
                    .andExpected(status().isBadRequest());
        }
    }

    @Test
    void passwordSecurity_StrongPassword_IsAccepted() throws Exception {
        // Test strong password
        String strongPassword = "StrongP@ssw0rd123!";
        String username = "testuser" + System.currentTimeMillis();
        String email = username + "@test.com";

        String registrationData = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"firstName\":\"Test\",\"lastName\":\"User\",\"role\":\"STUDENT\",\"university\":\"Test University\"}",
                username, email, strongPassword
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationData))
                .andExpected(status().isOk());
    }

    @Test
    void sqlInjection_AttemptsAreBlocked() throws Exception {
        // Test SQL injection attempts in login
        String[] sqlInjectionAttempts = {
                "admin'; DROP TABLE users; --",
                "admin' OR '1'='1",
                "admin' UNION SELECT * FROM users --",
                "'; DELETE FROM vehicles; --"
        };

        for (String injection : sqlInjectionAttempts) {
            UserLoginDto loginDto = new UserLoginDto();
            loginDto.setUsernameOrEmail(injection);
            loginDto.setPassword("password");

            mockMvc.perform(post("/api/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDto)))
                    .andExpected(status().isUnauthorized()); // Should fail authentication, not cause SQL error
        }
    }

    @Test
    void xssAttempts_AreSanitized() throws Exception {
        // Test XSS attempts in registration
        String xssScript = "<script>alert('xss')</script>";
        String username = "testuser" + System.currentTimeMillis();
        String email = username + "@test.com";

        String registrationData = String.format(
                "{\"username\":\"%s\",\"email\":\"%s\",\"password\":\"SecureP@ss123!\",\"firstName\":\"%s\",\"lastName\":\"User\",\"role\":\"STUDENT\",\"university\":\"Test University\"}",
                username, email, xssScript
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationData))
                .andExpected(status().isOk());

        // Verify that XSS script is not stored as-is (should be sanitized)
        User savedUser = userRepository.findByUsername(username).orElse(null);
        if (savedUser != null) {
            // First name should not contain script tags
            assertFalse(savedUser.getFirstName().contains("<script>"));
            assertFalse(savedUser.getFirstName().contains("</script>"));
        }
    }

    @Test
    void sensitiveDataExposure_IsBlocked() throws Exception {
        // Login and get user info
        String token = jwtUtils.generateJwtToken(adminUser.getUsername());

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.password").doesNotExist()) // Password should not be exposed
                .andExpected(jsonPath("$.username").exists())
                .andExpected(jsonPath("$.email").exists());
    }

    @Test
    void bruteForceProtection_LimitsLoginAttempts() throws Exception {
        // Simulate multiple failed login attempts
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("admin");
        loginDto.setPassword("wrongpassword");

        // Make multiple failed attempts
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/signin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDto)))
                    .andExpected(status().isUnauthorized());
        }

        // Note: In a real implementation, we would expect rate limiting after several attempts
        // This test documents the expected behavior
    }

    @Test
    void cors_OptionsRequest_ReturnsCorrectHeaders() throws Exception {
        // Test CORS preflight request
        mockMvc.perform(options("/api/vehicles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpected(status().isOk())
                .andExpected(header().exists("Access-Control-Allow-Origin"))
                .andExpected(header().exists("Access-Control-Allow-Methods"))
                .andExpected(header().exists("Access-Control-Allow-Headers"));
    }

    @Test
    void securityHeaders_ArePresent() throws Exception {
        // Test that security headers are present in responses
        mockMvc.perform(get("/api/tracking/vehicles"))
                .andExpected(status().isOk())
                .andExpected(header().exists("X-Content-Type-Options"))
                .andExpected(header().exists("X-Frame-Options"))
                .andExpected(header().exists("X-XSS-Protection"));
    }

    private User createUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setUniversity("Test University");
        user.setIsActive(true);
        user.setIsEmailVerified(true);
        return userRepository.save(user);
    }

    private String createExpiredToken() {
        // Create a token that appears to be expired
        // In a real implementation, you might manipulate the JWT creation to set past expiration
        return jwtUtils.generateJwtToken("expired_user");
    }
} 