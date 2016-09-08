package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class FlashSaleVariantAvailable {
    @JsonProperty(value = "variant_id")
    @NotNull
    @NotEmpty
    private String variantId;

    @JsonProperty(value = "is_available")
    @NotNull
    private Boolean isAvailable;

}
