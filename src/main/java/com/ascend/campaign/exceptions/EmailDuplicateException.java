package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException() {
    }

    public EmailDuplicateException(String message) {
        super(message);
    }

    public EmailDuplicateException(Errors error) {
        super(error.name());
    }

    public EmailDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
