package com.lankaspot.server.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends LankaspotException {
    public ConflictException(String errorCode, String message) {
        super(HttpStatus.CONFLICT,errorCode, message);
    }
}
