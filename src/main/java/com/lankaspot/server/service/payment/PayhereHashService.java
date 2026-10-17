package com.lankaspot.server.service.payment;

import com.lankaspot.server.config.PayhereProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

@Component
public class PayhereHashService {

    private final PayhereProperties props;

    public PayhereHashService(PayhereProperties props) {
        this.props = props;
    }

    /** "2500.00": two decimals, no thousand separators. Must be identical in the hash and in the fields. */
    public static String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /** UPPER(md5(merchant_id + order_id + amount + currency + UPPER(md5(secret)))) */
    public String checkoutHash(String orderId, String formattedAmount, String currency) {
        return md5Upper(props.merchantId() + orderId + formattedAmount + currency + md5Upper(props.merchantSecret()));
    }

    /** Used by the webhook: UPPER(md5(merchant_id + order_id + payhere_amount + payhere_currency + status_code + UPPER(md5(secret)))) */
    public String notifyHash(String orderId, String payhereAmount, String payhereCurrency, String statusCode) {
        return md5Upper(props.merchantId() + orderId + payhereAmount + payhereCurrency + statusCode
                + md5Upper(props.merchantSecret()));
    }

    private static String md5Upper(String value) {
        return DigestUtils.md5DigestAsHex(value.getBytes(StandardCharsets.UTF_8)).toUpperCase();
    }
}