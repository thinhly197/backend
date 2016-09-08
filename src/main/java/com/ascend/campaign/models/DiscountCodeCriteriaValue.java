package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DiscountCodeCriteriaValue {
    @JsonProperty(value = "variant_id")
    private String variantId;

    @JsonProperty(value = "show")
    Boolean show;
}
