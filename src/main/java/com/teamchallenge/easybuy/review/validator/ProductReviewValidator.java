package com.teamchallenge.easybuy.review.validator;

import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.review.api.ProductReviewProvider;
import com.teamchallenge.easybuy.review.exception.DeniedProductReviewCreationException;
import com.teamchallenge.easybuy.review.exception.DeniedProductReviewDeletionException;
import com.teamchallenge.easybuy.review.exception.EmptyProductReviewException;
import com.teamchallenge.easybuy.review.exception.ProductNotFoundForReviewException;
import com.teamchallenge.easybuy.review.exception.ProductReviewNotFoundException;
import com.teamchallenge.easybuy.review.exception.InvalidProductReviewTextException;
import com.teamchallenge.easybuy.review.repository.ProductReviewRepository;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewValidator {

    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final ProductReviewProvider productReviewProvider;
    private final ProductReviewRepository productReviewRepository;
    private final GoodsRepository goodsRepository;

    private static final Pattern INVALID_REVIEW_TEXT_PATTERN = Pattern.compile("[<>{}\\[\\]|\\\\^~`]");

    /**
     * Check if the product review's text is not empty and contains no forbidden characters
     */
    public void validateReviewText(final String productReviewText) {
        if (productReviewText.trim().isEmpty()) {
            throw new EmptyProductReviewException();
        }
        if (INVALID_REVIEW_TEXT_PATTERN.matcher(productReviewText).find()) {
            throw new InvalidProductReviewTextException();
        }
    }

    /**
     * Check if the product exists
     */
    public void validateProductExists(final UUID productId) {
        var productInfo = goodsRepository.findById(productId);
        if (productInfo.isEmpty()) {
            throw new ProductNotFoundForReviewException(productId);
        }
    }

    /**
     * Check if the user has already created a review for this product
     */
    public void validateReviewExistsForUser(final UUID userId,
                                            final UUID productId) {
        var productReview = productReviewRepository.findByUserIdAndProductId(userId, productId);
        if (productReview.isPresent()) {
            throw new DeniedProductReviewCreationException(userId, productId, productReview.get().getId());
        }
    }

    /**
     * Check if the product's review deletion is allowed
     */
    public void validateProductReviewDeletionAllowed(final UUID productReviewId) {
        var currentUserId = securityPrincipalProvider.getUserId();
        var creatorId = productReviewProvider.getReviewEntityById(productReviewId).getUser().getId();

        if (!currentUserId.equals(creatorId)) {
            throw new DeniedProductReviewDeletionException(productReviewId, currentUserId);
        }
    }

    /**
     * Check if the product and review both exist
     */
    public void validateProductIdIsValid(final UUID productId, final UUID productReviewId) {
        if (!goodsRepository.existsById(productId)) {
            throw new ProductNotFoundForReviewException(productId);
        }
        if (!productReviewRepository.existsById(productReviewId)) {
            throw new ProductReviewNotFoundException(productReviewId);
        }
    }
}
