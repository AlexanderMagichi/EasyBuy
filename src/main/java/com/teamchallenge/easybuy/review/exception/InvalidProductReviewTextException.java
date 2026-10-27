package com.teamchallenge.easybuy.review.exception;

public class InvalidProductReviewTextException extends RuntimeException {
    public InvalidProductReviewTextException() {
        super("The Product Review Text Is Invalid.");
    }
}
