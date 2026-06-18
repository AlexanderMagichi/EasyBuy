package com.teamchallenge.easybuy.email.message;

import com.teamchallenge.easybuy.email.dto.EmailTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Message builder implementation for email confirmation.
 * Retrieves the email template from the configured message source and populates it with token data.
 */
@Component
@RequiredArgsConstructor
public class EmailConfirmMessage implements MessageBuilder<EmailTokenDto> {

    private final MessageSource messageSource;

    /**
     * Builds the email message using the provided token data and locale.
     *
     * @param event  The data transfer object containing the token.
     * @param locale The locale for localization (e.g., US, RU).
     * @return The formatted email message string.
     */
    @Override
    public String buildMessage(EmailTokenDto event, Locale locale) {
        return messageSource.getMessage("email-template", new Object[]{event.token()}, locale);
    }

    /**
     * Checks if this builder supports the given class.
     *
     * @param clazz The class to check.
     * @return true if the builder supports the class, false otherwise.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == EmailConfirmMessage.class;
    }
}