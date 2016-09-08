package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class MNPException extends RuntimeException {
    public MNPException() {
    }

    public MNPException(String message) {
        super(message);
    }

    public MNPException(Errors error) {
        super(error.name());
    }

    public MNPException(String message, Throwable cause) {
        super(message, cause);
    }
}
