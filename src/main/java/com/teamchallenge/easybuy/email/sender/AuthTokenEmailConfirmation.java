package com.teamchallenge.easybuy.email.sender;

import com.teamchallenge.easybuy.email.dto.EmailTokenDto;
import com.teamchallenge.easybuy.email.message.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for constructing and sending email confirmation messages containing authentication tokens.
 * Only enabled when 'email.enabled' property is set to true.
 */
@Component
@ConditionalOnProperty(name = "email.enabled", havingValue = "true")
public class AuthTokenEmailConfirmation extends AbstractEmailSender<EmailTokenDto> {

    @Value("${spring.mail.subject.confirmation}")
    private String subject;

    /**
     * Constructs the email confirmation service.
     *
     * @param javaMailSender  The mail sender implementation.
     * @param mailMessage     The template message.
     * @param messageBuilders The list of available message builders.
     */
    @Autowired
    public AuthTokenEmailConfirmation(JavaMailSender javaMailSender,
                                      SimpleMailMessage mailMessage,
                                      List<MessageBuilder<EmailTokenDto>> messageBuilders) {
        super(javaMailSender, mailMessage, messageBuilders);
    }

    /**
     * Sends a temporary authentication code to the specified email address.
     *
     * @param email   The recipient's email address.
     * @param message The temporary code or token message.
     */
    public void sendTemporaryCode(String email, String message) {
        String buildMessage = getMessage(new EmailTokenDto(message));
        sendNotification(email, buildMessage, subject);
    }
}