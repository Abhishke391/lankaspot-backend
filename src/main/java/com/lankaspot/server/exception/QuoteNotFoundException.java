package com.lankaspot.server.exception;

import org.springframework.http.HttpStatus;

/**
 * The quote does not exist, or belongs to another advertiser (deliberately the same error,
 * so we never reveal whether someone else's quote exists).
 */
public class QuoteNotFoundException extends LankaspotException {

    public static final String CODE = "QUOTE_NOT_FOUND";

    public QuoteNotFoundException(Long quoteRef) {
        super(HttpStatus.NOT_FOUND, CODE, "Quote not found. (quoteRef=" + quoteRef + ")");
    }
}