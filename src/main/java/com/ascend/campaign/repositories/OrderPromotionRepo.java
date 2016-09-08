package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.OrderPromotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPromotionRepo extends JpaRepository<OrderPromotion, Long> {
    OrderPromotion findByOrderIdAndPromotionId(String orderId, Long promotionId);
}
