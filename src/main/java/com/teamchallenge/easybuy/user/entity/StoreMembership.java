package com.teamchallenge.easybuy.user.entity;

import com.teamchallenge.easybuy.infrastructure.persistence.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Binds a {@link UserEntity} to a single store ({@code store_id}) with a
 * {@link StoreMembershipRole}, implementing the context-scoped part of the RBAC
 * model.
 *
 * <p>Rights are resolved per record: a user who manages two shops has two
 * independent memberships, and the guard layer only ever consults the record
 * matching the target {@code store_id}. Withdrawal is a soft delete
 * ({@link #status} = {@code REVOKED} + {@link #deletedAt}) so the history is kept
 * for analytics and audit.
 *
 * <p>The store is referenced by its identifier ({@link #storeId}) rather than a
 * JPA association, to keep the user module decoupled from the shop module.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = "user")
@Schema(description = "Membership that grants a user a role inside a specific store.")
@Table(name = "store_membership", indexes = {
        @Index(name = "idx_store_membership_user", columnList = "user_id"),
        @Index(name = "idx_store_membership_store", columnList = "store_id"),
        @Index(name = "idx_store_membership_user_store", columnList = "user_id, store_id"),
        @Index(name = "idx_store_membership_status", columnList = "status")
})
public class StoreMembership extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique membership ID.", example = "e3b0c442-98fc-4629-8b9c-a5de62ed1df1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "UserEntity to whom the store role is granted.")
    private UserEntity user;

    @Column(name = "store_id", nullable = false)
    @Schema(description = "Identifier of the store the role applies to.",
            example = "9c1e2f4a-1b2c-4d5e-8f90-abcdef123456")
    private UUID storeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_role", nullable = false, length = 32)
    @Schema(description = "Role held inside the store.", example = "MANAGER")
    private StoreMembershipRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @Schema(description = "Lifecycle status; only ACTIVE memberships grant rights.", example = "ACTIVE")
    private MembershipStatus status;

    @Column(name = "granted_by")
    @Schema(description = "ID of the user (owner/admin) who granted the membership.",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID grantedBy;

    @Column(name = "granted_at")
    @Schema(description = "When the membership was granted.", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant grantedAt;

    @Column(name = "revoked_at")
    @Schema(description = "When the membership was revoked, if any.", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant revokedAt;

    @Column(name = "deleted_at")
    @Schema(description = "Soft-delete timestamp; null while the record is live.",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant deletedAt;

    @Column(name = "deleted_by")
    @Schema(description = "ID of the user who soft-deleted the membership.",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID deletedBy;

    /** True only when the membership is active and not soft-deleted. */
    public boolean isActive() {
        return status == MembershipStatus.ACTIVE && deletedAt == null;
    }
}
