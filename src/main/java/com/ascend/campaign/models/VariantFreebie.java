package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VariantFreebie {
    @JsonProperty(value = "variant_id")
    private String variantId;

    @JsonProperty(value = "quantity")
    private Integer quantity;



}
