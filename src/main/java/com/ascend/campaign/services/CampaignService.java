package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.BaseEntity;
import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.exceptions.DuplicateException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.CampaignResponse;
import com.ascend.campaign.models.VariantDuplicateFreebie;
import com.ascend.campaign.repositories.CampaignRepo;
import com.ascend.campaign.repositories.PendingPromotionItmRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.utils.DecimalUtil;
import com.ascend.campaign.utils.DroolsUtil;
import com.ascend.campaign.utils.JSONUtil;
import com.ascend.campaign.utils.PromotionUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CampaignService {
    @NonNull
    private final CampaignRepo campaignRepo;

    @NonNull
    private final PromotionItmRepo promotionItmRepo;

    @NonNull
    private final DroolsUtil droolsUtil;

    @NonNull
    private final PendingPromotionItmRepo pendingPromotionItmRepo;

    DecimalUtil decimalUtil = new DecimalUtil();

    PromotionUtil promotionUtil = new PromotionUtil(decimalUtil);

    @Autowired
    public CampaignService(CampaignRepo campaignRepo, PromotionItmRepo promotionItmRepo, DroolsUtil droolsUtil,
                           PromotionUtil promotionUtil, PendingPromotionItmRepo pendingPromotionItmRepo) {
        this.campaignRepo = campaignRepo;
        this.promotionItmRepo = promotionItmRepo;
        this.droolsUtil = droolsUtil;
        this.promotionUtil = promotionUtil;
        this.pendingPromotionItmRepo = pendingPromotionItmRepo;
    }

    public Campaign createCampaign(Campaign campaign) {
        log.warn("content={\"activity\":\"Create Campaign\", \"msg\":{}}", JSONUtil.toString(campaign));
        return campaignRepo.saveAndFlush(campaign);
    }

    public Page<Campaign> getAllCampaignITM(
            Integer page, Integer perPage, Sort.Direction direction, String sort, Long searchID, String searchName,
            Boolean enable, Boolean disabled, Boolean expired, Long startPeriod, Long endPeriod, Boolean live) {

        return findAllCampaignByCriteriaItm(searchID, searchName, enable, disabled, expired,
                page, perPage, direction, sort, startPeriod, endPeriod, live);
    }

    private Page<Campaign> findAllCampaignByCriteriaItm(
            Long searchID, String searchName, Boolean enable, Boolean disabled, Boolean expired, Integer page,
            Integer perPage, Sort.Direction direction, String sort, Long startPeriod, Long endPeriod, Boolean live) {
        PageRequest pageRequest = genPageRequest(page - 1, perPage, direction, sort);
        Page<Campaign> campaigns = campaignRepo.findAll(promotionUtil.customerSpecificationsFilterCampaign(searchID,
                searchName, startPeriod, endPeriod, enable, disabled, expired, live), pageRequest);
        campaigns.getContent().forEach(c -> c.setLive(
                promotionUtil.isLive(c.getEnable(), c.getStartPeriod(), c.getEndPeriod())));
        campaigns.getContent().forEach(campaign -> setFilterStatusCampaign(campaign, DateTime.now().toDate()));
        return campaigns;
    }

    private void setFilterStatusCampaign(Campaign campaign, Date currentTime) {
        if (Optional.ofNullable(campaign.getEndPeriod()).orElse(currentTime).before(currentTime)) {
            campaign.setFilterStatus(CampaignEnum.FILTER_EXPIRED.getContent());
        } else if (Optional.ofNullable(campaign.getLive()).orElse(false)) {
            campaign.setFilterStatus(CampaignEnum.FILTER_LIVE.getContent());
        } else if (Optional.ofNullable(campaign.getEnable()).orElse(false)) {
            campaign.setFilterStatus(CampaignEnum.FILTER_ENABLED.getContent());
        } else {
            campaign.setFilterStatus(CampaignEnum.FILTER_DISABLE.getContent());
        }
    }

    private PageRequest genPageRequest(int page, int number, Sort.Direction direction, String sort) {
        return new PageRequest(page, number, new Sort(direction, sort));
    }

    @Transactional(rollbackOn = Exception.class)
    public Campaign deleteCampaignItm(Long campaignId) {
        List<Promotion> promotions = Optional.ofNullable(promotionItmRepo.findByCampaignId(campaignId))
                .orElseThrow(PromotionNotFoundException::new);
        promotionItmRepo.delete(promotions);
        promotionItmRepo.flush();

        List<PendingPromotion> pendingPromotions = Optional.ofNullable(
                pendingPromotionItmRepo.findByCampaignId(campaignId))
                .orElseThrow(PromotionNotFoundException::new);
        pendingPromotionItmRepo.delete(pendingPromotions);
        pendingPromotionItmRepo.flush();

        log.info("content={\"activity\":\"Delete Promotion in campaign\", \"msg\":{\"campaign_id\":\"{}\"}}",
                campaignId);

        log.warn("content={\"activity\":\"Delete Campaign\", \"msg\":{\"campaign_id\":\"{}\"}}", campaignId);
        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(campaignId))
                .orElseThrow(CampaignNotFoundException::new);
        campaignRepo.delete(campaign);
        campaignRepo.flush();
        return campaign;

    }

    @Transactional(rollbackOn = Exception.class)
    public Campaign duplicateCampaignItm(Long campaignId, Campaign campaign) {
        log.warn("content={\"activity\":\"Duplicate Campaign\", \"msg\":{\"campaign_id\":\"{}\"}}", campaignId);

        Campaign campaignDuplicated = Optional.ofNullable(campaignRepo.saveAndFlush(campaign))
                .orElseThrow(DuplicateException::new);

        List<Promotion> promotionList = Optional.ofNullable(promotionItmRepo.findByCampaignId(campaignId))
                .orElseThrow(PromotionNotFoundException::new);

        List<PendingPromotion> pendingPromotionList = Optional.ofNullable(
                pendingPromotionItmRepo.findByCampaignId(campaignId))
                .orElseThrow(PromotionNotFoundException::new);

        List<Long> pendingPromotionIds = pendingPromotionList.stream().map(PendingPromotion::getPromotionId)
                .collect(Collectors.toList());

        List<Promotion> promotionNotHavePending = promotionList.stream().filter(promotion ->
                !pendingPromotionIds.contains(promotion.getId())).collect(Collectors.toList());

        duplicateNewPromotion(promotionNotHavePending, campaignDuplicated);

        List<Promotion> promotionHavePending = promotionList.stream().filter(promotion ->
                pendingPromotionIds.contains(promotion.getId())).collect(Collectors.toList());

        duplicateNewPendingPromotion(promotionHavePending, campaignDuplicated, pendingPromotionList);

        return campaignDuplicated;
    }

    private void duplicateNewPendingPromotion(List<Promotion> promotionHavePending, Campaign campaignDuplicated,
                                              List<PendingPromotion> pendingPromotionList) {

        promotionHavePending.forEach(promotion -> {
            Promotion promotionDuplicateResult = savePromotionDuplication(campaignDuplicated, promotion);

            Optional<PendingPromotion> pendingPromotion = pendingPromotionList.stream().filter(pendingPromotion1
                    -> pendingPromotion1.getPromotionId().equals(promotion.getId())).findFirst();
            savePendingPromotionDuplicate(campaignDuplicated, promotionDuplicateResult, pendingPromotion);

        });
    }

    private void savePendingPromotionDuplicate(Campaign campaignDuplicated, Promotion promotionDuplicateResult,
                                               Optional<PendingPromotion> pendingPromotion) {
        if (pendingPromotion.isPresent()) {
            PendingPromotion pendingPromotionDuplication = new PendingPromotion();
            pendingPromotionDuplication.setEnable(false);
            pendingPromotionDuplication.setConditionData(pendingPromotion.get().getConditionData());
            pendingPromotionDuplication.setDetailData(pendingPromotion.get().getDetailData());
            pendingPromotionDuplication.setDescription(pendingPromotion.get().getDescription());
            pendingPromotionDuplication.setDescriptionEn(pendingPromotion.get().getDescriptionEn());
            pendingPromotionDuplication.setShortDescription(pendingPromotion.get().getShortDescription());
            pendingPromotionDuplication.setShortDescriptionEn(pendingPromotion.get().getShortDescriptionEn());
            pendingPromotionDuplication.setNameEn(pendingPromotion.get().getNameEn());
            pendingPromotionDuplication.setEndPeriod(campaignDuplicated.getEndPeriod());
            pendingPromotionDuplication.setName(pendingPromotion.get().getName());
            pendingPromotionDuplication.setRepeat(pendingPromotion.get().getRepeat());
            pendingPromotionDuplication.setStartPeriod(campaignDuplicated.getStartPeriod());
            pendingPromotionDuplication.setType(pendingPromotion.get().getType());
            pendingPromotionDuplication.setMember(pendingPromotion.get().getMember());
            pendingPromotionDuplication.setNonMember(pendingPromotion.get().getNonMember());
            pendingPromotionDuplication.setImgUrl(pendingPromotion.get().getImgUrl());
            pendingPromotionDuplication.setImgThmUrl(pendingPromotion.get().getImgThmUrl());
            pendingPromotionDuplication.setImgUrlEn(pendingPromotion.get().getImgUrlEn());
            pendingPromotionDuplication.setImgThmUrlEn(pendingPromotion.get().getImgThmUrlEn());
            pendingPromotionDuplication.setCampaign(campaignDuplicated);
            pendingPromotionDuplication.setPromotionId(promotionDuplicateResult.getId());
            pendingPromotionDuplication.setStatus(pendingPromotion.get().getStatus());
            pendingPromotionItmRepo.saveAndFlush(pendingPromotionDuplication);
        }
    }

    private Promotion savePromotionDuplication(Campaign campaignDuplicated, Promotion promotion) {
        Promotion promotionDuplication = getNewPromotionForDuplicate(campaignDuplicated, promotion);
        return promotionItmRepo.saveAndFlush(promotionDuplication);
    }

    private void duplicateNewPromotion(List<Promotion> promotions, Campaign campaign) {
        log.info("content={\"activity\":\"Duplicate Campaign\", \"msg\":{\"campaign_id\":\"{}\", \"promotion\":{}}}",
                campaign.getId(), JSONUtil.toString(promotions));
        List<Promotion> promotionList = new ArrayList<>();
        promotions.forEach(promotion -> {
            Promotion promotionDuplication = getNewPromotionForDuplicate(campaign, promotion);
            promotionList.add(promotionDuplication);
        });

        Optional.ofNullable(promotionItmRepo.save(promotionList)).orElseThrow(DuplicateException::new);
        promotionItmRepo.flush();

    }

    private Promotion getNewPromotionForDuplicate(Campaign campaign, Promotion promotion) {
        Promotion promotionDuplication = new Promotion();
        promotionDuplication.setEnable(false);
        promotionDuplication.setConditionData(promotion.getConditionData());
        promotionDuplication.setDetailData(promotion.getDetailData());
        promotionDuplication.setDescription(promotion.getDescription());
        promotionDuplication.setDescriptionEn(promotion.getDescriptionEn());
        promotionDuplication.setShortDescription(promotion.getShortDescription());
        promotionDuplication.setShortDescriptionEn(promotion.getShortDescriptionEn());
        promotionDuplication.setNameEn(promotion.getNameEn());
        promotionDuplication.setEndPeriod(campaign.getEndPeriod());
        promotionDuplication.setName(promotion.getName());
        promotionDuplication.setRepeat(promotion.getRepeat());
        promotionDuplication.setStartPeriod(campaign.getStartPeriod());
        promotionDuplication.setType(promotion.getType());
        promotionDuplication.setMember(promotion.getMember());
        promotionDuplication.setNonMember(promotion.getNonMember());
        promotionDuplication.setImgUrl(promotion.getImgUrl());
        promotionDuplication.setImgThmUrl(promotion.getImgThmUrl());
        promotionDuplication.setImgUrlEn(promotion.getImgUrlEn());
        promotionDuplication.setImgThmUrlEn(promotion.getImgThmUrlEn());
        promotionDuplication.setCampaign(campaign);
        return promotionDuplication;
    }

    public Campaign updateCampaignItm(Long campaignId, Campaign campaign) {
        log.info("content={\"activity\":\"Update Campaign\", \"msg\":{\"campaign_id\":\"{}\", \"campaign\":{}}}",
                campaignId, JSONUtil.toString(campaign));
        Campaign campaign1 = Optional.ofNullable(campaignRepo.findOne(campaignId))
                .orElseThrow(CampaignNotFoundException::new);
        campaign1.setName(campaign.getName());
        campaign1.setNameTranslation(campaign.getNameTranslation());
        campaign1.setStartPeriod(campaign.getStartPeriod());
        campaign1.setEndPeriod(campaign.getEndPeriod());
        campaign1.setEnable(campaign.getEnable());
        campaign1.setDetail(campaign.getDetail());
        campaign1.setDetailTranslation(campaign.getDetailTranslation());
        campaign1.setUpdatedAt(null);

        return campaignRepo.saveAndFlush(campaign1);
    }

    public CampaignResponse getCampaignItm(Long campaignId) {
        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(campaignId))
                .orElseThrow(CampaignNotFoundException::new);

        return getCampaignResponse(campaignId, campaign);
    }

    private CampaignResponse getCampaignResponse(Long campaignId, Campaign campaign) {
        CampaignResponse campaignResponse = new CampaignResponse();
        campaignResponse.setCreatedAt(campaign.getCreatedAt());
        campaignResponse.setCreatedBy(campaign.getCreatedBy());
        campaignResponse.setDetail(campaign.getDetail());
        campaignResponse.setDetailTranslation(campaign.getDetailTranslation());
        campaignResponse.setEnable(campaign.getEnable());
        campaignResponse.setStartPeriod(campaign.getStartPeriod());
        campaignResponse.setEndPeriod(campaign.getEndPeriod());
        campaignResponse.setId(campaign.getId());
        campaignResponse.setName(campaign.getName());
        campaignResponse.setNameTranslation(campaign.getNameTranslation());
        List<Promotion> promotionList = Optional.ofNullable(promotionItmRepo.findByCampaignId(campaignId))
                .orElseThrow(CampaignNotFoundException::new);
        if (promotionList.size() > 0) {
            Date minDate = promotionList.stream().map(Promotion::getStartPeriod).min(Date::compareTo).get();
            Date maxDate = promotionList.stream().map(Promotion::getEndPeriod).max(Date::compareTo).get();
            campaignResponse.setMinPeriodPromotion(minDate);
            campaignResponse.setMaxPeriodPromotion(maxDate);
        }
        return campaignResponse;
    }

    public Campaign enableCampaignItm(Long campaignId) {
        log.warn("content={\"activity\":\"Enable Campaign\", \"msg\":{\"campaign_id\":\"{}\"}}", campaignId);
        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(campaignId))
                .orElseThrow(CampaignNotFoundException::new);
        campaign.setEnable(true);

        return campaignRepo.saveAndFlush(campaign);
    }

    public Campaign disableCampaignItm(Long campaignId) {
        List<Promotion> promotionList = Optional.ofNullable(promotionItmRepo.findByCampaignId(campaignId))
                .orElseThrow(CampaignNotFoundException::new);
        promotionList.forEach(promotion -> {
            log.info("content={\"activity\":\"Disable Promotion in campaign\", \"msg\":{\"promotion_id\":\"{}\"}}",
                    promotion.getId());
            promotion.setEnable(false);
            promotionItmRepo.saveAndFlush(promotion);
        });

        log.warn("content={\"activity\":\"Disable Campaign\", \"msg\":{\"campaign_id\":\"{}\"}}", campaignId);
        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(campaignId))
                .orElseThrow(CampaignNotFoundException::new);
        campaign.setEnable(false);

        return campaignRepo.saveAndFlush(campaign);
    }


    public Page<Promotion> getCampaignPromotion(
            Long campaignId, Integer page, Integer perPage,
            Sort.Direction direction, String sort, Long searchID, String searchName, Boolean enable, Boolean disable,
            Boolean active, Boolean expired, String promotionType) {
        Specifications specificationsPromotions = promotionUtil.customerSpecificationsFilterPromotion(campaignId,
                searchID, searchName, promotionType, enable, disable, active, expired, null);

        PageRequest pageRequest = genPageRequest(page - 1, perPage, direction, sort);

        Page<Promotion> promotionPage = Optional.ofNullable(promotionItmRepo.findAll(specificationsPromotions,
                pageRequest)).orElseThrow(CampaignNotFoundException::new);

        promotionPage.getContent().forEach(p -> p.setLive(
                promotionUtil.isLive(p.getEnable(), p.getStartPeriod(), p.getEndPeriod())));

        promotionPage = getPromotions(promotionPage);

        return promotionPage;

    }

    public Boolean checkCampaignNameItm(String campaignName) {
        List<Campaign> campaigns = campaignRepo.findByName(campaignName);
        return campaigns.size() > 0;
    }

    public Boolean checkCampaignNameEditItm(String campaignName, Long campaignId) {
        List<Campaign> campaigns = campaignRepo.findByName(campaignName);
        List<Long> campaignIdList = campaigns.stream().map(BaseEntity::getId).collect(Collectors.toList());
        return campaigns.size() > 0 && !(campaignIdList.size() == 1 && campaignIdList.contains(campaignId));
    }

    private Page<Promotion> getPromotions(Page<Promotion> promotionPage) {
        List<PendingPromotion> pendingPromotionList = pendingPromotionItmRepo.findByPromotionIdIsNotNull();

        if (!pendingPromotionList.isEmpty()) {
            List<Long> pendingPromotionId = pendingPromotionList.stream()
                    .map(PendingPromotion::getPromotionId).collect(Collectors.toList());
            promotionPage.getContent()
                    .forEach(promotion -> {
                        if (pendingPromotionId.contains(promotion.getId())) {
                            Optional<PendingPromotion> pendingPromotion = pendingPromotionList.stream().filter(pd
                                    -> pd.getPromotionId().equals(promotion.getId())).findFirst();
                            if (pendingPromotion.isPresent()) {
                                promotion.setEnable(pendingPromotion.get().getEnable());
                                promotion.setConditionData(pendingPromotion.get().getConditionData());
                                promotion.setDetailData(pendingPromotion.get().getDetailData());
                                promotion.setDescription(pendingPromotion.get().getDescription());
                                promotion.setDescriptionEn(pendingPromotion.get().getDescriptionEn());
                                promotion.setShortDescription(pendingPromotion.get().getShortDescription());
                                promotion.setShortDescriptionEn(pendingPromotion.get().getShortDescriptionEn());
                                promotion.setNameEn(pendingPromotion.get().getNameEn());
                                promotion.setEndPeriod(pendingPromotion.get().getEndPeriod());
                                promotion.setName(pendingPromotion.get().getName());
                                promotion.setRepeat(pendingPromotion.get().getRepeat());
                                promotion.setStartPeriod(pendingPromotion.get().getStartPeriod());
                                promotion.setType(pendingPromotion.get().getType());
                                promotion.setMember(pendingPromotion.get().getMember());
                                promotion.setNonMember(pendingPromotion.get().getNonMember());
                                promotion.setImgUrl(pendingPromotion.get().getImgUrl());
                                promotion.setImgThmUrl(pendingPromotion.get().getImgThmUrl());
                                promotion.setImgUrlEn(pendingPromotion.get().getImgUrlEn());
                                promotion.setImgThmUrlEn(pendingPromotion.get().getImgThmUrlEn());
                                promotion.setPendingStatus(pendingPromotion.get().getStatus());
                            }
                        }
                    });
        }

        promotionPage.forEach(p->promotionUtil.setFilterStatusPromotion(p,DateTime.now().toDate()));
        return promotionPage;
    }

    public List<VariantDuplicateFreebie> checkDuplicateCriteriaFreebie(Long campaignId, Long startPeriod,
                                                                       Long endPeriod) {
        List<Promotion> promotionList = Optional.ofNullable(promotionItmRepo.findByCampaignId(campaignId))
                .orElseThrow(PromotionNotFoundException::new);
        List<String> variantsFreebieDuplicateInCampaign = promotionList.stream().filter(this::isFreebie).flatMap(p ->
                p.getPromotionCondition().getCriteriaValue().stream()).distinct().collect(Collectors.toList());

        if (!variantsFreebieDuplicateInCampaign.isEmpty()) {
            List<Promotion> promotions = promotionItmRepo.findPromotionsByDateTime(
                    new DateTime(startPeriod).toString("yyyy-MM-dd HH:mm:ss"),
                    new DateTime(endPeriod).toString("yyyy-MM-dd HH:mm:ss"));

            final List<Promotion> allFreebiePromotions = promotions.stream().filter(this::isFreebie)
                    .collect(Collectors.toList());

            return variantsFreebieDuplicateInCampaign.stream().map(p -> {
                VariantDuplicateFreebie variantDuplicateFreebie = new VariantDuplicateFreebie();
                variantDuplicateFreebie.setVariantId(p);
                variantDuplicateFreebie.setDuplicatePromotionId(setVariantsDuplicatePromotionId(p,
                        allFreebiePromotions));
                return variantDuplicateFreebie;
            }).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private List<Long> setVariantsDuplicatePromotionId(String variant, List<Promotion> finalPromotions) {
        return finalPromotions.stream().filter(promotion ->
                promotion.getPromotionCondition().getCriteriaValue()
                        .contains(variant)).map(BaseEntity::getId).collect(Collectors.toList());
    }

    private Boolean isFreebie(Promotion promotion) {
        promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));

        return CampaignEnum.VARIANT.getContent().equalsIgnoreCase(
                promotion.getPromotionCondition().getCriteriaType())
                && PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(
                promotion.getType());
    }

}
