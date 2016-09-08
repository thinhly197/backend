package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.MNPVariant;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.VariantFreebie;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PromotionUtil {

    @NonNull
    DecimalUtil decimalUtil;
    //public Function<? super PendingPromotion,?> pendingPeomorionToPromotion;

    @Autowired
    public PromotionUtil(DecimalUtil decimalUtil) {
        this.decimalUtil = decimalUtil;
    }

    public void trimVariantsAndToUpperCase(PromotionCondition promotionCondition) {
        trimAndUpperCaseFreebieConditions(promotionCondition);
        trimAndUpperCaseCriteriaValueFreebie(promotionCondition);
        trimAndUpperCaseBundleVariant(promotionCondition);
        trimAndUpperCaseMnpVariant(promotionCondition);
    }

    private void trimAndUpperCaseMnpVariant(PromotionCondition promotionCondition) {
        if (Optional.ofNullable(promotionCondition.getMnpVariants()).isPresent()) {
            List<MNPVariant> newMnpVariants = promotionCondition.getMnpVariants().stream()
                    .map(mnpVariant -> {
                        mnpVariant.setMnpVariants(mnpVariant.getMnpVariants().trim().toUpperCase());
                        return mnpVariant;
                    }).collect(Collectors.toList());
            promotionCondition.setMnpVariants(newMnpVariants);
        }
    }

    private void trimAndUpperCaseBundleVariant(PromotionCondition promotionCondition) {
        if (Optional.ofNullable(promotionCondition.getBundleVariant()).isPresent()) {
            List<BundleVariant> newBundleVariants = promotionCondition.getBundleVariant().stream()
                    .map(bundleVariant -> {
                        bundleVariant.setBundleVariant(bundleVariant.getBundleVariant().trim().toUpperCase());
                        return bundleVariant;
                    }).collect(Collectors.toList());
            promotionCondition.setBundleVariant(newBundleVariants);
        }
    }

    private void trimAndUpperCaseCriteriaValueFreebie(PromotionCondition promotionCondition) {
        if (Optional.ofNullable(promotionCondition.getCriteriaValue()).isPresent()) {
            promotionCondition.setCriteriaValue(promotionCondition.getCriteriaValue().stream()
                    .map(v -> v.trim().toUpperCase()).collect(Collectors.toList()));
        }
    }

    private void trimAndUpperCaseFreebieConditions(PromotionCondition promotionCondition) {
        if (Optional.ofNullable(promotionCondition.getFreebieConditions()).isPresent()) {
            List<VariantFreebie> newVariantFreebies = promotionCondition.getFreebieConditions().stream()
                    .map(variantFreebie -> {
                        variantFreebie.setVariantId(variantFreebie.getVariantId().trim().toUpperCase());
                        return variantFreebie;
                    }).collect(Collectors.toList());
            promotionCondition.setFreebieConditions(newVariantFreebies);
        }
    }

    public Page<PromotionWM> filterIsLivePromotionWm(Page<PromotionWM> promotions, Boolean active) {
        return new PageImpl(promotions.getContent()
                .stream()
                .filter(x -> x.getLive() == active)
                .collect(Collectors.toList())
        );
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

    public Specifications customerSpecificationsFilterCampaign(Long searchID, String searchName, Long startPeriod,
                                                               Long endPeriod, Boolean enable, Boolean disabled,
                                                               Boolean expired, Boolean live) {
        return Specifications.where(getSpecificationsCampaignStatus(enable, disabled, expired, live))
                .and(getSpecificationsCampaignFilter(searchID, searchName, startPeriod, endPeriod));
    }

    private Specification<Campaign> getSpecificationsCampaignStatus(Boolean enable, Boolean disabled,
                                                                    Boolean expired, Boolean live) {
        Date currentTime = getCurrentDateTime();
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (enable) {
                Predicate enablePredicate = cb.equal(root.get("enable"), true);
                Predicate startPeriodPredicate = cb.greaterThanOrEqualTo(root.get("startPeriod"),
                        currentTime);
                Predicate endPeriodPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate enablePredicates = cb.and(enablePredicate, startPeriodPredicate,
                        endPeriodPredicate);
                predicates.add(enablePredicates);
            }
            if (disabled) {
                Predicate enablePredicate = cb.equal(root.get("enable"), false);
                Predicate endPeriodPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate disabledPredicate = cb.and(enablePredicate, endPeriodPredicate);
                predicates.add(disabledPredicate);
            }
            if (expired) {
                Predicate endpiredPredicate = cb.lessThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate startPeriodPredicate = cb.lessThanOrEqualTo(root.get("startPeriod"),
                        currentTime);
                Predicate expiredPredicates = cb.and(endpiredPredicate, startPeriodPredicate);
                predicates.add(expiredPredicates);
            }
            if (live) {
                Predicate enablePredicate = cb.equal(root.get("enable"), true);
                Predicate startPeriodPredicate = cb.lessThanOrEqualTo(root.get("startPeriod"), currentTime);
                Predicate endPeriodPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"), currentTime);
                Predicate isLivePredicate = cb.and(enablePredicate, startPeriodPredicate, endPeriodPredicate);
                predicates.add(isLivePredicate);
            }
            if (predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };

    }

    private Date getCurrentDateTime() {
        return DateTime.now().toDate();
    }

    private Specification<Campaign> getSpecificationsCampaignFilter(Long searchID, String searchName, Long startPeriod,
                                                                    Long endPeriod) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (Optional.ofNullable(searchID).isPresent()) {
                predicates.add(cb.equal(root.get("id"), searchID));
            }
            if (!StringUtils.isEmpty(searchName)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
            }
            if (startPeriod != null && endPeriod != null) {
                Date expiry = new Date(startPeriod);
                Date expiry2 = new Date(endPeriod);
                predicates.add(cb.lessThanOrEqualTo(root.get("startPeriod"), expiry2));
                predicates.add(cb.greaterThanOrEqualTo(root.get("endPeriod"), expiry));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    public Specification<PromotionWM> filterPromotionCriteriaWm(Long searchID, String searchName, Boolean enable,
                                                                String promotionType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (searchID != null) {
                predicates.add(cb.equal(root.get("id"), searchID));
            }
            if (!StringUtils.isEmpty(searchName)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
            }
            if (enable != null) {
                predicates.add(cb.equal(root.get("enable"), enable));
            }
            if (!StringUtils.isEmpty(promotionType)) {
                predicates.add(cb.equal(root.get("type"), promotionType));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }


    public Promotion pendingPromotionToPromotion(PendingPromotion pd) {
        Promotion promotion = new Promotion();
        promotion.setEnable(pd.getEnable());

        if (Optional.ofNullable(pd.getPromotionCondition()).isPresent()) {
            trimVariantsAndToUpperCase(pd.getPromotionCondition());
        }

        promotion.setPromotionCondition(pd.getPromotionCondition());
        promotion.setConditionData(JSONUtil.toString(pd.getPromotionCondition()));
        promotion.setDetailData(JSONUtil.toString(pd.getConditionData()));
        promotion.setPromotionData(pd.getPromotionData());
        promotion.setDescription(pd.getDescription());
        promotion.setDescriptionEn(pd.getDescriptionEn());
        promotion.setEndPeriod(pd.getEndPeriod());
        promotion.setName(pd.getName());
        promotion.setNameEn(pd.getNameEn());
        promotion.setRepeat(pd.getRepeat());
        promotion.setStartPeriod(pd.getStartPeriod());
        promotion.setType(pd.getType());
        promotion.setMember(pd.getMember());
        promotion.setNonMember(pd.getNonMember());
        promotion.setImgUrl(pd.getImgUrl());
        promotion.setImgThmUrl(pd.getImgThmUrl());
        promotion.setImgUrlEn(pd.getImgUrlEn());
        promotion.setImgThmUrlEn(pd.getImgThmUrlEn());
        promotion.setAppId(pd.getAppId());
        promotion.setShortDescription(pd.getShortDescription());
        promotion.setShortDescriptionEn(pd.getShortDescriptionEn());
        promotion.setCampaign(pd.getCampaign());
        promotion.setPendingStatus(pd.getStatus());
        promotion.setId(pd.getPromotionId());
        return promotion;
    }

    public PendingPromotion promotionToPendingPromotion(Promotion pd, String pendingStatus) {
        PendingPromotion pendingPromotion = new PendingPromotion();
        pendingPromotion.setEnable(pd.getEnable());
        pendingPromotion.setPromotionCondition(pd.getPromotionCondition());
        pendingPromotion.setConditionData(JSONUtil.toString(pd.getPromotionCondition()));

        if (Optional.ofNullable(pd.getPromotionCondition()).isPresent()) {
            trimVariantsAndToUpperCase(pd.getPromotionCondition());
        }

        if (Optional.ofNullable(pd.getPromotionData()).isPresent()) {
            pendingPromotion.setPromotionData(pd.getPromotionData());
            pendingPromotion.setDetailData(JSONUtil.toString(pd.getPromotionData()));
        }

        pendingPromotion.setDescription(pd.getDescription());
        pendingPromotion.setDescriptionEn(pd.getDescriptionEn());
        pendingPromotion.setEndPeriod(pd.getEndPeriod());
        pendingPromotion.setName(pd.getName());
        pendingPromotion.setNameEn(pd.getNameEn());
        pendingPromotion.setRepeat(pd.getRepeat());
        pendingPromotion.setStartPeriod(pd.getStartPeriod());
        pendingPromotion.setType(pd.getType());
        pendingPromotion.setMember(pd.getMember());
        pendingPromotion.setNonMember(pd.getNonMember());
        pendingPromotion.setImgUrl(pd.getImgUrl());
        pendingPromotion.setImgThmUrl(pd.getImgThmUrl());
        pendingPromotion.setImgUrlEn(pd.getImgUrlEn());
        pendingPromotion.setImgThmUrlEn(pd.getImgThmUrlEn());
        pendingPromotion.setAppId(pd.getAppId());
        pendingPromotion.setShortDescription(pd.getShortDescription());
        pendingPromotion.setShortDescriptionEn(pd.getShortDescriptionEn());
        pendingPromotion.setCampaign(pd.getCampaign());
        pendingPromotion.setStatus(pendingStatus);
        pendingPromotion.setPromotionId(pd.getId());
        return pendingPromotion;
    }

    public void setFilterStatusPromotion(Promotion promotion, Date currentTime) {
        if (Optional.ofNullable(promotion.getEndPeriod()).orElse(currentTime).before(currentTime)) {
            promotion.setFilterStatus(CampaignEnum.FILTER_EXPIRED.getContent());
        } else if (Optional.ofNullable(promotion.getLive()).orElse(false)) {
            promotion.setFilterStatus(CampaignEnum.FILTER_LIVE.getContent());
        } else if (Optional.ofNullable(promotion.getEnable()).orElse(false)) {
            promotion.setFilterStatus(CampaignEnum.FILTER_ENABLED.getContent());
        } else {
            promotion.setFilterStatus(CampaignEnum.FILTER_DISABLE.getContent());
        }
    }

    public Specifications customerSpecificationsFilterPromotion(Long campaignId, Long searchID,
                                                                String searchName, String promotionType,
                                                                Boolean enable, Boolean disable, Boolean live,
                                                                Boolean expired, String campaignName) {

        return Specifications.where(getSpecificationsPromotionStatus(enable, disable, expired, live))
                .and(getSpecificationsPromotionFilter(searchID, searchName, campaignId, campaignName, promotionType));
    }

    private Specification<Promotion> getSpecificationsPromotionFilter(Long searchID, String searchName,
                                                                      Long campaignId, String campaignName,
                                                                      String promotionType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (Optional.ofNullable(searchID).isPresent()) {
                predicates.add(cb.equal(root.get("id"), searchID));
            }
            if (!StringUtils.isEmpty(searchName)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
            }
            if (Optional.ofNullable(campaignId).isPresent()) {
                predicates.add(cb.equal(root.get("campaign").get("id"), campaignId));
            }
            if (!StringUtils.isEmpty(campaignName)) {
                predicates.add(cb.like(cb.lower(root.get("campaign").get("name")), "%"
                        + campaignName.toLowerCase() + "%"));
            }
            if (!StringUtils.isEmpty(promotionType)) {
                predicates.add(cb.equal(root.get("type"), promotionType));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<Promotion> getSpecificationsPromotionStatus(Boolean enable, Boolean disable,
                                                                      Boolean expired, Boolean live) {
        Date currentTime = getCurrentDateTime();
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (enable) {
                Predicate enableEnablePredicate = cb.equal(root.get("enable"), true);
                Predicate startPeriodEnablePredicate = cb.greaterThanOrEqualTo(root.get("startPeriod"),
                        currentTime);
                Predicate endPeriodEnablePredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate enablePredicate = cb.and(enableEnablePredicate, startPeriodEnablePredicate,
                        endPeriodEnablePredicate);
                predicates.add(enablePredicate);
            }
            if (disable) {
                Predicate enableDisabled = cb.equal(root.get("enable"), false);
                Predicate endPeriodDisabledPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate disabledPredicate = cb.and(enableDisabled, endPeriodDisabledPredicate);
                predicates.add(disabledPredicate);
            }
            if (expired) {
                Predicate startPeriodExpiredPredicate = cb.lessThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate endPeriodExpiredPredicate = cb.lessThanOrEqualTo(root.get("startPeriod"),
                        currentTime);
                Predicate expiredPredicate = cb.and(startPeriodExpiredPredicate, endPeriodExpiredPredicate);
                predicates.add(expiredPredicate);
            }
            if (live) {
                Predicate enablePredicate = cb.equal(root.get("enable"), true);
                Predicate startPeriodPredicate = cb.lessThanOrEqualTo(root.get("startPeriod"), currentTime);
                Predicate endPeriodPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"), currentTime);
                Predicate isLivePredicate = cb.and(enablePredicate, startPeriodPredicate, endPeriodPredicate);
                predicates.add(isLivePredicate);
            }
            if (predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };

    }
}
