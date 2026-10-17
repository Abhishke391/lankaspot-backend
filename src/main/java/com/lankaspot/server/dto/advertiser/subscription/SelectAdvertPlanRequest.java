package com.lankaspot.server.dto.advertiser.subscription;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SelectAdvertPlanRequest(
        @NotNull @Positive Long quoteRef,
        @NotNull @Positive Long planId,
        List<@NotNull @Positive Long> tierIds
) {
    // null -> empty, duplicates removed. [] means "no add-ons" and clears the previous selection.
    public SelectAdvertPlanRequest {
        tierIds = tierIds == null ? List.of() : tierIds.stream().distinct().toList();
    }
}
