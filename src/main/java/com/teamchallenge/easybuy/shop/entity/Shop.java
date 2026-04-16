package com.teamchallenge.easybuy.shop.entity;

import com.teamchallenge.easybuy.common.persistence.BaseEntity;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.user.entity.Manager;
import com.teamchallenge.easybuy.user.entity.Seller;
import com.teamchallenge.easybuy.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AllArgsConstructor
@ToString(exclude = {"goods", "shopContactInfo", "shopBillingInfo", "shopTaxInfo", "shopAnalytics", "moderationHistory", "shopManagers", "seoSettings", "seller", "moderatedByUser"})
@Schema(description = "Base information for a shop.")
@Table(name = "shops", indexes = {
        @Index(name = "idx_shops_slug", columnList = "slug"),
        @Index(name = "idx_shops_status", columnList = "shop_status"),
        @Index(name = "idx_shops_featured", columnList = "is_featured"),
        @Index(name = "idx_shop_sellerId", columnList = "seller_id"),
        @Index(name = "idx_shop_shopId", columnList = "shop_id"),
        @Index(name = "idx_shop_name", columnList = "shop_name")
})
/**
 * Represents the Shop persistence entity.
 */
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "shop_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    @Schema(description = "Unique shop ID. Just for database. Read only", example = "e3b0c442-98fc-4629-8b9c-a5de62ed1df1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID shopId;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "shop_name", nullable = false, unique = true, length = 100)
    @Schema(description = "Name of the shop. Max Length = 100.", example = "MyStore",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100, minLength = 1)
    private String shopName;

    @Lob
    @NotNull
    @Size(max = 1000)
    @Column(name = "shop_description", nullable = false, length = 1000)
    @Schema(description = "Description of the shop. Max Length = 1000.", example = "Leading seller of Shoe and Dress.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopDescription;

    @NotNull
    @Column(name = "shop_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Status of the shop", example = "ACTIVE")
    private ShopStatus shopStatus;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    @Schema(description = "Indicates if the shop is featured or recommended on the main page.", example = "false")
    private boolean isFeatured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    @Schema(description = "Owner of the shop (creator). Links to Seller entity.",
            example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Seller seller;

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShopBillingInfo shopBillingInfo;

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShopTaxInfo shopTaxInfo;

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShopAnalytics shopAnalytics;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "List of goods available in your store")
    private List<Goods> goods = new ArrayList<>();

    @DecimalMin(value = "0.0000")
    @DecimalMax(value = "1.0000")
    @Column(name = "commission_rate", precision = 5, scale = 4)
    @Schema(description = "Commission rate taken by marketplace (e.g., 0.05 for 5%)", example = "0.05")
    private BigDecimal commissionRate;

    @Column(name = "last_activity_at")
    @Schema(description = "Last activity timestamp for the shop (for example, remaining delivery of goods, processing orders)",
            example = "2025-06-27T10:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastActivityAt;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the store is verified by the marketplace (Like ID, TAX, Billing)", example = "false")
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_type")
    @Schema(description = "Type of store, e.g. Sale, Manufacturer, Reseller", example = "Producer")
    private ShopType shopType;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    @Schema(description = "Reason for rejection of the store", example = "Store is not suitable for selling goods")
    private String rejectionReason;

    @Lob
    @Column(name = "moderator_notes")
    @Schema(description = "Internal notes from admins/moderators regarding the store. Not visible to customers.",
            example = "Re-verification required in 3 months.")
    private String moderatorNotes;

    @Column(name = "last_moderated_at")
    @Schema(description = "Last time the store was moderated by an admin. Read only.", example = "2025-06-27T10:00:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastModeratedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderated_by_user_id")
    @Schema(description = "Admin user who moderated the store. Read only.",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY)
    private User moderatedByUser;

    @NotBlank
    @Column(name = "currency", nullable = false, length = 5)
    @Builder.Default
    @Size(max = 5, message = "Currency code must not exceed 5 characters")
    @Schema(description = "Store currency code ISO 4217", example = "UAH", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency = "UAH";

    @NotBlank
    @Column(name = "language", nullable = false, length = 5)
    @Builder.Default
    @Size(max = 5, message = "Language code must not exceed 5 characters")
    @Schema(description = "Primary language of the shop (ISO 639-1)", example = "uk",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String language = "uk";

    @NotBlank
    @Column(name = "timezone", nullable = false, length = 50)
    @Builder.Default
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    @Schema(description = "Timezone of the shop (IANA Time Zone Database)", example = "Europe/Kyiv",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String timezone = "Europe/Kyiv";

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "Shop contact information record")
    private ShopContactInfo shopContactInfo;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "History of moderation actions for this shop")
    private List<ShopModerationHistory> moderationHistory = new ArrayList<>();

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "SEO settings for the shop")
    private ShopSeoSettings seoSettings;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "List of manager link records for this shop")
    private List<Manager> shopManagers = new ArrayList<>();

    // Lifecycle callbacks

    public void addGood(Goods good) {
        if (good == null) return;
        this.goods.add(good);
        good.setShop(this);
    }

    public void removeGood(Goods good) {
        if (good == null) return;
        this.goods.remove(good);
        good.setShop(null);
    }



    public void addModerationRecord(ShopModerationHistory record) {
        if (record == null) return;
        this.moderationHistory.add(record);
        record.setShop(this);
    }

    public void removeModerationRecord(ShopModerationHistory record) {
        if (record == null) return;
        this.moderationHistory.remove(record);
        record.setShop(null);
    }

    public void addManager(Manager manager) {
        if (manager == null) return;
        this.shopManagers.add(manager);
        manager.setShop(this);
    }

    public void removeManager(Manager manager) {
        if (manager == null) return;
        this.shopManagers.remove(manager);
        manager.setShop(null);
    }

    public void setSeoSettings(ShopSeoSettings seoSettings) {
        // detach previous
        if (this.seoSettings != null) {
            this.seoSettings.setShop(null);
        }
        this.seoSettings = seoSettings;
        if (seoSettings != null) {
            seoSettings.setShop(this);
        }
    }

    public void setShopContactInfo(ShopContactInfo shopContactInfo) {
        if (this.shopContactInfo != null) {
            this.shopContactInfo.setShop(null);
        }
        this.shopContactInfo = shopContactInfo;
        if (shopContactInfo != null) {
            shopContactInfo.setShop(this);
        }
    }

    public void setShopBillingInfo(ShopBillingInfo shopBillingInfo) {
        if (this.shopBillingInfo != null) {
            this.shopBillingInfo.setShop(null);
        }
        this.shopBillingInfo = shopBillingInfo;
        if (shopBillingInfo != null) {
            shopBillingInfo.setShop(this);
        }
    }

    public void setShopTaxInfo(ShopTaxInfo shopTaxInfo) {
        if (this.shopTaxInfo != null) {
            this.shopTaxInfo.setShop(null);
        }
        this.shopTaxInfo = shopTaxInfo;
        if (shopTaxInfo != null) {
            shopTaxInfo.setShop(this);
        }
    }

    public void setShopAnalytics(ShopAnalytics shopAnalytics) {
        if (this.shopAnalytics != null) {
            this.shopAnalytics.setShop(null);
        }
        this.shopAnalytics = shopAnalytics;
        if (shopAnalytics != null) {
            shopAnalytics.setShop(this);
        }
    }

    public enum ShopStatus {
        @Schema(description = "Store is active and available for buyers")
        ACTIVE,
        @Schema(description = "Store is inactive and temporarily unavailable")
        INACTIVE,
        @Schema(description = "Store is awaiting administrator review before activation")
        PENDING,
        @Schema(description = "Store is blocked due to policy violations")
        BANNED,
        @Schema(description = "Store is rejected due to policy violations")
        REJECTED
    }

    public enum ShopType {
        @Schema(description = "A store that sells goods to other users")
        RETAILER,
        @Schema(description = "A store that manufactures goods itself")
        PRODUCER,
        @Schema(description = "A store that resells goods from other manufacturers")
        RESELLER,
        @Schema(description = "Another, unclassified type of store")
        OTHER
    }
}
