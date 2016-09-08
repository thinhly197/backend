package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Policy {
    @JsonProperty(value = "policy_number")
    private Long policy;

    @JsonProperty(value = "policy_img")
    private String policyImg;
}
