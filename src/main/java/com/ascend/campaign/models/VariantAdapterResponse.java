package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VariantAdapterResponse {
    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "data")
    private List<VariantAdapter> variantAdapters;
}
