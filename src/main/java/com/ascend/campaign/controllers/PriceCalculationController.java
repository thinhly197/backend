package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Response;
import com.ascend.campaign.models.Calculation;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.DroolsService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class PriceCalculationController {
    @NonNull
    private final DroolsService droolsService;

    @Autowired
    public PriceCalculationController(DroolsService droolsService) {
        this.droolsService = droolsService;
    }

    @RequestMapping(value = "itm/prices/calculation", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> getCalculationITM(@RequestBody Cart cart) {
        droolsService.buildDrlPromotion(false);
        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.calculationCartITM(cart)).build(HttpStatus.OK);

    }

    @RequestMapping(value = "wm/prices/calculation", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> getCalculationWM(@RequestBody Cart cart) {
        droolsService.buildDrlWM(false);

        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.calculationCartWM(cart)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/prices/recalculation", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> getReCalculationITM(@RequestBody Calculation calculation) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.reCalculationCart(calculation)).build(HttpStatus.OK);
    }

    @RequestMapping(value = "wm/prices/recalculation", method = RequestMethod.POST)
    public HttpEntity<ResponseModel> getReCalculationWM(@RequestBody Calculation calculation) {
        return new ResponseModel(Response.SUCCESS.getContent(),
                droolsService.reCalculationCart(calculation)).build(HttpStatus.OK);
    }
}
