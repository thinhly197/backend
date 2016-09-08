package com.ascend.campaign.utils;


import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.DrlOperator;
import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.DiscountCodeCriteriaValue;
import com.ascend.campaign.models.MNPVariant;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.PromotionParams;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DrlConditions {

    public String generateCondition(List<String> products, DrlOperator operator, String key) {
        return generateRuleCondition(products, operator, key, DrlOperator.DRL_OPERATOR_EQUALS.getSignal());
    }

    public String generateNotCondition(List<String> products, DrlOperator operator, String key) {
        return generateRuleCondition(products, operator, key, DrlOperator.DRL_OPERATOR_NOT_EQUALS.getSignal());
    }

    private String generateRuleCondition(List<String> products, DrlOperator operator, String key, String operation) {
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(products)) {
            products = products.stream().map(String::trim).collect(Collectors.toList());
            for (String product : products) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = key + " " + operation + " \"" + product + "\"";
                } else {
                    productFormat = " " + operator.getSignal() + " " + key + " " + operation + " \"" + product + "\"";
                }
                result.append(productFormat);
            }
        }

        return result.toString();
    }

    public String generateNameFunction(Long name) {
        return "buildPromotion" + name;
    }

    public String generateNameFunctionCheckCode(Long name) {
        return "checkCodePromotion" + name;
    }

    public String generateUserType(Boolean member, Boolean nonMember) {
        if (!member) {
            return "customerType == \"non-user\",";
        } else if (!nonMember) {
            return "customerType == \"user\",";
        } else {
            return "customerType == \"user\" || customerType == \"non-user\" ,";
        }
    }

    public String generateBundleTypeOrPromotionCode(String condition, String key) {
        if ("".equalsIgnoreCase(condition)) {
            return "";
        }
        return "eval(\"" + condition + "\".equals(" + key + "))";
    }

    public String generateBundlePricePlanCode(PromotionParams promotionParams, String key) {
        if (promotionParams == null) {
            return "";
        }
        return "eval(\"" + promotionParams.getPricePlanCode() + "\".equals(" + key + "))";
    }

    public Object generateTimePromotion4(String startTime, String endTime) {
        List<String> st = Splitter.on(":").trimResults().omitEmptyStrings().splitToList(startTime);
        List<String> end = Splitter.on(":").trimResults().omitEmptyStrings().splitToList(endTime);

        LocalDate date = LocalDate.of(1970, 1, 1);
        LocalTime startDateTime = LocalTime.of(Integer.valueOf(st.get(0)), Integer.valueOf(st.get(1)), 0);
        LocalTime endDateTime = LocalTime.of(Integer.valueOf(end.get(0)), Integer.valueOf(end.get(1)), 0);

        LocalDateTime startDt = LocalDateTime.of(date, startDateTime);
        LocalDateTime endDt = LocalDateTime.of(date, endDateTime);

        String result = "eval($cart.getEpochCurrentTime() > "
                + startDt.toEpochSecond(ZoneOffset.UTC)
                + " && $cart.getEpochCurrentTime()< "
                + endDt.toEpochSecond(ZoneOffset.UTC) + ")\n";

        log.debug("Result: {}", result);

        return result;
    }

    public String generateEqualsConditionInDrlTemplate(List<String> products, DrlOperator drlOperatorOr, String key) {
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(products)) {
            for (String product : products) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = " \"" + product + "\".equals(" + key + ")";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " \"" + product + "\".equals(" + key + ")";
                }
                result.append(productFormat);
            }
        }
        return result.toString();
    }

    public String generateRuleNotProductCondition(List<String> products, DrlOperator drlOperatorOr, String key) {
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(products)) {
            for (String product : products) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = "eval( !\"" + product + "\".equals(" + key + ")";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " !\"" + product + "\".equals($productVariant)";
                }
                result.append(productFormat);
            }
            result.append(")");
        }
        return result.toString();
    }

    public String generateBundleSkuCondition(List<BundleVariant> bundleVariants, DrlOperator drlOperatorOr,
                                             String skuId) {
        StringBuilder result = new StringBuilder("");
        bundleVariants.forEach(x -> {
            String deleteBlank = x.getBundleVariant().replaceAll(" ", "");
            x.setBundleVariant(deleteBlank);
        });

        if (CollectionUtils.isNotEmpty(bundleVariants)) {
            for (int i = 1; i <= bundleVariants.size() - 1; i++) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = skuId + " " + "contains" + " \""
                            + bundleVariants.get(i).getBundleVariant() + "\"";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " " + skuId + " "
                            + "contains" + " \"" + bundleVariants.get(i).getBundleVariant()
                            + "\"";
                }

                result.append(productFormat);
            }
        }

        return result.toString();
    }

    public String generateMNPSkuCondition(List<MNPVariant> mnpVariants, DrlOperator drlOperatorOr,
                                          String skuId) {
        StringBuilder result = new StringBuilder("");
        mnpVariants.forEach(x -> {
            String deleteBlank = x.getMnpVariants().replaceAll(" ", "");
            x.setMnpVariants(deleteBlank);
        });

        if (CollectionUtils.isNotEmpty(mnpVariants)) {
            for (int i = 1; i <= mnpVariants.size() - 1; i++) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = skuId + " " + "contains" + " \""
                            + mnpVariants.get(i).getMnpVariants() + "\"";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " " + skuId + " "
                            + "contains" + " \"" + mnpVariants.get(i).getMnpVariants()
                            + "\"";
                }

                result.append(productFormat);
            }
        }

        return result.toString();
    }

    public String generateArrayBundleCondition(List<BundleVariant> bundleVariant, String skuId) {
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(bundleVariant)) {
            for (BundleVariant product : bundleVariant) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = skuId + " " + DrlOperator.DRL_OPERATOR_EQUALS.getSignal()
                            + " \"" + product.getBundleVariant() + "\"";
                } else {
                    productFormat = " " + DrlOperator.DRL_OPERATOR_OR.getSignal() + " " + skuId + " "
                            + DrlOperator.DRL_OPERATOR_EQUALS.getSignal() + " \"" + product.getBundleVariant() + "\"";
                }

                result.append(productFormat);
            }
        }

        return result.toString();
    }

    public String generateRuleProductBundleCondition(List<BundleVariant> bundleVariant, DrlOperator drlOperatorOr,
                                                     String key) {
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(bundleVariant)) {
            for (BundleVariant product : bundleVariant) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = " \"" + product.getBundleVariant() + "\".equals(" + key + ")";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " \"" + product.getBundleVariant()
                            + "\".equals(" + key + ")";
                }
                result.append(productFormat);
            }
        }
        return result.toString();
    }

    public String generateRuleProductMNPCondition(PromotionCondition promotionCondition, DrlOperator drlOperatorOr,
                                                  String key) {
        List<String> mnpVariantList = getMnpVariantList(promotionCondition);
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(mnpVariantList)) {
            for (String variant : mnpVariantList) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = " \"" + variant + "\".equals(" + key + ")";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " \"" + variant
                            + "\".equals(" + key + ")";
                }
                result.append(productFormat);
            }
        }
        return result.toString();
    }


    public String generateRuleProductFreebieCondition(PromotionCondition promotionCondition, DrlOperator drlOperatorOr,
                                                      String key) {
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(promotionCondition.getCriteriaValue())) {
            List<String> variants =
                    promotionCondition.getCriteriaValue().stream().map(String::trim).collect(Collectors.toList());
            for (String variant : variants) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = " \"" + variant + "\".equals(" + key + ")";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " \"" + variant
                            + "\".equals(" + key + ")";
                }
                result.append(productFormat);
            }
        }
        return result.toString();
    }

    private List<String> getMnpVariantList(PromotionCondition promotionCondition) {
        List<String> variants = new ArrayList<>();
        Optional<List<MNPVariant>> mnpVariants = Optional.ofNullable(promotionCondition.getMnpVariants());
        if (mnpVariants.isPresent()) {
            String[] primaryList = mnpVariants.get().get(0).getMnpVariants().split(",");
            List<String> itemList = new ArrayList<>(Arrays.asList(primaryList));
            variants.addAll(itemList);
            for (int i = 1; i <= mnpVariants.get().size() - 1; i++) {
                variants.add(mnpVariants.get().get(i).getMnpVariants().trim());
            }
        }

        return variants;
    }

    public List<String> stringToList(String appId) {
        if (appId == null) {
            return null;
        }

        return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(appId
                .replaceAll("\\[", "")
                .replaceAll("\\]", "")
                .replaceAll("\"", ""));

    }

    public String generateCriteriaCondition(String criteriaType, List<String> criteriaValues) {
        if (criteriaValues != null && !criteriaValues.isEmpty()) {
            criteriaValues = criteriaValues.stream().map(String::trim).collect(Collectors.toList());
        }
        String productFormat = "";
        if ("brand".equalsIgnoreCase(criteriaType)) {
            productFormat = generateStringCriteriaCondition(criteriaValues, "brandCode");
        } else if ("category".equalsIgnoreCase(criteriaType)) {
            productFormat = generateStringCriteriaCondition(criteriaValues, "categoryCode");
        } else if ("variant".equalsIgnoreCase(criteriaType)) {
            productFormat = generateStringCriteriaCondition(criteriaValues, "variantId");
        } else if ("collection".equalsIgnoreCase(criteriaType)) {
            productFormat = generateStringCriteriaCondition(criteriaValues, "collection");
        } else if ("cart".equalsIgnoreCase(criteriaType)) {
            productFormat = "variantId != null";
        }
        return productFormat;
    }

    public String generateStringCriteriaCondition(List<String> criteriaValues, String key) {
        if (criteriaValues != null && !criteriaValues.isEmpty()) {
            criteriaValues = criteriaValues.stream().map(String::trim).collect(Collectors.toList());
        }
        StringBuilder result = new StringBuilder("");
        for (String criteriaValue : criteriaValues) {
            String productFormat;
            if (result.length() == 0) {
                productFormat = key + " " + DrlOperator.DRL_OPERATOR_EQUALS.getSignal() + " \"" + criteriaValue + "\"";
            } else {
                productFormat = " " + DrlOperator.DRL_OPERATOR_OR.getSignal()
                        + key + " " + DrlOperator.DRL_OPERATOR_EQUALS.getSignal() + " \"" + criteriaValue + "\"";
            }
            result.append(productFormat);
        }

        return result.toString();
    }

    public String generateCriteriaConditionFreebieAndExcluded(PromotionCondition promotionCondition) {
        return "$sumQuantity : Double()  from accumulate(Product(("
                + generateCriteriaCondition(promotionCondition.getCriteriaType(), promotionCondition.getCriteriaValue())
                + ") && (" + generateNotConditionExcluded(promotionCondition)
                + "),$quantity : quantity) from $products, sum( $quantity ) )";

    }

    public String generateCriteriaConditionFreebieByValueAndExcluded(PromotionCondition promotionCondition) {
        return "$sumQuantity : Double()  from accumulate(Product(("
                + generateCriteriaCondition(promotionCondition.getCriteriaType(), promotionCondition.getCriteriaValue())
                + ") && (" + generateNotConditionExcluded(promotionCondition)
                + "),$value : getDiscountPriceDrl() , "
                + "$quantity : quantity) from $products, sum( $value * $quantity ) )";
    }

    public String generateNotConditionExcluded(PromotionCondition promotionCondition) {
        List<String> fixed = Collections.singletonList("");
        if (promotionCondition.getExcludedBrands() == null) {
            promotionCondition.setExcludedBrands(fixed);
        }
        if (promotionCondition.getExcludedCategories() == null) {
            promotionCondition.setExcludedCategories(fixed);
        }
        if (promotionCondition.getExcludedCollections() == null) {
            promotionCondition.setExcludedCollections(fixed);
        }
        if (promotionCondition.getExcludedVariants() == null) {
            promotionCondition.setExcludedVariants(fixed);
        }
        StringBuilder result = new StringBuilder("");
        String and = " " + DrlOperator.DRL_OPERATOR_AND.getSignal() + " ";
        String excluded = generateRuleCondition(promotionCondition.getExcludedVariants(),
                DrlOperator.DRL_OPERATOR_AND, "variantId", DrlOperator.DRL_OPERATOR_NOT_EQUALS.getSignal());
        result.append(excluded);
        result.append(and);
        String excludedBrand = generateRuleCondition(promotionCondition.getExcludedBrands(),
                DrlOperator.DRL_OPERATOR_AND, "brandCode", DrlOperator.DRL_OPERATOR_NOT_EQUALS.getSignal());
        result.append(excludedBrand);
        result.append(and);
        String excludedCate = generateRuleCondition(promotionCondition.getExcludedCategories(),
                DrlOperator.DRL_OPERATOR_AND, "categoryCode", DrlOperator.DRL_OPERATOR_NOT_EQUALS.getSignal());
        result.append(excludedCate);
        result.append(and);
        String excludedCol = generateRuleCondition(promotionCondition.getExcludedCollections(),
                DrlOperator.DRL_OPERATOR_AND, "collection", DrlOperator.DRL_OPERATOR_NOT_EQUALS.getSignal());
        result.append(excludedCol);
        return result.toString();
    }

    public String generateCriteriaConditionDiscountByCode(PromotionCondition promotionCondition) {

        String productFormat = "";
        if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productFormat = generateStringCriteriaCondition(promotionCondition.getCriteriaValue(), "brandCode");
        } else if (CampaignEnum.CATEGORY.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productFormat = generateStringCriteriaCondition(promotionCondition.getCriteriaValue(), "categoryCode");
        } else if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productFormat = generateStringCriteriaConditionDiscountByCode(
                    promotionCondition.getDiscountCodeCriteriaValue(), "variantId");
        } else if (CampaignEnum.COLLECTION.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productFormat = generateStringCriteriaCondition(promotionCondition.getCriteriaValue(), "collection");
        } else if (CampaignEnum.CART.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productFormat = "variantId != null";
        }
        return productFormat;
    }

    private String generateStringCriteriaConditionDiscountByCode(
            List<DiscountCodeCriteriaValue> discountCodeCriteriaValue, String variantId) {

        StringBuilder result = new StringBuilder("");
        for (DiscountCodeCriteriaValue criteriaValue : discountCodeCriteriaValue) {
            String productFormat;
            if (result.length() == 0) {
                productFormat = variantId + " "
                        + DrlOperator.DRL_OPERATOR_EQUALS.getSignal() + " \"" + criteriaValue.getVariantId() + "\"";
            } else {
                productFormat = " " + DrlOperator.DRL_OPERATOR_OR.getSignal()
                        + variantId + " "
                        + DrlOperator.DRL_OPERATOR_EQUALS.getSignal() + " \"" + criteriaValue.getVariantId() + "\"";
            }
            result.append(productFormat);
        }

        return result.toString();
    }

    public String generateMNPPrimaryCondition(MNPVariant mnpPrimary, DrlOperator drlOperatorOr,
                                              String variantId) {
        List<String> mnpPrimaryList = getPrimaryMNPList(mnpPrimary);
        StringBuilder result = new StringBuilder("");
        if (CollectionUtils.isNotEmpty(mnpPrimaryList)) {
            for (String variant : mnpPrimaryList) {
                String productFormat;
                if (result.length() == 0) {
                    productFormat = variantId.trim() + " " + "==" + " \"" + variant + "\"";
                } else {
                    productFormat = " " + drlOperatorOr.getSignal() + " "
                            + variantId.trim() + " " + "==" + " \"" + variant + "\"";
                }
                result.append(productFormat);
            }
        }

        return result.toString();
    }

    private List<String> getPrimaryMNPList(MNPVariant mnpPrimary) {
        String[] primaryList = mnpPrimary.getMnpVariants().split(",");
        return new ArrayList<>(Arrays.asList(primaryList)).stream().map(String::trim).collect(Collectors.toList());

    }

    public String generateMNPBundleCondition(List<MNPVariant> mnpVariants) {
        if (mnpVariants.size() > 1) {
            return "$variant : List() from accumulate(Product($variantId : variantId)from "
                    + "$products,collectList($variantId))\n$countMNPSku : Double() from accumulate(Product("
                    + generateMNPSkuCondition(mnpVariants, DrlOperator.DRL_OPERATOR_AND, "$variant")
                    + ",$quantity : quantity) from $products, sum( $quantity ) )\neval($countMNPSku > 0)";
        } else {
            return "";
        }
    }
}

