package com.teamchallenge.easybuy.shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Entity for Stripe billing info.
 * Uses Shared Primary Key pattern to link with Shop entity.
 */
@Entity
@Table(name = "shop_billing_info", indexes = {
        @Index(name = "idx_stripe_account", columnList = "stripe_account_id")
})
/**
 * Represents the ShopBillingInfo persistence entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopBillingInfo {

    @Id
    @Column(name = "shop_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "shop_id")
    @ToString.Exclude
    private Shop shop;

    @Column(name = "stripe_account_id", unique = true)
    private String stripeAccountId;

    @Builder.Default
    @Column(name = "payouts_enabled", nullable = false)
    private boolean payoutsEnabled = false;

    @Column(name = "billing_email")
    private String billingEmail;

    @Builder.Default
    @Column(name = "default_currency", length = 3, nullable = false)
    private String defaultCurrency = "USD";
}
