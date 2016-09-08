package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PromotionParams {
    @JsonProperty("price_plan_code")
    String pricePlanCode;
}
