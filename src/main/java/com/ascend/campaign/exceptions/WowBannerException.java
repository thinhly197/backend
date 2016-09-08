package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class WowBannerException extends RuntimeException {
    public WowBannerException() {
    }

    public WowBannerException(String message) {
        super(message);
    }

    public WowBannerException(Errors error) {
        super(error.name());
    }

    public WowBannerException(String message, Throwable cause) {
        super(message, cause);
    }
}
