package com.ascend.campaign.repositories;


import com.ascend.campaign.entities.PromotionWM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionWMRepo extends JpaRepository<PromotionWM, Long>, JpaSpecificationExecutor<PromotionWM> {
    List<PromotionWM> findByEnable(Boolean enable);

    @Query(value = "SELECT revinfo.rev FROM revinfo ORDER BY revinfo.rev DESC LIMIT 1", nativeQuery = true)
    Integer getRevision();
}
