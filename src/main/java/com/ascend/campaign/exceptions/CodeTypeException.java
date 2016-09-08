package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class CodeTypeException extends RuntimeException {
    public CodeTypeException() {
    }

    public CodeTypeException(String message) {
        super(message);
    }

    public CodeTypeException(Errors error) {
        super(error.name());
    }

    public CodeTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
