package com.ascend.campaign.exceptions;

public class MissingRequiredFieldException extends RuntimeException {
    public MissingRequiredFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingRequiredFieldException(String message) {
        super(message);
    }

    public MissingRequiredFieldException() {
        super();
    }
}
