package com.lankaspot.server.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all expected (business) errors. The GlobalExceptionHandler turns it into a
 * LankaspotResponse using the status, error code and message carried here.
 */
@Getter
public abstract class LankaspotException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected LankaspotException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
