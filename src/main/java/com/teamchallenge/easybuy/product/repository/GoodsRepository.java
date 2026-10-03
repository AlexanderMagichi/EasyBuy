package com.teamchallenge.easybuy.product.repository;

import com.teamchallenge.easybuy.product.entity.Goods;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GoodsRepository extends JpaRepository<Goods, UUID>, JpaSpecificationExecutor<Goods> {
    boolean existsByArt(String art);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE goods
            SET rating = COALESCE((
                SELECT CAST(ROUND(AVG(pr.rating), 0) AS INTEGER)
                FROM product_reviews pr
                WHERE pr.product_id = :goodsId
            ), 0)
            WHERE id = :goodsId
            """, nativeQuery = true)
    void updateAverageRating(@Param("goodsId") UUID goodsId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE goods
            SET "reviewsCount" = COALESCE((
                SELECT COUNT(*)
                FROM product_reviews pr
                WHERE pr.product_id = :goodsId
            ), 0)
            WHERE id = :goodsId
            """, nativeQuery = true)
    void updateReviewsCount(@Param("goodsId") UUID goodsId);
}