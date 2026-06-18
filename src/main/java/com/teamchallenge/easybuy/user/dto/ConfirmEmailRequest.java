package com.teamchallenge.easybuy.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for confirming an email address or resetting a password.
 * Contains the unique verification token that was sent to the user.
 */
@Schema(description = "Request object containing the verification token.")
public record ConfirmEmailRequest(

        @Schema(description = "The verification token received by the user.",
                example = "123e4567-e89b-12d3-a456-426614174000")
        @NotBlank(message = "Token cannot be empty")
        String token
) {
}