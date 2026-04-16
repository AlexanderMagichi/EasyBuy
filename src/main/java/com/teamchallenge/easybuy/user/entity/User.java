package com.teamchallenge.easybuy.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Represents the User persistence entity.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "agreement")
    private boolean agreement = false;

    @Column(name = "privacy")
    private boolean privacy = false;

    @PrePersist
    @Schema(hidden = true)
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    @Schema(hidden = true)
    protected void onUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }
}
