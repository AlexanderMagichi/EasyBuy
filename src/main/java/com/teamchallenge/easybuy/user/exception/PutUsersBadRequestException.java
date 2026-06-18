package com.teamchallenge.easybuy.user.exception;

/**
 * Exception thrown when user profile update parameters fail business validation.
 * <p>
 * This exception aggregates specific validation error messages and is typically
 * intercepted by the global exception handler to translate it into a
 * 400 BAD REQUEST HTTP response for the client.
 */
public class PutUsersBadRequestException extends RuntimeException {

    /**
     * Constructs a new {@code PutUsersBadRequestException} with the aggregated validation errors.
     *
     * @param errorMessages a concatenated string detailing the specific validation failure reasons
     */
    public PutUsersBadRequestException(String errorMessages) {
        super(String.format("PutUsersRequest parameters are incorrect. Error messages are [ %s ].", errorMessages));
    }
}