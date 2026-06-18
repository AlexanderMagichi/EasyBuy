package com.teamchallenge.easybuy.email.sender;

import com.stripe.model.checkout.Session;
import com.teamchallenge.easybuy.email.message.EmailConfirmMessage;
import com.teamchallenge.easybuy.email.message.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for sending payment confirmation emails based on Stripe session data.
 */
@Component
public class PaymentEmailConfirmation extends AbstractEmailSender<EmailConfirmMessage> {

    private static final String DEFAULT_SUCCESSFUL_EMAIL_MESSAGE = "Your payment with total amount - %.2f %s was successfully processed";
    private static final String DEFAULT_EMAIL_SUBJECT = "Payment Confirmation for Your Recent Purchase";

    /**
     * Constructs the payment confirmation sender.
     *
     * @param javaMailSender  The mail sender implementation.
     * @param mailMessage     The template message.
     * @param messageBuilders The list of available message builders.
     */
    @Autowired
    public PaymentEmailConfirmation(JavaMailSender javaMailSender,
                                    SimpleMailMessage mailMessage,
                                    List<MessageBuilder<EmailConfirmMessage>> messageBuilders) {
        super(javaMailSender, mailMessage, messageBuilders);
    }

    /**
     * Sends a payment confirmation email using details from the Stripe session.
     *
     * @param stripeSession The Stripe checkout session object.
     */
    public void send(Session stripeSession) {
        sendNotification(
                stripeSession.getCustomerEmail(),
                DEFAULT_SUCCESSFUL_EMAIL_MESSAGE.formatted(
                        stripeSession.getAmountTotal() / 100.0,
                        stripeSession.getCurrency()
                ),
                DEFAULT_EMAIL_SUBJECT
        );
    }
}