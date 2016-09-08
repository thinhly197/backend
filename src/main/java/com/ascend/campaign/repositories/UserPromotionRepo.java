package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.UserPromotion;
import com.ascend.campaign.entities.UserPromotionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPromotionRepo extends JpaRepository<UserPromotion, UserPromotionKey> {
}
