package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class DeleteException extends RuntimeException {
    public DeleteException() {
    }

    public DeleteException(String message) {
        super(message);
    }

    public DeleteException(Errors error) {
        super(error.name());
    }

    public DeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
