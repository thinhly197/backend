package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VariantAdapter {
    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "sku")
    private String sku;

    @JsonProperty(value = "quantity")
    private Integer quantity;

    @JsonProperty(value = "img")
    private String img;

    @JsonProperty(value = "normal_price")
    private Double normalPrice;

    @JsonProperty(value = "special_price")
    private Double specialPrice;
}
