package com.teamchallenge.easybuy.email.api;

import com.teamchallenge.easybuy.email.api.token.TokenManager;
import com.teamchallenge.easybuy.email.sender.AuthTokenEmailConfirmation;
import com.teamchallenge.easybuy.openapi.dto.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the email verification process.
 * Coordinates token generation and triggers the email delivery.
 */
@Service
@RequiredArgsConstructor
public class EmailTokenSender {

    private final AuthTokenEmailConfirmation emailConfirmation;
    private final TokenManager tokenManager;

    /**
     * Generates a verification token for the user and sends it via email.
     *
     * @param request The user registration request containing the user's email.
     */
    public void sendEmailVerificationCode(final UserRegistrationRequest request) {
        String token = tokenManager.generateToken(request);
        emailConfirmation.sendTemporaryCode(request.getEmail(), token);
    }
}
