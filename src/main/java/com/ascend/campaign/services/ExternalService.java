package com.ascend.campaign.services;

import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.entities.FlashSaleVariant;
import com.ascend.campaign.models.PDSJson;
import com.ascend.campaign.models.PricingJson;
import com.ascend.campaign.models.VariantAdapter;
import com.ascend.campaign.models.VariantAdapterResponse;
import com.ascend.campaign.models.VariantDeal;
import com.ascend.campaign.models.VariantDealDetail;
import com.ascend.campaign.models.VariantDealResponse;
import com.ascend.campaign.utils.DealUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExternalService {
    @NonNull
    private final ConfigurationService configurationService;

    @NonNull
    private final DealUtil dealUtil;

    private RestTemplate restTemplate;
    private AsyncRestTemplate asyncRestTemplate;

    @Autowired
    public ExternalService(ConfigurationService configurationService, DealUtil dealUtil) {
        this.configurationService = configurationService;
        this.dealUtil = dealUtil;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            public boolean hasError(ClientHttpResponse response) throws IOException {
                log.warn("content={\"activity\":\"External Service\", "
                                + "\"msg\":{\"response_code\":\"{}\", \"response_desc\":\"{}\"}}",
                        response.getStatusCode(), response.getStatusText());

                return false;
            }
        });
        this.asyncRestTemplate = new AsyncRestTemplate();
    }

    public Optional<PDSJson> getPDSData(String variant) {
        log.warn("content={\"activity\":\"GET PDS\", \"msg\":{\"url\":\"{}\", \"variant\":\"{}\"}}",
                configurationService.getPdsUrl(), variant);
        PDSJson pdsJson = restTemplate.getForObject(configurationService.getPdsUrl(), PDSJson.class, variant);

        return Optional.ofNullable(pdsJson);
    }

    public Optional<PricingJson> getPricingData(String variant) {
        log.warn("content={\"activity\":\"GET Pricing\", \"msg\":{\"url\":\"{}\", \"variant\":\"{}\"}}",
                configurationService.getPricingUrl(), variant);
        PricingJson pricingJson = restTemplate.getForObject(configurationService.getPricingUrl(),
                PricingJson.class, variant);

        return Optional.ofNullable(pricingJson);
    }

    @Async
    public Future<VariantAdapterResponse>  getVariantsAdapter(List<FlashSaleVariant> flashSaleVariants) {

        String variant = flashSaleVariants.stream().map(FlashSaleVariant::getVariantId)
                .collect(Collectors.joining(","));

        log.warn("content={\"activity\":\"GET Variants\", \"msg\":{\"url\":\"{}\", \"variant\":\"{}\"}}",
                configurationService.getGeneralAdapterUrl(), variant);

        VariantAdapterResponse variantAdapterResponse = restTemplate.getForObject(
                configurationService.getGeneralAdapterUrl(), VariantAdapterResponse.class, variant);

        return new AsyncResult<>(variantAdapterResponse);
    }

    public void triggerPricing(VariantDeal variantDeal) {
        log.warn("content={\"activity\":\"Trigger Pricing\", \"msg\":{\"url\":\"{}\", \"variant\":{}}}",
                configurationService.getTriggerPricing(), variantDeal);
        VariantDealResponse variantDealResponse = new VariantDealResponse();
        variantDealResponse.setPromotionId(variantDeal.getPromotionId());
        variantDealResponse.setPromotionName(variantDeal.getPromotionName());
        variantDealResponse.setVariantID(variantDeal.getVariantID());
        if (variantDeal.getPromotionPrice() != null) {
            variantDealResponse.setPromotionPrice(variantDeal.getPromotionPrice().toString());
        }
        restTemplate.put(configurationService.getTriggerPricing(), variantDealResponse);
    }

    public void triggerPromotionPriceToPricing(String variant, List<Deal> dealList) {
        log.warn("content={\"activity\":\"Trigger Promotion Price\", \"msg\":{\"url_pricing\":\"{}\", "
                        + "\"url_pds\":\"{}\", \"variant\":\"{}\"}}",
                configurationService.getPricingUrl(), configurationService.getPdsUrl(), variant);
        ListenableFuture<ResponseEntity<PDSJson>> future = asyncRestTemplate.getForEntity(
                configurationService.getPdsUrl(), PDSJson.class, variant);
        future.addCallback(
                new ListenableFutureCallback<ResponseEntity<PDSJson>>() {
                    @Override
                    public void onSuccess(ResponseEntity<PDSJson> response) {
                        log.debug("PDSURL: {}, onSuccess Variant: {}", configurationService.getPdsUrl(), variant);
                        VariantDealDetail variantDealDetail = response.getBody().getData();
                        PricingJson pricingJson = getPricingData(variant).orElse(new PricingJson());
                        double normalPrice = 0.0;
                        if (pricingJson.getData() != null) {
                            normalPrice = Double.parseDouble(pricingJson.getData().getNormalPriceString());
                        }
                        VariantDeal variantDeal = dealUtil.setPromotionPrice(dealList, variantDealDetail, normalPrice);
                        triggerPricing(variantDeal);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error("Failure call PDS: {}", throwable.getMessage(), throwable);
                    }
                });
    }

}