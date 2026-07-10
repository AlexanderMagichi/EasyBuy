package com.teamchallenge.easybuy.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for advanced SEO settings to ensure Google search visibility and indexing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SEO configuration for shop discovery and adaptation")
public class ShopSeoSettingsDTO {

    @Schema(description = "Shop ID for SEO settings (read-only)",
            example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Size(max = 60)
    @Schema(description = "Meta title for search engines", example = "Best Coffee Shop in Kyiv")
    private String metaTitle;

    @Size(max = 160)
    @Schema(description = "Meta description for SEO snippets", example = "Organic coffee beans delivered to your door.")
    private String metaDescription;

    @Size(max = 255)
    @Schema(description = "Keywords for indexing", example = "coffee, organic, delivery, kyiv")
    private String metaKeywords;

    @Size(max = 255)
    @Schema(description = "Canonical URL to avoid duplicate content")
    private String canonicalUrl;

    @Size(max = 255)
    @Schema(description = "Open Graph URL for social media sharing")
    private String ogUrl;

    @Size(max = 50)
    @Schema(description = "Open Graph type, usually website")
    private String ogType;

    @Size(max = 255)
    @Schema(description = "Open Graph site name")
    private String ogSiteName;

    @Size(max = 20)
    @Schema(description = "Open Graph locale, for example uk_UA")
    private String ogLocale;

    @Size(max = 255)
    @Schema(description = "Open Graph title for social media sharing")
    private String ogTitle;

    @Size(max = 300)
    @Schema(description = "Open Graph description for social media sharing")
    private String ogDescription;

    @Size(max = 255)
    @Schema(description = "Open Graph image URL for social media sharing")
    private String ogImageUrl;

    @Size(max = 255)
    @Schema(description = "Secure Open Graph image URL for HTTPS sharing")
    private String ogImageSecureUrl;

    @Size(max = 255)
    @Schema(description = "Alternative text for the Open Graph image")
    private String ogImageAlt;

    @Schema(description = "JSON-LD structured data")
    private String structuredDataJson;

    @Size(max = 255)
    @Schema(description = "Robots directive for indexing")
    private String robotsDirective;

    @Schema(description = "Whether page is indexable")
    private Boolean indexable;

    @Schema(description = "Whether links are followable")
    private Boolean followable;

    @Schema(description = "Hreflang tags in JSON format")
    private String hreflangTags;

    @Schema(description = "Alternate language links in JSON format")
    private String alternateLanguages;

    @Schema(description = "Custom meta tags in HTML format")
    private String customMetaTags;

    @Schema(description = "Custom scripts for analytics or pixels")
    private String customScripts;

    @Schema(description = "Calculated SEO score (read-only)",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer seoScore;

    @Schema(description = "Last SEO audit timestamp (read-only)",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastSeoAudit;

    @Schema(description = "SEO optimization flag (read-only)",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean seoOptimized;

    @Schema(description = "Creation timestamp (read-only)",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Schema(description = "Last update timestamp (read-only)",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;
}