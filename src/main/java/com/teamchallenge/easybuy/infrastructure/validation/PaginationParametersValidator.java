package com.teamchallenge.easybuy.infrastructure.validation;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

/**
 * Service responsible for validating pagination and sorting parameters
 * used in API requests.
 */
@Service
public class PaginationParametersValidator {

    private static final Set<String> ALLOWED_SORT_DIRECTION_VALUES = Set.of("asc", "desc");

    /**
     * Validates pagination and sorting parameters.
     *
     * @param pageNumber                 The requested page number.
     * @param pageSize                   The requested page size.
     * @param sortAttribute              The field to sort by.
     * @param sortDirection              The direction of sorting ("asc" or "desc").
     * @param allowedSortAttributeValues The set of allowed fields for sorting.
     * @return A StringBuilder containing accumulated error messages, or an empty builder if valid.
     */
    public StringBuilder validate(final Integer pageNumber,
                                  final Integer pageSize,
                                  final String sortAttribute,
                                  final String sortDirection,
                                  final Set<String> allowedSortAttributeValues) {
        final StringBuilder errorMessages = new StringBuilder();

        // Validate Page Number
        if (pageNumber != null && pageNumber < 0) {
            String errorMessage = String.format("'%s' is an incorrect 'PageNumber' attribute value. " +
                    "'PageNumber' value should be a non-negative integer.", pageNumber);
            errorMessages.append(createErrorMessage(errorMessage));
        }

        // Validate Page Size
        if (pageSize == null) {
            errorMessages.append(createErrorMessage("PageSize value should not be null or empty. Please provide a numeric value."));
        } else if (pageSize < 1) {
            String errorMessage = String.format("'%s' is an incorrect 'PageSize' attribute value. " +
                    "'PageSize' value should be a positive integer greater than or equal to 1.", pageSize);
            errorMessages.append(createErrorMessage(errorMessage));
        }

        // Validate Sort Attribute
        if (sortAttribute != null && !allowedSortAttributeValues.contains(sortAttribute)) {
            String errorMessage = String.format("'%s' is an incorrect 'sortAttribute' value. Allowed 'sortAttribute' values are '%s'.",
                    sortAttribute, allowedSortAttributeValues);
            errorMessages.append(createErrorMessage(errorMessage));
        }

        // Validate Sort Direction
        if (sortDirection != null && !ALLOWED_SORT_DIRECTION_VALUES.contains(sortDirection.toLowerCase(Locale.ROOT))) {
            String errorMessage = String.format("'%s' is an incorrect 'sortDirection' value. Allowed 'sortDirection' values are '%s'.",
                    sortDirection, ALLOWED_SORT_DIRECTION_VALUES);
            errorMessages.append(createErrorMessage(errorMessage));
        }

        return errorMessages;
    }

    /**
     * Helper to format error messages consistently.
     */
    private static String createErrorMessage(String errorMessage) {
        return String.format(" Error: { %s }. ", errorMessage);
    }
}