package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FlashSaleCriteriaValue {
    @JsonProperty(value = "variant_id")
    private String variantId;

    @JsonProperty(value = "promotion_price")
    private Double promotionPrice;


    @JsonProperty(value = "limit_quantity")
    private Long limitQuantity;

}
