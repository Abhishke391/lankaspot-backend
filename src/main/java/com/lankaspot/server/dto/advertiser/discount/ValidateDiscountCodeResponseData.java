package com.lankaspot.server.dto.advertiser.discount;

import java.math.BigDecimal;

public record ValidateDiscountCodeResponseData(
        String code,
        BigDecimal discountPercentage
) {}
