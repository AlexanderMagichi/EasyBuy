package com.teamchallenge.easybuy.user.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested delivery address cannot be found in the system.
 * <p>
 * This exception is typically caught by the global exception handler
 * to translate it into a 404 NOT FOUND HTTP response for the client.
 */
public class DeliveryAddressNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code DeliveryAddressNotFoundException} with a detailed message
     * indicating the specific ID that could not be resolved.
     *
     * @param addressId the unique identifier of the missing delivery address
     */
    public DeliveryAddressNotFoundException(UUID addressId) {
        super(String.format("Delivery address with id = %s is not found.", addressId));
    }
}