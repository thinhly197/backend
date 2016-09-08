package com.ascend.campaign.models;

import com.ascend.campaign.entities.FlashSaleProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FlashSaleCondition {
    @JsonProperty(value = "flashsale_products")
    private List<FlashSaleProduct> flashSaleProducts;

    @JsonProperty(value = "limit_item_per_cart")
    @NotNull
    private Integer limitItem;

    @JsonProperty(value = "limit_item_per_cart_image")
    @NotNull
    private String limitItemImg;

    @JsonProperty(value = "payment_types")
    private List<String> paymentType;

    @JsonProperty(value = "flashsale_product")
    private FlashSaleProduct flashSaleProduct;
}
