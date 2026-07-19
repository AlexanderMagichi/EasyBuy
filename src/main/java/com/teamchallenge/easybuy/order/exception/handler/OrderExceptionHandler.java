package com.teamchallenge.easybuy.order.exception.handler;


import com.teamchallenge.easybuy.infrastructure.exception.dto.ApiErrorResponse;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice(basePackages = {"com.teamchallenge.easybuy.order.endpoint"})
@RequiredArgsConstructor
public class OrderExceptionHandler {

    private final ApiErrorResponseCreator apiErrorResponseCreator;

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentNotValidException(final MethodArgumentTypeMismatchException exception) {
        String message = "Incorrect status value. Supported status: " + Arrays.toString(OrderStatus.values());

        ApiErrorResponse apiErrorResponse = apiErrorResponseCreator.buildResponse(message, HttpStatus.BAD_REQUEST);
        log.warn("exception.order.type_mismatch: message={}", message);

        return apiErrorResponse;
    }
}
