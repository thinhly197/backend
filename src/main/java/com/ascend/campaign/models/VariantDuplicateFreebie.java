package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class VariantDuplicateFreebie {

    @JsonProperty(value = "variant_id")
    String variantId;

    @JsonProperty(value = "promotion_id")
    List<Long> duplicatePromotionId;
}
