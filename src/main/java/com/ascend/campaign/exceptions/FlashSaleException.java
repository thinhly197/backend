package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class FlashSaleException extends RuntimeException {
    public FlashSaleException() {
    }

    public FlashSaleException(String message) {
        super(message);
    }

    public FlashSaleException(Errors error) {
        super(error.name());
    }

    public FlashSaleException(String message, Throwable cause) {
        super(message, cause);
    }
}
