package com.lankaspot.server.dto.advertiser.uploads;

import com.lankaspot.server.enums.MediaType;

public record UploadedAsset(
        String publicId,
        String secureUrl,
        MediaType mediaType,
        String format,
        String resourceType,
        long bytes,
        Integer width,
        Integer height,
        Double duration
) {
}
