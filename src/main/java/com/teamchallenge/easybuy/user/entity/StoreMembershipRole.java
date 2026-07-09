package com.teamchallenge.easybuy.user.entity;

/**
 * Role a user holds <b>inside a specific store</b> ({@code store_id}).
 *
 * <p>Unlike {@link Authority} (platform-wide), these roles are always evaluated in
 * the context of one shop. The same user may hold different store roles in
 * different shops simultaneously — each is an independent
 * {@link StoreMembership} record, so rights never leak between stores.
 */
public enum StoreMembershipRole {

    /** Shop owner. Full control of the shop and the right to delegate other store roles. */
    OWNER,

    /** Limited product CRUD within the shop. Cannot manage billing or delegate roles. */
    MANAGER,

    /** Moderation of product descriptions and media within the shop. No financial access. */
    CONTENT_MANAGER
}
