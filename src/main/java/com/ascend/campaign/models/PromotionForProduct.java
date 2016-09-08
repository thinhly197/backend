package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PromotionForProduct {
    @JsonProperty(value = "id")
    String promotionId;

    @JsonProperty(value = "name")
    String promotionName;

    @JsonProperty(value = "name_translation")
    String promotionNameTranslation;

    @JsonProperty(value = "description")
    String promotionDescription;

    @JsonProperty(value = "description_translation")
    String promotionDescriptionTranslation;

    @JsonProperty(value = "short_description")
    String shortDescription;

    @JsonProperty(value = "short_description_translation")
    String promotionShortDescriptionTranslation;
    @JsonProperty(value = "img_web")
    private String imgWeb;

    @JsonProperty(value = "img_web_translation")
    private String imgWebTranslation;

    @JsonProperty(value = "img_mobile")
    private String imgMobile;

    @JsonProperty(value = "img_mobile_translation")
    private String imgMobileTranslation;
}
