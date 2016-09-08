package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.exceptions.BuildDroolsException;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.BuildResponse;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.DroolsService;
import com.ascend.campaign.services.PendingPromotionService;
import com.ascend.campaign.services.PromotionService;
import com.ascend.campaign.utils.GroupCartUtil;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/")
@Slf4j
public class PromotionITMController {
    @NonNull
    private final PromotionService promotionService;

    @NonNull
    private final PendingPromotionService pendingPromotionService;

    @NonNull
    private final DroolsService droolsService;

    @Autowired
    public PromotionITMController(PromotionService promotionService, DroolsService droolsService,
                                  PendingPromotionService pendingPromotionService) {
        this.promotionService = promotionService;
        this.droolsService = droolsService;
        this.pendingPromotionService = pendingPromotionService;
    }

    @RequestMapping(value = "v1/itm/promotions", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> listPromotions(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "30", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName,
            @RequestParam(required = false, defaultValue = "false", value = "enabled") Boolean enable,
            @RequestParam(required = false, defaultValue = "false", value = "disabled") Boolean disable,
            @RequestParam(required = false, defaultValue = "false", value = "live") Boolean active,
            @RequestParam(required = false, defaultValue = "false", value = "expired") Boolean expired,
            @RequestParam(required = false, value = "promotion_type") String promotionType,
            @RequestParam(required = false, value = "campaign_name") String campaignName,
            @RequestParam(required = false, value = "campaign_id") Long campaignId) {

        return new ResponseModel(Response.SUCCESS.getContent(), promotionService.getAllPromotionsITM(
                page, perPage, direction, sort, searchID, searchName, enable, disable, active, expired, promotionType,
                campaignName, campaignId))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/{promotionId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getPromotion(@PathVariable Long promotionId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                promotionService.getPromotionItm(promotionId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions", method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createPromotion(
            @Valid @RequestBody Promotion promotion, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getResponseModelHttpEntity(bindingResult);
        } else {
            Promotion promotionCreated = pendingPromotionService.createPromotionItruemart(promotion);
            return new ResponseModel(Response.SUCCESS.getContent(), promotionCreated).build(HttpStatus.CREATED);
        }
    }

    private HttpEntity<ResponseModel> getResponseModelHttpEntity(BindingResult bindingResult) {
        return new ResponseModel(getErrorMessage(bindingResult), "").build(HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldError().getObjectName() + " " + bindingResult.getFieldError().getField() + " "
                + bindingResult.getFieldError().getDefaultMessage();
    }

    @RequestMapping(value = "v1/itm/promotions/{promotionId}", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updatePromotion(@PathVariable Long promotionId,
                                                     @RequestBody Promotion promotion) {
        Promotion promotionUpdated = pendingPromotionService.updatePromotionItm(promotionId, promotion);
        return new ResponseModel(Response.SUCCESS.getContent(), promotionUpdated).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/{promotionId}", method = RequestMethod.DELETE)
    public HttpEntity<ResponseModel> deletePromotion(@PathVariable Long promotionId) {
        Promotion promotionDeleted = pendingPromotionService.deletePromotionItm(promotionId);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionDeleted).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/{promotionId}/duplication", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> duplicatePromotion(@PathVariable Long promotionId) {
        Promotion promotionDuplicated = pendingPromotionService.duplicatePromotionItm(promotionId);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionDuplicated).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/{promotionId}/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enablePromotion(@PathVariable Long promotionId) {
        Promotion promotion = promotionService.enablePromotionItm(promotionId);

        return new ResponseModel(Response.SUCCESS.getContent(), promotion).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enablePromotionBatch(
            @RequestParam(required = true, value = "ids") String promotionIdList) {
        List<Long> idList = Stream.of(promotionIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Promotion> promotions = new ArrayList<>();
        idList.forEach(id -> {
            Promotion promotion = promotionService.getPromotionItm(id);
            promotion.setEnable(true);
            pendingPromotionService.updatePromotionItm(id, promotion);
        });

        return new ResponseModel(Response.SUCCESS.getContent(), promotions).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disablePromotionBatch(
            @RequestParam(required = true, value = "ids") String promotionIdList) {
        List<Long> idList = Stream.of(promotionIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Promotion> promotions = new ArrayList<>();
        idList.forEach(id -> {
            Promotion promotion = promotionService.getPromotionItm(id);
            promotion.setEnable(false);
            pendingPromotionService.updatePromotionItm(id, promotion);
        });
        return new ResponseModel(Response.SUCCESS.getContent(), promotions).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/{promotionId}/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disablePromotion(@PathVariable Long promotionId) {
        Promotion promotion = promotionService.disablePromotionItm(promotionId);

        return new ResponseModel(Response.SUCCESS.getContent(), promotion).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/products/{productSKU}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getProductPromotion(
            @PathVariable String productSKU,
            @RequestParam(required = false) String brandSKU) {
        droolsService.buildDrlPromotion(false);
        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.getProductPromotionITM(productSKU, brandSKU)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v1/itm/promotions/cart", method = RequestMethod.POST)
    public Map<String, Object> getCartPromotion(@RequestBody Cart cart) {
        cart.setProducts(GroupCartUtil.groupProductCart(cart.getProducts()));

        droolsService.buildDrlPromotion(false);

        Map<String, Object> result = Maps.newHashMap();
        result.put("status_code", "200");
        result.put("body", droolsService.executeCartPromotionRuleITM(cart, CampaignEnum.ITM_V1.getContent()));

        return result;
    }

    @RequestMapping(value = "v1/itm/promotions/build", method = RequestMethod.PUT)
    private HttpEntity<ResponseModel> isBuildDrools() {
        BuildResponse buildResponse = new BuildResponse();
        buildResponse.setPromotionBuilt(droolsService.buildDrlPromotion(true));

        return new ResponseModel(Response.SUCCESS.getContent(), buildResponse).build(HttpStatus.OK);
    }

    @RequestMapping(value = "v2/itm/promotions/cart", method = RequestMethod.POST)
    public Map<String, Object> getCartPromotionV2(@RequestBody Cart cart) {
        cart.setProducts(GroupCartUtil.groupProductCart(cart.getProducts()));

        droolsService.buildDrlPromotion(false);

        Map<String, Object> result = Maps.newHashMap();
        result.put("status_code", "200");
        result.put("body", droolsService.executeCartPromotionRuleITM(cart, CampaignEnum.ITM_V2.getContent()));

        return result;
    }

    @RequestMapping(value = "v1/itm/promotions/isFreebieCriteriaDuplicate", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> isDuplicateCriteriaFreebie(
            @RequestParam(required = true, value = "variants") String variants,
            @RequestParam(required = true, value = "start_period") Long startPeriod,
            @RequestParam(required = true, value = "end_period") Long endPeriod,
            @RequestParam(required = false, value = "promotion_id") Long promotionId) {
        return new ResponseModel(Response.SUCCESS.getContent(), promotionService.checkDuplicateCriteriaFreebie(variants,
                startPeriod, endPeriod, promotionId)).build(HttpStatus.OK);
    }


    @ExceptionHandler(value = BuildDroolsException.class)
    public HttpEntity<ResponseModel> handleBuildDroolsException() {
        return new ResponseModel(Errors.BUILD_DROOLS_FAIL.getErrorDesc()).build(HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(value = PromotionNotFoundException.class)
    public HttpEntity<ResponseModel> handlePromotionNotFoundException() {
        return new ResponseModel(Errors.PROMOTION_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CampaignNotFoundException.class)
    public HttpEntity<ResponseModel> handleCampaignNotFoundException() {
        return new ResponseModel(Errors.CAMPAIGN_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

}
