package com.teamchallenge.easybuy.product.entity.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Represents an attribute definition for a category, such as 'Color', 'Size', etc.
 */
@Entity
@Table(name = "category_attributes", indexes = {
        @Index(name = "idx_category_attributes_category_id", columnList = "category_id")
})
/**
 * Represents the CategoryAttribute persistence entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Attribute definition for a category")
public class CategoryAttribute {

    @Id
    @NotNull
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @Schema(description = "Unique ID of the attribute", example = "63f1c7de-89f1-44a3-8288-8bff5f9ad47a")
    private UUID id;

    @NotNull
    @Column(nullable = false)
    @Schema(description = "Attribute name", example = "Color")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Type of the attribute", example = "STRING")
    private AttributeType type;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "Category this attribute belongs to", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private Category category;
}
