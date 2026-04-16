package com.teamchallenge.easybuy.product.entity;

import com.teamchallenge.easybuy.product.entity.category.Category;
import com.teamchallenge.easybuy.shop.entity.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

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
@AllArgsConstructor
@Table(name = "goods", indexes = {
        @Index(name = "idx_goods_art", columnList = "art"),
        @Index(name = "idx_goods_shopId", columnList = "shop_id")
})
/**
 * Represents the Goods persistence entity.
 */
@Schema(description = "Goods entity used to display goods in the catalog.")
public class Goods {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the product. Just for database. Read only", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotNull
    @Column(name = "art", nullable = false)
    @Schema(description = "Article number of the product", example = "ART-00123")
    private String art;

    @NotNull
    @Column(name = "name", nullable = false)
    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String name;

    @Lob
    @Column(name = "description")
    @Schema(description = "Detailed description of the product", example = "A high-precision wireless mouse with ergonomic design.")
    private String description;
    // todo chfnge to int and add converter servises
    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Price of the product", example = "1499.99")
    private BigDecimal price;

    @NotNull
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
    @Column(name = "mainImageUrl", nullable = false)
    @Schema(description = "Main image URL of the product", example = "https://example.com/images/product123.jpg")
    private String mainImageUrl;

    @NotNull
    @Column(name = "stock", nullable = false)
    @Schema(description = "Current stock level", example = "120")
    private Integer stock;

    @Column(name = "reviewsCount")
    @Schema(description = "Number of reviews for the product", example = "45")
    private Integer reviewsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @NotNull
    @Schema(description = "The store that owns this product", requiredMode = Schema.RequiredMode.REQUIRED)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    @NotNull
    @Schema(description = "Category this product belongs to")
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "goodsStatus", nullable = false)
    @Schema(description = "Status of the product", example = "ACTIVE")
    private GoodsStatus goodsStatus;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "discountStatus", nullable = false)
    @Schema(description = "Discount status of the product", example = "NONE")
    private DiscountStatus discountStatus;
    @Column(name = "discountValue", precision = 10, scale = 2)
    @Schema(description = "Discount value if applicable", example = "10.00")
    private BigDecimal discountValue;
    @Column(name = "rating")
    @Schema(description = "Average rating of the product", example = "4")
    private Integer rating;
    @Column(name = "slug", unique = true)
    @Schema(description = "SEO-friendly slug for the product", example = "wireless-mouse-123")
    private String slug;
    @Column(name = "meta_title")
    @Schema(description = "Meta title for SEO", example = "Wireless Mouse - Best Price")
    private String metaTitle;
    @Column(name = "meta_description")
    @Schema(description = "Meta description for SEO", example = "High-quality wireless mouse at the best price.")
    private String metaDescription;
    @Column(name = "created_at", updatable = false)
    @Schema(description = "Timestamp when the product was created", example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;
    @Column(name = "updated_at")
    @Schema(description = "Timestamp when the product was last updated", example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;
    /**
     * List of additional images associated with this product.
     * Cascade operations are enabled. Orphan removal ensures deleted images are removed from DB.
     */
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "List of additional images for the product")
    private List<GoodsImage> additionalImages = new ArrayList<>();

    @PrePersist
    @Schema(hidden = true)
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    @Schema(hidden = true)
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum GoodsStatus {
        @Schema(description = "Product is active and available for purchase", example = "ACTIVE")
        ACTIVE,
        @Schema(description = "Product is inactive and not available", example = "INACTIVE")
        INACTIVE,
        @Schema(description = "Product is archived and no longer sold", example = "ARCHIVED")
        ARCHIVED
    }

    public enum DiscountStatus {
        @Schema(description = "No discount applied", example = "NONE")
        NONE,
        @Schema(description = "Discount is active", example = "ACTIVE")
        ACTIVE,
        @Schema(description = "Discount has expired", example = "EXPIRED")
        EXPIRED
    }
}
