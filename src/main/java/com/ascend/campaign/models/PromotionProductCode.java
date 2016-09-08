package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PromotionProductCode {
    @JsonProperty("product_variant")
    String productVariant;

    @JsonProperty("brand_variant")
    String brandVariant;

    @JsonProperty(value = "category_code")
    String categoryCode;

    @JsonProperty(value = "collection_code")
    String collection;

    @JsonProperty("promotions_product")
    List<PromotionForProductCode> promotionForProductsCode;

    @JsonIgnore
    List<Integer> promotionIdList;

    public void addPromotionsForProductCode(PromotionForProductCode promotionForProductCode) {
        if (this.promotionForProductsCode == null) {
            this.promotionForProductsCode = new ArrayList();
        }

        this.promotionForProductsCode.add(promotionForProductCode);
    }
}
