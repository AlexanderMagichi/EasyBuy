package com.teamchallenge.easybuy.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 50, message = "Password must contain 6 to 50 characters")
    @Schema(description = "UserEntity password", example = "MySecurePassword1", required = true)
    private String password;

    @NotBlank(message = "Password confirmation cannot be empty")
    @Schema(description = "Password confirmation", example = "MySecurePassword1", required = true)
    private String confirmPassword;
}
