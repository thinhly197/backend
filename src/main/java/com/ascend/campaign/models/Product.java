package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Product {
    @JsonProperty("variant_id")
    String variantId;

    @JsonProperty("normal_price")
    Double normalPrice;

    @JsonProperty("discounted_price")
    Double discountPrice;

    @JsonProperty(value = "brand_code")
    String brandCode;

    @JsonProperty(value = "category_code")
    String categoryCode;

    @JsonProperty(value = "collection_code")
    String collection;

    @JsonIgnore
    Double finalPrice;

    @JsonIgnore
    String name;

    @JsonIgnore
    Integer quantity;

    public Double getDiscountPriceDrl() {
        double result = 0;
        if (this.discountPrice != null) {
            result = this.discountPrice;
        }

        if (this.discountPrice == null || result == 0.0) {
            result = this.normalPrice;
        }
        return result;
    }
}
