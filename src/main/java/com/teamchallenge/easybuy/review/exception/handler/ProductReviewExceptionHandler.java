package com.teamchallenge.easybuy.review.exception.handler;

import com.teamchallenge.easybuy.infrastructure.exception.dto.ApiErrorResponse;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;
import com.teamchallenge.easybuy.review.exception.DeniedProductReviewCreationException;
import com.teamchallenge.easybuy.review.exception.DeniedProductReviewDeletionException;
import com.teamchallenge.easybuy.review.exception.InvalidProductReviewTextException;
import com.teamchallenge.easybuy.review.exception.EmptyProductReviewException;
import com.teamchallenge.easybuy.review.exception.GetReviewsBadRequestException;
import com.teamchallenge.easybuy.review.exception.ProductNotFoundForReviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(0)
public class ProductReviewExceptionHandler {

    private final ApiErrorResponseCreator apiErrorResponseCreator;

    @ExceptionHandler(EmptyProductReviewException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedReviewFormatException(final EmptyProductReviewException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);

        log.warn("exception.review.empty: message={}", apiErrorResponse.message());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProductReviewTextException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidProductReviewTextException(final InvalidProductReviewTextException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);

        log.warn("exception.review.invalid_text: message={}", apiErrorResponse.message());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeniedProductReviewDeletionException.class)
    public ResponseEntity<ApiErrorResponse> handleDeniedProductReviewDeletionException(final DeniedProductReviewDeletionException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);

        log.warn("exception.review.deletion_denied: message={}", apiErrorResponse.message());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeniedProductReviewCreationException.class)
    public ResponseEntity<ApiErrorResponse> handleDeniedProductReviewCreationException(final DeniedProductReviewCreationException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);

        log.warn("exception.review.creation_denied: message={}", apiErrorResponse.message());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundForReviewException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFoundForReviewException(final ProductNotFoundForReviewException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.NOT_FOUND);

        log.warn("exception.review.product_not_found: message={}", apiErrorResponse.message());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GetReviewsBadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleGetReviewsBadRequestException(final GetReviewsBadRequestException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);

        log.warn("exception.review.invalid_params: message={}", apiErrorResponse.message());

        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
