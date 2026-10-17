package com.lankaspot.server.dto.advertiser.uploads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetPrimaryAdvertMediaRequest(
        @NotNull @Positive Long quoteRef,
        @NotNull @Positive Long mediaId
) {
}