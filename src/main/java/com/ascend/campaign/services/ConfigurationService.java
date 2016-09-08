package com.ascend.campaign.services;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring")
public class ConfigurationService {
    @Value("${drools.drl.folder}")
    String drlFolder;

    @Value("${drools.promotionITM.filename}")
    String drlPromotionFilename;

    @Value("${drools.promotionWM.filename}")
    String drlPromotionWMFilename;

    @Value("${drools.promotionForProduct.filename}")
    String drlPromotionForProductFilename;

    @Value("${drools.promotionForProductWM.filename}")
    String drlPromotionForProductWMFilename;

    @Value("${drools.bundlePromotionForProducts.filename}")
    String drlBundlePromotionForProductFilenames;

    @Value("${drools.mnpPromotionForProducts.filename}")
    String drlMNPPromotionForProductFilenames;

    @Value("${drools.freebiePromotionForProducts.filename}")
    String drlFreebiePromotionForProductFilenames;

    @Value("${drools.bundlePromotionForProductsWM.filename}")
    String drlBundlePromotionForProductWMFilenames;

    @Value("${spring.redis.key.promotionTM}")
    String redisKeyPromotionItm;

    @Value("${spring.redis.key.promotionWM}")
    String redisKeyPromotionWm;

    @Value("${spring.redis.key.promotionProductTM}")
    String redisKeyPromotionProductItm;

    @Value("${spring.redis.key.promotionProductWM}")
    String redisKeyPromotionProductWm;

    @Value("${spring.redis.key.bundlePromotionProductTM}")
    String redisKeyBundlePromotionProductItm;

    @Value("${spring.redis.key.mnpPromotionProductTM}")
    String redisKeyMNPPromotionProductItm;

    @Value("${spring.redis.key.freebiePromotionProductTM}")
    String redisKeyFreebiePromotionProductItm;

    @Value("${spring.redis.key.bundlePromotionProductWM}")
    String redisKeyBundlePromotionProductWm;

    @Value("${spring.application.name}")
    String applicationName;

    @Value("${spring.application.version}")
    String applicationVersion;

    @Value("${spring.external.url.pds}")
    String pdsUrl;

    @Value("${spring.external.url.pricing}")
    String pricingUrl;


    @Value("${spring.external.url.general}")
    String generalAdapterUrl;


    @Value("${spring.profiles}")
    String profiles;

    @Value("${spring.external.url.trigger}")
    String triggerPricing;


    HashMap<Long, String> policies;
}
