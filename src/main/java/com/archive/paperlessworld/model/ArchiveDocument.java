package com.archive.paperlessworld.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ArchiveDocument Model - JDBC Compatible
 * Removed MongoDB annotations for pure JDBC/MySQL implementation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveDocument {
    
    private String id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200)
    private String title;
    
    private String description;
    
    private String category;
    
    private String tags; // Stored as comma-separated string in DB
    
    private String accessLevel = "public"; // public, restricted, private
    
    private String fileName;
    
    private String filePath;
    
    private Long fileSize;
    
    private String mimeType; // file_type in DB
    
    // For JDBC compatibility - store uploader ID instead of object reference
    private String uploaderId; // uploader_id in DB
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Optional: Keep object reference for convenience (will not be stored in DB)
    // This can be populated by services when needed
    private transient User uploadedBy;
}
