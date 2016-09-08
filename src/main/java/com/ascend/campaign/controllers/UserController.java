package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.User;
import com.ascend.campaign.entities.UserPromotion;
import com.ascend.campaign.entities.UserPromotionKey;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.UserPromotionService;
import com.ascend.campaign.services.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @NonNull
    private final UserService userService;

    @NonNull
    private final UserPromotionService userPromotionService;

    @Autowired
    public UserController(UserService userService, UserPromotionService userPromotionService) {
        this.userService = userService;
        this.userPromotionService = userPromotionService;
    }

    @RequestMapping(method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createUser(@RequestBody User user) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                userService.createUser(user)).build(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<ResponseModel> listUsers() {
        return new ResponseModel(Response.SUCCESS.getContent(),
                userService.listUsers()).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/customers/{customerId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getUserByCustomerId(@PathVariable String customerId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                userService.getUserByCustomerId(customerId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}/{customerId}", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updateUserCustomerId(@PathVariable Long userId,
                                                          @PathVariable String customerId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                userService.updateCustomerId(userId, customerId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions", method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createUserPromotion(@RequestBody UserPromotion userPromotion) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                userPromotionService.createUserPromotion(userPromotion)).build(HttpStatus.CREATED);
    }

    @RequestMapping(value = "promotions", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> listUserPromotion() {
        return new ResponseModel(Response.SUCCESS.getContent(),
                userPromotionService.listUserPromotions()).build(HttpStatus.OK);
    }

    @RequestMapping(value = "{customerId}/promotions/{promotionId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getUserPromotion(@PathVariable String customerId,
                                                      @PathVariable Long promotionId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                userPromotionService.getUserPromotionById(new UserPromotionKey(customerId, promotionId)))
                .build(HttpStatus.OK);
    }
}
