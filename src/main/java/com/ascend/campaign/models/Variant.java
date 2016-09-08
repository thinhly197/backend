package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Variant {
    @JsonProperty(value = "variant_id")
    private String variantId;

    @JsonIgnore
    private Integer quantity;

    @JsonProperty(value = "discount_type")
    private String discountType;

    @JsonProperty(value = "discount_value")
    private Double discountValue;

    @JsonProperty(value = "discount_maximum")
    private Double discountMaximum;

    public Variant(Variant variant) {
        this.variantId = variant.getVariantId();
        this.quantity = variant.getQuantity();
        this.discountType = variant.getDiscountType();
        this.discountValue = variant.getDiscountValue();
        this.discountMaximum = variant.getDiscountMaximum();
    }

    public Variant() {
    }

}
