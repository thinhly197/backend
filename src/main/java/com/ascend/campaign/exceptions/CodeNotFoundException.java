package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class CodeNotFoundException extends RuntimeException {
    public CodeNotFoundException() {
    }

    public CodeNotFoundException(String message) {
        super(message);
    }

    public CodeNotFoundException(Errors error) {
        super(error.name());
    }

    public CodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
