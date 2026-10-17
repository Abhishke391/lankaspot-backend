package com.lankaspot.server.dto.advertiser.retrieve;

import java.util.List;

public record FeatureDTO(
        Long quoteRef,
        List<FeatureDefDTO> features
) {
}
