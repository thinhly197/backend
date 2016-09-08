package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WowExtra {


    @JsonProperty(value = "variant_id")
    private String variantId;

    @JsonProperty(value = "category_id")
    private String categoryId;

    @JsonProperty(value = "promotion_price")
    private Double promotionPrice;

    @JsonProperty(value = "partner")
    private String partner;

    @JsonProperty(value = "flashsale_name")
    private String flashsaleName;

    @JsonProperty(value = "flashsale_id")
    private Long flashsaleId;


}
