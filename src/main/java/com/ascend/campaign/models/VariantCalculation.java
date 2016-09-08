package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VariantCalculation {

    @JsonIgnore
    public List<Variant> variantList;

    @JsonProperty(value = "variant_id")
    String variantId;

    @JsonIgnore//@JsonProperty(value = "discount_percent_Double")
            Double percentDiscount;

    @JsonProperty(value = "discount_percent")
    String percentDiscountString;

    @JsonIgnore//@JsonProperty(value = "normal_price_Double_for check")
            Double normalPrice;

    @JsonProperty(value = "normal_price")
    String normalPriceString;

    @JsonIgnore//@JsonProperty(value = "final_price_Double_for check")
            Double finalPrice;

    @JsonProperty(value = "final_price")
    String finalPriceString;

    @JsonIgnore//@JsonProperty(value = "total_percent_discount_Double_for check")
            Double totalPercentDiscount;

    @JsonProperty(value = "total_percent_discount")
    String totalPercentDiscountString;

    @JsonIgnore//@JsonProperty(value = "flat_discount_Double_for check")
            Double flatDiscount;

    @JsonProperty(value = "flat_discount")
    String flatDiscountString;

}
