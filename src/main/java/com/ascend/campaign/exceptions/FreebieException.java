package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class FreebieException extends RuntimeException {
    public FreebieException() {
    }

    public FreebieException(String message) {
        super(message);
    }

    public FreebieException(Errors error) {
        super(error.name());
    }

    public FreebieException(String message, Throwable cause) {
        super(message, cause);
    }
}
