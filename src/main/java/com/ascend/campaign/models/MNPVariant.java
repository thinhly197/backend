package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MNPVariant {
    @JsonProperty(value = "mnp_variant")
    String mnpVariants;

    @JsonProperty(value = "discount_percent")
    Double discountPercent;

    @JsonProperty(value = "discount_fixed")
    Double discountFixed;
}
