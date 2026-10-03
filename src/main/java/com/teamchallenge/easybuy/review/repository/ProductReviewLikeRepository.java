package com.teamchallenge.easybuy.review.repository;

import com.teamchallenge.easybuy.review.entity.ProductReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductReviewLikeRepository extends JpaRepository<ProductReviewLike, UUID> {

    Optional<ProductReviewLike> findByUserIdAndProductReviewId(UUID userId, UUID reviewId);

    void deleteByProductIdAndProductReviewId(final UUID productId,
                                             final UUID productReviewId);
}
