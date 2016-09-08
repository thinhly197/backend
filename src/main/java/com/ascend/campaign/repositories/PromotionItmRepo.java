package com.ascend.campaign.repositories;


import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionItmRepo extends JpaRepository<Promotion, Long>, JpaSpecificationExecutor<Promotion> {
    List<Promotion> findByEnable(Boolean enable);

    @Query(value = "SELECT revinfo.rev FROM revinfo ORDER BY revinfo.rev DESC LIMIT 1", nativeQuery = true)
    Integer getRevision();

    List<Promotion> findByCampaignId(Long campaignId);

    List<Promotion> removeByCampaign(Campaign campaign);

    List<Promotion> findByType(String content);

    @Query(value = "SELECT * FROM promotion  WHERE :startPeriod BETWEEN  start_period AND end_period OR"
            + ":endPeriod BETWEEN  start_period AND end_period",
            nativeQuery = true)
    List<Promotion> findPromotionsByDateTime(@Param("startPeriod") String startPeriod,
                                             @Param("endPeriod") String endPeriod);
}
