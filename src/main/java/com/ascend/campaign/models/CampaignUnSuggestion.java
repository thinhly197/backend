package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CampaignUnSuggestion {
    @JsonProperty("id")
    Long promotionId;

    @JsonProperty("name")
    String promotionName;

    @JsonProperty("product_variants")
    List<String> skuId;

    @JsonProperty("limit")
    Integer limit;
}
