package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.exceptions.DeleteException;
import com.ascend.campaign.exceptions.DuplicateException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.CampaignService;
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
@RequestMapping("/api/v1/")
@Slf4j
public class CampaignController {
    @NonNull
    private final PromotionService promotionService;

    @NonNull
    private final DroolsService droolsService;

    @NonNull
    private final CampaignService campaignService;

    @Autowired
    public CampaignController(PromotionService promotionService, DroolsService droolsService,
                              CampaignService campaignService) {
        this.promotionService = promotionService;
        this.droolsService = droolsService;
        this.campaignService = campaignService;
    }

    @RequestMapping(value = "itm/campaigns", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> listCampaign(
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "6", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName,
            @RequestParam(required = false, defaultValue = "false", value = "enabled") Boolean enable,
            @RequestParam(required = false, defaultValue = "false", value = "disabled") Boolean disabled,
            @RequestParam(required = false, value = "start_period") Long startPeriod,
            @RequestParam(required = false, value = "end_period") Long endPeriod,
            @RequestParam(required = false, defaultValue = "false", value = "live") Boolean live,
            @RequestParam(required = false, defaultValue = "false", value = "expired") Boolean expired) {
        return new ResponseModel(Response.SUCCESS.getContent(), campaignService.getAllCampaignITM(page, perPage,
                direction, sort, searchID, searchName, enable, disabled, expired, startPeriod, endPeriod, live))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/campaigns", method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    public HttpEntity<ResponseModel> createCampaign(@RequestBody Campaign campaign,
                                                    @RequestParam(required = false,
                                                            value = "check_name",
                                                            defaultValue = "false")
                                                    Boolean checkName) {
        return createCampaignAndCheckCampaignName(campaign, checkName);
    }


    @RequestMapping(value = "itm/campaigns/{campaignId}", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getCampaign(@PathVariable Long campaignId) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                campaignService.getCampaignItm(campaignId)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/campaigns/{campaignId}", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> updateCampaign(@PathVariable Long campaignId,
                                                    @RequestBody Campaign campaign,
                                                    @RequestParam(required = false,
                                                            value = "check_name",
                                                            defaultValue = "false")
                                                    Boolean checkName) {

        return updateCampaignAndCheckCampaignName(campaignId, campaign, checkName);
    }

    @RequestMapping(value = "itm/campaigns/{campaignId}", method = RequestMethod.DELETE)
    public HttpEntity<ResponseModel> deleteCampaign(@PathVariable Long campaignId) {
        Campaign campaignDeleted = campaignService.deleteCampaignItm(campaignId);

        return new ResponseModel(Response.SUCCESS.getContent(), campaignDeleted).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/campaigns/{campaignId}/duplication", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> duplicateCampaign(@PathVariable Long campaignId,
                                                       @RequestBody Campaign campaign,
                                                       @RequestParam(required = false,
                                                               value = "check_name",
                                                               defaultValue = "false")
                                                       Boolean checkName) {

        return duplicateCampaignAndCheckCampaignName(campaignId, campaign, checkName);
    }

    @RequestMapping(value = "itm/campaigns/{campaignId}/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enableCampaign(@PathVariable Long campaignId) {
        Campaign campaign = campaignService.enableCampaignItm(campaignId);

        droolsService.buildDrlPromotionWhenApplicationStart();

        return new ResponseModel(Response.SUCCESS.getContent(), campaign).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/campaigns/{campaignId}/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disableCampaign(@PathVariable Long campaignId) {
        Campaign campaign = campaignService.disableCampaignItm(campaignId);

        droolsService.buildDrlPromotionWhenApplicationStart();
        return new ResponseModel(Response.SUCCESS.getContent(), campaign).build(HttpStatus.OK);

    }

    @RequestMapping(value = "itm/campaigns/enabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> enableCampaignBatch(
            @RequestParam(required = true, value = "ids") String campaignIdList) {
        List<Long> idList = Stream.of(campaignIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Campaign> campaigns = new ArrayList<>();
        idList.forEach(id -> campaigns.add(campaignService.enableCampaignItm(id)));
        droolsService.buildDrlPromotionWhenApplicationStart();

        return new ResponseModel(Response.SUCCESS.getContent(), campaigns).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/campaigns/disabled", method = RequestMethod.PUT)
    public HttpEntity<ResponseModel> disableCampaignBatch(
            @RequestParam(required = true, value = "ids") String campaignIdList) {
        List<Long> idList = Stream.of(campaignIdList.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Campaign> campaigns = new ArrayList<>();
        idList.forEach(id -> campaigns.add(campaignService.disableCampaignItm(id)));
        droolsService.buildDrlPromotionWhenApplicationStart();
        return new ResponseModel(Response.SUCCESS.getContent(), campaigns).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/campaigns/{campaignId}/promotions", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getPromotionsByCampaignId(
            @PathVariable Long campaignId,
            @RequestParam(required = false, defaultValue = "1", value = "page") Integer page,
            @RequestParam(required = false, defaultValue = "30", value = "per_page") Integer perPage,
            @RequestParam(required = false, defaultValue = "ASC", value = "order") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
            @RequestParam(required = false, value = "id") Long searchID,
            @RequestParam(required = false, value = "name") String searchName,
            @RequestParam(required = false, defaultValue = "false", value = "enabled") Boolean enable,
            @RequestParam(required = false, defaultValue = "false", value = "disabled") Boolean disable,
            @RequestParam(required = false, defaultValue = "false", value = "expired") Boolean expired,
            @RequestParam(required = false, defaultValue = "false", value = "live") Boolean active,
            @RequestParam(required = false, value = "promotion_type") String promotionType) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                campaignService.getCampaignPromotion(campaignId, page, perPage, direction, sort, searchID, searchName,
                        enable, disable, active, expired, promotionType)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/campaigns/{campaignId}/isFreebieCriteriaDuplicate", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> isDuplicateCriteriaFreebie(
            @PathVariable Long campaignId,
            @RequestParam(required = true, value = "start_period") Long startPeriod,
            @RequestParam(required = true, value = "end_period") Long endPeriod) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                campaignService.checkDuplicateCriteriaFreebie(campaignId,startPeriod,endPeriod)).build(HttpStatus.OK);
    }

    private HttpEntity<ResponseModel> createCampaignAndCheckCampaignName(
            Campaign campaign,
            Boolean checkName) {
        if (checkName) {
            return checkNameCampaignDuplicate(campaign);
        } else {
            Campaign campaign1 = campaignService.createCampaign(campaign);
            return new ResponseModel(Response.SUCCESS.getContent(), campaign1).build(HttpStatus.CREATED);
        }
    }

    private HttpEntity<ResponseModel> updateCampaignAndCheckCampaignName(
            Long campaignId,
            Campaign campaign,
            Boolean checkName) {
        if (checkName) {
            return checkNameCampaignDuplicateAndUpdateCampaign(campaign, campaignId);
        } else {
            Campaign campaign1 = campaignService.updateCampaignItm(campaignId, campaign);
            return new ResponseModel(Response.SUCCESS.getContent(), campaign1).build(HttpStatus.OK);
        }
    }

    private HttpEntity<ResponseModel> duplicateCampaignAndCheckCampaignName(
            Long campaignId,
            Campaign campaign,
            Boolean checkName) {
        if (checkName) {
            return checkNameCampaignDuplicateAndDuplicateCampaign(campaign, campaignId);
        } else {
            Campaign campaign1 = campaignService.duplicateCampaignItm(campaignId, campaign);
            return new ResponseModel(Response.SUCCESS.getContent(), campaign1).build(HttpStatus.OK);
        }
    }


    private HttpEntity<ResponseModel> checkNameCampaignDuplicate(Campaign campaign) {
        Boolean isDuplicateName = campaignService.checkCampaignNameItm(campaign.getName());
        if (isDuplicateName) {
            return new ResponseModel(Response.SUCCESS.getContent(), CampaignEnum.CAMPAIGN_NAME_DUPLICATE.getContent())
                    .build(HttpStatus.BAD_REQUEST);
        } else {
            Campaign campaign1 = campaignService.createCampaign(campaign);
            return new ResponseModel(Response.SUCCESS.getContent(), campaign1).build(HttpStatus.CREATED);
        }
    }

    private HttpEntity<ResponseModel> checkNameCampaignDuplicateAndUpdateCampaign(Campaign campaign, Long campaignId) {
        Boolean isDuplicateName = campaignService.checkCampaignNameEditItm(campaign.getName(), campaignId);
        if (isDuplicateName) {
            return new ResponseModel(Response.SUCCESS.getContent(), CampaignEnum.CAMPAIGN_NAME_DUPLICATE.getContent())
                    .build(HttpStatus.BAD_REQUEST);
        } else {
            Campaign campaign1 = campaignService.updateCampaignItm(campaignId, campaign);
            return new ResponseModel(Response.SUCCESS.getContent(), campaign1).build(HttpStatus.OK);
        }
    }

    private HttpEntity<ResponseModel> checkNameCampaignDuplicateAndDuplicateCampaign(Campaign campaign,
                                                                                     Long campaignId) {
        Boolean isDuplicateName = campaignService.checkCampaignNameItm(campaign.getName());
        if (isDuplicateName) {
            return new ResponseModel(Response.SUCCESS.getContent(), CampaignEnum.CAMPAIGN_NAME_DUPLICATE.getContent())
                    .build(HttpStatus.BAD_REQUEST);
        } else {
            Campaign campaign1 = campaignService.duplicateCampaignItm(campaignId, campaign);
            return new ResponseModel(Response.SUCCESS.getContent(), campaign1).build(HttpStatus.CREATED);
        }
    }

    @ExceptionHandler(value = CampaignNotFoundException.class)
    public HttpEntity<ResponseModel> handleCampaignNotFoundException() {
        return new ResponseModel(Errors.CAMPAIGN_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = PromotionNotFoundException.class)
    public HttpEntity<ResponseModel> handlePromotionNotFoundException() {
        return new ResponseModel(Errors.PROMOTION_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = DuplicateException.class)
    public HttpEntity<ResponseModel> handleDuplicateException() {
        return new ResponseModel(Errors.DUPLICATE_FAIL.getErrorDesc()).build(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DeleteException.class)
    public HttpEntity<ResponseModel> handleDeleteException() {
        return new ResponseModel(Errors.DELETE_FAIL.getErrorDesc()).build(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
