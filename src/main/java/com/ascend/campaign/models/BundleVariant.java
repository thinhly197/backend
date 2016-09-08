package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BundleVariant {
    @JsonProperty(value = "bundle_variant")
    String bundleVariant;

    @JsonProperty(value = "discount_percent")
    Double discountPercent;

    @JsonProperty(value = "discount_fixed")
    Double discountFixed;
}
