package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BundleProduct {
    @JsonProperty("product_variant")
    String productVariant;

    @JsonProperty("bundle_product")
    List<BundleForProduct> bundleForProducts;

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
