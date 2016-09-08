package com.ascend.campaign.constants;


import lombok.Getter;

@Getter
public enum PromotionTypeEnum {
    ITM_BUNDLE("itm-bundle"),
    ITM_MNP("itm-mnp"),
    ITM_FREEBIE("itm-freebie"),
    ITM_DISCOUNT_PROMOTION("itm-discount_promotion"),
    ITM_OPTION_TO_BUY("itm-option_to_buy"),
    ITM_DISCOUNT_BY_CODE("itm-discount_by_code"),
    ITM_SPECIFIC_TIME("itm-specific_time");

    private final String content;

    PromotionTypeEnum(String content) {
        this.content = content;
    }
}


