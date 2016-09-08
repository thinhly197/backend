package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FreebieForProduct {
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

    @JsonProperty(value = "freebie_note")
    String freebieNote;

    @JsonProperty(value = "freebie_note_translation")
    String freebieNoteTranslation;

    @JsonProperty(value = "image_freebie")
    ImageFreebie imageFreebie;

    @JsonProperty(value = "criteria_promotion")
    private CriteriaPromotionFreebie criteriaPromotionFreebie;

    @JsonProperty(value = "free_variants")
    List<VariantFreebie> freebieVariants;

    @JsonProperty(value = "options_variant")
    List<VariantFreebie> optionVariants;

}
