package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlashSaleRepo extends JpaRepository<FlashSale, Long>, JpaSpecificationExecutor<FlashSale> {

    List<FlashSale> findByEnable(boolean enable);

    List<FlashSale> findByType(String type);

    List<FlashSale> findByEnableAndType(boolean enable, String type);

    @Query(value = "select * from flash_sale  where :startPeriod between  start_period and end_period or"
            + ":endPeriod between  start_period and end_period",
            nativeQuery = true)
    List<FlashSale> findFlashSaleByDateTime(@Param("startPeriod") String startPeriod,
                                             @Param("endPeriod") String endPeriod);
}

