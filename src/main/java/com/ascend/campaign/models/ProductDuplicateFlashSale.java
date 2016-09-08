package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProductDuplicateFlashSale {

    @JsonProperty(value = "product_key")
    String productKey;

    @JsonProperty(value = "flashsale_id")
    List<Long> duplicateFlashsaleId;
}
