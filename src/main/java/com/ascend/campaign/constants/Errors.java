package com.ascend.campaign.constants;

import lombok.Getter;

@Getter
public enum Errors {
    EMAIL_FORMAT_INVALID("EMAIL_FORMAT_INVALID", "email format is invalid !!"),
    CODE_VIP_EMAIL_DUPLICATE("CODE_VIP_EMAIL_DUPLICATE", "email is duplicate !!"),
    UNEXPECTED_ERROR("5001", "Unexpected Errors."),
    EXTERNAL_PDS_SERVICE("E_00", "PDS Service fail !!"),
    EXTERNAL_PRC_SERVICE("E_01", "Pricing Service fail !!"),
    MISSING_REQUIRED_FIELD("4001", "Missing required field(s)."),
    ORDER_PROMOTION_00("ORDER_PROMOTION_00", "Order Promotion is expired !!"),
    ORDER_PROMOTION_01("ORDER_PROMOTION_01", "Order Promotion not found !!"),
    ORDER_PROMOTION_02("ORDER_PROMOTION_02", "Can not hold over limit !!"),
    ORDER_PROMOTION_03("ORDER_PROMOTION_03", "Order Promotion is already unhold !!"),
    ORDER_PROMOTION_04("ORDER_PROMOTION_04", "Invalid hold type !!"),
    DEAL_NOT_FOUND("DEAL_00", "Deal not found !!"),
    SUPER_DEAL_NOT_FOUND("DEAL_01", "Super Deal not found !!"),
    CODE_NOT_FOUND("CODE_00", "Code not found !!"),
    CODE_INVALID_REQUEST("CODE_01", "Code parameter is missing !!"),
    CODE_DETAIL_INVALID_REQUEST("CODE_DETAIL_00", "Code detail parameter is missing !!"),
    CODE_TYPE_NOT_VALID("CODE_DETAIL_01", "Code type not valid"),
    PROMOTION_NOT_FOUND("PROMOTION_00", "Promotion not found !!"),
    CAMPAIGN_NOT_FOUND("CAMPAIGN_00", "Campaign not found !!"),
    DUPLICATE_FAIL("CAMPAIGN_01", "Duplicate failed !!"),
    DELETE_FAIL("CAMPAIGN_02", "Delete failed !!"),
    INVALID_PROMOTION_TYPE("PROMOTION_01", "Invalid Promotion type !!"),
    INVALID_FLASHSALE_TYPE("FLASHSALE_01", "Invalid FlashSale type !!"),
    BUILD_DROOLS_FAIL("DROOLS_01)!", "Build drools fail !!"),
    VARIANT_NOT_VALID("VARIANT_00", "Variant not valid"),
    EMAILGROUP_NOT_FOUND("EMAIL_GROUP_00", "Email group not found !!"),
    FLASH_SALE_INVALID_PERIOD("FLASH_SALE_01", "The period cover other Wow Banner Promotion  !!!"),
    FLASH_SALE_NOT_FOUND("FLASH_SALE_00", "FlashSale not found !!"),
    POLICY_NOT_FOUND("FLASH_SALE_05", "Policy not found !!"),
    WOW_EXTRA_PRODUCT__NOT_FOUND("FLASH_SALE_06", "Wow Product not found !!"),
    FLASH_SALE_SORT_PARAM_INVALID("FLASH_SALE_02", "FlashSale sort param invalid type !!"),
    FLASH_SALE_ORDER_PARAM_INVALID("FLASH_SALE_03", "FlashSale order param invalid type !!"),
    FLASH_SALE_TYPE_IS_NOT_VALID("FLASH_SALE_04", "FlashSale type  is not valid"),
    FLASH_SALE_PRODUCT_NOT_FOUND("FLASH_SALE_07", "FlashSale Product  not found !!");

    private String errorCode;
    private String errorDesc;

    Errors(String errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }
}
