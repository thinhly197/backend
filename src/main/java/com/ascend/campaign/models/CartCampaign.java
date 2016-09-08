package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CartCampaign {
    @JsonProperty(value = "campaign_suggested")
    List<CampaignSuggestion> campaignSuggestion;

    @JsonProperty(value = "campaign_unsuggested")
    List<CampaignUnSuggestion> campaignUnSuggestion;
}
