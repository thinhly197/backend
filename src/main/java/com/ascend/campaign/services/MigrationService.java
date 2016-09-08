package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.MigrationEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.VersionMigration;
import com.ascend.campaign.models.DetailData;
import com.ascend.campaign.models.VariantFreebie;
import com.ascend.campaign.repositories.PendingPromotionItmRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.repositories.VersionMigrationRepo;
import com.ascend.campaign.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MigrationService {


    @NotNull
    private VersionMigrationRepo versionMigrationRepo;

    @NotNull
    private PromotionItmRepo promotionItmRepo;

    @NotNull
    private PendingPromotionItmRepo pendingPromotionItmRepo;

    @Autowired
    public MigrationService(VersionMigrationRepo versionMigrationRepo, PromotionItmRepo promotionItmRepo,
                            PendingPromotionItmRepo pendingPromotionItmRepo) {
        this.versionMigrationRepo = versionMigrationRepo;
        this.promotionItmRepo = promotionItmRepo;
        this.pendingPromotionItmRepo = pendingPromotionItmRepo;

    }

    @Transactional(rollbackFor = Exception.class)
    public void migrationPromotion() {
        try {
            Optional<VersionMigration> version = Optional.ofNullable(
                    versionMigrationRepo.findByVersionMigrations(MigrationEnum.MIGRATION_2016S2.toString()));
            if (version.isPresent()) {
                log.warn("content={\"activity\":\"Migration promotion freebie\", \"msg\":{}}", "migrated promotions");
            } else {
                setMigrationVersion();
                Optional<List<Promotion>> promotionList = Optional.ofNullable(promotionItmRepo.findAll());
                Optional<List<PendingPromotion>> pendingPromotionList = Optional.ofNullable(
                        pendingPromotionItmRepo.findAll());
                updateToPromotion(promotionList);
                updateToPendingPromotion(pendingPromotionList);
                log.warn("content={\"activity\":\"Migration promotion freebie\", \"msg\":{}}",
                        "migrated promotion Freebie  " + promotionList.get().size() + " promotions");
            }
        } catch (Exception e) {
            throw e;
        }
    }


    private void setMigrationVersion() {
        VersionMigration versionMigration = new VersionMigration();
        versionMigration.setVersionMigrations(MigrationEnum.MIGRATION_2016S2.toString());
        versionMigration.setMigrationsNote(MigrationEnum.MIGRATION_2016S2.getContent());
        versionMigrationRepo.saveAndFlush(versionMigration);
    }

    private void updateToPromotion(Optional<List<Promotion>> promotionFreebieList) {
        if (promotionFreebieList.isPresent()) {
            promotionFreebieList.get().forEach(promotion -> {
                promotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
                if (PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())
                        && promotion.getPromotionCondition().getFreeVariants() != null) {

                    List<VariantFreebie> variantFreebies = mapFreeVariantToVariantFreebie(
                            promotion.getPromotionCondition().getFreeVariants(),
                            promotion.getPromotionCondition().getFreeQuantity());
                    promotion.getPromotionCondition().setFreebieConditions(variantFreebies);
                    promotion.getPromotionCondition().setFreeVariantsSelectable(false);

                }
                setDetailDataPromotion(promotion);
                promotion.getPromotionCondition().setNote(null);
                promotion.getPromotionCondition().setNoteEn(null);
                promotion.setConditionData(JSONUtil.toString(promotion.getPromotionCondition()));
            });
            promotionItmRepo.save(promotionFreebieList.get());
            promotionItmRepo.flush();
        }
    }

    private void updateToPendingPromotion(Optional<List<PendingPromotion>> pendingPromotionList) {
        if (pendingPromotionList.isPresent()) {
            pendingPromotionList.get().forEach(pendingPromotion -> {
                pendingPromotion.setPromotionCondition(JSONUtil.parseToPromotionCondition(
                        pendingPromotion.getConditionData()));
                if (PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(pendingPromotion.getType())
                        && pendingPromotion.getPromotionCondition().getFreeVariants() != null) {
                    List<VariantFreebie> variantFreebies = mapFreeVariantToVariantFreebie(
                            pendingPromotion.getPromotionCondition().getFreeVariants(),
                            pendingPromotion.getPromotionCondition().getFreeQuantity());
                    pendingPromotion.getPromotionCondition().setFreebieConditions(variantFreebies);
                    pendingPromotion.getPromotionCondition().setFreeVariantsSelectable(false);
                }
                setDetailDataPendingPromotion(pendingPromotion);
                pendingPromotion.getPromotionCondition().setNote(null);
                pendingPromotion.getPromotionCondition().setNoteEn(null);
                pendingPromotion.setConditionData(JSONUtil.toString(pendingPromotion.getPromotionCondition()));
            });
            pendingPromotionItmRepo.save(pendingPromotionList.get());
            pendingPromotionItmRepo.flush();
        }
    }

    private void setDetailDataPendingPromotion(PendingPromotion pendingPromotion) {
        if (pendingPromotion.getDetailData() != null) {
            pendingPromotion.setDetailData(pendingPromotion.getDetailData());
        } else {
            DetailData detailData = new DetailData();
            detailData.setHtmlNote(pendingPromotion.getPromotionCondition().getNote());
            detailData.setHtmlNoteTranslation(pendingPromotion.getPromotionCondition().getNoteEn());
            pendingPromotion.setDetailData(JSONUtil.toString(detailData));
        }
    }

    private void setDetailDataPromotion(Promotion promotion) {
        if (promotion.getDetailData() != null) {
            promotion.setDetailData(promotion.getDetailData());
        } else {
            DetailData detailData = new DetailData();
            detailData.setHtmlNote(promotion.getPromotionCondition().getNote());
            detailData.setHtmlNoteTranslation(promotion.getPromotionCondition().getNoteEn());
            promotion.setDetailData(JSONUtil.toString(detailData));
        }
    }

    private List<VariantFreebie> mapFreeVariantToVariantFreebie(List<String> freeVariants, Integer freeQuantity) {
        List<VariantFreebie> variantFreebieList = new ArrayList<>();
        freeVariants.forEach(freeVariant -> {
            VariantFreebie variantFreebie = new VariantFreebie();
            variantFreebie.setVariantId(freeVariant);
            variantFreebie.setQuantity(freeQuantity);
            variantFreebieList.add(variantFreebie);
        });
        return variantFreebieList;
    }
}
