package com.archive.paperlessworld.exception;

/**
 * Custom exception thrown when a user attempts to access a document
 * without proper authorization or access level.
 */
public class DocumentAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String documentId;
    private final String userId;
    private final String requiredAccessLevel;

    /**
     * Constructs a new DocumentAccessDeniedException with the specified detail message.
     * 
     * @param message the detail message
     */
    public DocumentAccessDeniedException(String message) {
        super(message);
        this.documentId = null;
        this.userId = null;
        this.requiredAccessLevel = null;
    }

    /**
     * Constructs a new DocumentAccessDeniedException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DocumentAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
        this.documentId = null;
        this.userId = null;
        this.requiredAccessLevel = null;
    }

    /**
     * Constructs a new DocumentAccessDeniedException with detailed access information.
     * 
     * @param message the detail message
     * @param documentId the ID of the document
     * @param userId the ID of the user attempting access
     * @param requiredAccessLevel the required access level
     */
    public DocumentAccessDeniedException(String message, String documentId, String userId, String requiredAccessLevel) {
        super(message);
        this.documentId = documentId;
        this.userId = userId;
        this.requiredAccessLevel = requiredAccessLevel;
    }

    /**
     * Factory method to create exception for insufficient access level.
     * 
     * @param documentId the ID of the document
     * @param userId the ID of the user
     * @param requiredAccessLevel the required access level
     * @return DocumentAccessDeniedException instance
     */
    public static DocumentAccessDeniedException insufficientAccessLevel(
            String documentId, String userId, String requiredAccessLevel) {
        String message = String.format(
            "User %s does not have sufficient access level to view document %s. Required: %s",
            userId, documentId, requiredAccessLevel
        );
        return new DocumentAccessDeniedException(message, documentId, userId, requiredAccessLevel);
    }

    /**
     * Factory method to create exception for suspended user.
     * 
     * @param userId the ID of the suspended user
     * @return DocumentAccessDeniedException instance
     */
    public static DocumentAccessDeniedException userSuspended(String userId) {
        return new DocumentAccessDeniedException("User " + userId + " account is suspended and cannot access documents");
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRequiredAccessLevel() {
        return requiredAccessLevel;
    }
}
