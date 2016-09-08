package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.models.DealCondition;
import com.ascend.campaign.models.DealCriteriaValue;
import com.ascend.campaign.models.SuperDeal;
import com.ascend.campaign.models.VariantDeal;
import com.ascend.campaign.models.VariantDealDetail;
import lombok.NonNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DealUtil {

    @NonNull
    DecimalUtil decimalUtil;

    @Autowired
    public DealUtil(DecimalUtil decimalUtil) {
        this.decimalUtil = decimalUtil;
    }

    public VariantDeal setPromotionPrice(
            List<Deal> dealList, VariantDealDetail variantDealDetail, Double normalPrice) {
        VariantDeal variantDeal = new VariantDeal();
        variantDeal.setVariantID(variantDealDetail.getVariant());
        variantDeal.setPromotionPrice(normalPrice);

        setPromotionPriceAndDetailByDeal(dealList, variantDealDetail, normalPrice, variantDeal);
        setPromotionPriceToNullIfNotMatchDeail(normalPrice, variantDeal);

        return variantDeal;
    }

    private void setPromotionPriceAndDetailByDeal(List<Deal> dealList, VariantDealDetail variantDealDetail,
                                                  Double normalPrice, VariantDeal variantDeal) {
        dealList.forEach(deal -> {
            DealCondition dealCondition = JSONUtil.parseToDealCondition(deal.getConditionData());
            deal.setDealCondition(dealCondition);
            setVvriantDealDetailData(variantDealDetail, normalPrice, variantDeal, deal, dealCondition);
        });
    }

    private void setVvriantDealDetailData(VariantDealDetail variantDealDetail, Double normalPrice,
                                          VariantDeal variantDeal, Deal deal, DealCondition dealCondition) {
        if (isCriteriaConditionNotNull(dealCondition)) {
            List<String> criteriaValue = dealCondition.getCriteriaValue() == null
                    ? new ArrayList() : dealCondition.getCriteriaValue();
            if (CampaignEnum.CATEGORY.getContent().equalsIgnoreCase(dealCondition.getCriteriaType())
                    && criteriaValue.contains(variantDealDetail.getCategory())) {
                setPromotionPriceAndVariantDealDetail(variantDealDetail, normalPrice, variantDeal, deal);
            } else if (CampaignEnum.COLLECTION.getContent().equalsIgnoreCase(dealCondition.getCriteriaType())
                    && criteriaValue.stream()
                    .filter(variantDealDetail.getCollection()::contains)
                    .collect(Collectors.toList()).size() > 0) {
                setPromotionPriceAndVariantDealDetail(variantDealDetail, normalPrice, variantDeal, deal);
            } else if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(dealCondition.getCriteriaType())
                    && criteriaValue.contains(variantDealDetail.getBrand())) {
                setPromotionPriceAndVariantDealDetail(variantDealDetail, normalPrice, variantDeal, deal);
            } else if (CampaignEnum.PRODUCT.getContent().equalsIgnoreCase(dealCondition.getCriteriaType())
                    && criteriaValue.contains(variantDealDetail.getProduct())) {
                setPromotionPriceAndVariantDealDetail(variantDealDetail, normalPrice, variantDeal, deal);
            } else if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(dealCondition.getCriteriaType())
                    && criteriaValue.contains(variantDealDetail.getVariant())) {
                setPromotionPriceAndVariantDealDetail(variantDealDetail, normalPrice, variantDeal, deal);
            } else if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(dealCondition.getCriteriaType())
                    && dealCondition.getCriteriaVariants() != null) {
                setVariantDealDetailSuperDeal(variantDeal, deal);
            }
        }
    }

    private void setPromotionPriceToNullIfNotMatchDeail(Double normalPrice, VariantDeal variantDeal) {
        if (Objects.equals(variantDeal.getPromotionPrice(), normalPrice)) {
            variantDeal.setPromotionPrice(null);
        }
    }

    private void setVariantDealDetailSuperDeal(VariantDeal variantDeal, Deal deal) {
        Optional<DealCriteriaValue> dealCriteriaValue = deal.getDealCondition()
                .getCriteriaVariants()
                .stream()
                .filter(x -> x.getVariantId().equalsIgnoreCase(variantDeal.getVariantID()))
                .findFirst();
        if (dealCriteriaValue.isPresent() && dealCriteriaValue.get().getPromotionPrice() != null) {
            variantDeal.setPromotionPrice(decimalUtil.roundTo2Decimals(
                    calculationPromotionPriceDiscountFixedVariant(
                            dealCriteriaValue.get().getPromotionPrice(),
                            variantDeal.getPromotionPrice())));
            variantDeal.setPromotionId(deal.getId());
            variantDeal.setPromotionName(deal.getName());
        }
    }

    private void setPromotionPriceAndVariantDealDetail(VariantDealDetail variantDealDetail, Double normalPrice,
                                                       VariantDeal variantDeal, Deal deal) {
        if (isContainExcludedCriteria(deal.getDealCondition(), variantDealDetail)) {
            variantDeal.setPromotionPrice(variantDeal.getPromotionPrice());
        } else {
            double before = variantDeal.getPromotionPrice();
            variantDeal.setPromotionPrice(decimalUtil.roundTo2Decimals(
                    calculationPromotionPrice(deal.getDealCondition(),
                            variantDeal.getPromotionPrice(),
                            normalPrice)));
            if (variantDeal.getPromotionPrice() < before) {
                variantDeal.setPromotionId(deal.getId());
                variantDeal.setPromotionName(deal.getName());
            }
        }
    }

    private double calculationPromotionPrice(DealCondition dealCondition, Double promotionPrice, Double normalPrice) {
        double promotionPriceReturn;
        if (dealCondition.getDiscountFixed() != null) {
            if (dealCondition.getDiscountFixed() < promotionPrice) {
                promotionPriceReturn = dealCondition.getDiscountFixed();
            } else {
                promotionPriceReturn = promotionPrice;
            }
        } else {
            double discountPercent = normalPrice * dealCondition.getDiscountPercent() / 100;
            if ((normalPrice - discountPercent) < promotionPrice) {
                promotionPriceReturn = normalPrice - discountPercent;
            } else {
                promotionPriceReturn = promotionPrice;
            }
        }

        return promotionPriceReturn;
    }

    private boolean isContainExcludedCriteria(
            DealCondition dealCondition, VariantDealDetail variantDealDetail) {
        List<String> excludedCriteriaValue = dealCondition.getExcludedCriteriaValue() == null
                ? new ArrayList<>() : dealCondition.getExcludedCriteriaValue();
        Boolean contain = false;
        if (CampaignEnum.COLLECTION.getContent().equalsIgnoreCase(dealCondition.getExcludedCriteriaType())) {
            contain = excludedCriteriaValue.stream()
                    .filter(variantDealDetail.getCollection()::contains)
                    .collect(Collectors.toList()).size() > 0;
        } else if (CampaignEnum.CATEGORY.getContent().equalsIgnoreCase(dealCondition.getExcludedCriteriaType())) {
            contain = excludedCriteriaValue.contains(variantDealDetail.getCategory());
        } else if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(dealCondition.getExcludedCriteriaType())) {
            contain = excludedCriteriaValue.contains(variantDealDetail.getVariant());
        } else if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(dealCondition.getExcludedCriteriaType())) {
            contain = excludedCriteriaValue.contains(variantDealDetail.getBrand());
        } else if (CampaignEnum.PRODUCT.getContent().equalsIgnoreCase(dealCondition.getExcludedCriteriaType())) {
            contain = excludedCriteriaValue.contains(variantDealDetail.getProduct());
        }
        return contain;
    }

    private boolean isCriteriaConditionNotNull(DealCondition dealCondition) {
        return dealCondition.getCriteriaType() != null
                && dealCondition.getCriteriaValue() != null
                || dealCondition.getCriteriaType() != null
                && dealCondition.getCriteriaVariants() != null;
    }

    public Double calculationPromotionPriceDiscountFixedVariant(
            Double discountFixed, Double promotionPrice) {
        double promotionPriceReturn;
        if (discountFixed < promotionPrice) {
            promotionPriceReturn = discountFixed;
        } else {
            promotionPriceReturn = promotionPrice;
        }

        return promotionPriceReturn;
    }

    public Boolean isLive(Boolean isEnable, Date start, Date end) {
        if (isEnable != null && start != null && end != null) {
            DateTime startDT = new DateTime(start);
            DateTime endDT = new DateTime(end);
            return isEnable && startDT.isBeforeNow() && endDT.isAfterNow();
        } else {
            return false;
        }
    }

    public List<SuperDeal> getAllSuperDealByTodayDeal(List<Deal> deals) {
        List<SuperDeal> superDeals = new ArrayList<>();
        deals.forEach(d -> superDeals.addAll(mapDealCriteriaValueToSuperDeal(d)));

        Map<String, SuperDeal> stringSuperDealMap = new HashMap<>();
        superDeals.forEach(superDeal -> {
            if (stringSuperDealMap.get(superDeal.getVariantId()) == null
                    || superDeal.getDealPrice() < stringSuperDealMap.get(superDeal.getVariantId()).getDealPrice()) {
                stringSuperDealMap.put(superDeal.getVariantId(), superDeal);
            }
        });

        return new ArrayList<>(stringSuperDealMap.values());
    }

    public List<SuperDeal> getAllSuperDealByTomorrowDeal(List<Deal> deals) {
        List<SuperDeal> superDeals = new ArrayList<>();
        deals.forEach(d -> superDeals.addAll(mapDealCriteriaValueToSuperDeal(d)));
        return superDeals;
    }

    private List<SuperDeal> mapDealCriteriaValueToSuperDeal(Deal deal) {
        List<SuperDeal> superDeals = new ArrayList<>();
        deal.setDealCondition(JSONUtil.parseToDealCondition(deal.getConditionData()));
        deal.getDealCondition().getCriteriaVariants().forEach(c -> {
            SuperDeal superDeal = new SuperDeal();
            superDeal.setVariantId(c.getVariantId());
            superDeal.setRecommended(c.getRecommended());
            superDeal.setStartPeriod(deal.getStartPeriod());
            superDeal.setEndPeriod(deal.getEndPeriod());
            superDeal.setLimitAccount(deal.getDealCondition().getLimitAccount());
            superDeal.setLimitPerCart(deal.getDealCondition().getLimitItem());
            superDeal.setPartner(setPartNer(deal.getDealCondition()));
            superDeal.setPaymentType(deal.getDealCondition().getPaymentType());
            superDeal.setDealPrice(c.getPromotionPrice());
            superDeals.add(superDeal);
        });

        return superDeals;
    }

    private List<String> setPartNer(DealCondition conditionData) {
        List<String> partnerList = new ArrayList<>();
        if (conditionData.getTrueMoveHIcon() && conditionData.getTrueYouIcon()) {
            partnerList.addAll(Arrays.asList(CampaignEnum.TRUE_YOU.getContent(),
                    CampaignEnum.TRUE_MOVE_H.getContent()));
        } else if (conditionData.getTrueMoveHIcon()) {
            partnerList.add(CampaignEnum.TRUE_MOVE_H.getContent());
        } else if (conditionData.getTrueYouIcon()) {
            partnerList.add(CampaignEnum.TRUE_YOU.getContent());
        }
        return partnerList;
    }
}
