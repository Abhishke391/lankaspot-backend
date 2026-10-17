package com.lankaspot.server.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends LankaspotException {
    public BadRequestException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }
}
