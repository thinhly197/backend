package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class DealNotFoundException extends RuntimeException {
    public DealNotFoundException() {
    }

    public DealNotFoundException(String message) {
        super(message);
    }

    public DealNotFoundException(Errors error) {
        super(error.name());
    }

    public DealNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
