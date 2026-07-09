package com.teamchallenge.easybuy.user.repository;

import com.teamchallenge.easybuy.user.entity.MembershipStatus;
import com.teamchallenge.easybuy.user.entity.StoreMembership;
import com.teamchallenge.easybuy.user.entity.StoreMembershipRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for accessing and managing {@link StoreMembership} records.
 * <p>
 * All read methods filter by {@link MembershipStatus} and typically ignore soft-deleted
 * rows (where {@code deletedAt} is set together with {@code REVOKED}), ensuring that
 * callers retrieve only live or explicitly requested membership states by default.
 */
public interface StoreMembershipRepository extends JpaRepository<StoreMembership, UUID> {

    /**
     * Retrieves all memberships of a specific status that a user holds within a given store.
     * Note that a user may hold several distinct roles within the same store simultaneously.
     *
     * @param userId  the unique identifier of the user
     * @param storeId the unique identifier of the store
     * @param status  the lifecycle status to filter by (e.g., ACTIVE)
     * @return a list of matching {@link StoreMembership} entities
     */
    List<StoreMembership> findByUser_IdAndStoreIdAndStatus(UUID userId, UUID storeId, MembershipStatus status);

    /**
     * Retrieves all memberships of a specific status for a given store.
     * This is typically used to fetch the active team members managing a store.
     *
     * @param storeId the unique identifier of the store
     * @param status  the lifecycle status to filter by
     * @return a list of matching {@link StoreMembership} entities
     */
    List<StoreMembership> findByStoreIdAndStatus(UUID storeId, MembershipStatus status);

    /**
     * Retrieves all memberships of a specific status for a user across all stores.
     * Useful for displaying a user's dashboard of managed stores and delegated roles.
     *
     * @param userId the unique identifier of the user
     * @param status the lifecycle status to filter by
     * @return a list of matching {@link StoreMembership} entities
     */
    List<StoreMembership> findByUser_IdAndStatus(UUID userId, MembershipStatus status);

    /**
     * Retrieves a specific role held by a user in a particular store, filtered by status.
     *
     * @param userId  the unique identifier of the user
     * @param storeId the unique identifier of the store
     * @param role    the specific store role to find (e.g., MANAGER)
     * @param status  the lifecycle status to filter by
     * @return an {@link Optional} containing the {@link StoreMembership} if found, otherwise empty
     */
    Optional<StoreMembership> findByUser_IdAndStoreIdAndRoleAndStatus(UUID userId,
                                                                      UUID storeId,
                                                                      StoreMembershipRole role,
                                                                      MembershipStatus status);

    /**
     * Checks whether a user already holds a specific role with a specific status in a given store.
     * Acts as a duplicate guard before granting new roles.
     *
     * @param userId  the unique identifier of the user
     * @param storeId the unique identifier of the store
     * @param role    the specific store role to check
     * @param status  the lifecycle status to filter by
     * @return {@code true} if the matching membership exists, {@code false} otherwise
     */
    boolean existsByUser_IdAndStoreIdAndRoleAndStatus(UUID userId,
                                                      UUID storeId,
                                                      StoreMembershipRole role,
                                                      MembershipStatus status);

    /**
     * Checks if a user has any active role in the specified store.
     * Used by ShopAccessGuard to verify if the user is part of the store team.
     *
     * @param userId  the unique identifier of the user
     * @param storeId the unique identifier of the store
     * @param status  the lifecycle status to filter by
     * @return {@code true} if any membership exists, {@code false} otherwise
     */

    boolean existsByUser_IdAndStoreIdAndStatus(UUID userId, UUID storeId, MembershipStatus status);
}