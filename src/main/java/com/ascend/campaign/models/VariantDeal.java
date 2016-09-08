package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VariantDeal {
    @JsonProperty("promotion_id")
    Long promotionId;

    @JsonProperty("promotion_name")
    String promotionName;

    @JsonProperty("variant_id")
    String variantID;

    @JsonProperty("promotion_price")
    Double promotionPrice;
}
