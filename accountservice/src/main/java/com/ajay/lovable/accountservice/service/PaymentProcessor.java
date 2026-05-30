package com.ajay.lovable.accountservice.service;

import com.ajay.lovable.accountservice.dto.subscription.CheckoutRequest;
import com.ajay.lovable.accountservice.dto.subscription.CheckoutResponse;
import com.ajay.lovable.accountservice.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
