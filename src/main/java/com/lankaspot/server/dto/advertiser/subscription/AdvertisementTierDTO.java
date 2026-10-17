package com.lankaspot.server.dto.advertiser.subscription;

import com.lankaspot.server.model.AdvertisementTier;

import java.math.BigDecimal;

/** One active advertisement tier (optional add-on). */
public record AdvertisementTierDTO(
        Long id,
        String code,
        String name,
        String description,
        BigDecimal price,
        Integer durationDays
) {
    public static AdvertisementTierDTO from(AdvertisementTier tier) {
        return new AdvertisementTierDTO(
                tier.getId(),
                tier.getCode(),
                tier.getName(),
                tier.getDescription(),
                tier.getPrice(),
                tier.getDurationDays()
        );
    }
}
