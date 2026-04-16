package com.teamchallenge.easybuy.payment.service;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Provides business operations for StripeService.
 */
@Service
public class StripeService {

    private final StripeClient client;

    @Value("${stripe.onboarding.return-url}")
    private String returnUrl;

    @Value("${stripe.onboarding.refresh-url}")
    private String refreshUrl;

    public StripeService(@Value("${stripe.api.key}") String apiKey) {
        this.client = new StripeClient(apiKey);
    }

    /**
     * Create Express acc for seller.
     */
    public String createStripeAccount(String email) throws StripeException {
        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setEmail(email)
                .setCapabilities(
                        AccountCreateParams.Capabilities.builder()
                                .setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder().setRequested(true).build())
                                .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                                .build()
                )
                .build();

        Account account = client.v1().accounts().create(params);
        return account.getId();
    }

    /**
     * Generates a temporary link from the onboarding form.
     */
    public String createOnboardingLink(String stripeAccountId) throws StripeException {
        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                .setAccount(stripeAccountId)
                .setRefreshUrl(refreshUrl)
                .setReturnUrl(returnUrl)
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = client.v1().accountLinks().create(params);
        return accountLink.getUrl();
    }
}
