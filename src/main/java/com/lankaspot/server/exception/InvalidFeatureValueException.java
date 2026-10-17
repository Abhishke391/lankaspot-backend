package com.lankaspot.server.exception;

public class InvalidFeatureValueException extends BadRequestException {

    public static final String CODE = "INVALID_FEATURE_VALUE";

    /** @param reason what is wrong, e.g. "needs a value" or "must be a number" (no trailing period) */
    public InvalidFeatureValueException(String featureName, String reason) {
        super(CODE, featureName + " " + reason + ".");
    }
}