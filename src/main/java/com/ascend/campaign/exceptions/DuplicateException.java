package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class DuplicateException extends RuntimeException {
    public DuplicateException() {
    }

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(Errors error) {
        super(error.name());
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
