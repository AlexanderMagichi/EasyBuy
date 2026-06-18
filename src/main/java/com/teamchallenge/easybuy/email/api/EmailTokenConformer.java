package com.teamchallenge.easybuy.email.api;

import com.teamchallenge.easybuy.email.api.token.TokenManager;
import com.teamchallenge.easybuy.openapi.dto.ConfirmEmailRequest;
import com.teamchallenge.easybuy.openapi.dto.UserAuthenticationResponse;
import com.teamchallenge.easybuy.openapi.dto.UserRegistrationRequest;
import com.teamchallenge.easybuy.security.api.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing email confirmation requests.
 * Orchestrates token validation and user registration flow.
 */
@Service
@RequiredArgsConstructor
public class EmailTokenConformer {

    private final UserRegistrationService userRegistrationService;
    private final TokenManager tokenManager;

    /**
     * Validates the provided confirmation token and registers the user if the token is valid.
     *
     * @param confirmEmailRequest The request containing the validation token.
     * @return The {@link UserAuthenticationResponse} after successful registration.
     */
    public UserAuthenticationResponse confirmEmailByCode(final ConfirmEmailRequest confirmEmailRequest) {
        UserRegistrationRequest userRegistrationRequest = tokenManager.validateToken(confirmEmailRequest);
        return userRegistrationService.register(userRegistrationRequest);
    }

    /**
     * Validates the provided confirmation token for password reset flows.
     *
     * @param confirmEmailRequest The request containing the validation token.
     */
    public void confirmResetPasswordEmailByCode(final ConfirmEmailRequest confirmEmailRequest) {
        tokenManager.validateToken(confirmEmailRequest);
    }
}