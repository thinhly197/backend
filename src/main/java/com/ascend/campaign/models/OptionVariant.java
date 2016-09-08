package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OptionVariant {
    @JsonProperty(value = "variant_id")
    private String variantId;

    @JsonProperty(value = "quantity")
    private Integer quantity;

    @JsonProperty(value = "discount_type")
    private String discountType;

    @JsonProperty(value = "discount_value")
    private Double discountValue;

    @JsonProperty(value = "discount_maximum")
    private Double discountMaximum;

    public OptionVariant(OptionVariant variant) {
        this.variantId = variant.getVariantId();
        this.quantity = variant.getQuantity();
        this.discountType = variant.getDiscountType();
        this.discountValue = variant.getDiscountValue();
        this.discountMaximum = variant.getDiscountMaximum();
    }

    public OptionVariant() {
    }

}
