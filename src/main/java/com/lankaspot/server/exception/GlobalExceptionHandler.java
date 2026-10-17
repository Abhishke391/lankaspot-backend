package com.lankaspot.server.exception;

import com.lankaspot.server.response.LankaspotResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Every business error (400, 404, 409, ...) thrown as a LankaspotException. */
    @ExceptionHandler(LankaspotException.class)
    public ResponseEntity<LankaspotResponse<Void>> handleLankaspotException(LankaspotException ex) {
        log.info("Request rejected: status={}, code={}, message={}",
                ex.getStatus().value(), ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(LankaspotResponse.error(ex.getMessage(), ex.getErrorCode()));
    }

    /** @Valid failures on request bodies, returned in the same response shape. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<LankaspotResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.info("Request validation failed: {}", message);
        return ResponseEntity
                .badRequest()
                .body(LankaspotResponse.error(message, "VALIDATION_FAILED"));
    }
}