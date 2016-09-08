package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class AppIdNotFoundException extends RuntimeException {
    public AppIdNotFoundException() {
    }

    public AppIdNotFoundException(String message) {
        super(message);
    }

    public AppIdNotFoundException(Errors error) {
        super(error.name());
    }

    public AppIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
