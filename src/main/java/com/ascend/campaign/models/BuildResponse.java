package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BuildResponse {
    @JsonProperty(value = "promotion_built")
    Integer promotionBuilt;

}