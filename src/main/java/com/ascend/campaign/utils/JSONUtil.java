package com.ascend.campaign.utils;

import com.ascend.campaign.models.AuthenticationModel;
import com.ascend.campaign.models.BannerImages;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.DealCondition;
import com.ascend.campaign.models.DetailData;
import com.ascend.campaign.models.FlashSaleCondition;
import com.ascend.campaign.models.ImageBanner;
import com.ascend.campaign.models.PromotionCondition;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JSONUtil {

    public static Cart parseToCart(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(jsonData, Cart.class);
        } catch (Exception e) {
            return new Cart();
        }
    }

    public static PromotionCondition parseToPromotionCondition(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(data, PromotionCondition.class);
        } catch (Exception e) {
            return new PromotionCondition();
        }
    }

    public static DetailData parseToPromotionData(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(data, DetailData.class);
        } catch (Exception e) {
            return new DetailData();
        }
    }

    public static String toString(Object data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            return "";
        }
    }

    public static DealCondition parseToDealCondition(String conditionData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(conditionData, DealCondition.class);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            return new DealCondition();
        }
    }

    public static AuthenticationModel parseToAuthenticationModel(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(jsonData, AuthenticationModel.class);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            return new AuthenticationModel();
        }
    }

    public static FlashSaleCondition parseToFlashSaleCondition(String conditionData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(conditionData, FlashSaleCondition.class);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            return new FlashSaleCondition();
        }
    }


    public static ImageBanner parseToToImageBanner(String conditionData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(conditionData, ImageBanner.class);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            return new ImageBanner();
        }
    }

    public static BannerImages parseToBannerImages(String conditionData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        try {
            return objectMapper.readValue(conditionData, BannerImages.class);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage(), e);
            return new BannerImages();
        }
    }
}
