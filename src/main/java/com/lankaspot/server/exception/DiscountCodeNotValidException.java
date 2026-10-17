package com.lankaspot.server.exception;

public class DiscountCodeNotValidException extends BadRequestException {
    public static final String CODE = "DISCOUNT_CODE_NOT_VALID";

    public DiscountCodeNotValidException(String message) {
        super(CODE, message);
    }
}
