package com.teamchallenge.easybuy.security.exception.handler;

import com.teamchallenge.easybuy.infrastructure.exception.dto.ApiErrorResponse;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;
import com.teamchallenge.easybuy.security.exception.JwtTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class JwtTokenExceptionsHandler {

    private final ApiErrorResponseCreator apiErrorResponseCreator;

    @ExceptionHandler(JwtTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleJwtTokenException(final JwtTokenException exception) {
        return apiErrorResponseCreator.buildResponse(exception, HttpStatus.UNAUTHORIZED);
    }
}
