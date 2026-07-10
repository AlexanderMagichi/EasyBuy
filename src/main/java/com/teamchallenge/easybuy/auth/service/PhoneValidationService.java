package com.teamchallenge.easybuy.auth.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.springframework.stereotype.Service;

/**
 * Provides business operations for PhoneValidationService.
 */
@Service
public class PhoneValidationService {
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public String formatToE164(String phoneNumber) {
        try {
            PhoneNumber number = phoneNumberUtil.parse(phoneNumber, null);
            return phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number: " + e.getMessage());
        }
    }
}
