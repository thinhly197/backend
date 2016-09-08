package com.ascend.campaign.constants;

import lombok.Getter;

@Getter
public enum FlashSaleEnum {
    WOW_BANNER("wow_banner"),
    FLASH_SALE_TYPE("flashsale_type"),
    FLASH_SALE("flashsale"),
    WOW_EXTRA("wow_extra"),
    ORDER_ASC("ASC"),
    ORDER_DESC("DESC"),
    WOW_EXTRA_LATEST("latest"),
    SORT_LATEST("latest"),
    SORT_DISCOUNT_PERCENT("discountPercent"),
    SORT_PROMOTION_PRICE("promotionPrice"),
    SORT_MAX_DISCOUNT_PERCENT("maxDiscountPercent"),
    SORT_MIN_DISCOUNT_PERCENT("minDiscountPercent"),
    SORT_MAX_PROMOTION_PRICE("maxPromotionPrice"),
    SORT_MIN_PROMOTION_PRICE("minPromotionPrice"),
    SORT_LATEST_FLASHSALE("flashSale.startPeriod");

    private final String content;

    FlashSaleEnum(String content) {
        this.content = content;
    }
}
