package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class EmailGroupNotFoundException extends RuntimeException {
    public EmailGroupNotFoundException() {
    }

    public EmailGroupNotFoundException(String message) {
        super(message);
    }

    public EmailGroupNotFoundException(Errors error) {
        super(error.name());
    }

    public EmailGroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
