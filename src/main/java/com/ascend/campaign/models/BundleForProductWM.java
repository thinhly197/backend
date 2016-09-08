package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BundleForProductWM {
    @JsonProperty(value = "promotion_id")
    String promotionId;

    @JsonProperty(value = "promotion_name")
    String promotionName;

    @JsonProperty(value = "promotion_name_translation")
    String promotionNameTranslation;

    @JsonProperty(value = "promotion_description")
    String promotionDescription;

    @JsonProperty(value = "promotion_description_translation")
    String promotionDescriptionTranslation;

    @JsonProperty(value = "short_description")
    String shortDescription;

    @JsonProperty(value = "promotion_short_description_translation")
    String promotionShortDescriptionTranslation;

    @JsonProperty(value = "bundle_note")
    String bundleNote;

    @JsonProperty(value = "bundle_note_translation")
    String bundleNoteTranslation;

    @JsonProperty(value = "img_web")
    private String imgWeb;

    @JsonProperty(value = "img_web_translation")
    private String imgWebTranslation;

    @JsonProperty(value = "img_mobile")
    private String imgMobile;

    @JsonProperty(value = "img_mobile_translation")
    private String imgMobileTranslation;

    @JsonProperty(value = "bundle_variant")
    List<Variant> bundleVariants;

}
