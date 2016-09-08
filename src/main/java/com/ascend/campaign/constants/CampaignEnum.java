package com.ascend.campaign.constants;

import lombok.Getter;

@Getter
public enum CampaignEnum {
    USER_KEY("username"),
    LOG_TYPE("logType"),
    COMPONENT("component"),
    CORRELATION("correlation"),
    ANONYMOUS("anonymous"),
    USER("user"),
    NONUSER("non-user"),
    ACCESS_TOKEN("X-Wm-AccessToken"),
    REFRESH_TOKEN("X-Wm-RefreshToken"),
    SUCCESSFULLY("successfully"),
    FAILURE("failure"),
    HOLD("hold"),
    UNHOLD("unhold"),
    ACTIVATE("activate"),
    DEACTIVATE("deactivate"),
    SINGLE("single"),
    VIP("vip"),
    UNIQUE("unique"),
    FIX("fixed"),
    RANDOM("random"),
    ITRUEMART("itruemart"),
    WEMALL("wemall"),
    ENV_LOCAL("local"),
    ENV_DEV("dev"),
    ENV_TEST("test"),
    ENV_ALPHA("alpha"),
    ENV_STAGING("staging"),
    ENV_PRODUCTION("production"),
    WM_BUNDLE("wm-bundle"),
    WM_FREEBIE("wm-freebie"),
    WM_DISCOUNT_PROMOTION("wm-discount_promotion"),
    WM_OPTION_TO_BUY("wm-option_to_buy"),
    WM_SPECIFIC_TIME("wm-specific_time"),
    WM_DISCOUNT_BY_CODE("wm-discount_by_code"),
    CART("cart"),
    BRAND("brand"),
    VARIANT("variant"),
    PRODUCT("product"),
    CATEGORY("category"),
    COLLECTION("collection"),
    TRUE_MOVE_H("True Move H"),
    TRUE_YOU("True You"),
    CAMPAIGN_NAME_DUPLICATE("Duplicate Name !!"),
    PROMOTION_STATUS_CREATING("creating"),
    PROMOTION_STATUS_UPDATING("updating"),
    PROMOTION_STATUS_DELETING("deleting"),
    PROMOTION_STATUS_DUPLICATING("duplicating"),
    YES("Y"),
    NO("N"),
    BUILD_DROOLS("buildDrools"),
    ITM_V1("v1"),
    ITM_V2("v2"),
    FILTER_LIVE("live"),
    FILTER_ENABLED("enabled"),
    FILTER_DISABLE("disable"),
    FILTER_EXPIRED("expired"),
    FLASHSALE_TYPE("flashsale_type");

    private final String content;

    CampaignEnum(String content) {
        this.content = content;
    }
}
