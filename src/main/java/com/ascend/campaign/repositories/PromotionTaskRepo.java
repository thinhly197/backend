package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.PromotionTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PromotionTaskRepo extends JpaRepository<PromotionTask, Long> {
    Long deleteByPromotionId(Long promotionId);

    PromotionTask findByPromotionIdAndIsStart(Long promotionId, Boolean isStart);

    List<PromotionTask> findByTriggerAtLessThanEqual(Date now);
}
