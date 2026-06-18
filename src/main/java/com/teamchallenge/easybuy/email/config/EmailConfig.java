package com.teamchallenge.easybuy.email.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for email service integration.
 * Configures the {@link JavaMailSender} and {@link MessageSource} for email templates.
 */
@Configuration
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    /**
     * Configures the JavaMailSender bean.
     * Only initialized if 'email.enabled' property is set to true.
     *
     * @return A configured instance of {@link JavaMailSender}.
     */
    @Bean
    @ConditionalOnProperty(name = "email.enabled", havingValue = "true")
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    /**
     * Configures the template for simple email messages.
     *
     * @return A {@link SimpleMailMessage} bean.
     */
    @Bean
    @ConditionalOnProperty(name = "email.enabled", havingValue = "true")
    public SimpleMailMessage simpleMailMessage() {
        return new SimpleMailMessage();
    }

    /**
     * Configures the MessageSource to support internationalization and email template localization.
     *
     * @return A {@link ReloadableResourceBundleMessageSource} bean.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(1000);
        return messageSource;
    }
}