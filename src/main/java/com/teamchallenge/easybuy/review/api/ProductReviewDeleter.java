package com.teamchallenge.easybuy.review.api;

import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.review.ai.ProductReviewSummaryDebouncer;
import com.teamchallenge.easybuy.review.repository.ProductReviewRepository;
import com.teamchallenge.easybuy.review.validator.ProductReviewValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service handling deletion of product reviews and updating related aggregates.
 */
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
    @Operation(summary = "Delete a product review", description = "Removes a product review and updates product aggregates")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
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
