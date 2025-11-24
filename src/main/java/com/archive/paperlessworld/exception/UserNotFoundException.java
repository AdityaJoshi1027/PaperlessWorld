package com.archive.paperlessworld.exception;

/**
 * Custom exception thrown when a user is not found in the system.
 * This is a runtime exception for cleaner exception handling in REST APIs.
 */
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     * 
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserNotFoundException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UserNotFoundException with user ID.
     * 
     * @param userId the ID of the user that was not found
     */
    public static UserNotFoundException withId(String userId) {
        return new UserNotFoundException("User not found with ID: " + userId);
    }

    /**
     * Constructs a new UserNotFoundException with email.
     * 
     * @param email the email of the user that was not found
     */
    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }
}
