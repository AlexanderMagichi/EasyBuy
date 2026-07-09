package com.teamchallenge.easybuy.user.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested user cannot be found in the system.
 * <p>
 * This exception is typically intercepted by the global exception handler
 * to translate it into a 404 NOT FOUND HTTP response for the client.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code UserNotFoundException} indicating that a user
     * with the specified unique identifier could not be found.
     *
     * @param userId the unique identifier of the missing user
     */
    public UserNotFoundException(UUID userId) {
        super(String.format("UserEntity with id = %s is not found.", userId));
    }

    /**
     * Constructs a new {@code UserNotFoundException} indicating that a user
     * with the specified email address could not be found.
     *
     * @param email the email address of the missing user
     */
    public UserNotFoundException(String email) {
        super(String.format("UserEntity with email = %s is not found.", email));
    }
}