package com.teamchallenge.easybuy.user.validator;

import com.teamchallenge.easybuy.openapi.dto.AddressDto;
import com.teamchallenge.easybuy.user.exception.PutUsersBadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Service responsible for comprehensive business-level validation of user profile update requests.
 * <p>
 * This validator aggregates multiple field errors into a single exception, providing
 * the client with a complete list of validation failures at once. It enforces rules
 * that are complex to handle via standard Jakarta Validation annotations, such as
 * age restrictions and specific international format constraints.
 */
@Service
@RequiredArgsConstructor
public class PutUsersRequestValidator {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 64;
    private static final String PHONE_REGEXP = "^\\+[1-9]\\d{6,14}$";
    private static final String PHONE_ERROR = "Phone must be in international E.164 format, e.g. +12025550123.";

    /**
     * Validates the provided user profile update parameters.
     * <p>
     * If any parameter violates the business rules, all error messages are collected
     * and thrown together inside a {@link PutUsersBadRequestException}.
     *
     * @param firstName   the user's first name (must be 2-64 chars, letters/spaces/hyphens only)
     * @param lastName    the user's last name (must be 2-64 chars, letters/spaces/hyphens only)
     * @param phoneNumber the user's phone number (must match E.164 format if present)
     * @param birthDate   the user's birth date (must be past date and age >= 13)
     * @param addressDto  the user's address details (fields must not be blank if present)
     * @throws PutUsersBadRequestException if validation fails for one or more fields
     */
    public void validate(String firstName,
                         String lastName,
                         String phoneNumber,
                         String birthDate,
                         AddressDto addressDto) {
        StringBuilder errorMessages = new StringBuilder();

        errorMessages.append(validateNameParameter(firstName, "First name"));
        errorMessages.append(validateNameParameter(lastName, "Last name"));
        errorMessages.append(validatePhoneParameter(phoneNumber));
        errorMessages.append(validateBirthDateParameter(birthDate));
        errorMessages.append(validateAddressParameter(addressDto));

        if (!errorMessages.isEmpty()) {
            throw new PutUsersBadRequestException(errorMessages.toString());
        }
    }

    /**
     * Validates name fields for length, blankness, and allowed characters.
     */
    private StringBuilder validateNameParameter(String name, String parameterTypeForErrorMessage) {
        StringBuilder errorMessages = new StringBuilder();
        if (name == null) {
            errorMessages.append(createErrorMessage(parameterTypeForErrorMessage + " is required."));
        } else if (name.isBlank()) {
            errorMessages.append(createErrorMessage(parameterTypeForErrorMessage + " must not be blank."));
        } else if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            errorMessages.append(createErrorMessage(String.format("%s must be between %d and %d characters.", parameterTypeForErrorMessage, MIN_NAME_LENGTH, MAX_NAME_LENGTH)));
        } else if (!name.matches("^[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u00FF\\s'\\u2019\\-]+$")) {
            errorMessages.append(createErrorMessage(parameterTypeForErrorMessage + " can only contain letters, spaces, hyphens, and apostrophes."));
        }
        return errorMessages;
    }

    /**
     * Validates that the phone number matches the international E.164 standard.
     */
    private StringBuilder validatePhoneParameter(String phoneNumber) {
        StringBuilder errorMessages = new StringBuilder();
        if (phoneNumber != null && !phoneNumber.isBlank() && !phoneNumber.matches(PHONE_REGEXP)) {
            errorMessages.append(createErrorMessage(PHONE_ERROR));
        }
        return errorMessages;
    }

    /**
     * Validates the birth date format and enforces a minimum age requirement of 13 years.
     */
    private StringBuilder validateBirthDateParameter(String birthDate) {
        StringBuilder errorMessages = new StringBuilder();
        if (birthDate != null && !birthDate.isBlank()) {
            try {
                LocalDate localDate = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
                if (!localDate.isBefore(LocalDate.now())) {
                    errorMessages.append(createErrorMessage("Date of birth must be in the past."));
                } else if (localDate.isAfter(LocalDate.now().minusYears(13))) {
                    errorMessages.append(createErrorMessage("You must be at least 13 years old."));
                }
            } catch (DateTimeParseException e) {
                errorMessages.append(createErrorMessage("Date of birth must be in format YYYY-MM-DD."));
            }
        }
        return errorMessages;
    }

    /**
     * Validates that non-null address fields are not blank.
     */
    private StringBuilder validateAddressParameter(AddressDto addressDto) {
        StringBuilder errorMessages = new StringBuilder();
        if (addressDto != null) {
            validateAddressField(errorMessages, addressDto.getCountry(), "country");
            validateAddressField(errorMessages, addressDto.getCity(), "city");
            validateAddressField(errorMessages, addressDto.getLine(), "line");
            validateAddressField(errorMessages, addressDto.getPostcode(), "postcode");
        }
        return errorMessages;
    }

    private void validateAddressField(StringBuilder errors, String value, String fieldName) {
        if (value != null && value.isBlank()) {
            errors.append(createErrorMessage(String.format("Address field `%s` must not be blank.", fieldName)));
        }
    }

    private String createErrorMessage(String errorMessage) {
        return String.format(" Error: { %s }. ", errorMessage);
    }
}