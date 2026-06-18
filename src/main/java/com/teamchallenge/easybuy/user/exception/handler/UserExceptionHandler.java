package com.teamchallenge.easybuy.user.exception.handler;

import com.teamchallenge.easybuy.common.exception.dto.ApiErrorResponse;
import com.teamchallenge.easybuy.common.exception.handler.ApiErrorResponseCreator;
import com.teamchallenge.easybuy.user.exception.DeliveryAddressNotFoundException;
import com.teamchallenge.easybuy.user.exception.InvalidOldPasswordException;
import com.teamchallenge.easybuy.user.exception.PutUsersBadRequestException;
import com.teamchallenge.easybuy.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the user module.
 * <p>
 * Intercepts domain-specific exceptions thrown by user controllers and services,
 * translating them into standardized {@link ApiErrorResponse} objects with appropriate HTTP status codes.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UserExceptionHandler {

    private final ApiErrorResponseCreator apiErrorResponseCreator;

    /**
     * Handles cases where a requested delivery address cannot be found.
     *
     * @param exception the thrown exception containing the address ID
     * @return a standardized API error response with a 404 NOT FOUND status
     */
    @ExceptionHandler(DeliveryAddressNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleDeliveryAddressNotFoundException(final DeliveryAddressNotFoundException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.NOT_FOUND);
        log.warn("exception.delivery_address.not_found: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    /**
     * Handles cases where a user account cannot be found by its ID or email.
     *
     * @param exception the thrown exception containing the user identifier
     * @return a standardized API error response with a 404 NOT FOUND status
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.NOT_FOUND);
        log.warn("exception.user.not_found: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    /**
     * Handles Spring Security exceptions when an authentication attempt fails due to an unknown email.
     *
     * @param exception the thrown exception containing the unknown username
     * @return a standardized API error response with a 401 UNAUTHORIZED status
     */
    @ExceptionHandler({UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleUsernameNotFoundException(final UsernameNotFoundException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.UNAUTHORIZED);
        log.warn("exception.user.username_not_found: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    /**
     * Handles attempts to change a user's password using an incorrect current (old) password.
     *
     * @param exception the thrown exception
     * @return a standardized API error response with a 401 UNAUTHORIZED status
     */
    @ExceptionHandler({InvalidOldPasswordException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidOldPasswordException(final InvalidOldPasswordException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.UNAUTHORIZED);
        log.warn("exception.user.invalid_password: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    /**
     * Handles domain-specific validation failures during user profile updates.
     *
     * @param exception the thrown exception with business validation details
     * @return a standardized API error response with a 400 BAD REQUEST status
     */
    @ExceptionHandler({PutUsersBadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handlePutUsersBadRequestException(final PutUsersBadRequestException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);
        log.warn("exception.user.invalid_property: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    /**
     * Handles standard Spring Boot validation exceptions (e.g., @Valid failures on DTOs).
     * <p>
     * Extracts and concatenates all field-level validation errors into a single readable message.
     *
     * @param ex the thrown validation exception containing binding results
     * @return a standardized API error response with a 400 BAD REQUEST status and combined error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            if (!errorMessage.isEmpty()) {
                errorMessage.append(" and ");
            }
            errorMessage.append(error.getDefaultMessage());
        });
        return apiErrorResponseCreator.buildResponse(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }
}