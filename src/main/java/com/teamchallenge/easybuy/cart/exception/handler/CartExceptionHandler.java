package com.teamchallenge.easybuy.cart.exception.handler;

import com.teamchallenge.easybuy.cart.exception.InvalidItemProductQuantityException;
import com.teamchallenge.easybuy.cart.exception.InvalidShoppingCartIdException;
import com.teamchallenge.easybuy.cart.exception.ShoppingCartItemNotFoundException;
import com.teamchallenge.easybuy.cart.exception.ShoppingCartNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CartExceptionHandler {

    private final ApiErrorResponseCreator apiErrorResponseCreator;

    @ExceptionHandler(InvalidItemProductQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidItemProductQuantityException(final InvalidItemProductQuantityException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);
        log.warn("exception.cart.quantity_invalid: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    @ExceptionHandler(InvalidShoppingCartIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleInvalidShoppingCartIdException(final InvalidShoppingCartIdException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.BAD_REQUEST);
        log.warn("exception.cart.id_invalid: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    @ExceptionHandler(ShoppingCartItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleShoppingCartItemNotFoundException(final ShoppingCartItemNotFoundException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.NOT_FOUND);
        log.warn("exception.cart.item_not_found: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }

    @ExceptionHandler(ShoppingCartNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleShoppingCartNotFoundException(final ShoppingCartNotFoundException exception) {
        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(exception, HttpStatus.NOT_FOUND);
        log.warn("exception.cart.not_found: message={}", apiErrorResponse.message());
        return apiErrorResponse;
    }
}
