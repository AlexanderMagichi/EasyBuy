package com.teamchallenge.easybuy.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "products_quantity", nullable = false)
    private Integer productsQuantity;

    @Override
    public String toString() {
        return "OrderItem {" +
                "id = " + id +
                '}';
    }
}
