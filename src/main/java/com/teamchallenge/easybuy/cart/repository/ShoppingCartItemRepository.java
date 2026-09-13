package com.teamchallenge.easybuy.cart.repository;

import com.teamchallenge.easybuy.cart.entity.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, UUID> {
}