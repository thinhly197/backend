package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WowBannerProductResponse {

    @JsonProperty(value = "current_wow")
    WowBannerProduct currentWow;

    @JsonProperty(value = "next_wow")
    WowBannerProduct nextWow;

    @JsonProperty(value = "after_next_wow")
    WowBannerProduct incomingWow;
}
