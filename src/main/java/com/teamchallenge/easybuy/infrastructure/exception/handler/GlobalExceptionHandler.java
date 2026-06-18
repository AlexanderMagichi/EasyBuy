package com.teamchallenge.easybuy.infrastructure.exception.handler;


import com.teamchallenge.easybuy.infrastructure.exception.dto.ApiErrorResponse;
import com.teamchallenge.easybuy.shop.exception.ShopNotFoundException;
import com.teamchallenge.easybuy.shop.exception.ShopBillingIntegrationException;
import com.teamchallenge.easybuy.product.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * Global exception handler providing a centralized error handling strategy.
 * Uses ApiErrorResponseCreator to maintain a consistent API error contract.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiErrorResponseCreator apiErrorResponseCreator;

    // --- Product & Category Exceptions ---

    @ExceptionHandler({
            CategoryNotFoundException.class,
            GoodsNotFoundException.class,
            GoodsImageException.class,
            GoodsAttributeValueException.class,
            CategoryAttributeException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(Exception ex, WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    // --- Shop Exceptions ---

    @ExceptionHandler(ShopNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleShopNotFoundException(ShopNotFoundException ex) {
        return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShopBillingIntegrationException.class)
    public ResponseEntity<ApiErrorResponse> handleShopBillingIntegrationException(ShopBillingIntegrationException ex) {
        return buildResponseEntity(ex, HttpStatus.BAD_GATEWAY);
    }

    // --- Validation & Data Exceptions ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        ApiErrorResponse response = apiErrorResponseCreator.buildResponse(message, HttpStatus.BAD_REQUEST);
        log.warn("Validation failed: {}", message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Database constraint violated: " + ex.getMostSpecificCause().getMessage();
        ApiErrorResponse response = apiErrorResponseCreator.buildResponse(message, HttpStatus.CONFLICT);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // --- Standard Spring Exceptions ---

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(Exception ex) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        return buildResponseEntity(ex.getReason() != null ? ex.getReason() : ex.getMessage(),
                status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Fallback ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return buildResponseEntity("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // --- Helper Methods ---

    private ResponseEntity<ApiErrorResponse> buildResponseEntity(Exception ex, HttpStatus status) {
        return buildResponseEntity(ex.getMessage(), status);
    }

    private ResponseEntity<ApiErrorResponse> buildResponseEntity(String message, HttpStatus status) {
        ApiErrorResponse response = apiErrorResponseCreator.buildResponse(message, status);
        return new ResponseEntity<>(response, status);
    }
}