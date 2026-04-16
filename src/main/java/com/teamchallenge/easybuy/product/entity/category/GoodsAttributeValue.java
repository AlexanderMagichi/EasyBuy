package com.teamchallenge.easybuy.product.entity.category;

import com.teamchallenge.easybuy.product.entity.Goods;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents the value of a specific attribute for a product (goods),
 * supporting multiple data types.
 */
@Entity
@Table(name = "goods_attributes", indexes = {
        @Index(name = "idx_goods_attributes_goods_id", columnList = "goods_id"),
        @Index(name = "idx_goods_attributes_attribute_id", columnList = "attribute_id")
})
/**
 * Represents the GoodsAttributeValue persistence entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Value assigned to a goods' attribute")
public class GoodsAttributeValue {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @Schema(description = "Unique ID of the attribute value", example = "9f5f6212-3e18-4a4f-809c-f9fd1fc4e1e9")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id", nullable = false)
    @Schema(description = "Goods that owns this attribute value")
    private Goods goods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    @Schema(description = "Attribute definition this value belongs to")
    private CategoryAttribute attribute;

    @Column(name = "value_string")
    private String valueString;

    @Column(name = "value_number", precision = 12, scale = 2)
    private BigDecimal valueNumber;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    @Column(name = "value_enum")
    private String valueEnum;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    @Transient
    public Object getActualValue() {
        return switch (attribute.getType()) {
            case STRING -> valueString;
            case NUMBER -> valueNumber;
            case BOOLEAN -> valueBoolean;
            case ENUM -> valueEnum;
        };
    }
}
