package com.teamchallenge.easybuy.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email format")
    @Schema(description = "UserEntity email", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 50, message = "Password must contain 6 to 50 characters")
    @Schema(description = "UserEntity password", example = "mySecurePass", required = true)
    private String password;
}
