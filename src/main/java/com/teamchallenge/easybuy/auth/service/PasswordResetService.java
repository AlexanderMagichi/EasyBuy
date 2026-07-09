package com.teamchallenge.easybuy.auth.service;

import com.teamchallenge.easybuy.auth.entity.PasswordResetToken;
import com.teamchallenge.easybuy.auth.repository.PasswordResetTokenRepository;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordEncoder passwordEncoder;

    @Value("${frontend.server.url}")
    private String frontendUrl;

    public void sendResetLink(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserEntity not found"));

        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.deleteAllByExpiresAtBefore(now);

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(userEntity);
        passwordResetToken.setCreatedAt(now);
        passwordResetToken.setExpiresAt(now.plusHours(24));

        passwordResetTokenRepository.save(passwordResetToken);

        String link = frontendUrl + "/reset-password?token=" + token;
        emailConfirmationService.send(userEntity.getEmail(), "Password reset",
                "Click on the link to set a new password: " + link);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(
                () -> new IllegalStateException("Token not found"));

        if (passwordResetToken.isConfirmed() || passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("The token has expired or has already been used.");
        }

        UserEntity userEntity = passwordResetToken.getUser();
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);

        passwordResetToken.setConfirmed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Transactional
    public void deleteAllByUser(UserEntity userEntity) {
        passwordResetTokenRepository.deleteAllByUser(userEntity);
    }
}