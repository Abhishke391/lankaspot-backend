package com.lankaspot.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payhere")
public record PayhereProperties(
        String merchantId,
        String merchantSecret,
        boolean sandbox,
        String notifyUrl,
        String returnBaseUrl,
        String cancelBaseUrl
) {
    public String actionUrl() {
        return sandbox
                ? "https://sandbox.payhere.lk/pay/checkout"
                : "https://www.payhere.lk/pay/checkout";
    }
}