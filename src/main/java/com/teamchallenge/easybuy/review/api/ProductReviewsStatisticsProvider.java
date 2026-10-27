package com.teamchallenge.easybuy.review.api;

import com.teamchallenge.easybuy.openapi.dto.ProductReviewRatingStats;
import com.teamchallenge.easybuy.openapi.dto.RatingMap;
import com.teamchallenge.easybuy.review.converter.ProductReviewDtoConverter;
import com.teamchallenge.easybuy.review.dto.ProductRatingCount;
import com.teamchallenge.easybuy.review.repository.ProductReviewRepository;
import com.teamchallenge.easybuy.review.validator.ProductReviewValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewsStatisticsProvider {

    private final ProductReviewRepository reviewRepository;
    private final ProductReviewDtoConverter productReviewDtoConverter;
    private final ProductReviewValidator productReviewValidator;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ProductReviewRatingStats get(final UUID productId) {
        productReviewValidator.validateProductExists(productId);

        Double avg = reviewRepository.getAvgRatingByProductId(productId);
        double avgRating = avg != null ? avg : 0.0;
        return new ProductReviewRatingStats(productId,
                Math.round(avgRating * 10.0) / 10.0,
                reviewRepository.getReviewCountProductById(productId),
                getProductRatingMap(productId));
    }

    private RatingMap getProductRatingMap(UUID productId) {
        List<ProductRatingCount> productRatingCountPairs = reviewRepository.getRatingsMapByProductId(productId);
        if (productRatingCountPairs == null) {
            return new RatingMap();
        }
        return productReviewDtoConverter.convertToProductRatingMap(productRatingCountPairs);
    }
}
