package com.archive.paperlessworld.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Annotation Model - JDBC Compatible
 * Removed MongoDB annotations for pure JDBC/MySQL implementation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annotation {
    
    private String id;
    
    // For JDBC compatibility - store IDs instead of object references
    private String documentId;
    
    private String userId;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private Integer page;  // page_number in DB
    
    private Double positionX;
    
    private Double positionY;
    
    private String type;  // annotation_type in DB (highlight, note, comment, etc.)
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Optional: Keep object references for convenience (will not be stored in DB)
    // These can be populated by services when needed
    private transient ArchiveDocument document;
    private transient User user;
}
