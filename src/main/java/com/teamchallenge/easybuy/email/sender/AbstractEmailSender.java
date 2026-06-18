package com.teamchallenge.easybuy.email.sender;

import com.teamchallenge.easybuy.email.exception.MessageBuilderNotFoundException;
import com.teamchallenge.easybuy.email.message.MessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Abstract base class for sending emails.
 * Provides common functionality for dispatching notifications using specific {@link MessageBuilder} implementations.
 *
 * @param <T> The type of event or DTO to be used in message building.
 */
@Service
@RequiredArgsConstructor
public abstract class AbstractEmailSender<T> {

    private final JavaMailSender javaMailSender;
    private final SimpleMailMessage mailMessage;
    private final List<MessageBuilder<T>> messageBuilders;

    /**
     * Sends a notification email to the specified recipient.
     *
     * @param email   The recipient's email address.
     * @param message The content of the email.
     * @param subject The subject line of the email.
     */
    public void sendNotification(String email, String message, String subject) {
        mailMessage.setTo(email);
        mailMessage.setText(message);
        mailMessage.setSubject(subject);
        javaMailSender.send(mailMessage);
    }

    /**
     * Finds the appropriate {@link MessageBuilder} for the given event and builds the message.
     *
     * @param event The event data used to build the message.
     * @return The built message string.
     * @throws MessageBuilderNotFoundException if no builder supports the event class.
     */
    protected String getMessage(T event) {
        return messageBuilders.stream()
                .filter(builder -> builder.supports(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new MessageBuilderNotFoundException(event.getClass().getName()))
                .buildMessage(event, Locale.ROOT);
    }
}