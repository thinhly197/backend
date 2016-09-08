package com.ascend.campaign.controllers;


import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.exceptions.DealNotFoundException;
import com.ascend.campaign.exceptions.PDSServiceException;
import com.ascend.campaign.exceptions.PricingServiceException;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.DealService;
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

@RestController
@RequestMapping("/api/v1/deals")
@Slf4j
public class DealController {
    @NonNull
    DealService dealService;

    @Autowired
    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getListDeal(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "30", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName,
            @RequestParam(required = false, value = "enable") Boolean enable,
            @RequestParam(required = false, value = "active") Boolean active,
            @RequestParam(required = false, value = "start_period") Long startPeriod,
            @RequestParam(required = false, value = "end_period") Long endPeriod,
            @RequestParam(required = false, value = "super_deal") Boolean superDeal,
            @RequestParam(required = false, value = "recommended") Boolean recommended) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                dealService.getAllDeal(page, perPage, direction, sort, searchID, searchName, enable, active,
                        startPeriod, endPeriod, superDeal, recommended)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/superDeal", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getSuperDeal() {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.getSuperDeals()).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/today", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getTodayDeal() {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.getTodayDeals()).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/tomorrow", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getTomorrowDeal() {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.getTomorrowDeals()).build(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createDeal(@RequestBody Deal deal) {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.createDeal(deal)).build(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{dealId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getDeal(@PathVariable Long dealId) {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.getDeal(dealId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{dealId}", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updateDeal(@PathVariable Long dealId,
                                                @RequestBody Deal deal) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                dealService.updateDeal(dealId, deal)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{dealId}", method = RequestMethod.DELETE)
    public HttpEntity<ResponseModel> deleteDeal(@PathVariable Long dealId) {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.deleteDeal(dealId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{dealId}/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enablePromotion(@PathVariable Long dealId) {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.enableDeal(dealId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{dealId}/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disablePromotion(@PathVariable Long dealId) {
        return new ResponseModel(Response.SUCCESS.getContent(), dealService.disableDeal(dealId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/price/{variant}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getPromotionPrice(@PathVariable String variant) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                dealService.findPromotionPrice(variant)).build(HttpStatus.OK);
    }

    @ExceptionHandler(value = DealNotFoundException.class)
    public HttpEntity<ResponseModel> handleDealNotFoundException() {
        return new ResponseModel(Errors.DEAL_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = PDSServiceException.class)
    public HttpEntity<ResponseModel> handlePDSServiceException() {
        return new ResponseModel(Errors.EXTERNAL_PDS_SERVICE.getErrorDesc()).build(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = PricingServiceException.class)
    public HttpEntity<ResponseModel> handlePricingServiceException() {
        return new ResponseModel(Errors.EXTERNAL_PRC_SERVICE.getErrorDesc()).build(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
