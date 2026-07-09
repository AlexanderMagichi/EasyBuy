package com.teamchallenge.easybuy.user.converter;

import com.teamchallenge.easybuy.user.dto.StoreMembershipResponse;
import com.teamchallenge.easybuy.user.entity.StoreMembership;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping {@link StoreMembership} database entities
 * to their corresponding API read model, {@link StoreMembershipResponse}.
 * <p>
 * This converter is implemented manually to explicitly handle the null-safe
 * extraction of the associated user's ID and to provide a clear mapping of
 * role lifecycle and audit fields.
 */
@Component
public class StoreMembershipDtoConverter {

    /**
     * Converts a {@link StoreMembership} entity into a {@link StoreMembershipResponse} record.
     * <p>
     * Safely extracts the user ID from the associated {@code UserEntity} relationship.
     * Passes through the role context, current lifecycle status, and audit metadata
     * (such as the actor who granted the role and exact timestamps).
     *
     * @param membership the membership database entity to convert
     * @return the mapped response object containing role and status details
     */
    public StoreMembershipResponse toResponse(StoreMembership membership) {
        return new StoreMembershipResponse(
                membership.getId(),
                membership.getUser() != null ? membership.getUser().getId() : null,
                membership.getStoreId(),
                membership.getRole(),
                membership.getStatus(),
                membership.getGrantedBy(),
                membership.getGrantedAt(),
                membership.getRevokedAt()
        );
    }
}