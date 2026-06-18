package com.teamchallenge.easybuy.auth.repository;

import com.teamchallenge.easybuy.auth.entity.PasswordResetToken;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteAllByExpiresAtBefore(LocalDateTime expiresAt);

    void deleteAllByUser(UserEntity user);
}
