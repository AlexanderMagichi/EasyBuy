package com.teamchallenge.easybuy.review.api;

import com.teamchallenge.easybuy.openapi.dto.ProductReviewRequest;
import com.teamchallenge.easybuy.openapi.dto.ProductReviewDto;
import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.review.ai.AsyncReviewProcessingService;
import com.teamchallenge.easybuy.review.ai.ProductReviewSummaryDebouncer;
import com.teamchallenge.easybuy.review.converter.ProductReviewDtoConverter;
import com.teamchallenge.easybuy.review.entity.ProductReview;
import com.teamchallenge.easybuy.review.repository.ProductReviewRepository;
import com.teamchallenge.easybuy.review.validator.ProductReviewValidator;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import com.teamchallenge.easybuy.user.api.SingleUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
/**
 * Service for creating product reviews.
 */
@Service
@RequiredArgsConstructor
public class ProductReviewCreator {

    private final ProductReviewRepository reviewRepository;
    private final ProductReviewDtoConverter productReviewDtoConverter;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final SingleUserProvider singleUserProvider;
    private final ProductReviewValidator productReviewValidator;
    private final GoodsRepository goodsRepository;
    private final AsyncReviewProcessingService asyncReviewProcessingService;
    private final ProductReviewSummaryDebouncer summaryDebouncer;

    /**
 * Creates a new product review and updates related aggregates.
 *
 * @param productId the identifier of the product being reviewed
 * @param productReviewRequest the request payload containing review text and rating
 * @return the created product review DTO
 */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductReviewDto create(final UUID productId,
                                   final ProductReviewRequest productReviewRequest) {
        var userId = securityPrincipalProvider.getUserId();
        var productReviewText = productReviewRequest.getText();

        productReviewValidator.validateProductExists(productId);
        productReviewValidator.validateReviewText(productReviewText);
        productReviewValidator.validateReviewExistsForUser(userId, productId);

        var productReview = ProductReview.builder()
                .user(singleUserProvider.getUserEntityById(userId))
                .productId(productId)
                .text(productReviewText.trim())
                .productRating(productReviewRequest.getRating())
                .likesCount(0)
                .dislikesCount(0)
                .build();

        reviewRepository.saveAndFlush(productReview);

        asyncReviewProcessingService.process(productReview.getId(), productReviewText.trim());
        summaryDebouncer.schedule(productId);

        goodsRepository.updateAverageRating(productId);
        goodsRepository.updateReviewsCount(productId);

        return productReviewDtoConverter.toProductReviewDto(productReview);
    }
}
