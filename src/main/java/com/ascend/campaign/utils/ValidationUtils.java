package com.ascend.campaign.utils;


import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.FlashSaleEnum;
import com.ascend.campaign.exceptions.WowExtraException;

import java.util.Arrays;
import java.util.List;

public class ValidationUtils {

    public static void isValidWowExtraRequestParam(String param) {
        List<String> sortTypes = Arrays.asList(FlashSaleEnum.SORT_DISCOUNT_PERCENT.getContent(),
                FlashSaleEnum.SORT_PROMOTION_PRICE.getContent(),
                FlashSaleEnum.SORT_LATEST.getContent());
        if (!sortTypes.contains(param)) {
            throw new WowExtraException(Errors.FLASH_SALE_SORT_PARAM_INVALID.getErrorDesc());
        }
    }
}
