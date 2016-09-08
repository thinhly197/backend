package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageBanner {

    @JsonProperty(value = "banner_web")
    private String banner;

    @JsonProperty(value = "banner_web_translation")
    private String bannerTranslation;

    @JsonProperty(value = "banner_mobile")
    private String bannerMobile;

    @JsonProperty(value = "banner_mobile_translation")
    private String bannerMobileTranslation;
}
