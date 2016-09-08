package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.exceptions.BundleException;
import com.ascend.campaign.models.BundleForProduct;
import com.ascend.campaign.models.BundleForProductWM;
import com.ascend.campaign.models.FreebieForBatchVariant;
import com.ascend.campaign.models.FreebieForProduct;
import com.ascend.campaign.models.MNPForProduct;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.DroolsService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class PromotionForProductController {
    @NonNull
    private final DroolsService droolsService;

    @Autowired
    public PromotionForProductController(DroolsService droolsService) {
        this.droolsService = droolsService;
    }

    @RequestMapping(value = "wm/promotions/bundles/", method = RequestMethod.GET)
    public HttpEntity<ResponseModel> getBundlePromotionWM(
            @RequestParam(required = true) String productVariant) {
        StopWatch stopWatch = new StopWatch("WM Bundle Stop Watch");
        stopWatch.start("build rule wm");
        droolsService.buildDrlWM(false);
        stopWatch.stop();
        stopWatch.start("get bundle wm");
        List<BundleForProductWM> bundles = droolsService.getBundlePromotionWM(productVariant);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return new ResponseModel(Response.SUCCESS.getContent(), bundles).build(HttpStatus.OK);
    }

    @RequestMapping(value = "itm/promotions/bundles/", method = RequestMethod.GET)
    public List<BundleForProduct> getBundlePromotionITM(
            @RequestParam(required = true) String productVariant) {
        StopWatch stopWatch = new StopWatch("ITM Bundle Stop Watch");
        stopWatch.start("build rule itm");
        droolsService.buildDrlPromotion(false);
        stopWatch.stop();
        stopWatch.start("get bundle itm");
        List<BundleForProduct> bundles = droolsService.getBundlePromotionITM(productVariant);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return bundles;
    }


    @RequestMapping(value = "promotions/bundles/", method = RequestMethod.GET)
    public List<BundleForProduct> getBundlePromotionITM2(
            @RequestParam(required = true) String productVariant) {
        StopWatch stopWatch = new StopWatch("ITM Bundle Stop Watch");
        stopWatch.start("build rule itm");
        droolsService.buildDrlPromotion(false);
        stopWatch.stop();
        stopWatch.start("get bundle itm");
        List<BundleForProduct> bundles = droolsService.getBundlePromotionITM(productVariant);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return bundles;
    }

    @RequestMapping(value = "itm/promotions/mnp/", method = RequestMethod.GET)
    public List<MNPForProduct> getMNPPromotionITM(
            @RequestParam(required = true) String productVariant) {
        StopWatch stopWatch = new StopWatch("ITM MNP Stop Watch");
        stopWatch.start("build rule itm");
        droolsService.buildDrlPromotion(false);
        stopWatch.stop();
        stopWatch.start("get MNP itm");
        List<MNPForProduct> mnpForProducts = droolsService.getMNPPromotionITM(productVariant);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return mnpForProducts;
    }

    @RequestMapping(value = "itm/promotions/freebie/", method = RequestMethod.GET)
    public List<FreebieForBatchVariant> getFreebiePromotionITM(
            @RequestParam(required = true) String productVariant) {
        StopWatch stopWatch = new StopWatch("ITM Freebie Stop Watch");
        stopWatch.start("build rule itm");
        droolsService.buildDrlPromotion(false);
        stopWatch.stop();
        stopWatch.start("get Freebie itm");
        List<FreebieForBatchVariant> freebieForProducts = droolsService.getFreebiePromotionITM(productVariant);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return freebieForProducts;
    }


    @ExceptionHandler(value = BundleException.class)
    public HttpEntity<ResponseModel> handleBundleException() {
        return new ResponseModel(Errors.VARIANT_NOT_VALID.getErrorDesc()).build(HttpStatus.BAD_REQUEST);
    }


}
