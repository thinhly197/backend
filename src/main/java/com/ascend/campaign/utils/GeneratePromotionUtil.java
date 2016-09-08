package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.DrlOperator;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.models.DiscountCodeCriteriaValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.DateTime;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class GeneratePromotionUtil {

    private DrlConditions drlConditions = new DrlConditions();

    public String generateStringDataItmPromotion(Promotion promotion) throws Exception {
        String promotionStr = "";
        if (PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = itmFreebie(promotion);
        } else if (PromotionTypeEnum.ITM_DISCOUNT_PROMOTION.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = itmDiscountByBrand(promotion);
        } else if (PromotionTypeEnum.ITM_OPTION_TO_BUY.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = itmOptionToBuy(promotion);
        } else if (PromotionTypeEnum.ITM_SPECIFIC_TIME.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = itmSpecificTime(promotion);
        } else if (PromotionTypeEnum.ITM_BUNDLE.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = itmBundle(promotion);
        } else if (PromotionTypeEnum.ITM_DISCOUNT_BY_CODE.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = itmDiscountByCode(promotion);
        } else if (PromotionTypeEnum.ITM_MNP.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = itmMNP(promotion);
        }

        return promotionStr;
    }


    public String generateStringDataWmPromotion(PromotionWM promotion) throws Exception {
        String promotionStr = "";
        if (CampaignEnum.WM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = wmFreebie(promotion);
        } else if (CampaignEnum.WM_DISCOUNT_PROMOTION.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = wmDiscountByBrand(promotion);
        } else if (CampaignEnum.WM_OPTION_TO_BUY.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = wmOptionToBuy(promotion);
        } else if (CampaignEnum.WM_SPECIFIC_TIME.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = wmSpecificTime(promotion);
        } else if (CampaignEnum.WM_BUNDLE.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = wmBundle(promotion);
        } else if (CampaignEnum.WM_DISCOUNT_BY_CODE.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionStr = wmDiscountByCode(promotion);
        }

        return promotionStr;
    }

    public String generateStringDataItmProductPromotion(Promotion promotion) throws Exception {
        String productPromotionStr = "";
        if (PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = itmProductFreebie(promotion);
        } else if (PromotionTypeEnum.ITM_DISCOUNT_PROMOTION.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = itmDiscountByBrandOrSpecificTime(promotion);
        } else if (PromotionTypeEnum.ITM_OPTION_TO_BUY.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = optionToBuyOrWemallFreebieItm(promotion);
        } else if (PromotionTypeEnum.ITM_SPECIFIC_TIME.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = itmDiscountByBrandOrSpecificTime(promotion);
        }


        return productPromotionStr;
    }

    public String generateStringDataWmProductPromotion(PromotionWM promotion) throws Exception {
        String productPromotionStr = "";
        if (CampaignEnum.WM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = wmProductFreebie(promotion, CampaignEnum.WM_FREEBIE.getContent());
        } else if (CampaignEnum.WM_DISCOUNT_PROMOTION.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = wmDiscountByBrandOrSpecificTime(promotion);
        } else if (CampaignEnum.WM_OPTION_TO_BUY.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = optionToBuyOrWemallFreebieWm(promotion);
        } else if (CampaignEnum.WM_SPECIFIC_TIME.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = wmDiscountSpecificTime(promotion);
        } else if (CampaignEnum.WM_DISCOUNT_BY_CODE.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = wmProductFreebie(promotion, CampaignEnum.WM_DISCOUNT_BY_CODE.getContent());
        }


        return productPromotionStr;
    }

    public String generateStringDataItmProductBundlePromotion(Promotion promotion) throws Exception {
        String productPromotionStr = "";
        if (PromotionTypeEnum.ITM_BUNDLE.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = promotionForProductBundleItm(promotion);
        }

        return productPromotionStr;
    }

    public String generateStringDataItmProductMNPPromotion(Promotion promotion) throws Exception {
        String productPromotionStr = "";
        if (PromotionTypeEnum.ITM_MNP.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = promotionForProductMNPItm(promotion);
        }

        return productPromotionStr;
    }

    public String generateStringDataItmProductFreebiePromotion(Promotion promotion) throws Exception {
        String productPromotionStr = "";
        if (PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = promotionForProductFreebieItm(promotion);
        }

        return productPromotionStr;
    }

    public String generateStringDataWmProductBundlePromotion(PromotionWM promotion) throws Exception {
        String productPromotionStr = "";
        if (CampaignEnum.WM_BUNDLE.getContent().equalsIgnoreCase(promotion.getType())) {
            productPromotionStr = promotionForProductBundleWm(promotion);
        }

        return productPromotionStr;
    }


    private ST buildCommonPromotionItm(String template, Promotion promotion) {
        ST stTemplate = new ST(template);
        stTemplate.add("id", promotion.getId());
        stTemplate.add("name",
                StringEscapeUtils.escapeJava("promotion"));
        stTemplate.add("startPeriod",
                new DateTime(promotion.getStartPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("endPeriod",
                new DateTime(promotion.getEndPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("minTotalValue",
                promotion.getPromotionCondition().getMinTotalValue());
        stTemplate.add("repeatNumber",
                promotion.getRepeat() == null ? Integer.MAX_VALUE :
                        promotion.getRepeat() == 0 ? Integer.MAX_VALUE :
                                promotion.getRepeat());
        stTemplate.add("quantity",
                promotion.getPromotionCondition().getQuantity() == null ? 1
                        : promotion.getPromotionCondition().getQuantity());
        stTemplate.add("promotionId",
                promotion.getId());
        stTemplate.add("customerType",
                drlConditions.generateUserType(promotion.getMember(), promotion.getNonMember()));
        stTemplate.add("promotionCode",
                drlConditions.generateBundleTypeOrPromotionCode(
                        promotion.getPromotionCondition().getPromotionCode() == null ? ""
                                : promotion.getPromotionCondition().getPromotionCode(), "$promotionCode"));
        stTemplate.add("DayOfWeek", promotion.getPromotionCondition().getWeekDays());
        stTemplate.add("channelType",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        drlConditions.stringToList(promotion.getAppId()),
                        DrlOperator.DRL_OPERATOR_OR, "$channelType"));
        stTemplate.add("businessChannel",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        drlConditions.stringToList(promotion.getBusinessChannel()),
                        DrlOperator.DRL_OPERATOR_OR, "$businessChannel"));

        return stTemplate;
    }

    private ST buildCommonPromotionWm(String template, PromotionWM promotion) {
        ST stTemplate = new ST(template);
        stTemplate.add("id", promotion.getId());
        stTemplate.add("name",
                StringEscapeUtils.escapeJava("promotion"));
        stTemplate.add("startPeriod",
                new DateTime(promotion.getStartPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("endPeriod",
                new DateTime(promotion.getEndPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("minTotalValue",
                promotion.getPromotionCondition().getMinTotalValue());
        stTemplate.add("repeatNumber",
                promotion.getRepeat() == null ? Integer.MAX_VALUE :
                        promotion.getRepeat() == 0 ? Integer.MAX_VALUE :
                                promotion.getRepeat());
        stTemplate.add("quantity",
                promotion.getPromotionCondition().getQuantity() == null ? 1
                        : promotion.getPromotionCondition().getQuantity());
        stTemplate.add("promotionId",
                promotion.getId());
        stTemplate.add("customerType",
                drlConditions.generateUserType(promotion.getMember(), promotion.getNonMember()));
        stTemplate.add("promotionCode",
                drlConditions.generateBundleTypeOrPromotionCode(
                        promotion.getPromotionCondition().getPromotionCode() == null ? ""
                                : promotion.getPromotionCondition().getPromotionCode(), "$promotionCode"));
        stTemplate.add("DayOfWeek", promotion.getPromotionCondition().getWeekDays());
        stTemplate.add("channelType",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        drlConditions.stringToList(promotion.getAppId()),
                        DrlOperator.DRL_OPERATOR_OR, "$channelType"));
        stTemplate.add("businessChannel",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        drlConditions.stringToList(promotion.getBusinessChannel()),
                        DrlOperator.DRL_OPERATOR_OR, "$businessChannel"));

        return stTemplate;
    }

    private ST buildCommonPromotionForProductItm(String template, Promotion promotion) {
        ST stTemplate = new ST(template);
        stTemplate.add("id", promotion.getId());
        stTemplate.add("name",
                StringEscapeUtils.escapeJava("promotion"));
        stTemplate.add("startPeriod",
                new DateTime(promotion.getStartPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("endPeriod",
                new DateTime(promotion.getEndPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("promotionId",
                promotion.getId());
        return stTemplate;
    }

    private ST buildCommonPromotionForProductWm(String template, PromotionWM promotion) {
        ST stTemplate = new ST(template);
        stTemplate.add("id", promotion.getId());
        stTemplate.add("name",
                StringEscapeUtils.escapeJava("promotion"));
        stTemplate.add("startPeriod",
                new DateTime(promotion.getStartPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("endPeriod",
                new DateTime(promotion.getEndPeriod()).toString("dd-MMM-yyyy HH:mm:ss"));
        stTemplate.add("promotionId",
                promotion.getId());
        return stTemplate;
    }

    private String itmFreebie(Promotion promotion)
            throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/itm-freebie.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionItm(template, promotion);


        if (promotion.getPromotionCondition().getMinTotalValue() != null) {
            stTemplate.add("quantityFreebie", promotion.getPromotionCondition().getMinTotalValue());
            stTemplate.add("sumQuantityFreebie", "(double)$sumQuantity");
            stTemplate.add("evalFreebieCondition", "eval(Math.floor($sumQuantity/<quantityFreebie>) > 0)");
            stTemplate.add("freebieCondition", drlConditions.generateCriteriaConditionFreebieByValueAndExcluded(
                    promotion.getPromotionCondition()));
        } else {
            stTemplate.add("sumQuantityFreebie", "$sumQuantity");
            stTemplate.add("freebieCondition", drlConditions.generateCriteriaConditionFreebieAndExcluded(
                    promotion.getPromotionCondition()));
            stTemplate.add("quantityFreebie",
                    promotion.getPromotionCondition().getQuantity() == null ? 1
                            : promotion.getPromotionCondition().getQuantity());
        }
        return stTemplate.render();
    }

    private String wmFreebie(PromotionWM promotion)
            throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/wm-freebie.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionWm(template, promotion);

        if (promotion.getPromotionCondition().getMinTotalValue() != null) {
            stTemplate.add("quantityFreebie", promotion.getPromotionCondition().getMinTotalValue());
            stTemplate.add("sumQuantityFreebie", "(double)$sumQuantity");
            stTemplate.add("evalFreebieCondition", "eval(Math.floor($sumQuantity/<quantityFreebie>) > 0)");
            stTemplate.add("freebieCondition", drlConditions.generateCriteriaConditionFreebieByValueAndExcluded(
                    promotion.getPromotionCondition()));
        } else {
            stTemplate.add("sumQuantityFreebie", "$sumQuantity");
            stTemplate.add("freebieCondition", drlConditions.generateCriteriaConditionFreebieAndExcluded(
                    promotion.getPromotionCondition()));
            stTemplate.add("quantityFreebie",
                    promotion.getPromotionCondition().getQuantity() == null ? 1
                            : promotion.getPromotionCondition().getQuantity());

        }
        return stTemplate.render();
    }

    private String itmDiscountByBrand(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/itm-discount_by_brand.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionItm(template, promotion);

        stTemplate.add("brandCondition",
                drlConditions.generateCondition(
                        promotion.getPromotionCondition().getBrands(),
                        DrlOperator.DRL_OPERATOR_OR,
                        "brandCode"));
        stTemplate.add("excludedSkusCondition",
                drlConditions.generateNotCondition(
                        promotion.getPromotionCondition().getExcludedVariants(),
                        DrlOperator.DRL_OPERATOR_AND,
                        "variantId"));

        return stTemplate.render();

    }

    private String wmDiscountByBrand(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/wm-discount_by_brand.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionWm(template, promotion);
        if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("brandCondition",
                    drlConditions.generateCondition(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR,
                            "brandCode"));
        } else if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("brandCondition",
                    drlConditions.generateCondition(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR,
                            "variantId"));
        } else if (CampaignEnum.COLLECTION.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("brandCondition",
                    drlConditions.generateCondition(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR,
                            "collection"));
        } else if (CampaignEnum.CATEGORY.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("brandCondition",
                    drlConditions.generateCondition(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR,
                            "categoryCode"));
        }

       /* stTemplate.add("excludedSkusCondition",
                drlConditions.generateNotCondition(
                        promotion.getPromotionCondition().getExcludedVariants(),
                        DrlOperator.DRL_OPERATOR_AND,
                        "variantId"));*/

        stTemplate.add("excludedSkusCondition",
                drlConditions.generateNotConditionExcluded(
                        promotion.getPromotionCondition()));

        return stTemplate.render();

    }

    private String itmOptionToBuy(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/itm-option_to_buy.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionItm(template, promotion);

        stTemplate.add("condition",
                drlConditions.generateCondition(
                        promotion.getPromotionCondition().getVariants(),
                        DrlOperator.DRL_OPERATOR_OR,
                        "variantId"));
        return stTemplate.render();
    }


    private String wmOptionToBuy(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/wm-option_to_buy.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionWm(template, promotion);

        stTemplate.add("condition",
                drlConditions.generateCondition(
                        promotion.getPromotionCondition().getVariants(),
                        DrlOperator.DRL_OPERATOR_OR,
                        "variantId"));
        return stTemplate.render();
    }

    private String itmSpecificTime(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/itm-specific_time.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionItm(template, promotion);

        stTemplate.add("brandCondition",
                drlConditions.generateCondition(
                        promotion.getPromotionCondition().getBrands(),
                        DrlOperator.DRL_OPERATOR_OR,
                        "brandCode"));
        stTemplate.add("excludedSkusCondition",
                drlConditions.generateNotCondition(
                        promotion.getPromotionCondition().getExcludedVariants(),
                        DrlOperator.DRL_OPERATOR_AND,
                        "variantId"));
        stTemplate.add("timePromotion",
                drlConditions.generateTimePromotion4(promotion.getPromotionCondition().getStartTime(),
                        promotion.getPromotionCondition().getEndTime()));
        return stTemplate.render();
    }

    private String wmSpecificTime(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/wm-specific_time.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionWm(template, promotion);

        stTemplate.add("brandCondition",
                drlConditions.generateCondition(
                        promotion.getPromotionCondition().getBrands(),
                        DrlOperator.DRL_OPERATOR_OR,
                        "brandCode"));
        stTemplate.add("excludedSkusCondition",
                drlConditions.generateNotCondition(
                        promotion.getPromotionCondition().getExcludedVariants(),
                        DrlOperator.DRL_OPERATOR_AND,
                        "variantId"));
        stTemplate.add("timePromotion",
                drlConditions.generateTimePromotion4(promotion.getPromotionCondition().getStartTime(),
                        promotion.getPromotionCondition().getEndTime()));
        return stTemplate.render();
    }

    private String optionToBuyOrWemallFreebieItm(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/promotionForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductItm(template, promotion);

        stTemplate.add("compareSkuPromotion",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        promotion.getPromotionCondition().getVariants(),
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
        return stTemplate.render();
    }

    private String optionToBuyOrWemallFreebieWm(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/promotionForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductWm(template, promotion);

        stTemplate.add("compareSkuPromotion",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        promotion.getPromotionCondition().getVariants(),
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
        return stTemplate.render();
    }

    private String itmProductFreebie(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/promotionForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductItm(template, promotion);

        if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("compareSkuPromotion",
                    drlConditions.generateEqualsConditionInDrlTemplate(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
        } else if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("compareSkuPromotion",
                    drlConditions.generateEqualsConditionInDrlTemplate(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR, "$brandVariant"));
        } else {
            return "";
        }
        return stTemplate.render();
    }

    private String itmDiscountByBrandOrSpecificTime(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/promotionForProductTP2.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductItm(template, promotion);

        stTemplate.add("compareBrandPromotion",
                drlConditions.generateEqualsConditionInDrlTemplate(promotion.getPromotionCondition().getBrands(),
                        DrlOperator.DRL_OPERATOR_OR, "$brandVariant"));
        stTemplate.add("compareNotSkuPromotion",
                drlConditions.generateRuleNotProductCondition(promotion.getPromotionCondition().getExcludedVariants(),
                        DrlOperator.DRL_OPERATOR_AND, "$productVariant"));
        return stTemplate.render();
    }

    private String promotionForProductBundleItm(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/bundleForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductItm(template, promotion);

        stTemplate.add("compareSkuPromotion",
                drlConditions.generateRuleProductBundleCondition(promotion.getPromotionCondition().getBundleVariant(),
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
        return stTemplate.render();
    }

    private String promotionForProductMNPItm(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/mnpForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductItm(template, promotion);

        stTemplate.add("compareSkuPromotion",
                drlConditions.generateRuleProductMNPCondition(promotion.getPromotionCondition(),
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
        return stTemplate.render();
    }

    private String promotionForProductFreebieItm(Promotion promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/freebieForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductItm(template, promotion);

        stTemplate.add("compareSkuPromotion",
                drlConditions.generateRuleProductFreebieCondition(promotion.getPromotionCondition(),
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
        return stTemplate.render();
    }

    private String promotionForProductBundleWm(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/bundleForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductWm(template, promotion);

        stTemplate.add("compareSkuPromotion",
                drlConditions.generateRuleProductBundleCondition(promotion.getPromotionCondition().getBundleVariant(),
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
        return stTemplate.render();
    }

    private String wmDiscountByBrandOrSpecificTime(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/promotionForProductTP2.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductWm(template, promotion);


        stTemplate.add("compareBrandPromotion",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        promotion.getPromotionCondition().getCriteriaValue(),
                        DrlOperator.DRL_OPERATOR_OR, "$brandVariant"));

        stTemplate.add("category",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        promotion.getPromotionCondition().getCriteriaValue(),
                        DrlOperator.DRL_OPERATOR_OR, "$category"));


        stTemplate.add("compareNotSkuPromotion",
                drlConditions.generateRuleNotProductCondition(promotion.getPromotionCondition().getExcludedVariants(),
                        DrlOperator.DRL_OPERATOR_AND, "$productVariant"));
        return stTemplate.render();
    }

    private String wmDiscountSpecificTime(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/promotionForProductTP2.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductWm(template, promotion);


        stTemplate.add("compareBrandPromotion",
                drlConditions.generateEqualsConditionInDrlTemplate(
                        promotion.getPromotionCondition().getBrands(),
                        DrlOperator.DRL_OPERATOR_OR, "$brandVariant"));


        stTemplate.add("compareNotSkuPromotion",
                drlConditions.generateRuleNotProductCondition(promotion.getPromotionCondition().getExcludedVariants(),
                        DrlOperator.DRL_OPERATOR_AND, "$productVariant"));
        return stTemplate.render();
    }

    private String itmBundle(Promotion promotion) throws URISyntaxException, IOException {

        Path path = Paths.get(getClass().getResource("/rules/itm-bundle.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionItm(template, promotion);
        stTemplate.add("primaryVariant",
                "variantId == \""
                        + promotion.getPromotionCondition().getBundleVariant().get(0).getBundleVariant()
                        + "\""
        );
        stTemplate.add("bundleSkuCondition",
                drlConditions.generateBundleSkuCondition(
                        promotion.getPromotionCondition().getBundleVariant(),
                        DrlOperator.DRL_OPERATOR_AND,
                        "$variant"));

        stTemplate.add("bundleSkuToArray", drlConditions.generateArrayBundleCondition(
                promotion.getPromotionCondition().getBundleVariant(),
                "variantId"));

        stTemplate.add("note", StringEscapeUtils.escapeJava(promotion.getPromotionCondition().getNote()));
        stTemplate.add("noteEn", StringEscapeUtils.escapeJava(promotion.getPromotionCondition().getNoteEn()));
        return stTemplate.render();
    }

    private String wmProductFreebie(PromotionWM promotion, String content) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/promotionForProductTP1.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionForProductWm(template, promotion);

        if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(promotion.getPromotionCondition().getCriteriaType())) {
            if (CampaignEnum.WM_DISCOUNT_BY_CODE.getContent().equalsIgnoreCase(content)) {
                List<String> variants = promotion.getPromotionCondition().getDiscountCodeCriteriaValue().stream()
                        .map(DiscountCodeCriteriaValue::getVariantId).collect(Collectors.toList());

                stTemplate.add("compareSkuPromotion",
                        drlConditions.generateEqualsConditionInDrlTemplate(
                                variants,
                                DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
            } else {
                stTemplate.add("compareSkuPromotion",
                        drlConditions.generateEqualsConditionInDrlTemplate(
                                promotion.getPromotionCondition().getCriteriaValue(),
                                DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
            }
        } else if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("compareSkuPromotion",
                    drlConditions.generateEqualsConditionInDrlTemplate(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR, "$brandVariant"));
        } else if (CampaignEnum.COLLECTION.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("compareSkuPromotion",
                    drlConditions.generateEqualsConditionInDrlTemplate(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR, "$collection"));
        } else if (CampaignEnum.CATEGORY.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())) {
            stTemplate.add("compareSkuPromotion",
                    drlConditions.generateEqualsConditionInDrlTemplate(
                            promotion.getPromotionCondition().getCriteriaValue(),
                            DrlOperator.DRL_OPERATOR_OR, "$category"));
        } else {
            return "";
        }
        return stTemplate.render();
    }

    private String wmBundle(PromotionWM promotion) throws URISyntaxException, IOException {

        Path path = Paths.get(getClass().getResource("/rules/wm-bundle.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionWm(template, promotion);
        stTemplate.add("primaryVariant",
                "variantId == \""
                        + promotion.getPromotionCondition().getBundleVariant().get(0).getBundleVariant()
                        + "\""
        );
        stTemplate.add("bundleSkuCondition",
                drlConditions.generateBundleSkuCondition(
                        promotion.getPromotionCondition().getBundleVariant(),
                        DrlOperator.DRL_OPERATOR_AND,
                        "$variant"));

        stTemplate.add("bundleSkuToArray", drlConditions.generateArrayBundleCondition(
                promotion.getPromotionCondition().getBundleVariant(),
                "variantId"));

        stTemplate.add("note", StringEscapeUtils.escapeJava(promotion.getPromotionCondition().getNote()));
        stTemplate.add("noteEn", StringEscapeUtils.escapeJava(promotion.getPromotionCondition().getNoteEn()));
        return stTemplate.render();
    }

    private String itmDiscountByCode(Promotion promotion) throws URISyntaxException, IOException {

        Path path = Paths.get(getClass().getResource("/rules/itm-discount_by_code.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionItm(template, promotion);
        stTemplate.add("codeGroupId", promotion.getPromotionCondition().getCodeGroupId() == null ? 0
                : promotion.getPromotionCondition().getCodeGroupId());
        stTemplate.add("functionName", drlConditions.generateNameFunctionCheckCode(promotion.getId()));
        stTemplate.add("criteriaCondition",
                drlConditions.generateCriteriaCondition(
                        promotion.getPromotionCondition().getCriteriaType(),
                        promotion.getPromotionCondition().getCriteriaValue()
                ));
        stTemplate.add("maxDiscountValue",
                promotion.getPromotionCondition().getMaxDiscountValue() == null ? 0.0
                        : promotion.getPromotionCondition().getMaxDiscountValue());

        stTemplate.add("excludedCondition",
                drlConditions.generateNotConditionExcluded(
                        promotion.getPromotionCondition()));
        return stTemplate.render();
    }

    private String wmDiscountByCode(PromotionWM promotion) throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getResource("/rules/wm-discount_by_code.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionWm(template, promotion);
        stTemplate.add("codeGroupId", promotion.getPromotionCondition().getCodeGroupId() == null ? 0
                : promotion.getPromotionCondition().getCodeGroupId());
        stTemplate.add("functionName", drlConditions.generateNameFunctionCheckCode(promotion.getId()));
        stTemplate.add("criteriaCondition",
                drlConditions.generateCriteriaConditionDiscountByCode(promotion.getPromotionCondition()));
        stTemplate.add("maxDiscountValue",
                promotion.getPromotionCondition().getMaxDiscountValue() == null ? 0.0
                        : promotion.getPromotionCondition().getMaxDiscountValue());

        stTemplate.add("excludedCondition",
                drlConditions.generateNotConditionExcluded(
                        promotion.getPromotionCondition()));
        return stTemplate.render();
    }

    private String itmMNP(Promotion promotion) throws URISyntaxException, IOException {

        Path path = Paths.get(getClass().getResource("/rules/itm-mnp.st").toURI());
        String template = new String(Files.readAllBytes(path));
        ST stTemplate = buildCommonPromotionItm(template, promotion);

        stTemplate.add("primaryVariant", drlConditions.generateMNPPrimaryCondition(
                promotion.getPromotionCondition().getMnpVariants().get(0),
                DrlOperator.DRL_OPERATOR_OR,
                "variantId"));

        stTemplate.add("bundleSkuToArray", drlConditions.generateArrayBundleCondition(
                promotion.getPromotionCondition().getBundleVariant(),
                "variantId"));

        stTemplate.add("mnpBundle", drlConditions.generateMNPBundleCondition(
                promotion.getPromotionCondition().getMnpVariants()));

        stTemplate.add("note", StringEscapeUtils.escapeJava(promotion.getPromotionCondition().getNote()));
        stTemplate.add("noteEn", StringEscapeUtils.escapeJava(promotion.getPromotionCondition().getNoteEn()));
        return stTemplate.render();
    }

}
