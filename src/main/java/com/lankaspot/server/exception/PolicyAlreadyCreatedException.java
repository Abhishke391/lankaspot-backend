package com.lankaspot.server.exception;

public class PolicyAlreadyCreatedException extends ConflictException {
    public PolicyAlreadyCreatedException(String message) {
        super("POLICY_ALREADY_CREATED", message);
    }
}
