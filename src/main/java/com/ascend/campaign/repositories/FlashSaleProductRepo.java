package com.ascend.campaign.repositories;


import com.ascend.campaign.entities.FlashSale;
import com.ascend.campaign.entities.FlashSaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlashSaleProductRepo extends JpaRepository<FlashSaleProduct, Long>,
        JpaSpecificationExecutor<FlashSaleProduct> {
    List<FlashSaleProduct> deleteByFlashSale(FlashSale flashSale);

    List<FlashSaleProduct> findByFlashSale(FlashSale flashSale);

    List<FlashSaleProduct> findByProductKey(String productKey);

    @Query(value = "SELECT * FROM flashsale_product JOIN flash_sale ON  flashsale_product.flashsale_id=flash_sale.id  "
            + "WHERE :startPeriod BETWEEN  start_period AND end_period OR"
            + ":endPeriod BETWEEN  start_period AND end_period",
            nativeQuery = true)
    List<FlashSale> findFlashSalesByDateTime(@Param("startPeriod") String startPeriod,
                                             @Param("endPeriod") String endPeriod);

}
