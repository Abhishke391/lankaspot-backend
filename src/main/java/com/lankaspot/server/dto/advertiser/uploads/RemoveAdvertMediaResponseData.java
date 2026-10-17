package com.lankaspot.server.dto.advertiser.uploads;

import com.lankaspot.server.enums.MediaStatus;

public record RemoveAdvertMediaResponseData(
        Long quoteRef,
        Long mediaId,
        MediaStatus mediaStatus
) {
}