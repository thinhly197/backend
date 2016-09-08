package com.ascend.campaign.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BannerImages {

    @JsonProperty(value = "tomorrow_banner_img")
    ImageBanner tomorrowBanner;

    @JsonIgnore
    private String tomorrowBannerData;

    @JsonProperty(value = "today_banner_img")
    ImageBanner todayBanner;

    @JsonIgnore
    private String todayBannerData;

    @JsonProperty(value = "incoming_banner_img")
    ImageBanner incomingBanner;

    @JsonIgnore
    private String incomingBannerData;
}
