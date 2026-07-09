package com.teamchallenge.easybuy.user.exception;

/**
 * Exception thrown when a user attempts to change their password but provides an incorrect current (old) password.
 * <p>
 * This exception is typically intercepted by the global exception handler
 * to translate it into a 401 UNAUTHORIZED HTTP response for the client.
 */
public class InvalidOldPasswordException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidOldPasswordException} with a detailed message
     * indicating the email of the user who failed the password validation.
     *
     * @param userEmail the email address of the user providing the incorrect password
     */
    public InvalidOldPasswordException(String userEmail) {
        super(String.format("UserEntity with userEmail = '%s' provided incorrect password.", userEmail));
    }
}