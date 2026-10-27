package com.teamchallenge.easybuy.review.api;

import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.review.ai.ProductReviewSummaryDebouncer;
import com.teamchallenge.easybuy.review.repository.ProductReviewRepository;
import com.teamchallenge.easybuy.review.validator.ProductReviewValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service handling deletion of product reviews and updating related aggregates.
 */
@Service
@RequiredArgsConstructor
public class ProductReviewDeleter {

    private final ProductReviewRepository reviewRepository;
    private final ProductReviewValidator productReviewValidator;
    private final ProductReviewSummaryDebouncer summaryDebouncer;
    private final GoodsRepository goodsRepository;

    /**
     * Deletes a product review and updates the product's rating and review count.
     *
     * @param productId the ID of the product
     * @param productReviewId the ID of the review to delete
     */
    public void delete(final UUID productId,
                       final UUID productReviewId) {
        productReviewValidator.validateProductReviewDeletionAllowed(productReviewId);
        productReviewValidator.validateProductIdIsValid(productId, productReviewId);

        reviewRepository.deleteById(productReviewId);

        goodsRepository.updateAverageRating(productId);
        goodsRepository.updateReviewsCount(productId);

        summaryDebouncer.schedule(productId);
    }
}
