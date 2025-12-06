package com.archive.paperlessworld.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.archive.paperlessworld.dao.UserJdbcDAO;
import com.archive.paperlessworld.dto.AuthResponse;
import com.archive.paperlessworld.dto.LoginRequest;
import com.archive.paperlessworld.dto.RegisterRequest;
import com.archive.paperlessworld.exception.UserNotFoundException;
import com.archive.paperlessworld.model.User;
import com.archive.paperlessworld.security.JwtTokenProvider;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserJdbcDAO userJdbcDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userJdbcDAO.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        // Set access level based on role
        switch (request.getRole().toLowerCase()) {
            case "archivist":
                user.setAccessLevel("full");
                user.setStatus("approved");
                break;
            case "researcher":
                user.setAccessLevel("restricted");
                user.setStatus("pending");
                break;
            case "public":
            default:
                user.setAccessLevel("public");
                user.setStatus("approved");
                break;
        }

        user = userJdbcDAO.save(user);
        logger.info("User registered successfully with ID: {}", user.getId());

        // Generate token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().toUpperCase())
                .build();

        String token = tokenProvider.generateToken(userDetails, user.getId(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .accessLevel(user.getAccessLevel())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user details
        User user = userJdbcDAO.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("Login failed: User not found for email {}", request.getEmail());
                    return new UserNotFoundException("User not found with email: " + request.getEmail());
                });

        // Check if user is approved
        if (!"approved".equals(user.getStatus())) {
            logger.warn("Login failed: Account pending approval for email {}", request.getEmail());
            throw new SecurityException("Account pending approval");
        }
        
        logger.info("User {} logged in successfully", request.getEmail());

        // Generate token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userDetails, user.getId(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .accessLevel(user.getAccessLevel())
                .build();
    }
}
