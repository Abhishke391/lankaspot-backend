package com.lankaspot.server.dto.advertiser.payment;

public record PayhereCheckoutRequest(
        Long quoteRef,
        String discountCode
) {
}
