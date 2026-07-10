package com.teamchallenge.easybuy.cart.repository;

import java.util.Optional;
import java.util.UUID;

import com.teamchallenge.easybuy.cart.entity.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, UUID> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"items", "items.productInfo"})
    Optional<ShoppingCart> findShoppingCartByUserId(UUID userId);

    @Modifying(flushAutomatically = true)
    void deleteByUserId(UUID userId);
}