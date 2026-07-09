package com.teamchallenge.easybuy.user.service;

import com.teamchallenge.easybuy.user.entity.Authority;
import com.teamchallenge.easybuy.user.entity.MembershipStatus;
import com.teamchallenge.easybuy.user.entity.StoreMembership;
import com.teamchallenge.easybuy.user.entity.StoreMembershipRole;
import com.teamchallenge.easybuy.user.repository.StoreMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Context-scoped authorization for the RBAC model
 * (see {@code docs/architecture/users-rbac.md} §2.2, §7, §9).
 *
 * <p>HTTP-level {@code @PreAuthorize("hasRole('…')")} only decides whether a section
 * is reachable at all. This service performs the next pipeline step: given a target
 * {@code store_id}, does the user actually hold the needed role <b>in that store</b>?
 *
 * <p>Resolution rules implemented here:
 * <ul>
 * <li>Store-scoped rights come only from <b>active</b> {@link StoreMembership}
 * records — a global {@link Authority#MANAGER}/{@link Authority#CONTENT_MANAGER}
 * grants nothing on its own (§2.2).</li>
 * <li>Rights never leak between stores: each {@code store_id} is evaluated against
 * its own membership (§8.2).</li>
 * <li>{@link Authority#SUPER_ADMIN} is a platform-wide override for management;
 * {@link Authority#MODERATOR} is the override for block/soft-delete/restore.</li>
 * <li>Revocation/suspension takes effect immediately because the live membership
 * status is read here, not a cached JWT claim (§7).</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RBACGuardService {

    private final StoreMembershipRepository membershipRepository;

    // ---------------------------------------------------------------------
    // Store-scoped capability checks (explicit user)
    // ---------------------------------------------------------------------

    /**
     * Retrieves all active store roles that the specified user holds in the given store.
     *
     * @param userId  the unique identifier of the user
     * @param storeId the unique identifier of the store context
     * @return an unmodifiable set of active {@link StoreMembershipRole}s
     */
    public Set<StoreMembershipRole> storeRoles(UUID userId, UUID storeId) {
        List<StoreMembership> memberships =
                membershipRepository.findByUser_IdAndStoreIdAndStatus(userId, storeId, MembershipStatus.ACTIVE);
        return memberships.stream()
                .filter(StoreMembership::isActive)
                .map(StoreMembership::getRole)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    /**
     * Checks if the user holds a specific active role within the given store.
     *
     * @param userId  the user ID to check
     * @param storeId the store context ID
     * @param role    the specific role to verify
     * @return true if the user actively holds the role, false otherwise
     */
    public boolean hasStoreRole(UUID userId, UUID storeId, StoreMembershipRole role) {
        return storeRoles(userId, storeId).contains(role);
    }

    /**
     * Checks if the user is the explicit owner of the given store.
     *
     * @param userId  the user ID to check
     * @param storeId the store context ID
     * @return true if the user holds the OWNER role, false otherwise
     */
    public boolean isStoreOwner(UUID userId, UUID storeId) {
        return hasStoreRole(userId, storeId, StoreMembershipRole.OWNER);
    }

    /**
     * Evaluates if the user has sufficient rights to manage products within the store.
     * Both {@link StoreMembershipRole#OWNER} and {@link StoreMembershipRole#MANAGER} grant this capability (§4).
     *
     * @param userId  the user ID to check
     * @param storeId the store context ID
     * @return true if the user can create, update, or delete products
     */
    public boolean canManageProducts(UUID userId, UUID storeId) {
        Set<StoreMembershipRole> roles = storeRoles(userId, storeId);
        return roles.contains(StoreMembershipRole.OWNER) || roles.contains(StoreMembershipRole.MANAGER);
    }

    /**
     * Evaluates if the user has sufficient rights to edit product content and media.
     * Granted to OWNER, MANAGER, and explicitly to CONTENT_MANAGER (§4, §5.2).
     *
     * @param userId  the user ID to check
     * @param storeId the store context ID
     * @return true if the user can modify product descriptions and images
     */
    public boolean canManageContent(UUID userId, UUID storeId) {
        Set<StoreMembershipRole> roles = storeRoles(userId, storeId);
        return roles.contains(StoreMembershipRole.OWNER)
                || roles.contains(StoreMembershipRole.MANAGER)
                || roles.contains(StoreMembershipRole.CONTENT_MANAGER);
    }

    /**
     * Evaluates if the user can grant or revoke roles for other users within the store.
     * Restricted strictly to the store OWNER (§5.6).
     *
     * @param userId  the user ID to check
     * @param storeId the store context ID
     * @return true if the user can delegate roles
     */
    public boolean canDelegateRoles(UUID userId, UUID storeId) {
        return isStoreOwner(userId, storeId);
    }

    // ---------------------------------------------------------------------
    // Current-principal variants (require* throw AccessDeniedException)
    // ---------------------------------------------------------------------

    /**
     * Asserts that the currently authenticated user is the store OWNER or a global SUPER_ADMIN.
     *
     * @param storeId the target store ID
     * @throws AccessDeniedException if the user lacks the required ownership role
     */
    public void requireStoreOwner(UUID storeId) {
        UUID userId = currentUserId();
        if (isGlobalSuperAdmin() || isStoreOwner(userId, storeId)) {
            return;
        }
        denyStore(userId, storeId, "OWNER");
    }

    /**
     * Asserts that the currently authenticated user can manage products in the store or is a global SUPER_ADMIN.
     *
     * @param storeId the target store ID
     * @throws AccessDeniedException if the user lacks product management rights
     */
    public void requireCanManageProducts(UUID storeId) {
        UUID userId = currentUserId();
        if (isGlobalSuperAdmin() || canManageProducts(userId, storeId)) {
            return;

        }
    }
}