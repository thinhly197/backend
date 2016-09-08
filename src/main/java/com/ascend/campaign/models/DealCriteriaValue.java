package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DealCriteriaValue {
    @JsonProperty(value = "variant_id")
    private String variantId;

    @JsonProperty(value = "recommended")
    Boolean recommended;

    @JsonProperty(value = "promotion_price")
    private Double promotionPrice;

}
