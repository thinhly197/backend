package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class FlashSaleProductNotFoundException extends RuntimeException {
    public FlashSaleProductNotFoundException() {
    }

    public FlashSaleProductNotFoundException(String message) {
        super(message);
    }

    public FlashSaleProductNotFoundException(Errors error) {
        super(error.name());
    }

    public FlashSaleProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
