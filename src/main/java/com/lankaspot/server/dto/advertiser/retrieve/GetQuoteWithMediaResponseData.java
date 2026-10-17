package com.lankaspot.server.dto.advertiser.retrieve;

import java.util.List;

public record GetQuoteWithMediaResponseData(
    QuoteDTO quote,
    List<MediaDefDTO> media,
    List<FeatureDefDTO> features

) {
}
