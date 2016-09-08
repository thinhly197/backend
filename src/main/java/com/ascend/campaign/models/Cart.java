package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;

@Data
public class Cart {
    @JsonProperty("customer_id")
    String customerId;

    @JsonProperty("customer_type")
    String customerType;

    @JsonProperty(value = "app_id")
    String appId;

    @JsonProperty("channel_type")
    String channelType;

    @JsonProperty("payment_channel")
    String paymentChannel;

    @JsonProperty(value = "card_type")
    String cardType;

    @JsonProperty("current_date")
    Timestamp currentDate;

    @JsonProperty(value = "installment_month")
    Integer installmentMonth;

    @JsonProperty(value = "issuer_name")
    String issuerName;

    List<Product> products;

    @JsonProperty("cart_campaign")
    CartCampaign cartCampaign;

    @JsonProperty("user_id")
    String userId;

    @JsonProperty(value = "promotion_code")
    String promotionCode;

    @JsonProperty(value = "promotion_type")
    String promotionType;

    @JsonProperty(value = "promotion_params")
    PromotionParams promotionParams;

    @JsonProperty(value = "merchant_id")
    String merchantId;

    @JsonProperty(value = "category")
    String category;

    @JsonProperty(value = "campaign_applied")
    List<CampaignApplied> campaignApplied;

    @JsonProperty(value = "campaign_suggested")
    List<CampaignSuggestion> campaignSuggestion;

    Long codeGroupId;

    @JsonIgnore
    HashMap<String, Integer> promotionIdMatchPromotionList;

    List<String> promoIdDuplicate;


    public int getDayOfWeek() {
        DateTime dt = new DateTime(this.currentDate);
        return 64 >> (dt.getDayOfWeek() % 7);
    }

    public long getEpochCurrentTime() {
        LocalDate date = LocalDate.of(1970, 1, 1);
        LocalTime time = LocalTime.of(
                currentDate.toLocalDateTime().getHour(),
                currentDate.toLocalDateTime().getMinute(), 0);
        LocalDateTime epochDateTime = LocalDateTime.of(date, time);

        return epochDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public void addPromotion(String promotionId, Integer quantity) {
        if (this.promotionIdMatchPromotionList == null) {
            this.promotionIdMatchPromotionList = new HashMap<>();
            this.promotionIdMatchPromotionList.put(promotionId, quantity);
        } else {
            this.promotionIdMatchPromotionList.put(promotionId, quantity);
        }
    }

}