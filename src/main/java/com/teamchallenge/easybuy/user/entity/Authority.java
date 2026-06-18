package com.teamchallenge.easybuy.user.entity;

/**
 * Global (platform-wide) roles of an account.
 *
 * <p>Roles are <b>additive</b>: one account may hold several authorities at once
 * (e.g. {@link #CUSTOMER} + {@link #SELLER}). The effective permission set is the
 * union of all held roles, narrowed by the store context ({@code store_id}) and
 * the soft-delete status of the target entity.
 *
 * <p>Store-scoped behaviour (rights only inside a particular shop) is <b>not</b>
 * modelled here — it is expressed through
 * {@link com.teamchallenge.easybuy.user.entity.StoreMembership} and resolved by
 * {@code RBACGuardService}. A global {@link #MANAGER}/{@link #CONTENT_MANAGER}
 * authority grants nothing on its own; it requires an active membership record.
 *
 * @see StoreMembership
 * @see StoreMembershipRole
 */
public enum Authority {

    /** Technical marker that the account is authenticated. Carries no business rights on its own. */
    USER,

    /** Buyer: search, cart, checkout, order history, Stripe payments, return initiation. */
    CUSTOMER,

    /** Shop owner: CRUD of own shop(s) and products, own-shop analytics, delegation of managers. */
    SELLER,

    /** Store-scoped: limited product CRUD inside the shop(s) the owner attached them to. */
    MANAGER,

    /** Store-scoped: moderation of product descriptions and media. No financial access. */
    CONTENT_MANAGER,

    /** Customer support: disputes, order and return statuses. */
    SUPPORT,

    /** Read-only analyst: global data, sales statistics, marketplace reports. */
    ANALYST,

    /** Finance: transaction control, seller payout approval, payment-provider API. */
    FINANCIER,

    /** Moderator: block/unblock of users, shops and products via soft delete / restore. */
    MODERATOR,

    /** Super administrator: platform settings, commissions, assignment of administrative roles. */
    SUPER_ADMIN
}
