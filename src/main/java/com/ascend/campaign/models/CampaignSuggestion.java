package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CampaignSuggestion {
    @JsonProperty(value = "id")
    String promotionId;

    @JsonProperty(value = "name")
    String promotionName;

    @JsonProperty(value = "name_translation")
    String promotionNamTranslation;

    /*@JsonProperty(value = "is_allow_nonmember")
    Boolean isAllowNonMember;*/

    /*@JsonProperty(value = "cost")
    Double promotionCost;*/

    @JsonProperty(value = "description")
    String promotionDescription;

    @JsonProperty(value = "description_translation")
    String promotionDescriptionTranslation;

    @JsonProperty(value = "short_description")
    String shortDescription;

    @JsonProperty(value = "short_description_translation")
    String shortDescriptionTranslation;


    @JsonProperty(value = "note")
    String note;

    @JsonProperty(value = "note_translation")
    String noteEn;


    @JsonProperty(value = "img_web")
    private String imgWeb;

    @JsonProperty(value = "img_web_translation")
    private String imgWebTranslation;

    @JsonProperty(value = "img_mobile")
    private String imgMobile;

    @JsonProperty(value = "img_mobile_translation")
    private String imgMobileTranslation;

    @JsonProperty(value = "actions")
    List<PromotionAction> promotionAction;

    public void addPromotionAction(PromotionAction promotionAction) {
        if (this.promotionAction == null) {
            this.promotionAction = new ArrayList();
        }
        this.promotionAction.add(promotionAction);
    }
}