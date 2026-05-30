package com.ajay.lovable.accountservice.dto.subscription;

import com.ajay.lovable.commonlib.dto.PlanDto;

import java.time.Instant;

public record SubscriptionResponse(
        PlanDto plan,
        String status,
        Instant currentPeriodEnd,
        Long tokensUsedThisCycle
) {
}
