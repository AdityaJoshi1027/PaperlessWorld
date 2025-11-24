package com.archive.paperlessworld.exception;

/**
 * Custom exception thrown when a database operation fails.
 * Wraps SQLException and provides more context-specific error messages.
 */
public class DatabaseOperationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String operation;
    private final String entityType;

    /**
     * Constructs a new DatabaseOperationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public DatabaseOperationException(String message) {
        super(message);
        this.operation = null;
        this.entityType = null;
    }

    /**
     * Constructs a new DatabaseOperationException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception (usually SQLException)
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.entityType = null;
    }

    /**
     * Constructs a new DatabaseOperationException with operation details.
     * 
     * @param message the detail message
     * @param operation the database operation that failed (INSERT, UPDATE, DELETE, SELECT)
     * @param entityType the type of entity being operated on
     * @param cause the cause of the exception
     */
    public DatabaseOperationException(String message, String operation, String entityType, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.entityType = entityType;
    }

    /**
     * Factory method for INSERT operation failure.
     * 
     * @param entityType the type of entity
     * @param cause the SQLException cause
     * @return DatabaseOperationException instance
     */
    public static DatabaseOperationException insertFailed(String entityType, Throwable cause) {
        String message = String.format("Failed to insert %s into database", entityType);
        return new DatabaseOperationException(message, "INSERT", entityType, cause);
    }

    /**
     * Factory method for UPDATE operation failure.
     * 
     * @param entityType the type of entity
     * @param entityId the ID of the entity
     * @param cause the SQLException cause
     * @return DatabaseOperationException instance
     */
    public static DatabaseOperationException updateFailed(String entityType, String entityId, Throwable cause) {
        String message = String.format("Failed to update %s with ID: %s", entityType, entityId);
        return new DatabaseOperationException(message, "UPDATE", entityType, cause);
    }

    /**
     * Factory method for DELETE operation failure.
     * 
     * @param entityType the type of entity
     * @param entityId the ID of the entity
     * @param cause the SQLException cause
     * @return DatabaseOperationException instance
     */
    public static DatabaseOperationException deleteFailed(String entityType, String entityId, Throwable cause) {
        String message = String.format("Failed to delete %s with ID: %s", entityType, entityId);
        return new DatabaseOperationException(message, "DELETE", entityType, cause);
    }

    /**
     * Factory method for SELECT operation failure.
     * 
     * @param entityType the type of entity
     * @param cause the SQLException cause
     * @return DatabaseOperationException instance
     */
    public static DatabaseOperationException selectFailed(String entityType, Throwable cause) {
        String message = String.format("Failed to retrieve %s from database", entityType);
        return new DatabaseOperationException(message, "SELECT", entityType, cause);
    }

    /**
     * Factory method for transaction rollback failure.
     * 
     * @param cause the SQLException cause
     * @return DatabaseOperationException instance
     */
    public static DatabaseOperationException rollbackFailed(Throwable cause) {
        return new DatabaseOperationException("Failed to rollback database transaction", "ROLLBACK", "Transaction", cause);
    }

    public String getOperation() {
        return operation;
    }

    public String getEntityType() {
        return entityType;
    }
}
