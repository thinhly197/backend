package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FreebieProduct {
    @JsonProperty("product_variant")
    String productVariant;

    @JsonProperty("freebie_product")
    List<FreebieForProduct> freebieForProducts;

    @JsonIgnore
    List<String> promotionIdList;

    public void addPromotionId(String promotionId) {
        if (this.promotionIdList == null) {
            this.promotionIdList = new ArrayList<>();
            this.promotionIdList.add(promotionId);
        } else {
            this.promotionIdList.add(promotionId);
        }
    }
}
