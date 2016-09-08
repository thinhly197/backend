package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class PDSServiceException extends RuntimeException {
    public PDSServiceException() {
    }

    public PDSServiceException(String message) {
        super(message);
    }

    public PDSServiceException(Errors error) {
        super(error.name());
    }

    public PDSServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
