package com.lankaspot.server.exception;

public class FeatureNotAvailableException extends BadRequestException {

    public static final String CODE = "FEATURE_NOT_AVAILABLE";

    public FeatureNotAvailableException(Long featureId, Long categoryId) {
        super(CODE, "Feature " + featureId + " is not available for the selected property type. (categoryId="
                + categoryId + ")");
    }
}