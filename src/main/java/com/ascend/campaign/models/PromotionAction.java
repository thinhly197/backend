package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PromotionAction {
    String command;
    Integer limit;

    @JsonProperty(value = "variants")
    List<Variant> variants;

    @JsonProperty(value = "option_variants")
    List<OptionVariant> optionVariants;

    @JsonProperty(value = "discount_per_cart")
    Double discountPerCart;

}
