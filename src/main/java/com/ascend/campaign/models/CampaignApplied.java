package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CampaignApplied {
    @JsonProperty("id")
    Long promotionId;

    @JsonProperty("name")
    String promotionName;

    @JsonProperty("product_variants")
    List<String> variantId;

    @JsonProperty("limit")
    Integer limit;
}
