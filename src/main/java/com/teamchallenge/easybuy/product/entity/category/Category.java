package com.teamchallenge.easybuy.product.entity.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a hierarchical category in the marketplace.
 * A category can have subcategories and a list of attributes specific to this category.
 */
@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_name", columnList = "name", unique = true)
})
/**
 * Represents the Category persistence entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Category entity supporting hierarchical structure and attributes")
public class Category {

    @Id
    @NotNull
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @Schema(description = "Unique ID of the category", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID id;

    //todo maybe create non-unique
    @NotNull
    @Column(nullable = false, unique = true)
    @Schema(description = "Name of the category", example = "Men's Jackets")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Schema(description = "Parent category if this is a subcategory", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "Subcategories of this category")
    private List<Category> subcategories = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "List of attributes assigned to this category")
    private List<CategoryAttribute> attributes = new ArrayList<>();
}
