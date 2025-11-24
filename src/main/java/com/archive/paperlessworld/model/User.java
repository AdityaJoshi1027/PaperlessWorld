package com.archive.paperlessworld.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Model - JDBC Compatible
 * Removed MongoDB annotations for pure JDBC/MySQL implementation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private String id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;
    
    @NotBlank(message = "Role is required")
    private String role; // archivist, researcher, public, admin
    
    private String status = "pending"; // pending, approved, suspended
    
    private String accessLevel = "public"; // public, restricted, full
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
