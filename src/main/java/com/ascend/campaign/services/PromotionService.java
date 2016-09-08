package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.BaseEntity;
import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.exceptions.CampaignException;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.VariantDuplicateFreebie;
import com.ascend.campaign.repositories.CampaignRepo;
import com.ascend.campaign.repositories.PendingPromotionItmRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.repositories.PromotionWMRepo;
import com.ascend.campaign.repositories.UserRepo;
import com.ascend.campaign.utils.JSONUtil;
import com.ascend.campaign.utils.PromotionUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PromotionService {
    @NonNull
    private final PromotionItmRepo promotionItmRepo;

    @NonNull
    private final PromotionWMRepo promotionWMRepo;

    @NonNull
    private final UserPromotionService userPromotionService;

    @NonNull
    private final ConfigurationService configurationService;

    @NonNull
    private final UserRepo userRepo;

    @NonNull
    private final PromotionUtil promotionUtil;

    @NonNull
    private final PendingPromotionItmRepo pendingPromotionItmRepo;


    @NonNull
    private CampaignRepo campaignRepo;

    @Autowired
    public PromotionService(PromotionItmRepo promotionItmRepo,
                            PromotionWMRepo promotionWMRepo,
                            UserRepo userRepo,
                            UserPromotionService userPromotionService,
                            ConfigurationService configurationService,
                            PromotionUtil promotionUtil,
                            CampaignRepo campaignRepo,
                            PendingPromotionItmRepo pendingPromotionItmRepo) {
        this.promotionItmRepo = promotionItmRepo;
        this.promotionWMRepo = promotionWMRepo;
        this.userRepo = userRepo;
        this.configurationService = configurationService;
        this.userPromotionService = userPromotionService;
        this.promotionUtil = promotionUtil;
        this.campaignRepo = campaignRepo;
        this.pendingPromotionItmRepo = pendingPromotionItmRepo;
    }

    public Promotion createPromotionItruemart(Promotion promotion) {
        trimVariantsCondition(promotion);
        log.warn("content={\"activity\":\"Create Promotion ITM\", \"msg\":{}}", JSONUtil.toString(promotion));
        if (!checkPromotionType(promotion.getType(), CampaignEnum.ITRUEMART)) {
            throw new CampaignException(Errors.INVALID_PROMOTION_TYPE);
        }
        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(promotion.getCampaign().getId()))
                .orElseThrow(CampaignNotFoundException::new);
        promotion.setCampaign(campaign);
        promotion.setConditionData(JSONUtil.toString(promotion.getPromotionCondition()));

        return promotionItmRepo.saveAndFlush(promotion);
    }

    public PromotionWM createPromotionWemall(PromotionWM promotion) {
        log.warn("content={\"activity\":\"Create Promotion Wemall\", \"msg\":{}}", JSONUtil.toString(promotion));
        if (!checkPromotionType(promotion.getType(), CampaignEnum.WEMALL)) {
            throw new CampaignException(Errors.INVALID_PROMOTION_TYPE);
        }

        promotion.setConditionData(JSONUtil.toString(promotion.getPromotionCondition()));

        return promotionWMRepo.saveAndFlush(promotion);
    }

    private boolean checkPromotionType(String promotionType, CampaignEnum businessChannel) {
        log.info("content={\"activity\":\"Check Promotion Type\", \"msg\":\"{}\"}", promotionType);
        if (CampaignEnum.ITRUEMART.equals(businessChannel)) {
            List<String> promotionTypes = Arrays.asList(
                    PromotionTypeEnum.ITM_BUNDLE.getContent(),
                    PromotionTypeEnum.ITM_MNP.getContent(),
                    PromotionTypeEnum.ITM_DISCOUNT_BY_CODE.getContent(),
                    PromotionTypeEnum.ITM_DISCOUNT_PROMOTION.getContent(),
                    PromotionTypeEnum.ITM_FREEBIE.getContent(),
                    PromotionTypeEnum.ITM_OPTION_TO_BUY.getContent(),
                    PromotionTypeEnum.ITM_SPECIFIC_TIME.getContent());
            return promotionTypes.contains(promotionType);
        } else {
            List<String> promotionTypes = Arrays.asList(
                    CampaignEnum.WM_BUNDLE.getContent(),
                    CampaignEnum.WM_DISCOUNT_BY_CODE.getContent(),
                    CampaignEnum.WM_DISCOUNT_PROMOTION.getContent(),
                    CampaignEnum.WM_FREEBIE.getContent(),
                    CampaignEnum.WM_OPTION_TO_BUY.getContent(),
                    CampaignEnum.WM_SPECIFIC_TIME.getContent());
            return promotionTypes.contains(promotionType);
        }

    }

    public Page<Promotion> getAllPromotionsITM(
            Integer page, Integer perPage, Sort.Direction direction, String sort, Long searchID, String searchName,
            Boolean enable, Boolean disable, Boolean active, Boolean expired, String promotionType,
            String campaignName, Long campaignId) {

        PageRequest pageRequest = genPageRequest(page - 1, perPage, direction, sort);

        Specifications specificationsPromotions = promotionUtil.customerSpecificationsFilterPromotion(campaignId,
                searchID, searchName, promotionType, enable, disable, active, expired, campaignName);


        return findAllPromotionByCriteriaItm(specificationsPromotions, pageRequest);
    }

    public Page<PromotionWM> getAllPromotionsWM(
            Integer page, Integer perPage, Sort.Direction direction,
            String sort, Long searchID, String searchName, Boolean enable, Boolean active, String promotionType) {
        return findAllPromotionByCriteriaWm(searchID, searchName, enable, active, promotionType,
                page, perPage, direction, sort);
    }

    private PageRequest genPageRequest(int page, int number, Sort.Direction direction, String sort) {
        return new PageRequest(page, number, new Sort(direction, sort));
    }

    private Page<Promotion> findAllPromotionByCriteriaItm(Specifications promotionSpecifications,
                                                          PageRequest pageRequest) {
        Page<Promotion> promotionPage = promotionItmRepo.findAll(promotionSpecifications, pageRequest);
        promotionPage.getContent().forEach(p -> {
            p.setPromotionCondition(JSONUtil.parseToPromotionCondition(p.getConditionData()));
            p.setPromotionData(JSONUtil.parseToPromotionData(p.getDetailData()));
            p.setLive(promotionUtil.isLive(p.getEnable(), p.getStartPeriod(), p.getEndPeriod()));
        });

        return getPromotions(promotionPage);
    }

    private Page<Promotion> getPromotions(Page<Promotion> promotionPage) {

        List<PendingPromotion> pendingPromotionList = pendingPromotionItmRepo.findByPromotionIdIsNotNull();
        HashMap<Long, Integer> promotionHashMap = new HashMap<>();
        int[] idx = {0};
        promotionPage.getContent().stream().forEachOrdered(p -> promotionHashMap.put(p.getId(), idx[0]++));

        pendingPromotionList.forEach(pendingPromotion -> {
            Optional<Integer> indexUpdate = Optional.ofNullable(
                    promotionHashMap.get(pendingPromotion.getPromotionId()));
            if (indexUpdate.isPresent()) {
                Optional<Promotion> promotion = Optional.ofNullable(
                        promotionPage.getContent().get(indexUpdate.get()));
                if (promotion.isPresent()) {
                    promotion.get().setEnable(pendingPromotion.getEnable());
                    promotion.get().setConditionData(pendingPromotion.getConditionData());
                    promotion.get().setDescription(pendingPromotion.getDescription());
                    promotion.get().setDescriptionEn(pendingPromotion.getDescriptionEn());
                    promotion.get().setShortDescription(pendingPromotion.getShortDescription());
                    promotion.get().setShortDescriptionEn(pendingPromotion.getShortDescriptionEn());
                    promotion.get().setNameEn(pendingPromotion.getNameEn());
                    promotion.get().setEndPeriod(pendingPromotion.getEndPeriod());
                    promotion.get().setName(pendingPromotion.getName());
                    promotion.get().setRepeat(pendingPromotion.getRepeat());
                    promotion.get().setStartPeriod(pendingPromotion.getStartPeriod());
                    promotion.get().setType(pendingPromotion.getType());
                    promotion.get().setMember(pendingPromotion.getMember());
                    promotion.get().setNonMember(pendingPromotion.getNonMember());
                    promotion.get().setImgUrl(pendingPromotion.getImgUrl());
                    promotion.get().setImgThmUrl(pendingPromotion.getImgThmUrl());
                    promotion.get().setImgUrlEn(pendingPromotion.getImgUrlEn());
                    promotion.get().setImgThmUrlEn(pendingPromotion.getImgThmUrlEn());
                    promotion.get().setPendingStatus(pendingPromotion.getStatus());
                }
            }

        });
        promotionPage.getContent().forEach(p -> promotionUtil.setFilterStatusPromotion(p, DateTime.now().toDate()));
        return promotionPage;
    }

    private Page<PromotionWM> findAllPromotionByCriteriaWm(
            Long searchID, String searchName, Boolean enable, Boolean active,
            String promotionType, Integer page, Integer perPage,
            Sort.Direction direction, String sort) {

        Page<PromotionWM> promotionPage = promotionWMRepo.findAll(
                promotionUtil.filterPromotionCriteriaWm(searchID, searchName, enable, promotionType),
                genPageRequest(page - 1, perPage, direction, sort));

        promotionPage.getContent().forEach(p -> p.setPromotionCondition(
                JSONUtil.parseToPromotionCondition(p.getConditionData())));
        promotionPage.getContent().forEach(p -> p.setLive(
                promotionUtil.isLive(p.getEnable(), p.getStartPeriod(), p.getEndPeriod())));

        if (active != null) {
            promotionPage = promotionUtil.filterIsLivePromotionWm(promotionPage, active);
        }

        return promotionPage;
    }

    public Promotion getPromotionItm(Long promotionId) {
        Promotion promotion = Optional.ofNullable(promotionItmRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);
        Optional<PendingPromotion> pendingPromotion =
                Optional.ofNullable(pendingPromotionItmRepo.findByPromotionId(promotionId));
        if (pendingPromotion.isPresent()) {
            promotion.setPromotionData(JSONUtil.parseToPromotionData(pendingPromotion.get().getDetailData()));
        } else {
            promotion.setPromotionData(JSONUtil.parseToPromotionData(promotion.getDetailData()));
        }
        promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
        promotion.setLive(promotionUtil.isLive(promotion.getEnable(),
                promotion.getStartPeriod(), promotion.getEndPeriod()));

        return promotion;
    }

    public PromotionWM getPromotionWm(Long promotionId) {
        PromotionWM promotion = Optional.ofNullable(promotionWMRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);
        promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
        promotion.setLive(promotionUtil.isLive(promotion.getEnable(),
                promotion.getStartPeriod(), promotion.getEndPeriod()));

        return promotion;
    }

    public Promotion updatePromotionItm(Long promotionId, Promotion data) {
        trimVariantsCondition(data);

        log.warn("content={\"activity\":\"Update Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\", \"promotion\":{}}}",
                promotionId, JSONUtil.toString(data));
        Promotion promotion = Optional.ofNullable(promotionItmRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);

        promotion.setEnable(data.getEnable());
        promotion.setConditionData(JSONUtil.toString(data.getPromotionCondition()));
        promotion.setPromotionData(JSONUtil.parseToPromotionData(promotion.getDetailData()));
        promotion.setDescription(data.getDescription());
        promotion.setDescriptionEn(data.getDescriptionEn());
        promotion.setEndPeriod(data.getEndPeriod());
        promotion.setName(data.getName());
        promotion.setNameEn(data.getNameEn());
        promotion.setRepeat(data.getRepeat());
        promotion.setStartPeriod(data.getStartPeriod());
        promotion.setType(data.getType());
        promotion.setMember(data.getMember());
        promotion.setNonMember(data.getNonMember());
        promotion.setImgUrl(data.getImgUrl());
        promotion.setImgThmUrl(data.getImgThmUrl());
        promotion.setImgUrlEn(data.getImgUrlEn());
        promotion.setImgThmUrlEn(data.getImgThmUrlEn());
        promotion.setAppId(data.getAppId());
        promotion.setShortDescription(data.getShortDescription());
        promotion.setShortDescriptionEn(data.getShortDescriptionEn());
        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(promotion.getCampaign().getId()))
                .orElseThrow(CampaignNotFoundException::new);
        promotion.setCampaign(campaign);
        Promotion result = promotionItmRepo.saveAndFlush(promotion);
        result.setPromotionCondition(data.getPromotionCondition());

        return result;
    }

    private void trimVariantsCondition(Promotion data) {
        if (PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(data.getType())) {
            if (Optional.ofNullable(data.getPromotionCondition().getCriteriaValue()).isPresent()) {
                data.getPromotionCondition().getCriteriaValue().forEach(String::trim);
            }
            if (Optional.ofNullable(data.getPromotionCondition().getFreebieConditions()).isPresent()) {
                data.getPromotionCondition().getFreebieConditions().forEach(f -> f.getVariantId().trim());
            }
        }
    }

    public PromotionWM updatePromotionWm(Long promotionId, PromotionWM data) {
        log.warn("content={\"activity\":\"Update Promotion Wemall\", \"msg\":{\"promotion_id\":\"{}\", "
                + "\"promotion\":{}}}", promotionId, JSONUtil.toString(data));
        PromotionWM promotion = Optional.ofNullable(promotionWMRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);
        promotion.setEnable(data.getEnable());
        promotion.setConditionData(JSONUtil.toString(data.getPromotionCondition()));
        promotion.setDescription(data.getDescription());
        promotion.setDescriptionEn(data.getDescriptionEn());
        promotion.setEndPeriod(data.getEndPeriod());
        promotion.setName(data.getName());
        promotion.setNameEn(data.getNameEn());
        promotion.setRepeat(data.getRepeat());
        promotion.setStartPeriod(data.getStartPeriod());
        promotion.setType(data.getType());
        promotion.setMember(data.getMember());
        promotion.setNonMember(data.getNonMember());
        promotion.setImgUrl(data.getImgUrl());
        promotion.setImgThmUrl(data.getImgThmUrl());
        promotion.setImgUrlEn(data.getImgUrlEn());
        promotion.setImgThmUrlEn(data.getImgThmUrlEn());
        promotion.setAppId(data.getAppId());
        promotion.setShortDescription(data.getShortDescription());
        promotion.setShortDescriptionEn(data.getShortDescriptionEn());

        PromotionWM result = promotionWMRepo.saveAndFlush(promotion);
        result.setPromotionCondition(data.getPromotionCondition());

        return result;
    }

    public Promotion deletePromotionItm(Long promotionId) {
        log.warn("content={\"activity\":\"Delete Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        Promotion promotion = Optional.ofNullable(promotionItmRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);
        promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
        promotion.setPromotionData(JSONUtil.parseToPromotionData(promotion.getDetailData()));
        promotionItmRepo.delete(promotion);
        promotionItmRepo.flush();

        return promotion;
    }

    public PromotionWM deletePromotionWm(Long promotionId) {
        log.warn("content={\"activity\":\"Delete Promotion Wemall\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        PromotionWM promotion = Optional.ofNullable(promotionWMRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);
        promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
        promotionWMRepo.delete(promotion);
        promotionWMRepo.flush();

        return promotion;
    }

    public Promotion duplicatePromotionItm(Long promotionId) {
        log.warn("content={\"activity\":\"Duplicate Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        Promotion promotion = promotionItmRepo.getOne(promotionId);

        Promotion promotionDuplication = new Promotion();
        promotionDuplication.setEnable(promotion.getEnable());
        promotionDuplication.setConditionData(promotion.getConditionData());
        promotionDuplication.setDetailData(promotion.getDetailData());
        promotionDuplication.setDescription(promotion.getDescription());
        promotionDuplication.setDescriptionEn(promotion.getDescriptionEn());
        promotionDuplication.setShortDescription(promotion.getShortDescription());
        promotionDuplication.setShortDescriptionEn(promotion.getShortDescriptionEn());
        promotionDuplication.setNameEn(promotion.getNameEn());
        promotionDuplication.setEndPeriod(promotion.getEndPeriod());
        promotionDuplication.setName(promotion.getName());
        promotionDuplication.setRepeat(promotion.getRepeat());
        promotionDuplication.setStartPeriod(promotion.getStartPeriod());
        promotionDuplication.setType(promotion.getType());
        promotionDuplication.setMember(promotion.getMember());
        promotionDuplication.setNonMember(promotion.getNonMember());
        promotionDuplication.setImgUrl(promotion.getImgUrl());
        promotionDuplication.setImgThmUrl(promotion.getImgThmUrl());
        promotionDuplication.setImgUrlEn(promotion.getImgUrlEn());
        promotionDuplication.setImgThmUrlEn(promotion.getImgThmUrlEn());
        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(promotion.getCampaign().getId()))
                .orElseThrow(CampaignNotFoundException::new);
        promotion.setCampaign(campaign);

        Promotion result = promotionItmRepo.saveAndFlush(promotionDuplication);
        result.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotionDuplication.getConditionData()));

        return result;
    }

    public PromotionWM duplicatePromotionWm(Long promotionId) {
        log.warn("content={\"activity\":\"Duplicate Promotion Wemall\", \"msg\":{\"promotion_id\":\"{}\"}}",
                promotionId);
        PromotionWM promotion = promotionWMRepo.getOne(promotionId);

        PromotionWM promotionDuplication = new PromotionWM();
        promotionDuplication.setEnable(promotion.getEnable());
        promotionDuplication.setConditionData(promotion.getConditionData());
        promotionDuplication.setDescription(promotion.getDescription());
        promotionDuplication.setDescriptionEn(promotion.getDescriptionEn());
        promotionDuplication.setShortDescription(promotion.getShortDescription());
        promotionDuplication.setShortDescriptionEn(promotion.getShortDescriptionEn());
        promotionDuplication.setNameEn(promotion.getNameEn());
        promotionDuplication.setEndPeriod(promotion.getEndPeriod());
        promotionDuplication.setName(promotion.getName());
        promotionDuplication.setRepeat(promotion.getRepeat());
        promotionDuplication.setStartPeriod(promotion.getStartPeriod());
        promotionDuplication.setType(promotion.getType());
        promotionDuplication.setMember(promotion.getMember());
        promotionDuplication.setNonMember(promotion.getNonMember());
        promotionDuplication.setImgUrl(promotion.getImgUrl());
        promotionDuplication.setImgThmUrl(promotion.getImgThmUrl());
        promotionDuplication.setImgUrlEn(promotion.getImgUrlEn());
        promotionDuplication.setImgThmUrlEn(promotion.getImgThmUrlEn());

        PromotionWM result = promotionWMRepo.saveAndFlush(promotionDuplication);
        result.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotionDuplication.getConditionData()));

        return result;
    }

    public Promotion enablePromotionItm(Long promotionId) {
        log.warn("content={\"activity\":\"Enable Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        Promotion promotion = promotionItmRepo.getOne(promotionId);
        promotion.setEnable(true);

        return promotionItmRepo.saveAndFlush(promotion);
    }

    public PromotionWM enablePromotionWm(Long promotionId) {
        log.warn("content={\"activity\":\"Enable Promotion Wemall\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        PromotionWM promotion = promotionWMRepo.getOne(promotionId);
        promotion.setEnable(true);

        return promotionWMRepo.saveAndFlush(promotion);
    }

    public Promotion disablePromotionItm(Long promotionId) {
        log.warn("content={\"activity\":\"Disable Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        Promotion promotion = promotionItmRepo.getOne(promotionId);
        promotion.setEnable(false);

        return promotionItmRepo.saveAndFlush(promotion);
    }

    public PromotionWM disablePromotionWm(Long promotionId) {
        log.warn("content={\"activity\":\"Disable Promotion Wemall\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        PromotionWM promotion = promotionWMRepo.getOne(promotionId);
        promotion.setEnable(false);

        return promotionWMRepo.saveAndFlush(promotion);
    }

    public List<Promotion> getAllActivePromotionBusinessChannelItm() {
        List<Promotion> promotions = promotionItmRepo.findByEnable(true);
        promotions = promotions.stream().map(p -> {
            p.setLive(promotionUtil.isLive(p.getEnable(), p.getStartPeriod(), p.getEndPeriod()));
            p.setPromotionCondition(JSONUtil.parseToPromotionCondition(p.getConditionData()));
            p.setPromotionData(JSONUtil.parseToPromotionData(p.getDetailData()));
            return p;
        }).filter(p -> p.getCampaign().getEnable()).collect(Collectors.toList());

        return promotions;
    }

    public List<PromotionWM> getAllActivePromotionBusinessChannelWm() {
        List<PromotionWM> promotions = promotionWMRepo.findByEnable(true);
        promotions.forEach(p -> p.setPromotionCondition(JSONUtil.parseToPromotionCondition(p.getConditionData())));
        promotions.forEach(p -> p.setLive(promotionUtil.isLive(p.getEnable(), p.getStartPeriod(), p.getEndPeriod())));

        return promotions;
    }

    public List<VariantDuplicateFreebie> checkDuplicateCriteriaFreebie(String variantId, Long startPeriod,
                                                                       Long endPeriod, Long promotionId) {
        List<Promotion> promotions = promotionItmRepo.findPromotionsByDateTime(
                new DateTime(startPeriod).toString("yyyy-MM-dd HH:mm:ss"),
                new DateTime(endPeriod).toString("yyyy-MM-dd HH:mm:ss"));

        if (!promotions.isEmpty()) {
            List<Promotion> allFreebies = promotions.stream().filter(promotion -> isFreebie(promotion, promotionId))
                    .collect(Collectors.toList());

            List<String> variants = isValidateAndSplitBatchFreebie(variantId);

            return getCollectVariantDuplicateFreebieFromVariants(allFreebies, variants);
        }


        return new ArrayList<>();
    }

    private List<VariantDuplicateFreebie> getCollectVariantDuplicateFreebieFromVariants(List<Promotion> allFreebies,
                                                                                        List<String> variants) {
        return variants.stream().map(variant -> {
            VariantDuplicateFreebie variantDuplicateFreebie = new VariantDuplicateFreebie();
            variantDuplicateFreebie.setVariantId(variant);
            variantDuplicateFreebie.setDuplicatePromotionId(setVariantsDuplicatePromotionId(variant, allFreebies));
            return variantDuplicateFreebie;

        }).collect(Collectors.toList());
    }

    private List<Long> setVariantsDuplicatePromotionId(String variant, List<Promotion> finalPromotions) {
        return finalPromotions.stream().filter(promotion ->
                promotion.getPromotionCondition().getCriteriaValue()
                        .contains(variant)).map(BaseEntity::getId).collect(Collectors.toList());
    }

    private Boolean isFreebie(Promotion promotion, Long promotionId) {
        promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));

        if (Optional.ofNullable(promotionId).isPresent()) {
            return CampaignEnum.VARIANT.getContent().equalsIgnoreCase(
                    promotion.getPromotionCondition().getCriteriaType())
                    && PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(
                    promotion.getType())
                    && !promotion.getId().equals(promotionId);
        } else {
            return CampaignEnum.VARIANT.getContent().equalsIgnoreCase(
                    promotion.getPromotionCondition().getCriteriaType())
                    && PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(
                    promotion.getType());
        }
    }

    private List<String> isValidateAndSplitBatchFreebie(String productVariant) {
        if (isValidateBatch(productVariant)) {
            throw new CampaignException();
        } else {
            return Arrays.stream(productVariant.split(",")).collect(Collectors.toList());
        }
    }

    private boolean isValidateBatch(String productVariant) {
        return productVariant == null || productVariant.isEmpty();
    }
}
