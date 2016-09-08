package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class PromotionNotFoundException extends RuntimeException {
    public PromotionNotFoundException() {
    }

    public PromotionNotFoundException(String message) {
        super(message);
    }

    public PromotionNotFoundException(Errors error) {
        super(error.name());
    }

    public PromotionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
