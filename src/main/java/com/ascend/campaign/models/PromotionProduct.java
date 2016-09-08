package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PromotionProduct {
    @JsonProperty("product_variant")
    String productVariant;

    @JsonProperty("brand_variant")
    String brandVariant;

    @JsonProperty("collection")
    String collection;

    @JsonProperty("category")
    String category;

    @JsonProperty("promotions_product")
    List<PromotionForProduct> promotionForProducts;

    @JsonIgnore
    List<Integer> promotionIdList;

    public void addPromotionsForProduct(PromotionForProduct promotionForProduct) {
        if (this.promotionForProducts == null) {
            this.promotionForProducts = new ArrayList();
        }

        this.promotionForProducts.add(promotionForProduct);
    }

    public void addPromotionsIdList(int promotionId) {
        if (this.promotionIdList == null) {
            this.promotionIdList = new ArrayList();
            promotionIdList.add(promotionId);
        } else {
            promotionIdList.add(promotionId);
        }
    }
}
