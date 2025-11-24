package com.archive.paperlessworld.service;

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
import com.archive.paperlessworld.model.User;
import com.archive.paperlessworld.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private UserJdbcDAO userJdbcDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userJdbcDAO.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
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
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user details
        User user = userJdbcDAO.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is approved
        if (!"approved".equals(user.getStatus())) {
            throw new RuntimeException("Account pending approval");
        }

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
