package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.LoginRequest;
import com.firstclub.membership.dto.request.RegisterRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.AuthResponse;
import com.firstclub.membership.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Received registration request for username: {}", request.getUsername());
        
        AuthResponse response = authService.register(request);
        
        logger.info("Registration successful for username: {}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Received login request for username: {}", request.getUsername());
        
        AuthResponse response = authService.login(request);
        
        logger.info("Login successful for username: {}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}
