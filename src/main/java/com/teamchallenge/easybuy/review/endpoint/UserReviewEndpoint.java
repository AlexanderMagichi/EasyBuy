package com.teamchallenge.easybuy.review.endpoint;

import com.teamchallenge.easybuy.openapi.dto.ProductReviewsAndRatingsWithPagination;
import com.teamchallenge.easybuy.review.api.ProductReviewsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "User Review", description = "Endpoints for retrieving user reviews")
/**
 * REST controller providing user-specific product review listings.
 */
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class UserReviewEndpoint {

    private final ProductReviewsProvider productReviewsProvider;

    @Operation(summary = "Get reviews for the current user", description = "Returns paginated product reviews written by the authenticated user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User reviews retrieved"), @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")})
    public ResponseEntity<ProductReviewsAndRatingsWithPagination> getUserReviews(
            @RequestParam(name = "page", required = false) final Integer pageNumber,
            @RequestParam(name = "size", required = false) final Integer pageSize,
            @RequestParam(name = "sort_attribute", required = false) final String sortAttribute,
            @RequestParam(name = "sort_direction", required = false) final String sortDirection) {
        return ResponseEntity.ok(productReviewsProvider.getUserReviews(pageNumber, pageSize, sortAttribute, sortDirection));
    }
}
