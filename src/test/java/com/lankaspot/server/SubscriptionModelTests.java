package com.lankaspot.server;

import com.lankaspot.server.enums.AllowanceResetPolicy;
import com.lankaspot.server.enums.BillingPeriod;
import com.lankaspot.server.model.SubscriptionPlan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubscriptionModelTests {

    @Test
    void annualPlanKeepsItsTermAllowanceAndBillingInterval() {
        SubscriptionPlan plan = new SubscriptionPlan(
                "annual-20", "Annual", BigDecimal.valueOf(2000),
                BillingPeriod.YEARLY, 20);

        assertEquals(BillingPeriod.YEARLY, plan.getBillingPeriod());
        assertEquals(20, plan.getAdvertisementAllowance());
        assertEquals(365, plan.getValidityDays());
        assertEquals(AllowanceResetPolicy.BILLING_PERIOD, plan.getResetPolicy());
    }
}
