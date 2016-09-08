package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Calculation {
    @JsonProperty("variants")
    List<VariantCalculation> promotionForProducts;

    @JsonProperty("total_flat_discount_double")
    Double totalFlatDiscount;

    @JsonProperty("total_flat_discount")
    String totalFlatDiscountString;

}
