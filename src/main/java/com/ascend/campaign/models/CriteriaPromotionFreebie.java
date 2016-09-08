package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CriteriaPromotionFreebie {
    @JsonProperty(value = "criteria_variants")
    List<String> criteriaFreebie;

    @JsonProperty(value = "quantity")
    Integer quantity;
}
