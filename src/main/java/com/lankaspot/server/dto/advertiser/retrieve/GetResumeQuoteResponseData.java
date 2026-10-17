package com.lankaspot.server.dto.advertiser.retrieve;

import java.util.List;

public record GetResumeQuoteResponseData(
        QuoteDTO quote,
        List<MediaDefDTO> media,
        List<FeatureDefDTO> features,
        Integer lastCompletedStep
) {
}
