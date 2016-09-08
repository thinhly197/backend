package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.HoldTypeEnum;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.OrderPromotion;
import com.ascend.campaign.models.CustomerPromotion;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.OrderPromotionService;
import com.ascend.campaign.utils.EnumUtil;
import lombok.NonNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderPromotionController {
    @NonNull
    private final OrderPromotionService orderPromotionService;

    @Autowired
    public OrderPromotionController(OrderPromotionService orderPromotionService) {
        this.orderPromotionService = orderPromotionService;
    }

    @RequestMapping(value = "promotions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<ResponseModel> createOrderPromotion(@RequestBody OrderPromotion orderPromotion) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                orderPromotionService.createOrderPromotion(orderPromotion)).build(HttpStatus.CREATED);
    }

    @RequestMapping(value = "customers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<ResponseModel> processOrderCustomerPromotion(@RequestBody CustomerPromotion customerPromotion) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                orderPromotionService.applyCustomerPromotion(customerPromotion)).build(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{orderId}/promotions/{promotionId}/{holdType}/{holdTime}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<ResponseModel> holdOrderPromotion(@PathVariable String orderId,
                                                        @PathVariable Long promotionId,
                                                        @PathVariable String holdType,
                                                        @PathVariable Long holdTime) {
        if (EnumUtil.isInEnum(holdType, HoldTypeEnum.class)) {
            return new ResponseModel(Response.SUCCESS.getContent(),
                    orderPromotionService.holdOrderPromotion(orderId, promotionId, holdType, holdTime))
                    .build(HttpStatus.OK);
        } else {
            return new ResponseModel(Errors.ORDER_PROMOTION_04.getErrorDesc()).build(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{orderId}/promotions/{promotionId}/unhold",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<ResponseModel> holdOrderPromotion(@PathVariable String orderId,
                                                        @PathVariable Long promotionId) {
        try {
            return new ResponseModel(Response.SUCCESS.getContent(),
                    orderPromotionService.unholdOrderPromotion(orderId, promotionId, DateTime.now()))
                    .build(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseModel(e.getMessage()).build(HttpStatus.BAD_REQUEST);
        }
    }
}