package com.teamchallenge.easybuy.user.entity;

import com.teamchallenge.easybuy.user.entity.Address;
import com.teamchallenge.easybuy.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Represents the Seller persistence entity.
 */
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Seller extends User {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;
}
