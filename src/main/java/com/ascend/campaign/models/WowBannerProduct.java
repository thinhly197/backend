package com.ascend.campaign.models;


import com.ascend.campaign.entities.FlashSaleProduct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WowBannerProduct {

    @JsonProperty(value = "current_banner_img")
    ImageBanner currentBanner;

    @JsonProperty(value = "next_banner_img")
    ImageBanner nextBanner;

    @JsonProperty(value = "incoming_banner_img")
    ImageBanner incomingBanner;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "name_translation")
    private String nameTranslation;

    @JsonProperty(value = "short_description_local")
    private String shortDescription;

    @JsonProperty(value = "short_description_translation")
    private String shortDescriptionTranslation;

    @JsonProperty(value = "product")
    private FlashSaleProduct flashSaleProduct;

    @JsonProperty(value = "start_period")
    private Date startPeriod;

    @JsonProperty(value = "end_period")
    private Date endPeriod;


}
