package com.lankaspot.server.exception;

public class PaymentAlreadyCompletedException extends ConflictException {
    public PaymentAlreadyCompletedException(String message) {
        super("PAYMENT_ALREADY_COMPLETED", message);
    }
}
