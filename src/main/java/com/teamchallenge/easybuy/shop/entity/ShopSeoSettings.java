package com.teamchallenge.easybuy.shop.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shop_seo_settings", indexes = {
        @Index(name = "idx_shop_seo_shop_id", columnList = "shop_id"),
        @Index(name = "idx_shop_seo_meta_title", columnList = "meta_title"),
        @Index(name = "idx_shop_seo_meta_keywords", columnList = "meta_keywords")
})
/**
 * Represents the ShopSeoSettings persistence entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Seo settings for a shop")
public class ShopSeoSettings {

    @Id
    @Column(name = "shop_id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the shop", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "shop_id", nullable = false, unique = true)
    @Schema(description = "The shop this seo settings belongs to", requiredMode = Schema.RequiredMode.REQUIRED)
    private Shop shop;

    @Size(max = 60, message = "The meta title must be between 1 and 60 characters long")
    @Column(name = "meta_title", length = 60)
    @Schema(description = "Meta title for the shop page (recommended: 50-60 characters)",
            example = "MyStore - Leading Shoes Retailer", maxLength = 60)
    private String metaTitle;


    @Size(max = 160, message = "Meta description must not exceed 160 characters")
    @Column(name = "meta_description", length = 160)
    @Schema(description = "Meta description for the shop page (recommended: 150-160 characters)",
            example = "Discover high-quality shoes at MyStore. Best prices, fast delivery, and excellent " +
                    "customer service.",
            maxLength = 160)
    private String metaDescription;

    @Size(max = 255, message = "Meta keywords must not exceed 255 characters")
    @Column(name = "meta_keywords", length = 255)
    @Schema(description = "Meta keywords for the shop page (comma-separated)",
            example = "dress, shoes, , costume, tie, shirt", maxLength = 255)
    private String metaKeywords;

    @Size(max = 255, message = "Canonical URL must not exceed 255 characters. Prevents content duplication")
    @Column(name = "canonical_url", length = 255)
    @Schema(description = "Canonical URL for the shop page",
            example = "https://example.com/shop/mystore", maxLength = 255)
    private String canonicalUrl;

    // Heading for sharing on Facebook, Telegram ...
    @Size(max = 255, message = "Open Graph title must not exceed 255 characters. Heading for sharing like Facebook, Telegram, ets. Open Graph protocol https://ogp.me/")
    @Column(name = "og_title", length = 255)
    @Schema(description = "Open Graph title for social media sharing",
            example = "MyStore - Your Trusted Partner", maxLength = 255)
    private String ogTitle;

    @Size(max = 300, message = "Open Graph description must not exceed 300 characters")
    @Column(name = "og_description", length = 300)
    @Schema(description = "Open Graph description for social media sharing",
            example = "Shop the best clothes at MyStore. Quality products, competitive prices, and exceptional service.",
            maxLength = 300)
    private String ogDescription;

    @Size(max = 255, message = "Open Graph image URL must not exceed 255 characters")
    @Column(name = "og_image_url", length = 255)
    @Schema(description = "Open Graph image URL for social media sharing",
            example = "https://example.com/images/mystore-og.jpg", maxLength = 255)
    private String ogImageUrl;

    @Size(max = 255, message = "Structured data JSON must not exceed 255 characters")
    @Column(name = "structured_data_json", columnDefinition = "TEXT")
    @Schema(description = "JSON-LD structured data for the shop",
            example = "{\"@context\":\"https://schema.org\",\"@type\":\"Store\",\"name\":\"MyStore\"}")
    private String structuredDataJson;

    @Size(max = 255, message = "Robots directive must not exceed 255 characters. ")
    @Column(name = "robots_directive", length = 255)
    @Schema(description = "Robots meta tag directive. Schema.org markup for search engines",
            example = "index, follow", maxLength = 255)
    private String robotsDirective;

    @Column(name = "is_indexable", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the shop page should be indexed by search engines", example = "true")
    private boolean isIndexable = true;

    @Column(name = "is_followable", nullable = false)
    @Builder.Default
    @Schema(description = "Whether search engines should follow links on the shop page", example = "true")
    private boolean isFollowable = true;

    @Size(max = 255, message = "Hreflang tags must not exceed 255 characters")
    @Column(name = "hreflang_tags", columnDefinition = "TEXT")
    @Schema(description = "Hreflang tags for internationalization (JSON format)",
            example = "[{\"lang\":\"en\",\"url\":\"https://example.com/en/shop\"},{\"lang\":\"uk\",\"url\":\"https://example.com/uk/shop\"}]")
    private String hreflangTags;

    @Size(max = 255, message = "Alternate languages must not exceed 255 characters")
    @Column(name = "alternate_languages", columnDefinition = "TEXT")
    @Schema(description = "Alternate language versions of the shop page (JSON format)",
            example = "[{\"lang\":\"en\",\"url\":\"https://example.com/en/shop\"},{\"lang\":\"uk\",\"url\":\"https://example.com/uk/shop\"}]")
    private String alternateLanguages;

    @Size(max = 1000, message = "Custom meta tags must not exceed 1000 characters")
    @Column(name = "custom_meta_tags", columnDefinition = "TEXT")
    @Schema(description = "Custom meta tags in HTML format",
            example = "<meta name=\"author\" content=\"MyStore Team\"><meta name=\"theme-color\" content=\"#ff0000\">")
    private String customMetaTags;

    @Size(max = 1000, message = "Custom scripts must not exceed 1000 characters")
    @Column(name = "custom_scripts", columnDefinition = "TEXT")
    @Schema(description = "Custom scripts for the shop page (Google Analytics, etc.)",
            example = "<script async src=\"https://www.googletagmanager.com/gtag/js?id=GA_MEASUREMENT_ID\"></script>")
    private String customScripts;

    // --- SEO analytics ---

    @Column(name = "seo_score")
    @Schema(description = "SEO score calculated based on completeness of settings (0-100)", example = "85")
    private Integer seoScore;

    @Column(name = "last_seo_audit")
    @Schema(description = "Last time SEO audit was performed", example = "2025-01-15T10:30:00Z")
    private Instant lastSeoAudit;

    @Column(name = "is_seo_optimized", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the shop SEO settings are optimized", example = "false")
    private boolean isSeoOptimized = false;

    // --- Timestamps ---

    @Column(name = "created_at", updatable = false)
    @Schema(description = "When the SEO settings were created", example = "2025-01-15T10:30:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Column(name = "updated_at")
    @Schema(description = "When the SEO settings were last updated", example = "2025-01-15T10:30:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    // --- Business methods ---

    /**
     * Calculate SEO score based on completeness of settings
     * Designed to automatically assess the completeness and quality of SEO settings for a specific store.
     * Provides a quick overview of the current status of SEO settings. This is useful for both administrators and users.
     * A clear indicator (SEO Score) can motivate users to fill in all the necessary SEO fields to achieve better
     * visibility in search engines.
     */
    public void calculateSeoScore() {
        int score = 0;
        int totalFields = 8; // Number of important SEO fields

        if (metaTitle != null && !metaTitle.trim().isEmpty()) score++;
        if (metaDescription != null && !metaDescription.trim().isEmpty()) score++;
        if (metaKeywords != null && !metaKeywords.trim().isEmpty()) score++;
        if (canonicalUrl != null && !canonicalUrl.trim().isEmpty()) score++;
        if (ogTitle != null && !ogTitle.trim().isEmpty()) score++;
        if (ogDescription != null && !ogDescription.trim().isEmpty()) score++;
        if (ogImageUrl != null && !ogImageUrl.trim().isEmpty()) score++;
        if (structuredDataJson != null && !structuredDataJson.trim().isEmpty()) score++;

        this.seoScore = (score * 100) / totalFields;
        this.isSeoOptimized = this.seoScore >= 80; // Consider optimized if score >= 80%
    }

    /**
     * Get robots directive based on indexable and followable flags
     */
    public String getRobotsDirective() {
        if (robotsDirective != null && !robotsDirective.trim().isEmpty()) {
            return robotsDirective;
        }

        StringBuilder directive = new StringBuilder();
        if (isIndexable) {
            directive.append("index");
        } else {
            directive.append("noindex");
        }

        directive.append(", ");

        if (isFollowable) {
            directive.append("follow");
        } else {
            directive.append("nofollow");
        }

        return directive.toString();
    }

    /**
     * Check if SEO settings are complete
     */
    public boolean isComplete() {
        return metaTitle != null && !metaTitle.trim().isEmpty() &&
                metaDescription != null && !metaDescription.trim().isEmpty() &&
                canonicalUrl != null && !canonicalUrl.trim().isEmpty();
    }

    /**
     * Get default meta title if not set
     */
    public String getDefaultMetaTitle() {
        if (metaTitle != null && !metaTitle.trim().isEmpty()) {
            return metaTitle;
        }
        return shop != null ? shop.getShopName() : "Shop";
    }

    /**
     * Get default meta description if not set
     */
    public String getDefaultMetaDescription() {
        if (metaDescription != null && !metaDescription.trim().isEmpty()) {
            return metaDescription;
        }
        return shop != null ? shop.getShopDescription() : "Shop description";
    }

    // --- Lifecycle methods ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.seoScore == null) {
            calculateSeoScore();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        if (this.seoScore == null) {
            calculateSeoScore();
        }
    }
}
