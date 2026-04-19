package com.teamchallenge.easybuy.cart.entity;

import com.teamchallenge.easybuy.cart.ShoppingCart;
import com.teamchallenge.easybuy.product.entity.Goods;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Entity that represents one goods position in a shopping cart.
 */
@Entity
@Table(name = "shopping_cart_item")
@Schema(description = "Single line item in a shopping cart")
public class ShoppingCartItem {

    /** Unique shopping cart item identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Shopping cart item ID", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    /**
     * Optimistic lock version to prevent lost updates on concurrent quantity changes.
     */
    @Version
    @Schema(description = "Optimistic lock version", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer version;

    /** Parent shopping cart reference. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    @Schema(description = "Parent shopping cart", requiredMode = Schema.RequiredMode.REQUIRED)
    private ShoppingCart shoppingCart;

    /** Goods selected by customer. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goods_id", nullable = false)
    @Schema(description = "Selected goods", requiredMode = Schema.RequiredMode.REQUIRED)
    private Goods goods;

    /** Quantity of selected goods. */
    @Column(name = "goods_quantity", nullable = false)
    @Schema(description = "Quantity of goods in this cart item", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer goodsQuantity;

    /**
     * Creates cart item with required business fields.
     *
     * @param id cart item identifier
     * @param shoppingCart parent shopping cart
     * @param goods selected goods
     * @param goodsQuantity quantity of selected goods
     */
    public ShoppingCartItem(UUID id, ShoppingCart shoppingCart, Goods goods, Integer goodsQuantity) {
        this.id = id;
        this.shoppingCart = shoppingCart;
        this.goods = goods;
        this.goodsQuantity = goodsQuantity;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (object == null || getClass() != object.getClass())
            return false;

        ShoppingCartItem that = (ShoppingCartItem) object;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(goods, that.goods)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(goods)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ShoppingCartItem {" +
                "id = " + id +
                '}';
    }
}