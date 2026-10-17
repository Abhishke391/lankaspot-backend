package com.lankaspot.server.dto.advertiser.subscription;

import java.util.List;

public record SelectAdvertPlanResponseData(
        Long quoteRef,
        Long planId,
        List<Long> tierIds
) {}
