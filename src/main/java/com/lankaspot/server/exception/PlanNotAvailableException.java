package com.lankaspot.server.exception;

public class PlanNotAvailableException extends BadRequestException {
    public static final String CODE = "PLAN_NOT_AVAILABLE";

    public PlanNotAvailableException(Long planId) {
        super(CODE,"The selected advertising plan (" + planId + ") is not available.");
    }
}
