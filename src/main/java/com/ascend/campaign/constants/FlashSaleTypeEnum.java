package com.ascend.campaign.constants;


import lombok.Getter;

@Getter
public enum FlashSaleTypeEnum {
    WOW_BANNER("wow_banner"),
    WOW_EXTRA("wow_extra");

    private final String content;

    FlashSaleTypeEnum(String content) {
        this.content = content;
    }
}


