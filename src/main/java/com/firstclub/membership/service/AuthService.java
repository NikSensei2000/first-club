package com.firstclub.membership.service;

import com.firstclub.membership.constants.AppConstants;
import com.firstclub.membership.domain.entity.User;
import com.firstclub.membership.dto.request.LoginRequest;
import com.firstclub.membership.dto.request.RegisterRequest;
import com.firstclub.membership.dto.response.AuthResponse;
import com.firstclub.membership.exception.BusinessException;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      AuthenticationManager authenticationManager,
                      JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Starting user registration process for username: {}", request.getUsername());
        logger.debug("Registration request details - email: {}, cohort: {}", request.getEmail(), request.getCohort());

        logger.debug("Checking if username already exists: {}", request.getUsername());
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new BusinessException(AppConstants.ERROR_MESSAGE_USERNAME_EXISTS);
        }

        logger.debug("Checking if email already exists: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new BusinessException(AppConstants.ERROR_MESSAGE_EMAIL_EXISTS);
        }

        logger.debug("Creating new user entity for username: {}", request.getUsername());
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .cohort(request.getCohort())
                .roles(Set.of(AppConstants.ROLE_USER))
                .active(true)
                .build();

        logger.debug("Saving user to database: {}", request.getUsername());
        user = userRepository.save(user);
        logger.info("User saved successfully with ID: {} for username: {}", user.getId(), user.getUsername());

        logger.debug("Authenticating newly registered user: {}", request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Generating JWT token for user: {}", user.getUsername());
        String token = tokenProvider.generateToken(authentication);
        logger.info("User registration completed successfully for username: {}, userId: {}", user.getUsername(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());

        try {
            logger.debug("Authenticating user credentials for: {}", request.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Authentication successful, generating JWT token for: {}", request.getUsername());
            String token = tokenProvider.generateToken(authentication);

            logger.debug("Fetching user details from database for: {}", request.getUsername());
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        logger.error("User not found in database after successful authentication: {}", request.getUsername());
                        return new BusinessException("User not found");
                    });

            logger.info("Login successful for username: {}, userId: {}", user.getUsername(), user.getId());

            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        } catch (Exception e) {
            logger.error("Login failed for username: {} - Error: {}", request.getUsername(), e.getMessage());
            throw e;
        }
    }
}
