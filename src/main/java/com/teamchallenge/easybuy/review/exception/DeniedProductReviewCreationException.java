package com.teamchallenge.easybuy.review.exception;

import java.util.UUID;

public class DeniedProductReviewCreationException extends RuntimeException {
    public DeniedProductReviewCreationException(final UUID userId, final UUID productId, final UUID productReviewId) {
        super(String.format("Creation of the product's review for the user with userId = '%s' and the product with productId = '%s' is denied. " +
                        "Delete the previous product's review '%s' first.", userId, productId, productReviewId));
    }
}
