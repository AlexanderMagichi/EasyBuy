package com.teamchallenge.easybuy.user.entity;

/**
 * Lifecycle status of a {@link StoreMembership}.
 *
 * <p>Only {@link #ACTIVE} memberships grant any rights. Suspending or revoking a
 * membership (or soft-deleting/blocking its store) takes effect immediately,
 * because the guard layer reads the live status rather than trusting a cached JWT
 * claim.
 */
public enum MembershipStatus {

    /** Membership is in force and grants its store role. */
    ACTIVE,

    /** Temporarily disabled (e.g. shop frozen). Grants no rights until reactivated. */
    SUSPENDED,

    /** Permanently withdrawn. Kept for audit/history (soft delete). */
    REVOKED
}
