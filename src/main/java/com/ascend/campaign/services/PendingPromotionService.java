package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.repositories.CampaignRepo;
import com.ascend.campaign.repositories.PendingPromotionItmRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.utils.JSONUtil;
import com.ascend.campaign.utils.PromotionUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class PendingPromotionService {


    @NonNull
    private final PendingPromotionItmRepo pendingPromotionItmRepo;

    @NonNull
    private final CampaignRepo campaignRepo;

    @NonNull
    private final PromotionItmRepo promotionItmRepo;

    @NonNull
    private final PromotionUtil promotionUtil;


    @Autowired
    public PendingPromotionService(PendingPromotionItmRepo pendingPromotionItmRepo, CampaignRepo campaignRepo,
                                   PromotionUtil promotionUtil, PromotionItmRepo promotionItmRepo) {
        this.pendingPromotionItmRepo = pendingPromotionItmRepo;
        this.campaignRepo = campaignRepo;
        this.promotionUtil = promotionUtil;
        this.promotionItmRepo = promotionItmRepo;
    }

    public Promotion createPromotionItruemart(Promotion promotion) {
        log.warn("content={\"activity\":\"Create Promotion ITM\", \"msg\":{}}", JSONUtil.toString(promotion));
        final Campaign campaign = Optional.ofNullable(campaignRepo.findOne(promotion.getCampaign().getId()))
                .orElseThrow(CampaignNotFoundException::new);

        if (Optional.ofNullable(promotion.getPromotionCondition()).isPresent()) {
            promotionUtil.trimVariantsAndToUpperCase(promotion.getPromotionCondition());
        }

        promotion.setConditionData(JSONUtil.toString(promotion.getPromotionCondition()));
        promotion.setDetailData(JSONUtil.toString(promotion.getPromotionData()));
        Promotion promotionItm = promotionItmRepo.saveAndFlush(promotion);
        if (isExpiredPromotion(promotion) || !promotion.getEnable()) {
            return promotion;
        } else {
            PendingPromotion pendingPromotion = promotionUtil.promotionToPendingPromotion(
                    promotionItm, CampaignEnum.PROMOTION_STATUS_CREATING.getContent());
            pendingPromotion.setCampaign(campaign);

            pendingPromotionItmRepo.saveAndFlush(pendingPromotion);

            return promotionUtil.pendingPromotionToPromotion(pendingPromotion);
        }
    }

    private boolean isExpiredPromotion(Promotion promotion) {
        Date currentTime = DateTime.now().toDate();
        return promotion.getEndPeriod().compareTo(currentTime) < 0;
    }

    public Promotion updatePromotionItm(Long promotionId, Promotion promotion) {
        if (Optional.ofNullable(promotion.getPromotionCondition()).isPresent()) {
            promotionUtil.trimVariantsAndToUpperCase(promotion.getPromotionCondition());
        }
        return updateToPromotionITM(promotionId, promotion);
    }

    public Promotion duplicatePromotionItm(Long promotionId) {
        log.warn("content={\"activity\":\"Duplicate Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        Promotion promotion = Optional.ofNullable(promotionItmRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);

        return duplicatePromotion(promotionId, promotion);
    }


    public Promotion deletePromotionItm(Long promotionId) {
        log.warn("content={\"activity\":\"Delete Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\"}}", promotionId);
        PendingPromotion checkPendingPromotion = Optional.ofNullable(
                pendingPromotionItmRepo.findByPromotionId(promotionId)).orElse(new PendingPromotion());

        if (Objects.equals(checkPendingPromotion.getPromotionId(), promotionId)) {
            return updateStatusPendigPromotionToDelete(promotionId, checkPendingPromotion);
        } else {
            return addDeletePendingPromotion(promotionId);
        }
    }

    private Promotion addDeletePendingPromotion(Long promotionId) {
        Promotion promotion = Optional.ofNullable(promotionItmRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);
        promotion.setUpdatedAt(null);
        promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
        promotionItmRepo.saveAndFlush(promotion);
        PendingPromotion pendingPromotion = promotionUtil.promotionToPendingPromotion(
                promotion, CampaignEnum.PROMOTION_STATUS_DELETING.getContent());
        pendingPromotion = pendingPromotionItmRepo.saveAndFlush(pendingPromotion);
        return promotionUtil.pendingPromotionToPromotion(pendingPromotion);
    }

    private Promotion updateStatusPendigPromotionToDelete(Long promotionId, PendingPromotion checkPendingPromotion) {
        Promotion promotion = Optional.ofNullable(promotionItmRepo.findOne(promotionId))
                .orElseThrow(PromotionNotFoundException::new);
        promotion.setUpdatedAt(null);

        promotionItmRepo.saveAndFlush(promotion);
        checkPendingPromotion.setStatus(CampaignEnum.PROMOTION_STATUS_DELETING.getContent());
        checkPendingPromotion = pendingPromotionItmRepo.saveAndFlush(checkPendingPromotion);
        return promotionUtil.pendingPromotionToPromotion(checkPendingPromotion);
    }


    private Promotion updateToPromotionITM(Long promotionId, Promotion promotion) {
        Promotion promotionUpdate = Optional.ofNullable(promotionItmRepo.findOne(promotionId)).orElseThrow(
                PromotionNotFoundException::new);
        log.warn("content={\"activity\":\"Update Promotion ITM\", \"msg\":{\"promotion_id\":\"{}\", \"promotion\":{}}}",
                promotionId, JSONUtil.toString(promotion));

        Campaign campaign = Optional.ofNullable(campaignRepo.findOne(promotion.getCampaign().getId()))
                .orElseThrow(CampaignNotFoundException::new);
        promotion.setConditionData(JSONUtil.toString(promotion.getPromotionCondition()));
        promotionUpdate.setLive(promotionUtil.isLive(promotionUpdate.getEnable(),
                promotionUpdate.getStartPeriod(), promotionUpdate.getEndPeriod()));
        if (isUpdatePromotion(promotion, promotionUpdate)) {
            return updatePromotionDetail(promotionId, promotion, promotionUpdate, campaign);

        } else {
            return updatePromotionPending(promotionId, promotion, promotionUpdate, campaign);
        }

    }

    private Promotion updatePromotionDetail(Long promotionId, Promotion promotion, Promotion promotionUpdate,
                                            Campaign campaign) {
        PendingPromotion checkPendingPromotion = Optional.ofNullable(
                pendingPromotionItmRepo.findByPromotionId(promotionId)).orElse(new PendingPromotion());

        if (Objects.equals(checkPendingPromotion.getPromotionId(), promotionId)) {
            updatePendingPromotion(promotionId, promotion, checkPendingPromotion, campaign);
            setUpdateEnableAndPeriodToPromotion(promotion, promotionUpdate);
            promotionItmRepo.saveAndFlush(promotionUpdate);

            return promotionUtil.pendingPromotionToPromotion(checkPendingPromotion);
        } else {
            return updateToPromotionRepo(promotion, campaign, promotionUpdate);
        }
    }

    private Promotion updatePromotionPending(Long promotionId, Promotion promotion, Promotion promotionUpdate,
                                             Campaign campaign) {
        PendingPromotion checkPendingPromotion = Optional.ofNullable(
                pendingPromotionItmRepo.findByPromotionId(promotionId)).orElse(new PendingPromotion());
        if (Objects.equals(checkPendingPromotion.getPromotionId(), promotionId)) {
            updatePendingPromotion(promotionId, promotion, checkPendingPromotion, campaign);
            setUpdateEnableAndPeriodToPromotion(promotion, promotionUpdate);
            promotionItmRepo.saveAndFlush(promotionUpdate);

            return promotionUtil.pendingPromotionToPromotion(checkPendingPromotion);
        } else {

            if (promotion.getEnable() && !isExpiredPromotion(promotion)
                    || promotionUpdate.getEnable() && !isExpiredPromotion(promotionUpdate)) {
                setUpdateEnableAndPeriodToPromotion(promotion, promotionUpdate);
                promotionItmRepo.saveAndFlush(promotionUpdate);
                PendingPromotion pendingPromotion = promotionUtil.promotionToPendingPromotion(
                        promotion, CampaignEnum.PROMOTION_STATUS_CREATING.getContent());
                pendingPromotion.setCampaign(campaign);
                pendingPromotion.setStatus(CampaignEnum.PROMOTION_STATUS_UPDATING.getContent());
                pendingPromotion.setPromotionId(promotionId);
                pendingPromotion = pendingPromotionItmRepo.saveAndFlush(pendingPromotion);
                return promotionUtil.pendingPromotionToPromotion(pendingPromotion);
            } else {
                setUpdateEnableAndPeriodToPromotion(promotion, promotionUpdate);
                promotionUpdate.setConditionData(promotion.getConditionData());
                return updateToPromotionRepo(promotion, campaign, promotionUpdate);
            }
        }
    }

    private Promotion updateToPromotionRepo(Promotion promotion, Campaign campaign, Promotion promotionUpdate1) {
        promotionUpdate1.setDescription(promotion.getDescription());
        promotionUpdate1.setDescriptionEn(promotion.getDescriptionEn());
        promotionUpdate1.setName(promotion.getName());
        promotionUpdate1.setPromotionData(promotion.getPromotionData());
        promotionUpdate1.setDetailData(JSONUtil.toString(promotion.getPromotionData()));
        promotionUpdate1.setNameEn(promotion.getNameEn());
        promotionUpdate1.setType(promotion.getType());
        promotionUpdate1.setImgUrl(promotion.getImgUrl());
        promotionUpdate1.setImgThmUrl(promotion.getImgThmUrl());
        promotionUpdate1.setImgUrlEn(promotion.getImgUrlEn());
        promotionUpdate1.setImgThmUrlEn(promotion.getImgThmUrlEn());
        promotionUpdate1.setAppId(promotion.getAppId());
        promotionUpdate1.setShortDescription(promotion.getShortDescription());
        promotionUpdate1.setShortDescriptionEn(promotion.getShortDescriptionEn());
        promotionUpdate1.setCampaign(campaign);
        promotionItmRepo.saveAndFlush(promotionUpdate1);
        return promotionUpdate1;
    }

    private boolean isUpdatePromotion(Promotion promotion, Promotion promotionUpdate1) {
        return promotionUpdate1.getStartPeriod().getTime() == promotion.getStartPeriod().getTime()
                && promotionUpdate1.getEndPeriod().getTime() == promotion.getEndPeriod().getTime()
                && promotionUpdate1.getMember().equals(promotion.getMember())
                && promotionUpdate1.getEnable().equals(promotion.getEnable())
                && promotionUpdate1.getConditionData().equalsIgnoreCase(promotion.getConditionData())
                && Objects.equals(promotionUpdate1.getRepeat(), promotion.getRepeat());
    }

    private void setUpdateEnableAndPeriodToPromotion(Promotion promotion, Promotion promotionUpdate) {
        promotionUpdate.setUpdatedAt(null);
        promotionUpdate.setStartPeriod(promotion.getStartPeriod());
        promotionUpdate.setEndPeriod(promotion.getEndPeriod());
        promotionUpdate.setEnable(promotion.getEnable());
    }

    private void updatePendingPromotion(Long promotionId, Promotion promotion, PendingPromotion checkPendingPromotion,
                                        Campaign campaign) {
        checkPendingPromotion.setEnable(promotion.getEnable());
        checkPendingPromotion.setPromotionCondition(promotion.getPromotionCondition());
        checkPendingPromotion.setConditionData(JSONUtil.toString(promotion.getPromotionCondition()));
        checkPendingPromotion.setDetailData(JSONUtil.toString(promotion.getPromotionData()));
        checkPendingPromotion.setPromotionData(promotion.getPromotionData());
        checkPendingPromotion.setDescription(promotion.getDescription());
        checkPendingPromotion.setDescriptionEn(promotion.getDescriptionEn());
        checkPendingPromotion.setEndPeriod(promotion.getEndPeriod());
        checkPendingPromotion.setName(promotion.getName());
        checkPendingPromotion.setNameEn(promotion.getNameEn());
        checkPendingPromotion.setRepeat(promotion.getRepeat());
        checkPendingPromotion.setStartPeriod(promotion.getStartPeriod());
        checkPendingPromotion.setType(promotion.getType());
        checkPendingPromotion.setMember(promotion.getMember());
        checkPendingPromotion.setNonMember(promotion.getNonMember());
        checkPendingPromotion.setImgUrl(promotion.getImgUrl());
        checkPendingPromotion.setImgThmUrl(promotion.getImgThmUrl());
        checkPendingPromotion.setImgUrlEn(promotion.getImgUrlEn());
        checkPendingPromotion.setImgThmUrlEn(promotion.getImgThmUrlEn());
        checkPendingPromotion.setAppId(promotion.getAppId());
        checkPendingPromotion.setShortDescription(promotion.getShortDescription());
        checkPendingPromotion.setShortDescriptionEn(promotion.getShortDescriptionEn());
        checkPendingPromotion.setCampaign(campaign);
        checkPendingPromotion.setStatus(CampaignEnum.PROMOTION_STATUS_UPDATING.getContent());
        checkPendingPromotion.setPromotionId(promotionId);
    }


    private Promotion duplicatePromotion(Long promotionId, Promotion promotion) {
        Promotion promotionDuplication = duplicateNewPromotion(promotion);

        Promotion result = promotionItmRepo.saveAndFlush(promotionDuplication);
        result.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
        result.setPromotionData(JSONUtil.parseToPromotionData(promotion.getConditionData()));
        PendingPromotion pendingPromotion = promotionUtil.promotionToPendingPromotion(
                result, CampaignEnum.PROMOTION_STATUS_DUPLICATING.getContent());
        pendingPromotion.setPromotionId(promotionId);
        pendingPromotion = pendingPromotionItmRepo.saveAndFlush(pendingPromotion);
        return promotionUtil.pendingPromotionToPromotion(pendingPromotion);
    }

    private Promotion duplicateNewPromotion(Promotion promotion) {
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
        promotionDuplication.setCampaign(promotion.getCampaign());
        return promotionDuplication;
    }

    public List<PendingPromotion> getAllPromotionDrools() {
        return Optional.ofNullable(pendingPromotionItmRepo.findAll())
                .orElse(new ArrayList<>());
    }

    public void deletePendingPromotion(Long pendingPromotionId) {
        Optional<PendingPromotion> promotion = Optional.ofNullable(pendingPromotionItmRepo.findOne(pendingPromotionId));
        if (promotion.isPresent()) {
            pendingPromotionItmRepo.delete(promotion.get());
            pendingPromotionItmRepo.flush();
        }
    }

}
