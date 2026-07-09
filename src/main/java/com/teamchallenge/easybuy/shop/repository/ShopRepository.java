package com.teamchallenge.easybuy.shop.repository;

import com.teamchallenge.easybuy.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID>, JpaSpecificationExecutor<Shop> {

    boolean existsByShopName(String shopName);

    boolean existsByShopNameIgnoreCase(String shopName);

    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"seller", "moderatedByUser"})
    Page<Shop> findBySellerId(UUID sellerId, Pageable pageable);

    boolean existsByShopIdAndSeller_Id(UUID shopId, UUID sellerId);

    @Override
    @EntityGraph(attributePaths = {"seller", "moderatedByUser", "shopContactInfo", "seoSettings"})
    Page<Shop> findAll(Specification<Shop> spec, Pageable pageable);


    @Query("SELECT s FROM Shop s JOIN StoreMembership m ON s.shopId = m.storeId WHERE m.user.id = :userId")
    Page<Shop> findShopsByUserId(@Param("userId") UUID userId, Pageable pageable);

}