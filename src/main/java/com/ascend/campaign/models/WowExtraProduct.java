package com.ascend.campaign.models;


import com.ascend.campaign.entities.FlashSaleVariant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Data
public class WowExtraProduct {

    @JsonProperty(value = "product_key")
    private String productKey;

    @JsonProperty(value = "flashsale_variants")
    private List<FlashSaleVariant> flashSaleVariants;

    @JsonProperty(value = "category")
    private List<String> categorise;

    @JsonProperty(value = "min_promotion_price")
    private Double minPromotionPrice;

    @JsonProperty(value = "max_promotion_price")
    private Double maxPromotionPrice;

    @JsonProperty(value = "min_discount_percent")
    private Double minDiscountPercent;

    @JsonProperty(value = "max_discount_percent")
    private Double maxDiscountPercent;

    @JsonProperty(value = "partner")
    private String partner;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "start_period")
    private Date startPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "end_period")
    private Date endPeriod;

    @JsonProperty(value = "policy")
    private Policy policy;
}
