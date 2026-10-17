package com.lankaspot.server.dto.advertiser.payment;

import java.util.Map;

public record PayhereCheckoutResponseData(
        String invoiceNumber,
        String actionUrl,
        Map<String, String> fields
) {
}
