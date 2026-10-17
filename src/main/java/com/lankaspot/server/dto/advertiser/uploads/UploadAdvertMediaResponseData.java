package com.lankaspot.server.dto.advertiser.uploads;

import java.util.List;

public record UploadAdvertMediaResponseData(
        Long quoteRef,
        List<UploadedAdvertMediaDTO> media
) {
}