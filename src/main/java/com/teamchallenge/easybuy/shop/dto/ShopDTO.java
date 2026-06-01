package com.teamchallenge.easybuy.shop.dto;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.shop.dto.shopbillinginfo.ShopBillingInfoDTO;
import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for Shop entity, used in API responses and requests.")
public class ShopDTO {

    @Schema(description = "Unique identifier for the shop. Read-only.",
            example = "e3b0c442-98fc-4629-8b9c-a5de62ed1df1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID shopId;

    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "Name of the shop. Maximum 100 characters.",
            example = "MyStore",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 100)
    private String shopName;

    @NotBlank
    @Size(max = 1000)
    @Schema(description = "Description of the shop. Maximum 1000 characters.",
            example = "Leading seller of shoes and dresses.",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 1000)
    private String shopDescription;

    @NotNull
    @Schema(description = "Status of the shop.",
            example = "ACTIVE",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Shop.ShopStatus shopStatus;

    @Schema(description = "Flag indicating if the shop is featured or recommended.",
            example = "false")
    private boolean featured = false;

    @NotNull
    @Schema(description = "ID of the shop's seller (owner).",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID sellerId;

    @DecimalMin("0.0000")
    @DecimalMax("1.0000")
    @Schema(description = "Commission rate taken by the marketplace (0 to 1).",
            example = "0.05")
    private BigDecimal commissionRate;

    @Schema(description = "Timestamp of the last activity in the shop (read-only).",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastActivityAt;

    @Schema(description = "Flag indicating if the shop is verified (read-only).",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY)
    private boolean verified = false;

    @Schema(description = "Type of shop, e.g., Retailer, Producer, Reseller.",
            example = "PRODUCER")
    private Shop.ShopType shopType;

    @Schema(description = "Public URL to the shop's logo image.",
            example = "https://shop.com/logo.png")
    private String shopLogoUrl;

    @Size(min = 3, max = 100, message = "Slug must be between 3 and 100 characters long")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Schema(description = "URL-friendly identifier (slug) of the shop. Lowercase letters, numbers, and hyphens only.",
            example = "coffee-world-kyiv", minLength = 3, maxLength = 100)
    private String slug;

    @Schema(description = "Reason why the shop was rejected, if applicable.",
            example = "Store is not suitable for selling goods")
    private String rejectionReason;

    @Schema(description = "Internal moderator notes. Not visible to customers.",
            example = "Re-verification required in 3 months.",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String moderatorNotes;

    @Schema(description = "Timestamp of the last moderation (read-only).",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastModeratedAt;

    @Schema(description = "ID of the user (admin) who last moderated the shop (read-only).",
            example = "00000000-0000-0000-0000-000000000000",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID moderatedByUserId;

    @Schema(description = "SEO settings for the shop.")
    private ShopSeoSettingsDTO seoSettings;

    @NotBlank
    @Size(max = 5)
    @Schema(description = "Currency code (ISO 4217).",
            example = "UAH",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 5)
    private String currency;

    @NotBlank
    @Size(max = 5)
    @Schema(description = "Primary language of the shop (ISO 639-1).",
            example = "uk",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 5)
    private String language;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Timezone of the shop (IANA format).",
            example = "Europe/Kyiv",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 50)
    private String timezone;

    @Schema(description = "Optimistic lock version (read-only).",
            example = "0",
            accessMode = Schema.AccessMode.READ_ONLY)
    private long version;

    @Schema(description = "Timestamp when the shop was created (read-only).",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Schema(description = "Timestamp when the shop was last updated (read-only).",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    @Schema(description = "Contact information of the shop.",
            implementation = ShopContactInfoDTO.class)
    private ShopContactInfoDTO shopContactInfo;

    @Schema(description = "Stripe billing information for the shop.",
            implementation = ShopBillingInfoDTO.class)
    private ShopBillingInfoDTO shopBillingInfo;

    @Schema(description = "Tax and legal information for the shop.",
            implementation = ShopTaxInfoDTO.class)
    private ShopTaxInfoDTO shopTaxInfo;

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