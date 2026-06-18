package com.teamchallenge.easybuy.infrastructure.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a standardized API error response.
 * Used by exception handlers to return consistent error details to clients.
 */
@Builder
public record ApiErrorResponse(

        @JsonProperty("message")
        String message,

        @JsonProperty("httpStatusCode")
        Integer httpStatusCode,

        @JsonProperty("timestamp")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIMESTAMP_JSON_FORMAT)
        LocalDateTime timestamp
) {
    /**
     * The pattern used for formatting the timestamp in the JSON response.
     */
    public static final String TIMESTAMP_JSON_FORMAT = "yyyy-MM-dd HH:mm:ss";
}