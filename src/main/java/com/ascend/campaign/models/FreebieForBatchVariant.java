package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FreebieForBatchVariant {
    @JsonProperty(value = "variant_id")
    String variantId;

    @JsonProperty(value = "promotions")
    List<FreebieForProduct> promotionsFreebieForVariant;
}
