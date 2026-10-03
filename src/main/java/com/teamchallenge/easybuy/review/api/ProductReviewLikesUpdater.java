package com.teamchallenge.easybuy.review.api;

import com.teamchallenge.easybuy.openapi.dto.ProductReviewDto;
import com.teamchallenge.easybuy.review.converter.ProductReviewDtoConverter;
import com.teamchallenge.easybuy.review.entity.ProductReview;
import com.teamchallenge.easybuy.review.entity.ProductReviewLike;
import com.teamchallenge.easybuy.review.repository.ProductReviewLikeRepository;
import com.teamchallenge.easybuy.review.repository.ProductReviewRepository;
import com.teamchallenge.easybuy.review.validator.ProductReviewValidator;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
/**
 * Service responsible for handling like/dislike updates on product reviews.
 * It ensures proper validation, updates like/dislike counters, and returns the updated review DTO.
 */
@RequiredArgsConstructor
public class ProductReviewLikesUpdater {

    private final ProductReviewLikeRepository productReviewLikeRepository;
    private final ProductReviewRepository productReviewRepository;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final ProductReviewDtoConverter productReviewDtoConverter;
    private final ProductReviewValidator productReviewValidator;
    private final ProductReviewProvider productReviewProvider;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductReviewDto update(final UUID productId,
                                   final UUID productReviewId,
                                   final Boolean newProductReviewLike) {
        var userId = securityPrincipalProvider.getUserId();

        productReviewValidator.validateProductIdIsValid(productId, productReviewId);

        Optional<ProductReviewLike> productReviewLike = productReviewLikeRepository.findByUserIdAndProductReviewId(userId, productReviewId);

        productReviewLike.ifPresentOrElse(
                productReviewLikeEntity -> {
                    if (productReviewLikeEntity.getIsLike().equals(newProductReviewLike)) {
                        productReviewLikeRepository.deleteByProductIdAndProductReviewId(productId, productReviewId);
                    } else {
                        productReviewLikeEntity.setIsLike(newProductReviewLike);
                        productReviewLikeRepository.saveAndFlush(productReviewLikeEntity);
                    }
                },
                () -> {
                    ProductReviewLike newReviewLike = ProductReviewLike.builder()
                            .userId(userId)
                            .productId(productId)
                            .productReviewId(productReviewId)
                            .isLike(newProductReviewLike)
                            .build();
                    productReviewLikeRepository.saveAndFlush(newReviewLike);
                }
        );

        productReviewRepository.updateLikesCount(productReviewId);
        productReviewRepository.updateDislikesCount(productReviewId);

        ProductReview productReview = productReviewProvider.getReviewEntityById(productReviewId);
        return productReviewDtoConverter.toProductReviewDto(productReview);
    }
}
