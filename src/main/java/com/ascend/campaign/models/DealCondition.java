package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DealCondition {
    @JsonProperty(value = "criteria_type")
    private String criteriaType;

    @JsonProperty(value = "variants")
    private List<DealCriteriaValue> criteriaVariants;

    @JsonProperty(value = "excluded_criteria_type")
    private String excludedCriteriaType;

    @JsonProperty(value = "criteria_values")
    private List<String> criteriaValue;

    @JsonProperty(value = "excluded_criteria_values")
    private List<String> excludedCriteriaValue;

    @JsonProperty(value = "limit_account")
    private Integer limitAccount;

    @JsonProperty(value = "limit_item_per_cart")
    private Integer limitItem;

    @JsonProperty(value = "discount_percent")
    private Double discountPercent;

    @JsonProperty(value = "fixed_price")
    private Double discountFixed;

    @JsonProperty(value = "max_discount_value")
    private Double maxDiscountValue;

    @JsonProperty(value = "policy_web")
    private String policyWeb;

    @JsonProperty(value = "policy_web_translation")
    private String policyWebTranslation;

    @JsonProperty(value = "policy_mobile")
    private String policyMobile;

    @JsonProperty(value = "policy_mobile_translation")
    private String policyMobileTranslation;

    @JsonProperty(value = "trueyou_icon")
    private Boolean trueYouIcon;

    @JsonProperty(value = "truemoveh_icon")
    private Boolean trueMoveHIcon;

    @JsonProperty(value = "payment_types")
    private List<String> paymentType;
}
