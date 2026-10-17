package com.lankaspot.server.exception;

public class CityNotAvailableException extends BadRequestException {

    public static final String CODE = "CITY_NOT_AVAILABLE";

    public CityNotAvailableException(Long cityId) {
        super(CODE, "The selected city is not available. (cityId=" + cityId + ")");
    }
}