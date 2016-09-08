package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.entities.OrderPromotion;
import com.ascend.campaign.entities.User;
import com.ascend.campaign.entities.UserPromotionKey;
import com.ascend.campaign.exceptions.CampaignException;
import com.ascend.campaign.models.CampaignSuggestion;
import com.ascend.campaign.models.CustomerPromotion;
import com.ascend.campaign.repositories.OrderPromotionRepo;
import com.ascend.campaign.repositories.UserRepo;
import com.ascend.campaign.utils.JSONUtil;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class OrderPromotionService {
    @NonNull
    private final OrderPromotionRepo orderPromotionRepo;

    @NonNull
    private final UserRepo userRepo;

    @NonNull
    private final UserPromotionService userPromotionService;

    @Autowired
    public OrderPromotionService(OrderPromotionRepo orderPromotionRepo,
                                 UserRepo userRepo,
                                 UserPromotionService userPromotionService) {
        this.orderPromotionRepo = orderPromotionRepo;
        this.userRepo = userRepo;
        this.userPromotionService = userPromotionService;
    }

    public OrderPromotion createOrderPromotion(OrderPromotion entity) {
        log.warn("content={\"activity\":\"Create Order Promotion\", \"msg\":{}}", JSONUtil.toString(entity));
        return orderPromotionRepo.saveAndFlush(entity);
    }

    public List<OrderPromotion> applyCustomerPromotion(CustomerPromotion customerPromotion) {
        log.warn("content={\"activity\":\"Apply Customer Promotion\", \"msg\":{}}",
                JSONUtil.toString(customerPromotion));
        List<OrderPromotion> orderPromotions = Lists.newArrayList();

        for (CampaignSuggestion campaignSuggestion : customerPromotion.getPromotions()) {
            OrderPromotion orderPromotion = new OrderPromotion();
            orderPromotion.setOrderId(customerPromotion.getOrderId());
            orderPromotion.setCustomerId(customerPromotion.getCustomerId());
            orderPromotion.setPromotionId(Long.valueOf(campaignSuggestion.getPromotionId()));
            //orderPromotion.setDiscountValue(campaignSuggestion.getPromotionCost());

            orderPromotions.add(orderPromotion);
        }

        orderPromotions = orderPromotionRepo.save(orderPromotions);
        orderPromotionRepo.flush();

        processUser(customerPromotion.getCustomerId(), customerPromotion.getCustomerType());

        return orderPromotions;
    }

    private void processUser(String customerId, String customerType) {
        log.warn("content={\"activity\":\"Apply Customer User\", \"msg\":{\"customer_id\":\"{}\"}}", customerId);
        if (CampaignEnum.USER.getContent().equalsIgnoreCase(customerType)) {
            User user = userRepo.findByCustomerId(customerId);
            if (user == null) {
                user = new User();
                user.setCustomerId(customerId);

                userRepo.saveAndFlush(user);
            }
        }
    }

    public OrderPromotion holdOrderPromotion(String orderId, Long promotionId, String holdType, Long holdTime) {
        log.warn("content={\"activity\":\"Hold Order Promotion\", \"msg\":{\"order_id\":\"{}\", "
                        + "\"promotion_id\":\"{}\", \"hold_type\":\"{}\", \"hold_time\":\"{}\"}}",
                orderId, promotionId, holdType, holdTime);
        OrderPromotion orderPromotion = orderPromotionRepo.findByOrderIdAndPromotionId(orderId, promotionId);
        orderPromotion.setHoldType(holdType);
        orderPromotion.setHoldTime(new Date(holdTime));
        orderPromotion.setHoldAt(DateTime.now().toDate());

        return orderPromotionRepo.saveAndFlush(orderPromotion);
    }

    public OrderPromotion unholdOrderPromotion(String orderId, Long promotionId, DateTime now)
            throws CampaignException {
        log.warn("content={\"activity\":\"Unhold Order Promotion\", \"msg\":{\"order_id\":\"{}\", "
                + "\"promotion_id\":\"{}\"}}", orderId, promotionId);
        OrderPromotion orderPromotion = orderPromotionRepo.findByOrderIdAndPromotionId(orderId, promotionId);
        if (orderPromotion != null) {
            if (CampaignEnum.UNHOLD.getContent().equalsIgnoreCase(orderPromotion.getHoldType())) {
                throw new CampaignException(Errors.ORDER_PROMOTION_03);
            } else {
                DateTime holdTime = new DateTime(orderPromotion.getHoldTime());
                if (now.isBefore(holdTime)) {
                    orderPromotion.setHoldType(CampaignEnum.UNHOLD.getContent());
                    orderPromotion.setHoldTime(null);
                    orderPromotion.setHoldAt(null);

                    boolean isUpdateExecuteTime = userPromotionService.isUpdateExecuteTime(
                            new UserPromotionKey(orderPromotion.getCustomerId(), promotionId));
                    if (!isUpdateExecuteTime) {
                        throw new CampaignException(Errors.ORDER_PROMOTION_02);
                    }
                } else {
                    throw new CampaignException(Errors.ORDER_PROMOTION_00);
                }
            }
        } else {
            throw new CampaignException(Errors.ORDER_PROMOTION_01);
        }
        return orderPromotionRepo.saveAndFlush(orderPromotion);
    }
}