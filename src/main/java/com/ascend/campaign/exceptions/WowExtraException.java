package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class WowExtraException extends RuntimeException {
    public WowExtraException() {
    }

    public WowExtraException(String message) {
        super(message);
    }

    public WowExtraException(Errors error) {
        super(error.name());
    }

    public WowExtraException(String message, Throwable cause) {
        super(message, cause);
    }
}
