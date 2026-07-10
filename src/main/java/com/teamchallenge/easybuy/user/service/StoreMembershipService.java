package com.teamchallenge.easybuy.user.service;

import com.teamchallenge.easybuy.user.dto.CreateStoreMembershipRequest;
import com.teamchallenge.easybuy.user.entity.MembershipStatus;
import com.teamchallenge.easybuy.user.entity.StoreMembership;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.repository.StoreMembershipRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Lifecycle of {@link StoreMembership} records — the context-scoped half of the
 * RBAC model (see {@code docs/architecture/users-rbac.md}).
 *
 * <p>Every state change is:
 * <ul>
 * <li><b>idempotent</b> — re-granting an already-active role returns the existing
 * record; re-revoking a revoked one is a no-op;</li>
 * <li><b>soft</b> — revocation never hard-deletes, it sets {@code REVOKED} +
 * {@code revokedAt}/{@code deletedAt} so history survives for audit/analytics;</li>
 * <li><b>traceable</b> — the acting user is recorded in {@code grantedBy}/
 * {@code deletedBy} and emitted to the audit log.</li>
 * </ul>
 *
 * <p>This service performs no HTTP-level authorization itself; callers must enforce
 * that the actor may delegate in the target store via
 * {@link RBACGuardService#requireCanDelegateRoles(UUID)}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoreMembershipService {

    private final StoreMembershipRepository membershipRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Grants a specified store role to a user within a specific store.
     * <p>
     * This operation is idempotent: if the user already holds the active role,
     * the existing record is returned unchanged. It utilizes {@link EntityManager#getReference}
     * to link the user entity without executing an additional database SELECT query.
     *
     * @param storeId   the unique identifier of the store
     * @param request   the payload containing the target user ID and the role to grant
     * @param grantedBy the ID of the owner or admin granting the role
     * @return the newly created or existing active {@link StoreMembership}
     */
    @Transactional
    public StoreMembership grant(UUID storeId, CreateStoreMembershipRequest request, UUID grantedBy) {
        return membershipRepository
                .findByUser_IdAndStoreIdAndRoleAndStatus(request.userId(), storeId, request.role(), MembershipStatus.ACTIVE)
                .map(existing -> {
                    log.info("store_membership.grant.idempotent: membershipId={}, userId={}, storeId={}, role={}, grantedBy={}",
                            existing.getId(), request.userId(), storeId, request.role(), grantedBy);
                    return existing;
                })
                .orElseGet(() -> {
                    UserEntity userRef = entityManager.getReference(UserEntity.class, request.userId());
                    StoreMembership membership = StoreMembership.builder()
                            .user(userRef)
                            .storeId(storeId)
                            .role(request.role())
                            .status(MembershipStatus.ACTIVE)
                            .grantedBy(grantedBy)
                            .grantedAt(Instant.now())
                            .build();
                    StoreMembership saved = membershipRepository.save(membership);
                    log.info("store_membership.grant: membershipId={}, userId={}, storeId={}, role={}, grantedBy={}",
                            saved.getId(), request.userId(), storeId, request.role(), grantedBy);
                    return saved;
                });
    }

    /**
     * Permanently withdraws a membership via a soft delete mechanism.
     * <p>
     * Idempotent operation — revoking an already-revoked record simply returns it
     * without altering the original revocation timestamps.
     *
     * @param membershipId the unique identifier of the membership to revoke
     * @param revokedBy    the ID of the owner or admin revoking the role
     * @return the revoked {@link StoreMembership}
     * @throws NoSuchElementException if the membership does not exist
     */
    @Transactional
    public StoreMembership revoke(UUID membershipId, UUID revokedBy) {
        StoreMembership membership = getOrThrow(membershipId);
        if (membership.getStatus() == MembershipStatus.REVOKED) {
            log.info("store_membership.revoke.idempotent: membershipId={}, revokedBy={}", membershipId, revokedBy);
            return membership;
        }
        Instant now = Instant.now();
        membership.setStatus(MembershipStatus.REVOKED);
        membership.setRevokedAt(now);
        membership.setDeletedAt(now);
        membership.setDeletedBy(revokedBy);
        log.info("store_membership.revoke: membershipId={}, userId={}, storeId={}, role={}, revokedBy={}",
                membershipId, membership.getUser().getId(), membership.getStoreId(), membership.getRole(), revokedBy);
        return membership;
    }

    /**
     * Temporarily disables a membership (e.g., while a store is frozen or under review).
     * <p>
     * The record stays live in the database and can be reactivated later.
     * Only currently {@link MembershipStatus#ACTIVE} memberships can be suspended.
     *
     * @param membershipId the unique identifier of the membership to suspend
     * @param actorId      the ID of the user performing the suspension
     * @return the suspended {@link StoreMembership}
     * @throws NoSuchElementException if the membership does not exist
     */
    @Transactional
    public StoreMembership suspend(UUID membershipId, UUID actorId) {
        StoreMembership membership = getOrThrow(membershipId);
        if (membership.getStatus() != MembershipStatus.ACTIVE) {
            log.info("store_membership.suspend.skip: membershipId={}, status={}", membershipId, membership.getStatus());
            return membership;
        }
        membership.setStatus(MembershipStatus.SUSPENDED);
        log.info("store_membership.suspend: membershipId={}, storeId={}, actorId={}",
                membershipId, membership.getStoreId(), actorId);
        return membership;
    }

    /**
     * Re-enables a previously suspended membership.
     * <p>
     * Memberships that have been fully {@link MembershipStatus#REVOKED} cannot be reactivated;
     * a new grant must be issued instead.
     *
     * @param membershipId the unique identifier of the membership to reactivate
     * @param actorId      the ID of the user performing the reactivation
     * @return the reactivated {@link StoreMembership}
     * @throws AccessDeniedException if attempting to reactivate a revoked membership
     * @throws NoSuchElementException if the membership does not exist
     */
    @Transactional
    public StoreMembership reactivate(UUID membershipId, UUID actorId) {
        StoreMembership membership = getOrThrow(membershipId);
        if (membership.getStatus() == MembershipStatus.REVOKED) {
            throw new AccessDeniedException("Revoked membership cannot be reactivated: " + membershipId);
        }
        membership.setStatus(MembershipStatus.ACTIVE);
        log.info("store_membership.reactivate: membershipId={}, storeId={}, actorId={}",
                membershipId, membership.getStoreId(), actorId);
        return membership;
    }

    /**
     * Suspends every active membership associated with a specific store.
     * <p>
     * Typically invoked when a store is administratively frozen or soft-deleted
     * so that all delegated user rights immediately cease to take effect
     * (as defined in users-rbac.md §8.5).
     *
     * @param storeId the unique identifier of the store
     * @param actorId the ID of the administrator or system process performing the suspension
     * @return the number of memberships that were suspended
     */
    @Transactional
    public int suspendAllForStore(UUID storeId, UUID actorId) {
        List<StoreMembership> active = membershipRepository.findByStoreIdAndStatus(storeId, MembershipStatus.ACTIVE);
        active.forEach(membership -> membership.setStatus(MembershipStatus.SUSPENDED));
        log.info("store_membership.suspend_all: storeId={}, count={}, actorId={}", storeId, active.size(), actorId);
        return active.size();
    }

    /**
     * Retrieves a list of all currently active memberships within a specific store.
     *
     * @param storeId the target store ID
     * @return a list of active {@link StoreMembership} records
     */
    @Transactional(readOnly = true)
    public List<StoreMembership> listActiveForStore(UUID storeId) {
        return membershipRepository.findByStoreIdAndStatus(storeId, MembershipStatus.ACTIVE);
    }

    /**
     * Retrieves a list of all currently active memberships held by a specific user across all stores.
     *
     * @param userId the target user ID
     * @return a list of active {@link StoreMembership} records
     */
    @Transactional(readOnly = true)
    public List<StoreMembership> listActiveForUser(UUID userId) {
        return membershipRepository.findByUser_IdAndStatus(userId, MembershipStatus.ACTIVE);
    }

    /**
     * Helper method to fetch a membership by ID or throw an exception if not found.
     *
     * @param membershipId the unique identifier to look up
     * @return the found {@link StoreMembership}
     * @throws NoSuchElementException if no record exists for the given ID
     */
    private StoreMembership getOrThrow(UUID membershipId) {
        return membershipRepository.findById(membershipId)
                .orElseThrow(() -> new NoSuchElementException("Store membership not found: " + membershipId));
    }
}