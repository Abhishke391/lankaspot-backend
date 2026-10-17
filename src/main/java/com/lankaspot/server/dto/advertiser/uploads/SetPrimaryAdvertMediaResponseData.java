package com.lankaspot.server.dto.advertiser.uploads;

public record SetPrimaryAdvertMediaResponseData(
        Long quoteRef,
        Long mediaId,
        boolean primary
) {
}