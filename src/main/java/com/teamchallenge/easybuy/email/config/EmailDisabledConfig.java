package com.teamchallenge.easybuy.email.config;

import com.teamchallenge.easybuy.email.sender.AuthTokenEmailConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;

/**
 * Configuration class that provides a no-op implementation of email services
 * when 'email.enabled' is set to false.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "email.enabled", havingValue = "false", matchIfMissing = true)
public class EmailDisabledConfig {

    /**
     * Creates a dummy JavaMailSender.
     *
     * @return An instance of {@link JavaMailSenderImpl}.
     */
    @Bean
    public JavaMailSender noOpMailSender() {
        return new JavaMailSenderImpl();
    }

    /**
     * Creates a dummy SimpleMailMessage.
     *
     * @return An instance of {@link SimpleMailMessage}.
     */
    @Bean
    public SimpleMailMessage noOpMailMessage() {
        return new SimpleMailMessage();
    }

    /**
     * Creates a no-op implementation of AuthTokenEmailConfirmation
     * that logs the skipped email attempt instead of sending it.
     *
     * @param mailSender  the mock mail sender.
     * @param mailMessage the mock mail message.
     * @return An anonymous subclass of {@link AuthTokenEmailConfirmation}.
     */
    @Bean
    public AuthTokenEmailConfirmation noOpAuthTokenEmailConfirmation(
            JavaMailSender mailSender, SimpleMailMessage mailMessage) {
        return new AuthTokenEmailConfirmation(mailSender, mailMessage, List.of()) {
            @Override
            public void sendTemporaryCode(String email, String message) {
                log.debug("email.send.skipped: email.enabled=false");
            }
        };
    }
}