package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.exceptions.BuildDroolsException;
import com.ascend.campaign.exceptions.FreebieException;
import com.ascend.campaign.exceptions.MNPException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.BundleForProduct;
import com.ascend.campaign.models.BundleForProductWM;
import com.ascend.campaign.models.BundleProduct;
import com.ascend.campaign.models.Calculation;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.CartCampaign;
import com.ascend.campaign.models.FreebieForBatchVariant;
import com.ascend.campaign.models.FreebieForProduct;
import com.ascend.campaign.models.FreebieProduct;
import com.ascend.campaign.models.KieFactory;
import com.ascend.campaign.models.MNPForProduct;
import com.ascend.campaign.models.MNPProduct;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.PromotionForProduct;
import com.ascend.campaign.models.PromotionForProductCode;
import com.ascend.campaign.models.PromotionProduct;
import com.ascend.campaign.models.PromotionProductCode;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.utils.CalculationUtil;
import com.ascend.campaign.utils.DrlUtil;
import com.ascend.campaign.utils.DroolsUtil;
import com.ascend.campaign.utils.GroupCartUtil;
import com.ascend.campaign.utils.JSONUtil;
import com.ascend.campaign.utils.KieUtil;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DroolsService {
    private static final XStream XSTREAM = new XStream();

    @NonNull
    private final ConfigurationService configurationService;

    @NonNull
    private final DealService dealService;

    @NonNull
    private final CodeGeneratorService codeGeneratorService;

    @NonNull
    private final PromotionService promotionService;

    @NonNull
    private final CalculationUtil calculationUtil;

    @NonNull
    private final PendingPromotionService pendingPromotionService;

    @NonNull
    private final DrlUtil drlUtil;
    Function<BundleForProduct, BundleForProductWM> bundleForProductBundleForProductWMFunction =
            bundleForProduct -> {
                BundleForProductWM bundleForProductWM = new BundleForProductWM();
                bundleForProductWM.setPromotionId(bundleForProduct.getPromotionId());
                bundleForProductWM.setPromotionName(bundleForProduct.getPromotionName());
                bundleForProductWM.setPromotionNameTranslation(bundleForProduct.getPromotionNameTranslation());
                bundleForProductWM.setBundleNote(bundleForProduct.getBundleNote());
                bundleForProductWM.setBundleNoteTranslation(bundleForProduct.getBundleNoteTranslation());
                bundleForProductWM.setImgWeb(bundleForProduct.getImgWeb());
                bundleForProductWM.setImgMobile(bundleForProduct.getImgMobile());
                bundleForProductWM.setImgWebTranslation(bundleForProduct.getImgWebTranslation());
                bundleForProductWM.setImgMobileTranslation(bundleForProduct.getImgMobileTranslation());
                bundleForProductWM.setPromotionDescription(bundleForProduct.getPromotionDescription());
                bundleForProductWM.setPromotionDescriptionTranslation(
                        bundleForProduct.getPromotionDescriptionTranslation());
                bundleForProductWM.setShortDescription(bundleForProduct.getShortDescription());
                bundleForProductWM.setPromotionShortDescriptionTranslation(
                        bundleForProduct.getPromotionShortDescriptionTranslation());
                bundleForProductWM.setBundleVariants(bundleForProduct.getBundleVariants());
                return bundleForProductWM;
            };

    @NonNull
    private PromotionItmRepo promotionItmRepo;

    @NonNull
    private DroolsUtil droolsUtil;

    @NonNull
    private RedisService redisService;

    private KieFactory promotionITMKieFactory;
    private KieFactory promotionWMKieFactory;
    private KieFactory productKieFactory;
    private KieFactory bundleKieFactory;
    private KieFactory mnpKieFactory;
    private KieFactory freebieKieFactory;
    private KieFactory productWMKieFactory;
    private KieFactory bundleWMKieFactory;
    private boolean isPromotionWMBuild = false;
    private boolean isDrlBuild = false;
    private boolean isProductWMBuild = false;
    private boolean isBundleWMBuild = false;


    @Autowired
    public DroolsService(ConfigurationService configurationService,
                         CodeGeneratorService codeGeneratorService,
                         DealService dealService,
                         CalculationUtil calculationUtil,
                         DrlUtil drlUtil,
                         PromotionItmRepo promotionItmRepo,
                         DroolsUtil droolsUtil,
                         RedisService redisService,
                         PromotionService promotionService,
                         PendingPromotionService pendingPromotionService) {
        this.configurationService = configurationService;
        this.codeGeneratorService = codeGeneratorService;
        this.dealService = dealService;
        this.calculationUtil = calculationUtil;
        this.drlUtil = drlUtil;
        this.promotionItmRepo = promotionItmRepo;
        this.droolsUtil = droolsUtil;
        this.redisService = redisService;
        this.promotionService = promotionService;
        this.pendingPromotionService = pendingPromotionService;
    }

    public Calculation calculationCartITM(Cart cart) {
        return calculationCart(cart, KieUtil.createStatelessKieSession(this.promotionITMKieFactory));
    }

    public Calculation calculationCartWM(Cart cart) {
        return calculationCart(cart, KieUtil.createStatelessKieSession(this.promotionWMKieFactory));
    }

    private Calculation calculationCart(Cart cart, StatelessKieSession statelessKieSession) {
        final Cart clonedCart = (Cart) XSTREAM.fromXML(XSTREAM.toXML(cart));
        List<Product> productsSuperDeal = new ArrayList<>();
        List<Product> productList = dealService.setSuperDealAndDeleteSuperDealFromProductCart(
                cart.getProducts(), productsSuperDeal);
        cart.setProducts(productList);
        cart.setCodeGroupId(codeGeneratorService.isCodeCanApply(cart.getPromotionCode()));
        cart.setProducts(GroupCartUtil.groupProductCart(cart.getProducts()));
        statelessKieSession.execute(cart);
        if (cart.getPromotionIdMatchPromotionList() != null) {
            droolsUtil.addCartPromotionsWm(cart);
        }

        Calculation calculation;
        if (cart.getCartCampaign() != null && clonedCart.getCampaignApplied().size() >= 0) {
            calculation = calculationUtil.setFlatDiscountData(clonedCart.getProducts(), cart.getCartCampaign()
                            .getCampaignSuggestion(), clonedCart.getCampaignApplied(),
                    productsSuperDeal);
        } else {
            calculation = calculationUtil.setCalculationIfCartNotHavePromotion(

                    clonedCart.getProducts(), productsSuperDeal);
        }
        return calculation;
    }

    public Calculation reCalculationCart(Calculation cart) {
        return calculationUtil.reCalculation(cart.getPromotionForProducts(), cart.getTotalFlatDiscount());
    }

    public CartCampaign executeCartPromotionRuleWM(Cart cart) {
        StopWatch stopWatch = new StopWatch("executeCartPromotionRule");
        stopWatch.start("service");
        cart.setCodeGroupId(codeGeneratorService.isCodeCanApply(cart.getPromotionCode()));
        stopWatch.stop();

        stopWatch.start("drool");
        cart.setProducts(GroupCartUtil.groupProductCart(cart.getProducts()));
        KieUtil.createStatelessKieSession(this.promotionWMKieFactory).execute(cart);
        if (cart.getPromotionIdMatchPromotionList() != null) {
            droolsUtil.addCartPromotionsWm(cart);
        }

        if (cart.getCampaignApplied() != null) {
            droolsUtil.deleteUnSuggested(cart);
            droolsUtil.setEmptyCampaignSuggestionOrCampaignUnSuggestionToNull(cart);
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return cart.getCartCampaign();
    }

    public CartCampaign executeCartPromotionRuleITM(Cart cart, String version) {
        StopWatch stopWatch = new StopWatch("executeCartPromotionRule");
        stopWatch.start("service");
        cart.setCodeGroupId(codeGeneratorService.isCodeCanApply(cart.getPromotionCode()));
        stopWatch.stop();

        stopWatch.start("drool");
        KieUtil.createStatelessKieSession(this.promotionITMKieFactory).execute(cart);


        log.info("content={\"activity\":\"Check promotion match\", \"msg\":{}}", "promotions are "
                + cart.getPromotionIdMatchPromotionList());

        if (cart.getPromotionIdMatchPromotionList() != null) {
            droolsUtil.addCartPromotionsItm(cart, version);
        }

        if (cart.getCampaignApplied() != null) {
            droolsUtil.deleteUnSuggested(cart);
            droolsUtil.setEmptyCampaignSuggestionOrCampaignUnSuggestionToNull(cart);
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        log.warn("content={\"activity\":\"Check promotion match\", \"msg\":{}}", "\"products are "
                + cart.getProducts() + " and response is " + cart.getCartCampaign() + "\"");

        return cart.getCartCampaign();
    }

    public List<PromotionForProduct> getProductPromotionITM(String productSKU, String brandSKU) {
        return getProductPromotionITrueMart(
                productSKU, brandSKU, KieUtil.createStatelessKieSession(this.productKieFactory));
    }

    public List<PromotionForProduct> getProductPromotionWM(String productSKU, String brandSKU) {
        return getProductPromotionWeMall(
                productSKU, brandSKU, KieUtil.createStatelessKieSession(this.productWMKieFactory));
    }

    private List<PromotionForProduct> getProductPromotionWeMall(String productSKU, String brandSKU,
                                                                StatelessKieSession statelessKieSession) {
        StopWatch stopWatch = new StopWatch("getProductPromotion");
        stopWatch.start("service");
        PromotionProduct promotionProduct = new PromotionProduct();
        promotionProduct.setProductVariant(productSKU);
        promotionProduct.setBrandVariant(brandSKU);
        stopWatch.stop();

        stopWatch.start("drool");
        statelessKieSession.execute(promotionProduct);
        droolsUtil.setPromotionForProductWm(promotionProduct);
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        return promotionProduct.getPromotionForProducts();
    }

    private List<PromotionForProduct> getProductPromotionITrueMart(String productSKU, String brandSKU,
                                                                   StatelessKieSession statelessKieSession) {
        StopWatch stopWatch = new StopWatch("getProductPromotion");
        stopWatch.start("service");
        PromotionProduct promotionProduct = new PromotionProduct();
        promotionProduct.setProductVariant(productSKU);
        promotionProduct.setBrandVariant(brandSKU);
        stopWatch.stop();

        stopWatch.start("drool");
        statelessKieSession.execute(promotionProduct);

        droolsUtil.setPromotionForProductITM(promotionProduct);
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());

        return promotionProduct.getPromotionForProducts();
    }

    public List<BundleForProduct> getBundlePromotionITM(String productSKU) {
        StopWatch stopWatch = new StopWatch("getBundlePromotion");
        stopWatch.start("service");
        BundleProduct bundleProduct = new BundleProduct();
        bundleProduct.setProductVariant(productSKU);
        stopWatch.stop();

        stopWatch.start("drool");
        KieUtil.createStatelessKieSession(this.bundleKieFactory).execute(bundleProduct);
        stopWatch.stop();
        stopWatch.start("post process");
        List<BundleForProduct> promotions = droolsUtil.setBundleForProductItm(bundleProduct.getPromotionIdList());
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return promotions;
    }

    public List<MNPForProduct> getMNPPromotionITM(String productSKU) {
        if (productSKU == null || productSKU.isEmpty()) {
            throw new MNPException();
        }
        StopWatch stopWatch = new StopWatch("getMNPPromotion");
        stopWatch.start("service");
        MNPProduct mnpProduct = new MNPProduct();
        mnpProduct.setProductVariant(productSKU);
        stopWatch.stop();

        stopWatch.start("drool");
        KieUtil.createStatelessKieSession(this.mnpKieFactory).execute(mnpProduct);
        stopWatch.stop();
        stopWatch.start("post process");
        List<MNPForProduct> promotions = droolsUtil.setMNPForProductItm(mnpProduct.getPromotionIdList());
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return promotions;
    }

    public List<FreebieForBatchVariant> getFreebiePromotionITM(String productVariant) {
        List<String> variants = isValidateAndSplitBatchFreebie(productVariant);
        StopWatch stopWatch = new StopWatch("getFreebiePromotion");
        stopWatch.start("service and drool");
        List<FreebieForBatchVariant> freebieForBatchVariants = variants.stream().map(this::getFreebieProduct).collect(
                Collectors.toList()).stream().map(this::getFreebieForProductDrools).collect(Collectors.toList());
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return freebieForBatchVariants;
    }

    private FreebieProduct getFreebieProduct(String variant) {
        FreebieProduct freebieProduct = new FreebieProduct();
        freebieProduct.setProductVariant(variant);
        return freebieProduct;
    }

    private FreebieForBatchVariant getFreebieForProductDrools(FreebieProduct freebieProduct) {
        if (Optional.ofNullable(this.freebieKieFactory).isPresent()) {
            KieUtil.createStatelessKieSession(this.freebieKieFactory).execute(freebieProduct);
        }
        List<FreebieForProduct> promotions = droolsUtil.setFreeForProductItm(freebieProduct.getPromotionIdList());
        FreebieForBatchVariant freebieForBatchVariant = new FreebieForBatchVariant();
        freebieForBatchVariant.setVariantId(freebieProduct.getProductVariant());
        freebieForBatchVariant.setPromotionsFreebieForVariant(promotions);
        return freebieForBatchVariant;
    }

    private List<String> isValidateAndSplitBatchFreebie(String productVariant) {
        if (isValidateBatch(productVariant)) {
            throw new FreebieException();
        } else {
            return Arrays.stream(productVariant.split(",")).collect(Collectors.toList());
        }
    }

    private boolean isValidateBatch(String productVariant) {
        return productVariant == null || productVariant.isEmpty();
    }

    public List<BundleForProductWM> getBundlePromotionWM(String productSKU) {
        StopWatch stopWatch = new StopWatch("getBundlePromotion");
        stopWatch.start("service");
        BundleProduct bundleProduct = new BundleProduct();
        bundleProduct.setProductVariant(productSKU);
        stopWatch.stop();

        stopWatch.start("drool");
        KieUtil.createStatelessKieSession(this.bundleWMKieFactory).execute(bundleProduct);

        List<BundleForProduct> bundleForProducts = droolsUtil.setBundleForProductWm(bundleProduct.getPromotionIdList());
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return bundleForProducts
                .stream()
                .map(bundleForProductBundleForProductWMFunction)
                .collect(Collectors.<BundleForProductWM>toList());
    }

    public List<PromotionForProductCode> getProductPromotionCodeWM(String productSKU, String brandSKU) {
        return getProductPromotionCode(productSKU,
                KieUtil.createStatelessKieSession(this.productWMKieFactory));
    }

    private List<PromotionForProductCode> getProductPromotionCode(String productSKU,
                                                                  StatelessKieSession statelessKieSession) {
        StopWatch stopWatch = new StopWatch("getProductPromotionCode");
        stopWatch.start("service");
        PromotionProduct promotionProduct = droolsUtil.setProductDetail(productSKU);
        stopWatch.stop();
        stopWatch.start("drool");
        statelessKieSession.execute(promotionProduct);
        PromotionProductCode promotionProductCode = droolsUtil.setPromotionForProductCode(promotionProduct);
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return promotionProductCode.getPromotionForProductsCode();
    }

    public void buildDrlWM(boolean isDeleted) {
        StopWatch stopWatch = new StopWatch("Generate DRL WM");
        if (isDeleted) {
            stopWatch.start("delete drl");
            drlUtil.deleteDrl(".*WM\\.drl");
            stopWatch.stop();
        }

        stopWatch.start("redis promotion");
        String prefixFileName = String.valueOf(System.currentTimeMillis());
        String promotion = String.format("%s%s", prefixFileName, configurationService.getDrlPromotionWMFilename());
        String redisPromotion = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyPromotionWm(), promotion, configurationService.getProfiles(),
                isDeleted);
        stopWatch.stop();

        List<PromotionWM> activePromotions = Lists.newArrayList();
        String drlFolder = configurationService.getDrlFolder();
        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotion))
                && !isPromotionWMBuild) {
            isPromotionWMBuild = true;
            activePromotions = getWMPromotions(activePromotions);
            stopWatch.start("generate drl promotion");
            drlUtil.generateDrlRuleBaseWm(activePromotions, redisPromotion);
            stopWatch.stop();

            stopWatch.start("build promotion WM KIE");
            this.promotionWMKieFactory = KieUtil.createKieFactory(String.format("%s%s", drlFolder, redisPromotion));
            isPromotionWMBuild = false;
            stopWatch.stop();
        }

        stopWatch.start("redis promotion product");
        String promotionProduct = String.format("%s%s", prefixFileName,
                configurationService.getDrlPromotionForProductWMFilename());
        String redisPromotionProduct = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyPromotionProductWm(), promotionProduct,
                configurationService.getProfiles(), isDeleted);
        stopWatch.stop();
        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotionProduct))
                && !isProductWMBuild) {
            isProductWMBuild = true;
            activePromotions = getWMPromotions(activePromotions);
            stopWatch.start("generate drl promotion product");
            drlUtil.generateDrlRuleBaseForProductWm(activePromotions, redisPromotionProduct);
            stopWatch.stop();

            stopWatch.start("build promotion WM KIE");
            this.productWMKieFactory = KieUtil.createKieFactory(String.format("%s%s", drlFolder,
                    redisPromotionProduct));
            isProductWMBuild = false;
            stopWatch.stop();
        }

        stopWatch.start("redis promotion bundle");
        String promotionBundle = String.format("%s%s", prefixFileName,
                configurationService.getDrlBundlePromotionForProductWMFilenames());
        String redisPromotionBundle = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyBundlePromotionProductWm(), promotionBundle,
                configurationService.getProfiles(), isDeleted);
        stopWatch.stop();
        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotionBundle))
                && !isBundleWMBuild) {
            isBundleWMBuild = true;
            activePromotions = getWMPromotions(activePromotions);
            stopWatch.start("generate drl promotion bundle");
            drlUtil.generateDrlRuleBaseForProductBundleWm(activePromotions, redisPromotionBundle);
            stopWatch.stop();

            stopWatch.start("build bundle WM KIE");
            this.bundleWMKieFactory = KieUtil.createKieFactory(String.format("%s%s", drlFolder, redisPromotionBundle));
            isBundleWMBuild = false;
            stopWatch.stop();
        }

        log.info(stopWatch.prettyPrint());
    }

    private List<PendingPromotion> getPendingPromotions(List<PendingPromotion> pendingPromotions) {
        if (pendingPromotions == null || pendingPromotions.isEmpty()) {
            pendingPromotions = pendingPromotionService.getAllPromotionDrools();
            pendingPromotions.forEach(p -> p.setPromotionCondition(
                    JSONUtil.parseToPromotionCondition(p.getConditionData())));
        }

        pendingPromotions = pendingPromotions.stream().filter(p -> p.getCampaign().getEnable())
                .collect(Collectors.toList());

        return pendingPromotions;
    }

    private List<PromotionWM> getWMPromotions(List<PromotionWM> activePromotions) {
        if (activePromotions == null || activePromotions.isEmpty()) {
            activePromotions = promotionService.getAllActivePromotionBusinessChannelWm();
        }

        return activePromotions;
    }

    private List<Promotion> getITMPromotions(List<Promotion> activePromotions) {
        if (activePromotions == null || activePromotions.isEmpty()) {
            activePromotions = promotionService.getAllActivePromotionBusinessChannelItm();
        }

        return activePromotions;
    }

    public void buildDrlPromotionWhenApplicationStart() {
        redisService.setBuildDrools(CampaignEnum.BUILD_DROOLS.getContent(), CampaignEnum.NO.getContent());
        buildDrools(true);
    }

    public Integer buildDrlPromotion(boolean isDeleted) {
        List<PendingPromotion> pendingPromotions = new ArrayList<>();
        pendingPromotions = getPendingPromotions(pendingPromotions);
        if (!pendingPromotions.isEmpty() && CampaignEnum.NO.getContent()
                .equals(redisService.getBuildDrools(
                        CampaignEnum.BUILD_DROOLS.getContent()))) {

            log.info("content={\"activity\":\"Build drools\","
                            + " \"msg\":{\"delete and build new drl if pending not empty \":\"{}\"}}",
                    pendingPromotions.size());

            return buildDrools(isDeleted);
        } else {

            log.info("content={\"activity\":\"Build drools\","
                    + " \"msg\":{\"build new drl if drl not sync}}");
            return buildDrools(false);
        }
    }

    private Integer buildDrools(boolean isDeleted) {
        try {
            StopWatch stopWatch = new StopWatch("Generate DRL ITM");
            String prefixFileName = String.valueOf(System.currentTimeMillis());
            String drlFolder = configurationService.getDrlFolder();
            if (isDeleted) {
                stopWatch.start("delete drl");
                drlUtil.deleteDrl(".*ITM\\.drl");
                stopWatch.stop();
            }

            if (CampaignEnum.NO.getContent().equals(redisService.getBuildDrools(
                    CampaignEnum.BUILD_DROOLS.getContent())) && isDeleted) {
                redisService.setBuildDrools(CampaignEnum.BUILD_DROOLS.getContent(), CampaignEnum.YES.getContent());
                List<PendingPromotion> pendingPromotions = Lists.newArrayList();
                List<Promotion> activePromotions = Lists.newArrayList();
                activePromotions = getITMPromotions(activePromotions);
                pendingPromotions = getPendingPromotions(pendingPromotions);
                if (!pendingPromotions.isEmpty()) {
                    activePromotions = setUpdatePendingPromotionBeforeBuildDrools(activePromotions, pendingPromotions);

                    activePromotions = activePromotions.stream().filter(promotion -> DateTime.now().toDate()
                            .before(promotion.getEndPeriod())).collect(Collectors.toList());

                    buildPromotionDrools(true, stopWatch, prefixFileName, drlFolder, activePromotions);
                    updatePendingPromotionToPromotion(pendingPromotions);

                } else {
                    if (!isDrlBuild) {
                        isDrlBuild = true;
                        activePromotions = activePromotions.stream().filter(promotion -> DateTime.now().toDate()
                                .before(promotion.getEndPeriod())).collect(Collectors.toList());
                        buildPromotionDrools(true, stopWatch, prefixFileName, drlFolder, activePromotions);
                        isDrlBuild = false;
                    }
                }
                redisService.setBuildDrools(CampaignEnum.BUILD_DROOLS.getContent(), CampaignEnum.NO.getContent());

                log.info(stopWatch.prettyPrint());
                log.info("content={\"activity\":\"Build drools\", \"msg\":{\"Build drools success\""
                        + ":\"{} promotion(s)\"}}", pendingPromotions.size());

                return pendingPromotions.size();
            } else {

                if (!isDrlBuild) {

                    isDrlBuild = true;
                    List<Promotion> activePromotions = Lists.newArrayList();
                    activePromotions = getITMPromotions(activePromotions);

                    activePromotions = activePromotions.stream().filter(promotion -> DateTime.now().toDate()
                            .before(promotion.getEndPeriod())).collect(Collectors.toList());
                    buildPromotionDrools(isDeleted, stopWatch, prefixFileName, drlFolder, activePromotions);
                    isDrlBuild = false;
                }
                log.info(stopWatch.prettyPrint());
                return 0;
            }

        } catch (Exception massage) {
            log.warn("content={\"activity\":\"Build drools\", \"msg\":{\"Exception massage\":\"{}\"}}", massage);
            redisService.setBuildDrools(CampaignEnum.BUILD_DROOLS.getContent(), CampaignEnum.NO.getContent());
            isDrlBuild = false;
            throw new BuildDroolsException();
        }
    }

    private void buildPromotionDrools(boolean isDeleted, StopWatch stopWatch, String prefixFileName,
                                      String drlFolder, List<Promotion> activePromotions) {
        generateAndBuildPromotionITM(stopWatch, prefixFileName, activePromotions, drlFolder, isDeleted);
        generateAndBuildPromotionProductITM(
                stopWatch, prefixFileName, activePromotions, drlFolder, isDeleted);
        generateAndBuildPromotionBundleITM(
                stopWatch, prefixFileName, activePromotions, drlFolder, isDeleted);
        generateAndBuildPromotionMNPITM(stopWatch, prefixFileName, activePromotions, drlFolder, isDeleted);
        generateAndBuildPromotionFreebieITM(stopWatch, prefixFileName, activePromotions, drlFolder, isDeleted);
    }

    private void generateAndBuildPromotionMNPITM(StopWatch stopWatch, String prefixFileName, List<Promotion>
            activePromotions, String drlFolder, Boolean isDeleted) {
        stopWatch.start("redis promotion mnp");
        String promotionMNP = String.format("%s%s", prefixFileName,
                configurationService.getDrlMNPPromotionForProductFilenames());
        String redisPromotionMNP = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyMNPPromotionProductItm(), promotionMNP,
                configurationService.getProfiles(), isDeleted);
        stopWatch.stop();
        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotionMNP))) {
            stopWatch.start("generate drl promotion mnp");
            drlUtil.generateDrlRuleBaseForProductMNPItm(activePromotions, redisPromotionMNP);
            stopWatch.stop();

            stopWatch.start("build mnp ITM KIE");
            this.mnpKieFactory = Optional.ofNullable(KieUtil.createKieFactory(String.format("%s%s", drlFolder,
                    redisPromotionMNP))).orElseThrow(BuildDroolsException::new);
            stopWatch.stop();
        }
    }

    private void generateAndBuildPromotionFreebieITM(StopWatch stopWatch, String prefixFileName, List<Promotion>
            activePromotions, String drlFolder, Boolean isDeleted) {
        stopWatch.start("redis promotion freebie");
        String promotionFreebie = String.format("%s%s", prefixFileName,
                configurationService.getDrlFreebiePromotionForProductFilenames());
        String redisPromotionFreebie = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyFreebiePromotionProductItm(), promotionFreebie,
                configurationService.getProfiles(), isDeleted);
        stopWatch.stop();
        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotionFreebie))) {
            stopWatch.start("generate drl promotion freebie");
            drlUtil.generateDrlRuleBaseForProductFreebieItm(activePromotions, redisPromotionFreebie);
            stopWatch.stop();

            stopWatch.start("build freebie ITM KIE");
            this.freebieKieFactory = Optional.ofNullable(KieUtil.createKieFactory(String.format("%s%s", drlFolder,
                    redisPromotionFreebie))).orElseThrow(BuildDroolsException::new);
            stopWatch.stop();
        }
    }

    private void generateAndBuildPromotionBundleITM(StopWatch stopWatch, String prefixFileName, List<Promotion>
            activePromotions, String drlFolder, Boolean isDeleted) {
        stopWatch.start("redis promotion bundle");
        String promotionBundle = String.format("%s%s", prefixFileName,
                configurationService.getDrlBundlePromotionForProductFilenames());
        String redisPromotionBundle = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyBundlePromotionProductItm(), promotionBundle,
                configurationService.getProfiles(), isDeleted);
        stopWatch.stop();

        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotionBundle))) {
            stopWatch.start("generate drl promotion bundle");
            drlUtil.generateDrlRuleBaseForProductBundleItm(activePromotions, redisPromotionBundle);
            stopWatch.stop();

            stopWatch.start("build bundle ITM KIE");
            this.bundleKieFactory = Optional.ofNullable(KieUtil.createKieFactory(String.format("%s%s", drlFolder,
                    redisPromotionBundle))).orElseThrow(BuildDroolsException::new);
            stopWatch.stop();
        }
    }

    private void generateAndBuildPromotionProductITM(StopWatch stopWatch, String prefixFileName, List<Promotion>
            activePromotions, String drlFolder, Boolean isDeleted) {
        stopWatch.start("redis promotion product");
        String promotionProduct = String.format("%s%s", prefixFileName,
                configurationService.getDrlPromotionForProductFilename());
        String redisPromotionProduct = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyPromotionProductItm(), promotionProduct,
                configurationService.getProfiles(), isDeleted);
        stopWatch.stop();

        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotionProduct))) {
            stopWatch.start("generate drl promotion product");
            drlUtil.generateDrlRuleBaseForProductItm(activePromotions, redisPromotionProduct);
            stopWatch.stop();

            stopWatch.start("build product ITM KIE");
            this.productKieFactory = Optional.ofNullable(KieUtil.createKieFactory(String.format("%s%s", drlFolder,
                    redisPromotionProduct))).orElseThrow(BuildDroolsException::new);
            stopWatch.stop();
        }
    }

    private void generateAndBuildPromotionITM(StopWatch stopWatch, String prefixFileName,
                                              List<Promotion> activePromotions, String drlFolder, Boolean isDeleted) {
        stopWatch.start("redis promotion");
        String promotion = String.format("%s%s", prefixFileName, configurationService.getDrlPromotionFilename());
        String redisPromotion = redisService.syncRedisDrlFileName(
                configurationService.getRedisKeyPromotionItm(), promotion, configurationService.getProfiles(),
                isDeleted);
        stopWatch.stop();

        if (redisService.isFileNotExists(String.format("/%s%s", drlFolder, redisPromotion))) {

            stopWatch.start("generate drl promotion");
            drlUtil.generateDrlRuleBaseItm(activePromotions, redisPromotion);
            stopWatch.stop();
            stopWatch.start("build promotion ITM KIE");
            this.promotionITMKieFactory = Optional.ofNullable(KieUtil.createKieFactory(String.format("%s%s", drlFolder,
                    redisPromotion))).orElseThrow(BuildDroolsException::new);
            stopWatch.stop();
        }
    }

    private List<Promotion> setUpdatePendingPromotionBeforeBuildDrools(List<Promotion> activePromotions,
                                                                       List<PendingPromotion> droolsPromotions) {
        List<Long> droolsPromotionsDelete = droolsPromotions.stream().filter(pd ->
                CampaignEnum.PROMOTION_STATUS_DELETING.getContent().equals(pd.getStatus())).map(
                PendingPromotion::getPromotionId).collect(Collectors.toList());
        List<Promotion> active = activePromotions.stream().filter(p ->
                !droolsPromotionsDelete.contains(p.getId())).collect(Collectors.toList());
        HashMap<Long, Integer> promotionHashMap = new HashMap<>();
        int[] idx = {0};
        active.stream().forEachOrdered(p -> promotionHashMap.put(p.getId(), idx[0]++));
        List<PendingPromotion> droolsPromotionsUpdate = droolsPromotions.stream().filter(pd -> CampaignEnum
                .PROMOTION_STATUS_UPDATING.getContent().equals(pd.getStatus())).collect(Collectors.toList());
        droolsPromotionsUpdate.forEach(promotion -> {
            Optional<Integer> indexUpdate = Optional.ofNullable(promotionHashMap.get(promotion.getPromotionId()));
            if (indexUpdate.isPresent()) {
                updatePendingToPromotion(active, promotion, indexUpdate);
            }
        });
        return active;
    }

    private void updatePendingToPromotion(List<Promotion> active, PendingPromotion promotion,
                                          Optional<Integer> indexUpdate) {
        Optional<Promotion> promotionForUpdate = Optional.ofNullable(active.get(indexUpdate.get()));
        if (promotionForUpdate.isPresent()) {
            promotionForUpdate.get().setConditionData(JSONUtil.toString(promotion.getPromotionCondition()));
            promotionForUpdate.get().setPromotionCondition(
                    JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
            promotionForUpdate.get().setRepeat(promotion.getRepeat());
            promotionForUpdate.get().setMember(promotion.getMember());
            promotionForUpdate.get().setNonMember(promotion.getNonMember());
            promotionForUpdate.get().setAppId(promotion.getAppId());
            active.set(indexUpdate.get(), promotionForUpdate.get());
        }
    }

    private void updatePendingPromotionToPromotion(List<PendingPromotion> droolsPromotions) {
        droolsPromotions.forEach(promotion -> {
            if (CampaignEnum.PROMOTION_STATUS_UPDATING.getContent().equalsIgnoreCase(
                    promotion.getStatus())) {
                Optional<Promotion> promotion2 = Optional.ofNullable(promotionItmRepo.findOne(
                        promotion.getPromotionId()));
                if (promotion2.isPresent()) {
                    upDatePendingDetailToPromotion(promotion, promotion2.get());
                    promotionItmRepo.saveAndFlush(promotion2.get());
                    pendingPromotionService.deletePendingPromotion(promotion.getId());
                }


            } else if (CampaignEnum.PROMOTION_STATUS_DELETING.getContent().equalsIgnoreCase(
                    promotion.getStatus())) {
                deletePendingPromotion(promotion);
                pendingPromotionService.deletePendingPromotion(promotion.getId());

            } else {
                pendingPromotionService.deletePendingPromotion(promotion.getId());
            }
        });
    }

    private void upDatePendingDetailToPromotion(PendingPromotion promotion, Promotion promotion2) {
        promotion2.setEnable(promotion.getEnable());
        promotion2.setConditionData(promotion.getConditionData());
        promotion2.setPromotionCondition(JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
        if (Optional.ofNullable(promotion.getDetailData()).isPresent()) {
            promotion2.setDetailData(promotion.getDetailData());
            promotion2.setPromotionData(JSONUtil.parseToPromotionData(promotion.getDetailData()));
        }
        promotion2.setDescription(promotion.getDescription());
        promotion2.setDescriptionEn(promotion.getDescriptionEn());
        promotion2.setEndPeriod(promotion.getEndPeriod());
        promotion2.setName(promotion.getName());
        promotion2.setNameEn(promotion.getNameEn());
        promotion2.setRepeat(promotion.getRepeat());
        promotion2.setStartPeriod(promotion.getStartPeriod());
        promotion2.setType(promotion.getType());
        promotion2.setMember(promotion.getMember());
        promotion2.setNonMember(promotion.getNonMember());
        promotion2.setImgUrl(promotion.getImgUrl());
        promotion2.setImgThmUrl(promotion.getImgThmUrl());
        promotion2.setImgUrlEn(promotion.getImgUrlEn());
        promotion2.setImgThmUrlEn(promotion.getImgThmUrlEn());
        promotion2.setAppId(promotion.getAppId());
        promotion2.setShortDescription(promotion.getShortDescription());
        promotion2.setShortDescriptionEn(promotion.getShortDescriptionEn());
        promotion2.setCampaign(promotion.getCampaign());
    }

    private void deletePendingPromotion(PendingPromotion promotion1) {
        Promotion promotion = Optional.ofNullable(promotionItmRepo.findOne(promotion1.getPromotionId()))
                .orElseThrow(PromotionNotFoundException::new);
        promotionItmRepo.delete(promotion);
        promotionItmRepo.flush();
    }
}
