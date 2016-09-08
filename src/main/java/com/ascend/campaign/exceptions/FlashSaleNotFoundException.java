package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class FlashSaleNotFoundException extends RuntimeException {
    public FlashSaleNotFoundException() {
    }

    public FlashSaleNotFoundException(String message) {
        super(message);
    }

    public FlashSaleNotFoundException(Errors error) {
        super(error.name());
    }

    public FlashSaleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
