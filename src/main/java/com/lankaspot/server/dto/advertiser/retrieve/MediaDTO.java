package com.lankaspot.server.dto.advertiser.retrieve;

import java.util.List;

public record MediaDTO(
        Long quoteRef,
        List<MediaDefDTO> media
) {
}
