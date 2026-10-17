package com.lankaspot.server.enums;

public enum PaymentRegistryStatus {
    PENDING,        // checkout started, waiting for PayHere's notify
    SUCCESS,        // notify verified, status_code = 2
    FAILED,         // status_code = -2
    CANCELED,       // status_code = -1
    CHARGED_BACK,    // status_code = -3
    REJECTED        // notify arrived but failed validation (amount/currency mismatch)
}