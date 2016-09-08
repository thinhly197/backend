package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class EmailFormatException extends RuntimeException {
    public EmailFormatException() {
    }

    public EmailFormatException(String message) {
        super(message);
    }

    public EmailFormatException(Errors error) {
        super(error.name());
    }

    public EmailFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
