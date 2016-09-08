package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DealRepo extends JpaRepository<Deal, Long>, JpaSpecificationExecutor<Deal> {
    List<Deal> findBySuperDeal(Boolean deal);

    @Query(value = "SELECT * FROM deal"
            + " WHERE enable = 1 and :timeNow <= end_period",
            nativeQuery = true)
    List<Deal> findDealsByDateTime(@Param("timeNow") String timeNow);
}