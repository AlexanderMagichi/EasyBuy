package com.teamchallenge.easybuy.user.dto;

import com.teamchallenge.easybuy.user.entity.MembershipStatus;
import com.teamchallenge.easybuy.user.entity.StoreMembershipRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Read model of a {@link com.teamchallenge.easybuy.user.entity.StoreMembership}
 * record returned to clients.
 * * <p>Used primarily in owner/admin team-management screens to display the current
 * active or historical roles granted to users within a specific store.
 */
@Schema(description = "Representation of a user's role and status inside a specific store.")
public record StoreMembershipResponse(

        @Schema(description = "The unique identifier of the membership record.",
                example = "e3b0c442-98fc-4629-8b9c-a5de62ed1df1")
        UUID id,

        @Schema(description = "The ID of the user to whom the role is granted.",
                example = "3f1c8c2a-5b6d-4e7f-9a0b-1c2d3e4f5a6b")
        UUID userId,

        @Schema(description = "The ID of the store where the role applies.",
                example = "9c1e2f4a-1b2c-4d5e-8f90-abcdef123456")
        UUID storeId,

        @Schema(description = "The specific role held by the user inside the store.",
                example = "MANAGER")
        StoreMembershipRole role,

        @Schema(description = "The current lifecycle status of the membership (e.g., ACTIVE, SUSPENDED, REVOKED).",
                example = "ACTIVE")
        MembershipStatus status,

        @Schema(description = "The ID of the user (owner or admin) who granted this membership. Used for audit purposes.",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID grantedBy,

        @Schema(description = "The exact timestamp when the membership was granted.",
                example = "2023-10-01T12:00:00Z")
        Instant grantedAt,

        @Schema(description = "The exact timestamp when the membership was revoked. Null if the membership is still active.",
                example = "2023-10-15T15:30:00Z",
                nullable = true)
        Instant revokedAt
) {
}