package com.lankaspot.server.exception;

import org.springframework.http.HttpStatus;

/**
 * The quote changed (edited elsewhere) or stopped being editable (purchased) between loading it
 * and saving the update.
 */
public class QuoteOutdatedException extends LankaspotException {

    public static final String CODE = "QUOTE_OUTDATED";

    public QuoteOutdatedException(Long quoteRef) {
        super(HttpStatus.CONFLICT, CODE,
                "This quote was changed or can no longer be edited. Please reload and try again. (quoteRef="
                        + quoteRef + ")");
    }
}