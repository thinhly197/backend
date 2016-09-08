package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PDSJson {
    private String message;
    private VariantDealDetail data;
}
