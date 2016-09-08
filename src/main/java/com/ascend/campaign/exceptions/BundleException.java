package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class BundleException extends RuntimeException {
    public BundleException() {
    }

    public BundleException(String message) {
        super(message);
    }

    public BundleException(Errors error) {
        super(error.name());
    }

    public BundleException(String message, Throwable cause) {
        super(message, cause);
    }
}
