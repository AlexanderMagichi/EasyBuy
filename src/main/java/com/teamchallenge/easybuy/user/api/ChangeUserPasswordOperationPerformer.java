package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.openapi.dto.ChangeUserPasswordRequest;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import com.teamchallenge.easybuy.user.exception.InvalidOldPasswordException;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for securely handling user password changes and resets.
 * <p>
 * Provides operations for both authenticated users updating their own credentials
 * (which requires old password verification) and system-driven password resets
 * (where the old password check is bypassed).
 */
@Service
@RequiredArgsConstructor
public class ChangeUserPasswordOperationPerformer {

    private final SingleUserProvider singleUserProvider;
    private final UserRepository userRepository;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Changes the password for the currently authenticated user.
     * <p>
     * Retrieves the user's ID from the security context and enforces verification
     * of the currently active password before applying the new one.
     *
     * @param changeUserPasswordRequest the payload containing both the old and new passwords
     * @throws InvalidOldPasswordException if the provided old password does not match the stored hash
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void changeUserPassword(final ChangeUserPasswordRequest changeUserPasswordRequest) throws InvalidOldPasswordException {
        UUID userId = securityPrincipalProvider.getUserId();
        var userEntity = singleUserProvider.getUserEntityById(userId);

        if (!passwordEncoder.matches(changeUserPasswordRequest.getOldPassword(), userEntity.getPassword())) {
            throw new InvalidOldPasswordException(userEntity.getEmail());
        }

        userRepository.changeUserPassword(passwordEncoder.encode(changeUserPasswordRequest.getNewPassword()), userId);
    }

    /**
     * Directly changes the password for a specific user without verifying the old password.
     * <p>
     * This method is typically invoked during administrative operations or
     * password recovery flows (e.g., after a user has successfully verified an email reset token).
     *
     * @param userId      the unique identifier of the user whose password is to be changed
     * @param newPassword the new raw password to be encoded and stored
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void changeUserPassword(final UUID userId, final String newPassword) {
        String newEncryptedPassword = passwordEncoder.encode(newPassword);
        userRepository.changeUserPassword(newEncryptedPassword, userId);
    }
}