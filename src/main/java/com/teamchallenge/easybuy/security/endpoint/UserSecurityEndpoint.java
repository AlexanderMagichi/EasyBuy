package com.teamchallenge.easybuy.security.endpoint;

import com.teamchallenge.easybuy.email.api.EmailTokenConformer;
import com.teamchallenge.easybuy.email.api.EmailTokenSender;
import com.teamchallenge.easybuy.openapi.dto.*;
import com.teamchallenge.easybuy.security.api.UserAuthenticationService;
import com.teamchallenge.easybuy.security.exception.AbsentBearerHeaderException;
import com.teamchallenge.easybuy.security.jwt.JwtBlacklistValidator;
import com.teamchallenge.easybuy.security.jwt.JwtRefreshTokenValidator;
import com.teamchallenge.easybuy.security.jwt.JwtTokenFromAuthHeaderExtractor;
import com.teamchallenge.easybuy.user.api.ChangeUserPasswordOperationPerformer;
import com.teamchallenge.easybuy.user.api.SingleUserProvider;
import com.teamchallenge.easybuy.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping(value = UserSecurityEndpoint.USER_SECURITY_API_URL)
@RequiredArgsConstructor
public class UserSecurityEndpoint {

    public static final String USER_SECURITY_API_URL = "/api/v1/auth/";

    private final UserAuthenticationService userAuthenticationService;
    private final JwtTokenFromAuthHeaderExtractor jwtTokenFromAuthHeaderExtractor;
    private final JwtBlacklistValidator jwtBlacklistValidator;
    private final EmailTokenSender emailTokenSender;
    private final EmailTokenConformer emailTokenConformer;
    private final JwtRefreshTokenValidator jwtRefreshTokenValidator;
    private final UserDetailsService userDetailsService;
    private final SingleUserProvider singleUserProvider;
    private final ChangeUserPasswordOperationPerformer changeUserPasswordOperationPerformer;

    private final HttpServletRequest httpRequest;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid final UserRegistrationRequest request) {
        log.info("auth.register.processing");
        emailTokenSender.sendEmailVerificationCode(request);
        log.debug("auth.register.email_sent");
        return ResponseEntity.ok("Email verification token sent");
    }

    @Override
    @PostMapping(value = "/confirm")
    public ResponseEntity<UserAuthenticationResponse> confirmEmail(@Validated @Valid @RequestBody final ConfirmEmailRequest confirmEmailRequest) {
        log.info("auth.email.confirming");
        var response = emailTokenConformer.confirmEmailByCode(confirmEmailRequest);
        log.info("auth.email.confirmed");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationResponse> authenticate(@Valid @RequestBody final UserAuthenticationRequest request) {
        var response = userAuthenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<UserAuthenticationResponse> refreshToken() {
        log.info("auth.token.refreshing");
        String userEmail = jwtRefreshTokenValidator.extractEmail(httpRequest);
        var userDetails = userDetailsService.loadUserByUsername(userEmail);
        var response = userAuthenticationService.authenticate(userDetails, userEmail);
        log.info("auth.token.refreshed");
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("auth.logout.processing");
        String authHeader = httpRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader)) {
            log.debug("auth.logout.no_token");
            return ResponseEntity.ok().build();
        }

        try {
            String token = jwtTokenFromAuthHeaderExtractor.extract(authHeader);
            jwtBlacklistValidator.addToBlacklist(token);
            log.info("auth.logout.completed");
        } catch (AbsentBearerHeaderException ex) {
            log.warn("auth.logout.token_error: reason={}", ex.getMessage(), ex);
            // Still return success to prevent information leakage
        }

        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody final ForgotPasswordRequest request) {
        log.info("auth.password.forgot.processing");
        try {
            var user = singleUserProvider.getUserByEmail(request.getEmail());
            var verificationRequest = new UserRegistrationRequest(user.getFirstName(), user.getLastName(), user.getEmail(), "");
            emailTokenSender.sendEmailVerificationCode(verificationRequest);
        } catch (UserNotFoundException e) {
            log.warn("auth.password.forgot.unknown_email");
        }
        return ResponseEntity.ok().build();
    }

    @Override
    // amazonq-ignore-next-line
    @PostMapping("/password/change")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody final ChangePasswordRequest request) {
        var user = singleUserProvider.getUserByEmail(request.getEmail());
        emailTokenConformer.confirmResetPasswordEmailByCode(new ConfirmEmailRequest(request.getCode()));
        changeUserPasswordOperationPerformer.changeUserPassword(user.getId(), request.getPassword());
        log.info("auth.password.changed");
        return ResponseEntity.ok().build();
    }
}
