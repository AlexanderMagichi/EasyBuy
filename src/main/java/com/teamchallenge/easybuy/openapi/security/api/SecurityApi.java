package com.teamchallenge.easybuy.openapi.security.api;

import com.teamchallenge.easybuy.openapi.dto.*;
import org.springframework.http.ResponseEntity;

public interface SecurityApi {
    ResponseEntity<UserAuthenticationResponse> confirmEmail(ConfirmEmailRequest confirmEmailRequest);

    ResponseEntity<UserAuthenticationResponse> authenticate(UserAuthenticationRequest request);

    ResponseEntity<UserAuthenticationResponse> refreshToken();

    ResponseEntity<Void> logout();

    ResponseEntity<Void> forgotPassword(ForgotPasswordRequest request);

    ResponseEntity<Void> changePassword(ChangePasswordRequest request);
}
