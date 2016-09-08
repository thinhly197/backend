package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class PricingServiceException extends RuntimeException {
    public PricingServiceException() {
    }

    public PricingServiceException(String message) {
        super(message);
    }

    public PricingServiceException(Errors error) {
        super(error.name());
    }

    public PricingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
