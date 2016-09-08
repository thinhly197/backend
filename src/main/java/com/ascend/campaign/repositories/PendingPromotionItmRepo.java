package com.ascend.campaign.repositories;


import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PendingPromotionItmRepo extends JpaRepository<PendingPromotion, Long>,
        JpaSpecificationExecutor<PendingPromotion> {
    List<PendingPromotion> findByEnable(Boolean enable);

    List<PendingPromotion> findByPromotionIdIsNotNull();

    List<PendingPromotion> findByCampaignId(Long campaignId);

    PendingPromotion findByPromotionId(Long promotionId);

    List<PendingPromotion> findByStatus(String content);

    @Query(value = "SELECT * FROM pending_promotion  WHERE :startPeriod BETWEEN  start_period AND end_period OR"
            + ":endPeriod BETWEEN  start_period AND end_period",
            nativeQuery = true)
    List<PendingPromotion> findPendingPromotionsByDateTime(@Param("startPeriod") String startPeriod,
                                             @Param("endPeriod") String endPeriod);
}
