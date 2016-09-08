package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class BuildDroolsException extends RuntimeException {
    public BuildDroolsException() {
    }

    public BuildDroolsException(String message) {
        super(message);
    }

    public BuildDroolsException(Errors error) {
        super(error.name());
    }

    public BuildDroolsException(String message, Throwable cause) {
        super(message, cause);
    }
}
