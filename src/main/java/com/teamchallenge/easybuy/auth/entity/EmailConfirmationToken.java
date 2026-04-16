package com.teamchallenge.easybuy.auth.entity;

import com.teamchallenge.easybuy.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the EmailConfirmationToken persistence entity.
 */
@Entity
@Data
@Table(name = "email_confirmation_token")
public class EmailConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean confirmed = false;
}
