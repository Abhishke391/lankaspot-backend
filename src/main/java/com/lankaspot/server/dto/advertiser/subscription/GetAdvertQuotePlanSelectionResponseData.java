package com.lankaspot.server.dto.advertiser.subscription;

import java.util.List;

public record GetAdvertQuotePlanSelectionResponseData(
        Long quoteRef,
        Long planId,          // null if step 4 hasn't been saved yet
        List<Long> tierIds    // empty list, never null
) {}
