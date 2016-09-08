package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
public class FlashSaleProductAvailable {
    @JsonProperty(value = "product_key")
    @NotNull
    @NotEmpty
    private String productKey;

    @JsonProperty(value = "is_available")
    @NotNull
    private Boolean isAvailable;

}
