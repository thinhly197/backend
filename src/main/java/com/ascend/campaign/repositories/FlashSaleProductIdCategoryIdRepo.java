package com.ascend.campaign.repositories;


import com.ascend.campaign.entities.FlashSaleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FlashSaleProductIdCategoryIdRepo extends JpaRepository<FlashSaleCategory, Long>,
        JpaSpecificationExecutor<FlashSaleCategory> {

}
