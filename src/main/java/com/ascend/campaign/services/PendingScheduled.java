package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.exceptions.BuildDroolsException;
import com.ascend.campaign.models.ResponseModel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.transaction.Transactional;

@Component
@Slf4j
@Transactional
public class PendingScheduled {

    @NonNull
    private final DroolsService droolsService;

    @Autowired
    public PendingScheduled(DroolsService droolsService) {
        this.droolsService = droolsService;
    }

    @Scheduled(fixedRateString = "${spring.fixedRate.buildPromotion}")
    public void pollingPromotions() {
        int builtPromotion = droolsService.buildDrlPromotion(true);
        if (builtPromotion > 0) {
            log.warn("content={\"activity\":\"Built Promotion ITM\", \"msg\":{}}", "Built " + builtPromotion
                    + " promotions");
        }
    }
}