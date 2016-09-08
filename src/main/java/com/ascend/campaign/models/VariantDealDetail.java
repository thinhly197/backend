package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VariantDealDetail {
    @JsonProperty(value = "variant_id")
    private String variant;

    @JsonProperty(value = "brand_id")
    private String brand;

    @JsonProperty(value = "product_id")
    private String product;

    @JsonProperty(value = "category_id")
    private String category;

    @JsonProperty(value = "collections")
    private List<String> collection;
}
