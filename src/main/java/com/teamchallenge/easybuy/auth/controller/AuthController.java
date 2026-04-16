package com.teamchallenge.easybuy.auth.controller;

import com.teamchallenge.easybuy.auth.dto.*;
import com.teamchallenge.easybuy.user.entity.User;
import com.teamchallenge.easybuy.infrastructure.image.CloudinaryImageService;
import com.teamchallenge.easybuy.auth.service.AuthenticationService;
import com.teamchallenge.easybuy.auth.service.EmailConfirmationService;
import com.teamchallenge.easybuy.auth.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

/**
 * Exposes API endpoints in AuthController.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordResetService passwordResetService;
    private final CloudinaryImageService cloudinaryImageService;

    @Operation(summary = "User registration", description = "Receives registration data and sends a link to the email address to confirm it. The link looks like this: \".../confirm?token=...\"")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful registration"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Registration failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Input validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = "{\"email\": \"Incorrect email format\", \"password\": \"Password cannot be empty\"}"
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        User user = authenticationService.register(registerRequestDto);
        emailConfirmationService.sendConfirmationEmail(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Email confirmation", description = "Confirms email using token from email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Email confirmation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(emailConfirmationService.confirmEmail(token));
    }

    @Operation(summary = "Resend link to email", description = "Resends the email link to the user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sending successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @PostMapping("/resend-confirmation")
    public ResponseEntity<?> resendConfirmation(@RequestParam String email) {
        emailConfirmationService.resendConfirmationEmail(email);
        return ResponseEntity.ok("The new token has been sent to the email");
    }

    @Operation(summary = "User authorization", description = "Returns JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful authorization",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Incorrect login or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Authorization unsuccessful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Input validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = "{\"email\": \"Incorrect email format\", \"password\": \"Password cannot be empty\"}"
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @Operation(summary = "Access token update", description = "Returns new access and old refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful authorization",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Input validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = "{\"refreshToken\": \"Refresh token cannot be empty\"}"
                            )
                    )
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        AuthResponseDto authResponseDto = authenticationService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(authResponseDto);
    }

    @Operation(summary = "Log out of account", description = "Logs out of user account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Log out successful"
            )
    })
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        authenticationService.logout();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password in account", description = "Change of account's password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Passwords do not match"
            )
    })
    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto request) {
        authenticationService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password in account", description = "Receiving mail for the account for which the password needs to be changed and sending an email with a password reset link to that email address. The link looks like this: \".../reset-password?token=...\"")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "A message to change your password has been sent to your email address."
            )
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestParam("email") String email, HttpServletRequest request) {
        passwordResetService.sendResetLink(email);
        return ResponseEntity.ok("A reset link has been sent to your email address, if one exists.");
    }

    @Operation(summary = "Change password in account", description = "Obtaining a password change token, new password, and confirmation of the new password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "A message to change your password has been sent to your email address."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Passwords do not match"
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @Valid @RequestBody ChangePasswordDto request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        passwordResetService.resetPassword(token, request.getPassword());
        return ResponseEntity.ok("Your password has been reset.");
    }

    @PutMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = cloudinaryImageService.uploadImage(file, "easybuy/users");
            authenticationService.updateAvatarUrl(avatarUrl);
            return ResponseEntity.ok(avatarUrl);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<?> deleteAvatar() {
        try {
            authenticationService.deleteAvatarUrl();
            return ResponseEntity.ok("Avatar has been deleted.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Delete failed: " + e.getMessage());
        }
    }
}
