package com.archive.paperlessworld.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feedback Model - JDBC Compatible
 * Removed MongoDB annotations for pure JDBC/MySQL implementation
 * Schema fields: id, user_id, document_id, subject, message, status, priority
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    
    private String id;
    
    // For JDBC compatibility - store IDs instead of object references
    private String userId;
    
    private String documentId; // Can be null
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    private String status = "pending"; // pending, reviewed, resolved
    
    private String priority = "normal"; // low, normal, high, urgent
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Optional: Keep object reference for convenience (will not be stored in DB)
    // This can be populated by services when needed
    private transient User user;
    private transient ArchiveDocument document;
}
