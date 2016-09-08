package com.ascend.campaign.exceptions;

import com.ascend.campaign.constants.Errors;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException() {
    }

    public CampaignNotFoundException(String message) {
        super(message);
    }

    public CampaignNotFoundException(Errors error) {
        super(error.name());
    }

    public CampaignNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
