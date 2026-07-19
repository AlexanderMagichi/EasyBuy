package com.teamchallenge.easybuy.order.entity;


import com.teamchallenge.easybuy.infrastructure.audit.AuditableEntity;
import com.teamchallenge.easybuy.openapi.dto.OrderStatus;
import com.teamchallenge.easybuy.user.entity.Address;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "session_id", updatable = false, nullable = false)
    private String sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    private Address deliveryAddress;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_surname", nullable = false)
    private String recipientSurname;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "items_quantity", nullable = false)
    private Integer itemsQuantity;

    @Column(name = "items_total_price", nullable = false)
    private BigDecimal itemsTotalPrice;

    @PrePersist
    public void prePersist() {
        for (OrderItem orderItem : items) {
            orderItem.setOrderId(this.id);
        }
    }

    @Override
    public String toString() {
        return "Order {" +
                "id=" + id +
                '}';
    }
}
