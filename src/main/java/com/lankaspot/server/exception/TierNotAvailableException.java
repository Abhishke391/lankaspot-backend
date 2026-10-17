package com.lankaspot.server.exception;

public class TierNotAvailableException extends BadRequestException {
    public static final String CODE = "TIER_NOT_AVAILABLE";

    public TierNotAvailableException(Long tierId) {
        super(CODE, "The selected add-on (" + tierId + ") is not available.");
    }
}
