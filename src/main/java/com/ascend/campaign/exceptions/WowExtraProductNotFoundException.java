package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class WowExtraProductNotFoundException extends RuntimeException {
    public WowExtraProductNotFoundException() {
    }

    public WowExtraProductNotFoundException(String message) {
        super(message);
    }

    public WowExtraProductNotFoundException(Errors error) {
        super(error.name());
    }

    public WowExtraProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
