package com.lankaspot.server.exception;

public class CategoryNotAvailableException extends BadRequestException {

    public static final String CODE = "CATEGORY_NOT_AVAILABLE";

    public CategoryNotAvailableException(Long categoryId) {
        super(CODE, "The selected property type is not available. (categoryId=" + categoryId + ")");
    }
}
