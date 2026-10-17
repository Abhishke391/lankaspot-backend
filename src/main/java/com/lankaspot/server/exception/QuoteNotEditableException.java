package com.lankaspot.server.exception;

import org.springframework.http.HttpStatus;

/**
 * The quote is past the editable stage (e.g. purchased and now a policy), so this flow can no
 * longer change it. Changes after purchase go through the amendment flow.
 */
public class QuoteNotEditableException extends LankaspotException {

    public static final String CODE = "QUOTE_NOT_EDITABLE";

    public QuoteNotEditableException(Long quoteRef, String status) {
        super(HttpStatus.CONFLICT, CODE,
                "This quote can no longer be edited here. Changes to a purchased policy must go through "
                        + "the amendment flow. (quoteRef=" + quoteRef + ", status=" + status + ")");
    }
}