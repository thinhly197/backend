package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException() {
    }

    public PolicyNotFoundException(String message) {
        super(message);
    }

    public PolicyNotFoundException(Errors error) {
        super(error.name());
    }

    public PolicyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
