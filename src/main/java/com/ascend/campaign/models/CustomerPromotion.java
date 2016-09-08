package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CustomerPromotion {
    @JsonProperty(value = "order_id")
    String orderId;

    @JsonProperty(value = "customer_id")
    String customerId;

    @JsonProperty(value = "customer_type")
    String customerType;

    @JsonProperty(value = "promotions")
    List<CampaignSuggestion> promotions;
}
