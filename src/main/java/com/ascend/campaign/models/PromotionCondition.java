package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PromotionCondition {
    List<String> brands;

    List<String> variants;

    Integer quantity;

    Integer discount;

    @JsonProperty(value = "mastercard_percent")
    Double masterCardPercent;

    @JsonProperty(value = "promotion_code")
    String promotionCode;

    @JsonProperty(value = "option_variants")
    List<String> optionVariants;

    @JsonProperty(value = "excluded_variants")
    List<String> excludedVariants;

    @JsonProperty(value = "discount_code_criteria_value")
    List<DiscountCodeCriteriaValue> discountCodeCriteriaValue;

    @JsonProperty(value = "show_code", defaultValue = "false")
    Boolean showCode;

    @JsonProperty(value = "excluded_collections")
    List<String> excludedCollections;

    @JsonProperty(value = "excluded_brands")
    List<String> excludedBrands;

    @JsonProperty(value = "excluded_categories")
    List<String> excludedCategories;

    @JsonProperty(value = "discount_mastercard")
    Double discountMasterCard;

    @JsonProperty(value = "free_variants")
    List<String> freeVariants;

    @JsonProperty(value = "discount_percent")
    Double discountPercent;

    @JsonProperty(value = "discount_fixed")
    Double discountFixed;

    @JsonProperty(value = "min_total_value")
    Integer minTotalValue;

    @JsonProperty(value = "start_time")
    String startTime;

    @JsonProperty(value = "end_time")
    String endTime;

    @JsonProperty(value = "week_days")
    Long weekDays;

    @JsonProperty(value = "primary_variant")
    Long primaryVariant;

    @JsonProperty(value = "bundle_conditions")
    List<BundleVariant> bundleVariant;

    @JsonProperty(value = "mnp_conditions")
    List<MNPVariant> mnpVariants;

    @JsonProperty(value = "criteria_type")
    String criteriaType;

    @JsonProperty(value = "criteria_values")
    List<String> criteriaValue;

    @JsonProperty(value = "max_discount_value")
    Double maxDiscountValue;

    @JsonProperty(value = "code_group_id")
    private Long codeGroupId;

    @JsonProperty(value = "note_en")
    private String noteEn;

    @JsonProperty(value = "note_local")
    private String note;

    @JsonProperty(value = "free_quantity")
    private Integer freeQuantity;

    @JsonProperty(value = "free_variants_selectable")
    Boolean freeVariantsSelectable;

    @JsonProperty(value = "freebie_conditions")
    List<VariantFreebie> freebieConditions;

    @JsonProperty(value = "pc_option_type")
    String pcOptionType;
}
