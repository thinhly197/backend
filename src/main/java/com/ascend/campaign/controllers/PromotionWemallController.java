package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.exceptions.CampaignException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.DroolsService;
import com.ascend.campaign.services.PromotionService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/wm/")
@Slf4j
public class PromotionWemallController {
    @NonNull
    private final PromotionService promotionService;

    @NonNull
    private final DroolsService droolsService;

    @Autowired
    public PromotionWemallController(PromotionService promotionService, DroolsService droolsService) {
        this.promotionService = promotionService;
        this.droolsService = droolsService;
    }

    @RequestMapping(value = "promotions", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> listPromotions(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "30", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName,
            @RequestParam(required = false, value = "enable") Boolean enable,
            @RequestParam(required = false, value = "active") Boolean active,
            @RequestParam(required = false, value = "promotion_type") String promotionType) {
        return new ResponseModel(Response.SUCCESS.getContent(), promotionService.getAllPromotionsWM(
                page, perPage, direction, sort, searchID, searchName, enable, active, promotionType))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/{promotionId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getPromotion(@PathVariable Long promotionId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                promotionService.getPromotionWm(promotionId)).build(HttpStatus.OK);
    }


    @RequestMapping(value = "promotions", method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createPromotion(@RequestBody PromotionWM promotion) {
        PromotionWM promotionCreated = promotionService.createPromotionWemall(promotion);
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionCreated).build(HttpStatus.CREATED);
    }

    @RequestMapping(value = "promotions/{promotionId}", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updatePromotion(@PathVariable Long promotionId,
                                                     @RequestBody PromotionWM promotion) {
        PromotionWM promotionUpdated = promotionService.updatePromotionWm(promotionId, promotion);
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionUpdated).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/{promotionId}", method = RequestMethod.DELETE)
    public HttpEntity<ResponseModel> deletePromotion(@PathVariable Long promotionId) {
        PromotionWM promotionDeleted = promotionService.deletePromotionWm(promotionId);
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionDeleted).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/{promotionId}/duplication", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> duplicatePromotion(@PathVariable Long promotionId) {
        PromotionWM promotionDuplicated = promotionService.duplicatePromotionWm(promotionId);
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionDuplicated).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/{promotionId}/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enablePromotion(@PathVariable Long promotionId) {
        PromotionWM promotionWM = promotionService.enablePromotionWm(promotionId);
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionWM).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enablePromotionBatch(
            @RequestParam(required = true, value = "ids") String promotionIdList) {
        List<Long> idList = Stream.of(promotionIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<PromotionWM> promotions = new ArrayList<>();
        idList.forEach(id -> promotions.add(promotionService.enablePromotionWm(id)));
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotions).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disablePromotionBatch(
            @RequestParam(required = true, value = "ids") String promotionIdList) {
        List<Long> idList = Stream.of(promotionIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<PromotionWM> promotions = new ArrayList<>();
        idList.forEach(id -> promotions.add(promotionService.disablePromotionWm(id)));
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotions).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/{promotionId}/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disablePromotion(@PathVariable Long promotionId) {
        PromotionWM promotionWM = promotionService.disablePromotionWm(promotionId);
        droolsService.buildDrlWM(true);

        return new ResponseModel(Response.SUCCESS.getContent(), promotionWM).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/cart", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> getCartPromotion(@RequestBody Cart cart) {
        droolsService.buildDrlWM(false);

        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.executeCartPromotionRuleWM(cart)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/code/{productSKU}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getProductPromotionCode(
            @PathVariable String productSKU,
            @RequestParam(required = false) String brandSKU) {
        droolsService.buildDrlWM(false);

        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.getProductPromotionCodeWM(productSKU, brandSKU)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "promotions/products/{productSKU}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getProductPromotion(
            @PathVariable String productSKU,
            @RequestParam(required = false) String brandSKU) {
        droolsService.buildDrlWM(false);

        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.getProductPromotionWM(productSKU, brandSKU)).build(HttpStatus.OK);
    }

    @ExceptionHandler(value = PromotionNotFoundException.class)
    public HttpEntity<ResponseModel> handlePromotionNotFoundException() {
        return new ResponseModel(Errors.PROMOTION_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CampaignException.class)
    public HttpEntity<ResponseModel> handleInvalidPromotionType() {
        return new ResponseModel(Errors.INVALID_PROMOTION_TYPE.getErrorDesc()).build(HttpStatus.BAD_REQUEST);
    }
}