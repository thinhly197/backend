package com.ascend.campaign.services;

import com.ascend.campaign.entities.UserPromotion;
import com.ascend.campaign.entities.UserPromotionKey;
import com.ascend.campaign.repositories.UserPromotionRepo;
import com.ascend.campaign.utils.JSONUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserPromotionService {
    @NonNull
    private final UserPromotionRepo userPromotionRepo;

    @Autowired
    public UserPromotionService(UserPromotionRepo userPromotionRepo) {
        this.userPromotionRepo = userPromotionRepo;
    }

    public UserPromotion createUserPromotion(UserPromotion entity) {
        log.warn("content={\"activity\":\"Create User Promotion\", \"msg\":{}}", JSONUtil.toString(entity));
        return userPromotionRepo.saveAndFlush(entity);
    }

    public List<UserPromotion> listUserPromotions() {
        return userPromotionRepo.findAll();
    }

    public UserPromotion getUserPromotionById(UserPromotionKey key) {
        return userPromotionRepo.getOne(key);
    }

    public boolean isUpdateExecuteTime(UserPromotionKey userPromotionKey) {
        boolean isUpdate = false;
        UserPromotion userPromotion = userPromotionRepo.getOne(userPromotionKey);
        if (userPromotion != null) {
            Integer increaseExecuteTime = userPromotion.getExeTime() + 1;
            if (increaseExecuteTime <= userPromotion.getExeLimit()) {
                userPromotion.setExeTime(increaseExecuteTime);
                isUpdate = true;
                log.warn("content={\"activity\":\"Update Execute Time  User Promotion\", \"msg\":{}}",
                        JSONUtil.toString(userPromotion));
                userPromotionRepo.saveAndFlush(userPromotion);
            }
        }

        return isUpdate;
    }

    public boolean canApplyPromotion(UserPromotionKey userPromotionKey) {
        UserPromotion userPromotion = userPromotionRepo.getOne(userPromotionKey);
        if (userPromotion != null) {
            Integer increaseExecuteTime = userPromotion.getExeTime() + 1;
            if (increaseExecuteTime <= userPromotion.getExeLimit()) {
                return true;
            }
        }

        return false;
    }
}
