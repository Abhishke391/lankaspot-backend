package com.lankaspot.server.exception;

public class DuplicateFeatureException extends BadRequestException {

    public static final String CODE = "DUPLICATE_FEATURE";

    public DuplicateFeatureException(Long featureId) {
        super(CODE, "A feature was selected more than once. (featureId=" + featureId + ")");
    }
}