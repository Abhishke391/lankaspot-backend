package com.lankaspot.server.exception;

public class InvalidPayhereNotificationException extends BadRequestException {
    public static final String CODE = "INVALID_PAYHERE_NOTIFICATION";
    public InvalidPayhereNotificationException(String message) {
        super(CODE,message);
    }
}
