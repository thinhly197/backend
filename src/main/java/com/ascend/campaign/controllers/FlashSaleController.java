package com.ascend.campaign.controllers;


import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.AppId;
import com.ascend.campaign.entities.FlashSale;
import com.ascend.campaign.exceptions.FlashSaleNotFoundException;
import com.ascend.campaign.exceptions.PolicyNotFoundException;
import com.ascend.campaign.exceptions.WowBannerException;
import com.ascend.campaign.exceptions.WowExtraProductNotFoundException;
import com.ascend.campaign.models.FlashSaleProductAvailable;
import com.ascend.campaign.models.FlashSaleVariantAvailable;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.FlashSaleService;
import com.ascend.campaign.utils.ValidationUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/flashsales")
@Slf4j
public class FlashSaleController {
    @NonNull
    FlashSaleService flashSaleService;

    @Autowired
    public FlashSaleController(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public HttpEntity<ResponseModel> listFlashSales(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "30", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName,
            @RequestParam(required = false, value = "flash_sale_type") String flashSaleType,
            @RequestParam(required = false, defaultValue = "false", value = "enabled") Boolean enable,
            @RequestParam(required = false, defaultValue = "false", value = "disabled") Boolean disable,
            @RequestParam(required = false, defaultValue = "false", value = "live") Boolean active,
            @RequestParam(required = false, defaultValue = "false", value = "expired") Boolean expired,
            @RequestParam(required = false, value = "start_period") Long startPeriod,
            @RequestParam(required = false, value = "end_period") Long endPeriod
    ) {

        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.getAllFlashSale(page, perPage,
                direction, sort, searchID, searchName, enable, disable, active, expired, startPeriod,
                endPeriod, flashSaleType)).build(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createFlashSale(@Valid @RequestBody FlashSale flashSale,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getResponseModelHttpEntity(bindingResult);
        } else {
            return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.createFlashSale(flashSale))
                    .build(HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/{flashsaleId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getFlashSale(@PathVariable Long flashsaleId) {
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.getFlashSaleById(flashsaleId))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{flashsaleId}", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updateFlashSale(@PathVariable Long flashsaleId,
                                                     @Valid @RequestBody FlashSale flashSaleUpdate,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getResponseModelHttpEntity(bindingResult);
        } else {
            return new ResponseModel(Response.SUCCESS.getContent(),
                    flashSaleService.updateFlashSale(flashsaleId, flashSaleUpdate)).build(HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{flashsaleId}", method = RequestMethod.DELETE)
    public HttpEntity<ResponseModel> deleteFlashSale(@PathVariable Long flashsaleId) {
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.deleteFlashSale(flashsaleId))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/wowBanner", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getWow(@RequestParam(required = true, value = "current_time") Long currentDate) {
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.getWowBanner(currentDate))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/wowExtra", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getWowExtra(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "6", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "latest", value = "sort") String sort,
            @RequestParam(required = false, value = "category") String category) {
        ValidationUtils.isValidWowExtraRequestParam(sort);
        return new ResponseModel(Response.SUCCESS.getContent(),
                flashSaleService.getWowExtra(page, perPage, direction, sort, category))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/appId", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getAppId() {
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.getAppIds())
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/appId", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> createAppId(@Valid @RequestBody AppId appId) {
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.createAppId(appId))
                .build(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{flashsaleId}/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enableFlashSale(@PathVariable Long flashsaleId) {
        FlashSale flashSale = flashSaleService.enableFlashSale(flashsaleId);
        return new ResponseModel(Response.SUCCESS.getContent(), flashSale).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{flashsaleId}/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disableFlashSale(@PathVariable Long flashsaleId) {
        FlashSale flashSale = flashSaleService.disabledFlashSale(flashsaleId);
        return new ResponseModel(Response.SUCCESS.getContent(), flashSale).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enableFlashSaleBatch(
            @RequestParam(required = true, value = "ids") String flashSaleIdList) {
        List<Long> idList = Stream.of(flashSaleIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<FlashSale> flashSales = new ArrayList<>();
        idList.forEach(id -> flashSales.add(flashSaleService.enableFlashSale(id)));

        return new ResponseModel(Response.SUCCESS.getContent(), flashSales).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disableFlashSaleBatch(
            @RequestParam(required = true, value = "ids") String flashSaleIdList) {
        List<Long> idList = Stream.of(flashSaleIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<FlashSale> flashSales = new ArrayList<>();
        idList.forEach(id -> flashSales.add(flashSaleService.disabledFlashSale(id)));
        return new ResponseModel(Response.SUCCESS.getContent(), flashSales).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/policies/images/{policyNumber}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getFlashSalesPoliciesImage(@PathVariable Long policyNumber) {

        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.getFlashSalePolicyImage(policyNumber))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/products/{productKey}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getFlashSalesPoliciesImage(@PathVariable String productKey) {

        return new ResponseModel(Response.SUCCESS.getContent(),
                flashSaleService.getFlashSaleProductByProductKey(productKey))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/products/status", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updateFlashSaleProductStatus(
            @Valid @RequestBody List<FlashSaleProductAvailable> flashSaleProductAvailable) {
        List<FlashSaleProductAvailable> flashSaleProductAvailableResult =
                flashSaleService.updateFlashSaleProductStatus(flashSaleProductAvailable);
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleProductAvailableResult).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/variants/status", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updateFlashSaleVariantStatus(
            @Valid @RequestBody List<FlashSaleVariantAvailable> flashSaleProductAvailable) {
        List<FlashSaleVariantAvailable> flashSaleVariantAvailableResult =
                flashSaleService.updateFlashSaleVariantStatus(flashSaleProductAvailable);
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleVariantAvailableResult).build(HttpStatus.OK);
    }

    @RequestMapping(value = "/isProductDuplicate", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> isProductDuplicate(
            @RequestParam(required = true, value = "products") String products,
            @RequestParam(required = true, value = "start_period") Long startPeriod,
            @RequestParam(required = true, value = "end_period") Long endPeriod,
            @RequestParam(required = false, value = "flashsale_id") Long promotionId) {
        return new ResponseModel(Response.SUCCESS.getContent(), flashSaleService.checkProductDuplicate(products,
                startPeriod, endPeriod, promotionId)).build(HttpStatus.OK);
    }

    private HttpEntity<ResponseModel> getResponseModelHttpEntity(BindingResult bindingResult) {
        return new ResponseModel(getErrorMessage(bindingResult), "").build(HttpStatus.BAD_REQUEST);
    }

    private String getErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldError().getObjectName() + " " + bindingResult.getFieldError().getField() + " "
                + bindingResult.getFieldError().getDefaultMessage();
    }

    @ExceptionHandler(value = FlashSaleNotFoundException.class)
    public HttpEntity<ResponseModel> handleFlashSaleNotFoundException() {
        return new ResponseModel(Errors.FLASH_SALE_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = PolicyNotFoundException.class)
    public HttpEntity<ResponseModel> handlePolicyNotFoundException() {
        return new ResponseModel(Errors.POLICY_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = WowExtraProductNotFoundException.class)
    public HttpEntity<ResponseModel> handleWowExtraProductNotFoundException() {
        return new ResponseModel(Errors.WOW_EXTRA_PRODUCT__NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = WowBannerException.class)
    public HttpEntity<ResponseModel> handleWowBannerPeriodException(WowBannerException message) {
        return new ResponseModel(message.getMessage()).build(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidDataAccessApiUsageException.class)
    public HttpEntity<ResponseModel> handleInvalidDataAccessApiUsageException(
            InvalidDataAccessApiUsageException message) {
        return new ResponseModel(message.getMessage()).build(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public HttpEntity<ResponseModel> handleConstraintViolationException(
            ConstraintViolationException message) {
        return new ResponseModel(message.toString()).build(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public HttpEntity<ResponseModel> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException message) {
        return new ResponseModel(message.toString()).build(HttpStatus.BAD_REQUEST);
    }

}
