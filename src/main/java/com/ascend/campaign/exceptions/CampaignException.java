package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class CampaignException extends RuntimeException {
    public CampaignException() {
    }

    public CampaignException(String message) {
        super(message);
    }

    public CampaignException(Errors error) {
        super(error.name());
    }

    public CampaignException(String message, Throwable cause) {
        super(message, cause);
    }
}
