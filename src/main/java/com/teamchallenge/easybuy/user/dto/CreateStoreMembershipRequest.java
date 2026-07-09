package com.teamchallenge.easybuy.user.dto;

import com.teamchallenge.easybuy.user.entity.StoreMembershipRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Command to grant a user a store-scoped role (delegation).
 *
 * <p>The granting actor (owner / admin) and the target {@code storeId} are supplied
 * by the calling endpoint from the security context and the request path, so they
 * are intentionally absent here.
 */
@Schema(description = "Payload for granting a specific store-scoped role to a user.")
public record CreateStoreMembershipRequest(

        @Schema(description = "The unique identifier of the user who will receive the store role.",
                example = "3f1c8c2a-5b6d-4e7f-9a0b-1c2d3e4f5a6b",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "userId must not be null")
        UUID userId,

        @Schema(description = "The specific store role to grant to the user. Note: The 'OWNER' role is reserved for shop creation and cannot be delegated.",
                example = "MANAGER",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "role must not be null")
        StoreMembershipRole role
) {
}