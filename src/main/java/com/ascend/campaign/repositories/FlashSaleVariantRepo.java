package com.ascend.campaign.repositories;


import com.ascend.campaign.entities.FlashSaleVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FlashSaleVariantRepo extends JpaRepository<FlashSaleVariant, Long>,
        JpaSpecificationExecutor<FlashSaleVariant> {

    List<FlashSaleVariant> findByVariantId(String variantId);
}
