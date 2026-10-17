package com.lankaspot.server.dto.advertiser.subscription;

import com.lankaspot.server.enums.BillingPeriod;
import com.lankaspot.server.model.SubscriptionPlan;

import java.math.BigDecimal;


public record SubscriptionPlanDTO(
        Long id,
        String code,
        String name,
        String description,
        BigDecimal price,
        String currency,
        BillingPeriod billingPeriod,
        boolean mostPopular

) {
    public static SubscriptionPlanDTO from(SubscriptionPlan plan) {
        return new SubscriptionPlanDTO(
                plan.getId(),
                plan.getCode(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getBillingPeriod(),
                plan.isMostPopular()
        );
    }
}
