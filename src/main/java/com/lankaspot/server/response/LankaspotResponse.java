package com.lankaspot.server.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LankaspotResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private T data;
    private String error;
    private Instant timestamp;

    public static <T> LankaspotResponse<T> success(T data) {
        return success(data, "Request successful");
    }

    public static <T> LankaspotResponse<T> success(T data, String message) {
        return LankaspotResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> LankaspotResponse<T> error(String message) {
        return error(message, null);
    }

    public static <T> LankaspotResponse<T> error(
            String message,
            String errorCode
    ) {
        return LankaspotResponse.<T>builder()
                .success(false)
                .message(message)
                .error(errorCode)
                .timestamp(Instant.now())
                .build();
    }
}