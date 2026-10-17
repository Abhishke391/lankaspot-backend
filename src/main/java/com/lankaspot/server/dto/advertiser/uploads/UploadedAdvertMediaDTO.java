package com.lankaspot.server.dto.advertiser.uploads;

import com.lankaspot.server.enums.MediaStatus;
import com.lankaspot.server.enums.MediaType;
import com.lankaspot.server.model.AdQuoteMedia;

public record UploadedAdvertMediaDTO(
        Long id,
        MediaType mediaType,
        String url,
        String publicId,
        Integer sortOrder,
        boolean primary,
        MediaStatus mediaStatus
) {
    public static UploadedAdvertMediaDTO from(AdQuoteMedia media) {
        return new UploadedAdvertMediaDTO(
                media.getId(),
                media.getMediaType(),
                media.getUrl(),
                media.getPublicId(),
                media.getSortOrder(),
                media.isPrimary(),
                media.getMediaStatus()
        );
    }
}