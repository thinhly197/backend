package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.Code;
import com.ascend.campaign.entities.CodeDetail;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.exceptions.PDSServiceException;
import com.ascend.campaign.models.BundleForProduct;
import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.CampaignApplied;
import com.ascend.campaign.models.CampaignSuggestion;
import com.ascend.campaign.models.CampaignUnSuggestion;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.CartCampaign;
import com.ascend.campaign.models.CriteriaPromotionFreebie;
import com.ascend.campaign.models.DetailData;
import com.ascend.campaign.models.DiscountCodeCriteriaValue;
import com.ascend.campaign.models.FreebieForProduct;
import com.ascend.campaign.models.ImageFreebie;
import com.ascend.campaign.models.MNPForProduct;
import com.ascend.campaign.models.MNPVariant;
import com.ascend.campaign.models.PDSJson;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.PromotionAction;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.PromotionForProduct;
import com.ascend.campaign.models.PromotionForProductCode;
import com.ascend.campaign.models.PromotionProduct;
import com.ascend.campaign.models.PromotionProductCode;
import com.ascend.campaign.models.Variant;
import com.ascend.campaign.repositories.CodeDetailRepo;
import com.ascend.campaign.repositories.CodeRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.repositories.PromotionWMRepo;
import com.ascend.campaign.services.ExternalService;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DroolsUtil {

    @NonNull
    private final ExternalService externalService;
    @NonNull
    PromotionItmRepo promotionItmRepo;
    @NonNull
    PromotionWMRepo promotionWmRepo;
    @NonNull
    CodeRepo codeRepo;
    @NonNull
    CodeDetailRepo codeDetailRepo;
    @NonNull
    GenerateActionPromotionUtil generateActionPromotionUtil;

    Function<Promotion, PromotionForProduct> promotionToPromotionForProductFunctionItm = promotion -> {
        PromotionForProduct promotionForProduct = new PromotionForProduct();
        promotionForProduct.setPromotionId(String.valueOf(promotion.getId()));
        promotionForProduct.setPromotionName(promotion.getName());
        promotionForProduct.setPromotionDescription(promotion.getDescription());
        promotionForProduct.setShortDescription(promotion.getShortDescription());
        promotionForProduct.setImgWeb(promotion.getImgUrl());
        promotionForProduct.setImgMobile(promotion.getImgUrlEn());
        promotionForProduct.setImgWebTranslation(promotion.getImgThmUrl());
        promotionForProduct.setImgMobileTranslation(promotion.getImgThmUrlEn());
        promotionForProduct.setPromotionNameTranslation(promotion.getNameEn());
        promotionForProduct.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        promotionForProduct.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        return promotionForProduct;
    };

    Function<PromotionWM, PromotionForProduct> promotionToPromotionForProductFunctionWm = promotion -> {
        PromotionForProduct promotionForProduct = new PromotionForProduct();
        promotionForProduct.setPromotionId(String.valueOf(promotion.getId()));
        promotionForProduct.setPromotionName(promotion.getName());
        promotionForProduct.setPromotionDescription(promotion.getDescription());
        promotionForProduct.setShortDescription(promotion.getShortDescription());
        promotionForProduct.setImgWeb(promotion.getImgUrl());
        promotionForProduct.setImgMobile(promotion.getImgUrlEn());
        promotionForProduct.setImgWebTranslation(promotion.getImgThmUrl());
        promotionForProduct.setImgMobileTranslation(promotion.getImgThmUrlEn());
        promotionForProduct.setPromotionNameTranslation(promotion.getNameEn());
        promotionForProduct.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        promotionForProduct.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        return promotionForProduct;
    };

    Function<Promotion, PromotionForProduct> promotionToPromotionForProductFunctionITM = promotion -> {
        PromotionForProduct promotionForProduct = new PromotionForProduct();
        promotionForProduct.setPromotionId(String.valueOf(promotion.getId()));
        promotionForProduct.setPromotionName(promotion.getName());
        promotionForProduct.setPromotionDescription(promotion.getDescription());
        promotionForProduct.setShortDescription(promotion.getShortDescription());
        promotionForProduct.setImgWeb(promotion.getImgUrl());
        promotionForProduct.setImgMobile(promotion.getImgUrlEn());
        promotionForProduct.setImgWebTranslation(promotion.getImgThmUrl());
        promotionForProduct.setImgMobileTranslation(promotion.getImgThmUrlEn());
        promotionForProduct.setPromotionNameTranslation(promotion.getNameEn());
        promotionForProduct.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        promotionForProduct.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        return promotionForProduct;
    };

    Function<PromotionProduct, PromotionProductCode> promotionProductPromotionForProductCodeFunction
            = promotionProduct -> {
        PromotionProductCode promotionProductCode = new PromotionProductCode();
        promotionProductCode.setBrandVariant(promotionProduct.getBrandVariant());
        promotionProductCode.setProductVariant(promotionProduct.getProductVariant());
        promotionProductCode.setPromotionIdList(promotionProduct.getPromotionIdList());
        return promotionProductCode;
    };

    Function<BundleVariant, Variant> bundleVariantToVariant = new Function<BundleVariant, Variant>() {
        final Double[] discountValue = new Double[1];
        final String[] discountType = new String[1];

        @Override
        public Variant apply(BundleVariant bundleVariant2) {
            Variant variant = new Variant();
            variant.setVariantId(bundleVariant2.getBundleVariant());
            if (bundleVariant2.getDiscountPercent() == 0.0) {
                discountValue[0] = bundleVariant2.getDiscountFixed();
                discountType[0] = "total";
            } else {
                discountValue[0] = bundleVariant2.getDiscountPercent();
                discountType[0] = "percent";
            }
            variant.setDiscountType(discountType[0]);
            variant.setDiscountValue(discountValue[0]);
            variant.setDiscountMaximum(0.0);
            return variant;
        }
    };

    Function<MNPVariant, Variant> mnpVariantToVariant = new Function<MNPVariant, Variant>() {
        final Double[] discountValue = new Double[1];
        final String[] discountType = new String[1];

        @Override
        public Variant apply(MNPVariant mnpVariant2) {
            Variant variant = new Variant();
            variant.setVariantId(mnpVariant2.getMnpVariants());
            if (mnpVariant2.getDiscountPercent() == 0.0) {
                discountValue[0] = mnpVariant2.getDiscountFixed();
                discountType[0] = "total";
            } else {
                discountValue[0] = mnpVariant2.getDiscountPercent();
                discountType[0] = "percent";
            }
            variant.setDiscountType(discountType[0]);
            variant.setDiscountValue(discountValue[0]);
            variant.setDiscountMaximum(0.0);
            return variant;
        }
    };

    Function<CampaignApplied, CampaignUnSuggestion> campaignAppliedToCampaignUnSuggestionFunction
            = campaignApplied -> {
        CampaignUnSuggestion campaignUnSuggestion1 = new CampaignUnSuggestion();
        campaignUnSuggestion1.setPromotionId(campaignApplied.getPromotionId());
        campaignUnSuggestion1.setPromotionName(campaignApplied.getPromotionName());
        campaignUnSuggestion1.setLimit(campaignApplied.getLimit());
        campaignUnSuggestion1.setSkuId(campaignApplied.getVariantId());
        return campaignUnSuggestion1;
    };

    Function<Promotion, CampaignSuggestion> promotionToCampaignSuggestionItm = promotion -> {
        CampaignSuggestion campaignSuggestion1 = new CampaignSuggestion();
        campaignSuggestion1.setPromotionId(String.valueOf(promotion.getId()));
        campaignSuggestion1.setPromotionName(promotion.getName());
        campaignSuggestion1.setPromotionNamTranslation(promotion.getNameEn());
        campaignSuggestion1.setPromotionDescription(promotion.getDescription());
        campaignSuggestion1.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        campaignSuggestion1.setShortDescription(promotion.getShortDescription());
        campaignSuggestion1.setShortDescriptionTranslation(promotion.getShortDescriptionEn());
        campaignSuggestion1.setImgWeb(promotion.getImgUrl());
        campaignSuggestion1.setImgMobile(promotion.getImgUrlEn());
        campaignSuggestion1.setImgWebTranslation(promotion.getImgThmUrl());
        campaignSuggestion1.setImgMobileTranslation(promotion.getImgThmUrlEn());
        return campaignSuggestion1;
    };

    Function<PromotionWM, CampaignSuggestion> promotionToCampaignSuggestionWm = promotion -> {
        CampaignSuggestion campaignSuggestion1 = new CampaignSuggestion();
        campaignSuggestion1.setPromotionId(String.valueOf(promotion.getId()));
        campaignSuggestion1.setPromotionName(promotion.getName());
        campaignSuggestion1.setPromotionNamTranslation(promotion.getNameEn());
        campaignSuggestion1.setPromotionDescription(promotion.getDescription());
        campaignSuggestion1.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        campaignSuggestion1.setShortDescription(promotion.getShortDescription());
        campaignSuggestion1.setShortDescriptionTranslation(promotion.getShortDescriptionEn());
        campaignSuggestion1.setImgWeb(promotion.getImgUrl());
        campaignSuggestion1.setImgMobile(promotion.getImgUrlEn());
        campaignSuggestion1.setImgWebTranslation(promotion.getImgThmUrl());
        campaignSuggestion1.setImgMobileTranslation(promotion.getImgThmUrlEn());
        return campaignSuggestion1;
    };

    Function<Promotion, BundleForProduct> promotionToBundleForProductItm = promotion -> {
        BundleForProduct bundleForProduct = new BundleForProduct();
        bundleForProduct.setPromotionId(String.valueOf(promotion.getId()));
        bundleForProduct.setPromotionName(promotion.getName());
        bundleForProduct.setPromotionNameTranslation(promotion.getNameEn());
        bundleForProduct.setBundleNote(promotion.getPromotionData().getHtmlNote());
        bundleForProduct.setBundleNoteTranslation(promotion.getPromotionData().getHtmlNoteTranslation());
        bundleForProduct.setImgWeb(promotion.getImgUrl());
        bundleForProduct.setImgMobile(promotion.getImgUrlEn());
        bundleForProduct.setImgWebTranslation(promotion.getImgThmUrl());
        bundleForProduct.setImgMobileTranslation(promotion.getImgThmUrlEn());
        bundleForProduct.setPromotionDescription(promotion.getDescription());
        bundleForProduct.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        bundleForProduct.setShortDescription(promotion.getShortDescription());
        bundleForProduct.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        bundleForProduct.setBundleVariants(setBundleVariantForBundleForProduct(
                promotion.getPromotionCondition().getBundleVariant()));
        return bundleForProduct;
    };

    Function<Promotion, MNPForProduct> promotionToMNPForProductItm = promotion -> {
        MNPForProduct mnpForProduct = new MNPForProduct();
        mnpForProduct.setPromotionId(String.valueOf(promotion.getId()));
        mnpForProduct.setPromotionName(promotion.getName());
        mnpForProduct.setPromotionNameTranslation(promotion.getNameEn());
        mnpForProduct.setBundleNote(promotion.getPromotionData().getHtmlNote());
        mnpForProduct.setBundleNoteTranslation(promotion.getPromotionData().getHtmlNoteTranslation());
        mnpForProduct.setImgWeb(promotion.getImgUrl());
        mnpForProduct.setImgMobile(promotion.getImgUrlEn());
        mnpForProduct.setImgWebTranslation(promotion.getImgThmUrl());
        mnpForProduct.setImgMobileTranslation(promotion.getImgThmUrlEn());
        mnpForProduct.setPromotionDescription(promotion.getDescription());
        mnpForProduct.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        mnpForProduct.setShortDescription(promotion.getShortDescription());
        mnpForProduct.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        mnpForProduct.setMnpVariants(setMNPVariantForBundleForProduct(
                promotion.getPromotionCondition().getMnpVariants()));
        return mnpForProduct;
    };

    Function<Promotion, FreebieForProduct> promotionToFreebieForProductItm = promotion -> {
        FreebieForProduct freebieForProduct = new FreebieForProduct();
        freebieForProduct.setPromotionId(String.valueOf(promotion.getId()));
        freebieForProduct.setPromotionName(promotion.getName());
        freebieForProduct.setPromotionNameTranslation(promotion.getNameEn());
        freebieForProduct.setFreebieNote(promotion.getPromotionData().getPlainNote());
        freebieForProduct.setFreebieNoteTranslation(promotion.getPromotionData().getPlainNoteTranslation());
        freebieForProduct.setImageFreebie(setImageFreebie(promotion));
        freebieForProduct.setPromotionDescription(promotion.getDescription());
        freebieForProduct.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        freebieForProduct.setShortDescription(promotion.getShortDescription());
        freebieForProduct.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        freebieForProduct.setCriteriaPromotionFreebie(getCriteriaPromotionFreebie(promotion.getPromotionCondition()));
        setFreebieFreeVariants(freebieForProduct, promotion.getPromotionCondition());

        return freebieForProduct;
    };
    Function<PromotionWM, BundleForProduct> promotionToBundleForProductWm = promotion -> {
        BundleForProduct bundleForProduct = new BundleForProduct();
        bundleForProduct.setPromotionId(String.valueOf(promotion.getId()));
        bundleForProduct.setPromotionName(promotion.getName());
        bundleForProduct.setPromotionNameTranslation(promotion.getNameEn());
        bundleForProduct.setBundleNote(promotion.getPromotionCondition().getNote());
        bundleForProduct.setBundleNoteTranslation(promotion.getPromotionCondition().getNoteEn());
        bundleForProduct.setImgWeb(promotion.getImgUrl());
        bundleForProduct.setImgMobile(promotion.getImgUrlEn());
        bundleForProduct.setImgWebTranslation(promotion.getImgThmUrl());
        bundleForProduct.setImgMobileTranslation(promotion.getImgThmUrlEn());
        bundleForProduct.setPromotionDescription(promotion.getDescription());
        bundleForProduct.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        bundleForProduct.setShortDescription(promotion.getShortDescription());
        bundleForProduct.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        bundleForProduct.setBundleVariants(setBundleVariantForBundleForProduct(
                promotion.getPromotionCondition().getBundleVariant()));
        return bundleForProduct;
    };

    @Autowired
    public DroolsUtil(PromotionItmRepo promotionItmRepo,
                      PromotionWMRepo promotionWmRepo,
                      GenerateActionPromotionUtil generateActionPromotionUtil,
                      CodeRepo codeRepo,
                      CodeDetailRepo codeDetailRepo,
                      ExternalService externalService) {
        this.promotionItmRepo = promotionItmRepo;
        this.promotionWmRepo = promotionWmRepo;
        this.generateActionPromotionUtil = generateActionPromotionUtil;
        this.codeRepo = codeRepo;
        this.codeDetailRepo = codeDetailRepo;
        this.externalService = externalService;
    }

    private ImageFreebie setImageFreebie(Promotion promotion) {
        ImageFreebie imageFreebie = new ImageFreebie();
        imageFreebie.setImgWeb(promotion.getPromotionData().getImgWeb());
        imageFreebie.setImgMobile(promotion.getPromotionData().getImgMobile());
        imageFreebie.setImgWebTranslation(promotion.getPromotionData().getImgWebTranslation());
        imageFreebie.setImgMobileTranslation(promotion.getPromotionData().getImgMobileTranslation());
        imageFreebie.setThumbMobile(promotion.getPromotionData().getThumbMobile());
        imageFreebie.setThumbMobileTranslation(promotion.getPromotionData().getThumbMobileTranslation());
        imageFreebie.setThumbWeb(promotion.getPromotionData().getThumbWeb());
        imageFreebie.setThumbWebTranslation(promotion.getPromotionData().getThumbWebTranslation());
        return imageFreebie;
    }

    private CriteriaPromotionFreebie getCriteriaPromotionFreebie(PromotionCondition promotionCondition) {
        CriteriaPromotionFreebie criteriaPromotionFreebie = new CriteriaPromotionFreebie();
        criteriaPromotionFreebie.setCriteriaFreebie(promotionCondition.getCriteriaValue());
        criteriaPromotionFreebie.setQuantity(promotionCondition.getQuantity());
        return criteriaPromotionFreebie;
    }

    private void setFreebieFreeVariants(FreebieForProduct freebieForProduct, PromotionCondition promotionCondition) {
        if (promotionCondition.getFreeVariantsSelectable()) {
            freebieForProduct.setOptionVariants(promotionCondition.getFreebieConditions());
            freebieForProduct.setFreebieVariants(Collections.emptyList());
        } else {
            freebieForProduct.setFreebieVariants(promotionCondition.getFreebieConditions());
            freebieForProduct.setOptionVariants(Collections.emptyList());
        }
    }

    public void addCartPromotionsItm(Cart cart, String version) {
        List<CampaignSuggestion> campaignSuggestionList = new ArrayList<>();
        cart.getPromotionIdMatchPromotionList().keySet().forEach(promotionId -> {
            Optional<Promotion> promotion = Optional.ofNullable(promotionItmRepo.findOne(Long.valueOf(promotionId)));
            if (promotion.isPresent()) {
                promotion.get().setPromotionCondition(
                        JSONUtil.parseToPromotionCondition(promotion.get().getConditionData()));
                CampaignSuggestion campaignSuggestion1 = new CampaignSuggestion();
                campaignSuggestion1 = promotionToCampaignSuggestionItm.apply(promotion.get());
                campaignSuggestion1.addPromotionAction(
                        setPromotionActionItm(cart, promotion.get(), promotionId, version));
                if ("discount_bundle".equalsIgnoreCase(campaignSuggestion1.getPromotionAction().get(0).getCommand())
                        || "discount_mnp".equalsIgnoreCase(
                        campaignSuggestion1.getPromotionAction().get(0).getCommand())) {
                    setBundleNoteItm(campaignSuggestion1, promotion.get());
                }
                campaignSuggestionList.add(campaignSuggestion1);
            }
        });

        addCampaignSuggestionToCartCampaign(cart, campaignSuggestionList);

        addPromotionIdDuplicateToCart(cart);

        List<CampaignSuggestion> campaignSuggestions = cart.getCartCampaign().getCampaignSuggestion().stream()
                .filter(x -> x.getPromotionId() != null).collect(Collectors.toList());
        cart.getCartCampaign().setCampaignSuggestion(campaignSuggestions);
    }

    public void addCartPromotionsWm(Cart cart) {
        List<CampaignSuggestion> campaignSuggestionList = new ArrayList<>();
        cart.getPromotionIdMatchPromotionList().keySet().forEach(promotionId -> {
            PromotionWM promotion = promotionWmRepo.findOne(Long.valueOf(promotionId));
            promotion.setPromotionCondition(
                    JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
            CampaignSuggestion campaignSuggestion1 = new CampaignSuggestion();
            campaignSuggestion1 = promotionToCampaignSuggestionWm.apply(promotion);
            campaignSuggestion1.addPromotionAction(setPromotionActionWm(cart, promotion, promotionId));
            setBundleNote(promotion, campaignSuggestion1);
            campaignSuggestionList.add(campaignSuggestion1);
        });

        addCampaignSuggestionToCartCampaign(cart, campaignSuggestionList);
        addPromotionIdDuplicateToCart(cart);

        List<CampaignSuggestion> campaignSuggestions = cart.getCartCampaign().getCampaignSuggestion().stream()
                .filter(x -> x.getPromotionId() != null).collect(Collectors.toList());
        cart.getCartCampaign().setCampaignSuggestion(campaignSuggestions);
    }

    private void setBundleNote(PromotionWM promotion, CampaignSuggestion campaignSuggestion1) {
        if ("discount_bundle".equalsIgnoreCase(campaignSuggestion1.getPromotionAction().get(0).getCommand())) {
            setBundleNoteWm(campaignSuggestion1, promotion);
        }
    }

    private void setBundleNoteItm(CampaignSuggestion campaignSuggestion1, Promotion promotion) {
        campaignSuggestion1.setNote(promotion.getPromotionCondition().getNote());
        campaignSuggestion1.setNoteEn(promotion.getPromotionCondition().getNoteEn());
    }

    private void setBundleNoteWm(CampaignSuggestion campaignSuggestion1, PromotionWM promotion) {
        campaignSuggestion1.setNote(promotion.getPromotionCondition().getNote());
        campaignSuggestion1.setNoteEn(promotion.getPromotionCondition().getNoteEn());
    }

    private PromotionAction setPromotionActionItm(Cart cart, Promotion promotion, String promotionId, String version) {
        PromotionAction promotionAction = null;
        if (PromotionTypeEnum.ITM_BUNDLE.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotion(
                    cart.getProducts(), promotion.getPromotionCondition().getBundleVariant());
            promotionAction = generateActionPromotionUtil.generateActionPromotionBundle(productList,
                    promotion.getPromotionCondition().getBundleVariant());
        } else if (PromotionTypeEnum.ITM_MNP.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotionMNP(
                    cart.getProducts(), promotion.getPromotionCondition().getMnpVariants());
            promotionAction = generateActionPromotionUtil.generateActionPromotionMNP(productList,
                    promotion.getPromotionCondition().getMnpVariants());
        } else if (PromotionTypeEnum.ITM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())) {
            List<String> freeVariant;
            if (CampaignEnum.ITM_V1.getContent().equalsIgnoreCase(version)) {
                freeVariant = getFreeVariantForPromotionFreebieItm(promotion);
                promotionAction = generateActionPromotionUtil.generateActionPromotionFreebie(
                        freeVariant, cart.getPromotionIdMatchPromotionList().get(promotionId));
            } else {
                promotionAction = generateActionPromotionUtil.generateActionPromotionFreebieV2(
                        promotion.getPromotionCondition(),
                        cart.getPromotionIdMatchPromotionList().get(promotionId));
            }

        } else if (PromotionTypeEnum.ITM_DISCOUNT_PROMOTION.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotionWithExcluded(
                    cart.getProducts(), promotion.getPromotionCondition());
            promotionAction = generateActionPromotionUtil.generateActionPromotionDiscountByBrand(productList,
                    cart.getPromotionIdMatchPromotionList().get(promotionId), promotion.getPromotionCondition());
        } else if (PromotionTypeEnum.ITM_OPTION_TO_BUY.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionAction = generateActionPromotionUtil.generateActionPromotionOptionToBuy(
                    cart.getPromotionIdMatchPromotionList().get(promotionId), promotion.getPromotionCondition());
        } else if (PromotionTypeEnum.ITM_SPECIFIC_TIME.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotionWithExcluded(
                    cart.getProducts(), promotion.getPromotionCondition());
            promotionAction = generateActionPromotionUtil.generateActionPromotionSpecificTime(
                    productList, promotion.getPromotionCondition());
        } else if (PromotionTypeEnum.ITM_DISCOUNT_BY_CODE.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotionWithExcludedDiscountByCode(
                    cart.getProducts(), promotion.getPromotionCondition());
            promotionAction = generateActionPromotionUtil.generateActionPromotionDiscountByCode(
                    productList, promotion.getPromotionCondition());
        }
        return promotionAction;
    }

    private PromotionAction setPromotionActionWm(Cart cart, PromotionWM promotion, String promotionId) {
        PromotionAction promotionAction = null;
        if (CampaignEnum.WM_BUNDLE.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotion(
                    cart.getProducts(), promotion.getPromotionCondition().getBundleVariant());
            promotionAction = generateActionPromotionUtil.generateActionPromotionBundle(productList,
                    promotion.getPromotionCondition().getBundleVariant());
        } else if (CampaignEnum.WM_FREEBIE.getContent().equalsIgnoreCase(promotion.getType())) {
            List<String> freeVariant = getFreeVariantForPromotionFreebieWm(promotion);
            promotionAction = generateActionPromotionUtil.generateActionPromotionFreebie(
                    freeVariant, cart.getPromotionIdMatchPromotionList().get(promotionId));
        } else if (CampaignEnum.WM_DISCOUNT_PROMOTION.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotionWithExcluded(
                    cart.getProducts(), promotion.getPromotionCondition());
            promotionAction = generateActionPromotionUtil.generateActionPromotionDiscountByBrand(productList,
                    cart.getPromotionIdMatchPromotionList().get(promotionId), promotion.getPromotionCondition());
        } else if (CampaignEnum.WM_OPTION_TO_BUY.getContent().equalsIgnoreCase(promotion.getType())) {
            promotionAction = generateActionPromotionUtil.generateActionPromotionOptionToBuy(
                    cart.getPromotionIdMatchPromotionList().get(promotionId), promotion.getPromotionCondition());
        } else if (CampaignEnum.WM_SPECIFIC_TIME.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotionWithExcluded(
                    cart.getProducts(), promotion.getPromotionCondition());
            promotionAction = generateActionPromotionUtil.generateActionPromotionSpecificTime(
                    productList, promotion.getPromotionCondition());
        } else if (CampaignEnum.WM_DISCOUNT_BY_CODE.getContent().equalsIgnoreCase(promotion.getType())) {
            List<Product> productList = getProductListPromotionWithExcludedDiscountByCode(
                    cart.getProducts(), promotion.getPromotionCondition());
            promotionAction = generateActionPromotionUtil.generateActionPromotionDiscountByCode(
                    productList, promotion.getPromotionCondition());
        }
        return promotionAction;
    }


    private List<Product> getProductListPromotionWithExcludedDiscountByCode(List<Product> products,
                                                                            PromotionCondition promotionCondition) {
        setExcludedCriteriaIfValueNull(promotionCondition);
        List<Product> productList = null;
        if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getBrandCode())
                            && !promotionCondition.getExcludedVariants()
                            .contains(product.getVariantId())).collect(Collectors.toList());
        } else if (CampaignEnum.CATEGORY.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getCategoryCode())
                            && !promotionCondition.getExcludedVariants()
                            .contains(product.getVariantId())).collect(Collectors.toList());
        } else if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getVariantId())
                            && !promotionCondition.getExcludedVariants()
                            .contains(product.getVariantId())).collect(Collectors.toList());
        } else if (CampaignEnum.COLLECTION.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getCollection())
                            && !promotionCondition.getExcludedVariants()
                            .contains(product.getVariantId())
                            && !promotionCondition.getExcludedBrands()
                            .contains(product.getBrandCode())
                            && !promotionCondition.getExcludedCollections()
                            .contains(product.getCollection())
                            && !promotionCondition.getExcludedCategories()
                            .contains(product.getCategoryCode())).collect(Collectors.toList());
        } else if (CampaignEnum.CART.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product -> !promotionCondition.getExcludedVariants()
                    .contains(product.getVariantId()) && !promotionCondition.getExcludedBrands()
                    .contains(product.getBrandCode()) && !promotionCondition.getExcludedCategories()
                    .contains(product.getCategoryCode()) && !promotionCondition.getExcludedCollections()
                    .contains(product.getCollection())).collect(Collectors.toList());

        }

        return productList;
    }

    private List<Product> getProductListPromotionWithExcluded(List<Product> products,
                                                              PromotionCondition promotionCondition) {
        setExcludedCriteriaIfValueNull(promotionCondition);
        List<Product> productList;

        if (CampaignEnum.BRAND.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getBrandCode())
                            && !promotionCondition.getExcludedVariants()
                            .contains(product.getVariantId())).collect(Collectors.toList());
        } else if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getVariantId())
                            && !promotionCondition.getExcludedCollections()
                            .contains(product.getCollection())).collect(Collectors.toList());
        } else if (CampaignEnum.CATEGORY.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getCategoryCode())
                            && !promotionCondition.getExcludedVariants().contains(product.getVariantId())
                            && !promotionCondition.getExcludedBrands().contains(product.getBrandCode())
                            && !promotionCondition.getExcludedCollections().contains(
                            product.getCollection())).collect(Collectors.toList());
        } else if (CampaignEnum.COLLECTION.getContent().equalsIgnoreCase(promotionCondition.getCriteriaType())) {
            productList = products.stream().filter(product ->
                    promotionCondition.getCriteriaValue().contains(product.getCollection())
                            && !promotionCondition.getExcludedVariants()
                            .contains(product.getVariantId())
                            && !promotionCondition.getExcludedBrands()
                            .contains(product.getBrandCode())
                            && !promotionCondition.getExcludedCollections()
                            .contains(product.getCollection())
                            && !promotionCondition.getExcludedCategories()
                            .contains(product.getCategoryCode())).collect(Collectors.toList());
        } else {
            productList = products.stream().filter(product ->
                    promotionCondition.getBrands().contains(product.getBrandCode())
                            && !promotionCondition.getExcludedVariants()
                            .contains(product.getVariantId())).collect(Collectors.toList());
        }

        return productList;

    }

    private void setExcludedCriteriaIfValueNull(PromotionCondition promotionCondition) {
        if (promotionCondition.getExcludedVariants() == null) {
            promotionCondition.setExcludedVariants(new ArrayList<>());
        }
        if (promotionCondition.getExcludedBrands() == null) {
            promotionCondition.setExcludedBrands(new ArrayList<>());
        }
        if (promotionCondition.getExcludedCollections() == null) {
            promotionCondition.setExcludedCollections(new ArrayList<>());
        }
        if (promotionCondition.getExcludedCategories() == null) {
            promotionCondition.setExcludedCategories(new ArrayList<>());
        }
    }

    private void addPromotionIdDuplicateToCart(Cart cart) {
        if (cart.getCampaignApplied() != null && cart.getCampaignApplied().size() > 0) {
            cart.getCartCampaign().getCampaignSuggestion().forEach(campaignSuggestion -> {
                final CampaignSuggestion finalCampaignSuggestion = campaignSuggestion;
                Optional<CampaignApplied> campaignApplied = cart.getCampaignApplied().stream()
                        .filter(campaignApplied1 -> campaignApplied1.getPromotionId()
                                .equals(Long.valueOf(finalCampaignSuggestion.getPromotionId()))).findFirst();
                if (campaignApplied.isPresent()) {
                    if (cart.getPromoIdDuplicate() == null) {
                        cart.setPromoIdDuplicate(Lists.newArrayList());
                    }
                    if (Objects.equals(campaignSuggestion.getPromotionAction()
                            .get(0).getLimit(), campaignApplied.get().getLimit())) {
                        cart.getPromoIdDuplicate().add(campaignSuggestion.getPromotionId());
                        campaignSuggestion.setPromotionId(null);
                    } else if (campaignSuggestion.getPromotionAction().get(0)
                            .getLimit() > campaignApplied.get().getLimit()) {
                        campaignSuggestion.getPromotionAction().get(0)
                                .setLimit(campaignSuggestion.getPromotionAction()
                                        .get(0).getLimit() - campaignApplied.get().getLimit());
                        cart.getPromoIdDuplicate().add(campaignSuggestion.getPromotionId());
                    } else {
                        cart.getCampaignApplied().stream().forEach(x -> {
                            if (x.getPromotionId().equals(
                                    Long.valueOf(finalCampaignSuggestion.getPromotionId()))) {
                                x.setLimit(x.getLimit() - finalCampaignSuggestion.getPromotionAction()
                                        .get(0).getLimit());
                            }
                        });
                        campaignSuggestion.setPromotionId(null);
                    }
                }
            });
        }
    }

    private void addCampaignSuggestionToCartCampaign(Cart cart, List<CampaignSuggestion> campaignSuggestionList) {
        if (cart.getCartCampaign() == null) {
            cart.setCartCampaign(new CartCampaign());
            cart.getCartCampaign().setCampaignSuggestion(new ArrayList<>());
            cart.getCartCampaign().setCampaignSuggestion(campaignSuggestionList);
        } else {
            cart.getCartCampaign().setCampaignSuggestion(campaignSuggestionList);
        }
    }

    private List<String> getFreeVariantForPromotionFreebieItm(Promotion promotion) {
        List<String> freeVariant = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(promotion.getPromotionCondition().getFreeVariants())) {
            promotion.getPromotionCondition().getFreeVariants().forEach(x -> {
                for (int i = 1; i <= promotion.getPromotionCondition().getFreeQuantity(); i++) {
                    freeVariant.add(x);
                }
            });
        } else {
            promotion.getPromotionCondition().getFreebieConditions().forEach(fv -> {
                for (int i = 1; i <= fv.getQuantity(); i++) {
                    freeVariant.add(fv.getVariantId());
                }
            });
        }

        return freeVariant;
    }

    private List<String> getFreeVariantForPromotionFreebieWm(PromotionWM promotion) {
        List<String> freeVariant = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(promotion.getPromotionCondition().getFreeVariants())) {
            promotion.getPromotionCondition().getFreeVariants().forEach(x -> {
                for (int i = 1; i <= promotion.getPromotionCondition().getFreeQuantity(); i++) {
                    freeVariant.add(x);
                }
            });
        }

        return freeVariant;
    }

    private List<Product> getProductListPromotion(List<Product> products, List<BundleVariant> bundleVariant) {
        bundleVariant.forEach(x -> {
            String deleteBlank = x.getBundleVariant().replaceAll(" ", "");
            x.setBundleVariant(deleteBlank);
        });
        List<String> bundleList = bundleVariant.stream()
                .map(BundleVariant::getBundleVariant).collect(Collectors.toList());
        return products.stream().filter(x -> isContain(x.getVariantId(), bundleList)).collect(Collectors.toList());
    }

    private List<Product> getProductListPromotionMNP(List<Product> products, List<MNPVariant> mnpVariants) {
        mnpVariants.forEach(x -> {
            String deleteBlank = x.getMnpVariants().replaceAll(" ", "");
            x.setMnpVariants(deleteBlank);
        });
        List<String> mnpList = mnpVariants.stream()
                .map(MNPVariant::getMnpVariants).collect(Collectors.toList());
        return products.stream().filter(x -> isContain(x.getVariantId(), mnpList)).collect(Collectors.toList());
    }

    private boolean isContain(String variantId, List<String> bundleList) {
        return bundleList.contains(variantId);
    }

    public void deleteUnSuggested(Cart cart) {
        if (cart.getCartCampaign() == null) {
            cart.setCartCampaign(new CartCampaign());
            cart.getCartCampaign().setCampaignUnSuggestion(new ArrayList<>());
            for (CampaignApplied campaignApplied : cart.getCampaignApplied()) {
                cart.getCartCampaign().getCampaignUnSuggestion().add(
                        campaignAppliedToCampaignUnSuggestionFunction.apply(campaignApplied));
            }

        } else {
            if (cart.getCartCampaign().getCampaignUnSuggestion() == null) {
                cart.getCartCampaign().setCampaignUnSuggestion(new ArrayList<>());
            }
            for (CampaignApplied campaignApplied : cart.getCampaignApplied()) {
                if (cart.getPromoIdDuplicate() == null) {
                    cart.setPromoIdDuplicate(new ArrayList<>());
                }
                Optional<String> promotionIdDuplicate = cart.getPromoIdDuplicate().stream().filter(
                        p -> p.equalsIgnoreCase(campaignApplied.getPromotionId().toString())).findFirst();
                if (!promotionIdDuplicate.isPresent()) {
                    cart.getCartCampaign().getCampaignUnSuggestion().add(
                            campaignAppliedToCampaignUnSuggestionFunction.apply(campaignApplied));
                }
            }
        }
    }

    public void setEmptyCampaignSuggestionOrCampaignUnSuggestionToNull(Cart cart) {
        if (cart.getCartCampaign().getCampaignSuggestion() != null
                || cart.getCartCampaign().getCampaignUnSuggestion() != null) {
            if (cart.getCartCampaign().getCampaignSuggestion() != null
                    && cart.getCartCampaign().getCampaignSuggestion().size() < 1) {
                cart.getCartCampaign().setCampaignSuggestion(null);
            }
            if (cart.getCartCampaign().getCampaignUnSuggestion() != null
                    && cart.getCartCampaign().getCampaignUnSuggestion().size() < 1) {
                cart.getCartCampaign().setCampaignUnSuggestion(null);
            }
        }
    }

    public List<BundleForProduct> setBundleForProductItm(List<String> promotionIdList) {
        List<BundleForProduct> bundleForProducts = new ArrayList<>();
        if (promotionIdList != null) {
            promotionIdList.forEach(promotionId -> {
                Optional<Promotion> promotion = Optional.ofNullable(
                        promotionItmRepo.findOne(Long.valueOf(promotionId)));
                if (promotion.isPresent()) {
                    promotion.get().setPromotionCondition(
                            JSONUtil.parseToPromotionCondition(promotion.get().getConditionData()));
                    promotion.get().setPromotionData(JSONUtil.parseToPromotionData(
                            JSONUtil.toString(Optional.ofNullable(promotion.get().getDetailData())
                                    .orElse(JSONUtil.toString(new DetailData())))));
                    bundleForProducts.add(promotionToBundleForProductItm.apply(promotion.get()));
                }
            });
        }
        return bundleForProducts;
    }

    public List<MNPForProduct> setMNPForProductItm(List<String> promotionIdList) {
        List<MNPForProduct> mnpForProducts = new ArrayList<>();
        if (promotionIdList != null) {
            promotionIdList.forEach(promotionId -> {
                Optional<Promotion> promotion = Optional.ofNullable(
                        promotionItmRepo.findOne(Long.valueOf(promotionId)));
                if (promotion.isPresent()) {
                    promotion.get().setPromotionCondition(
                            JSONUtil.parseToPromotionCondition(promotion.get().getConditionData()));
                    promotion.get().setPromotionData(JSONUtil.parseToPromotionData(
                            JSONUtil.toString(Optional.ofNullable(promotion.get().getDetailData())
                                    .orElse(JSONUtil.toString(new DetailData())))));
                    mnpForProducts.add(promotionToMNPForProductItm.apply(promotion.get()));
                }
            });
        }
        return mnpForProducts;
    }

    public List<FreebieForProduct> setFreeForProductItm(List<String> promotionIdList) {
        List<FreebieForProduct> freebieForProducts = new ArrayList<>();
        if (promotionIdList != null) {
            promotionIdList.forEach(promotionId -> {
                Optional<Promotion> promotion = Optional.ofNullable(
                        promotionItmRepo.findOne(Long.valueOf(promotionId)));
                if (promotion.isPresent()) {
                    promotion.get().setPromotionCondition(
                            JSONUtil.parseToPromotionCondition(promotion.get().getConditionData()));
                    promotion.get().setPromotionData(
                            JSONUtil.parseToPromotionData(promotion.get().getDetailData()));
                    setPromotionData(promotion);
                    freebieForProducts.add(promotionToFreebieForProductItm.apply(promotion.get()));
                }
            });
        }
        return freebieForProducts;
    }

    private void setPromotionData(Optional<Promotion> promotion) {
        if (promotion.get().getDetailData() != null) {
            promotion.get().setPromotionData(JSONUtil.parseToPromotionData(
                    promotion.get().getDetailData()));
        } else {
            promotion.get().setPromotionData(new DetailData());
        }
    }

    public List<BundleForProduct> setBundleForProductWm(List<String> promotionIdList) {
        List<BundleForProduct> bundleForProducts = new ArrayList<>();
        if (promotionIdList != null) {
            promotionIdList.forEach(promotionId -> {
                Optional<PromotionWM> promotion = Optional.ofNullable(
                        promotionWmRepo.findOne(Long.valueOf(promotionId)));
                if (promotion.isPresent()) {
                    promotion.get().setPromotionCondition(
                            JSONUtil.parseToPromotionCondition(promotion.get().getConditionData()));
                    bundleForProducts.add(promotionToBundleForProductWm.apply(promotion.get()));
                }
            });
        }
        return bundleForProducts;
    }

    private List<Variant> setBundleVariantForBundleForProduct(List<BundleVariant> bundleVariants) {
        if (bundleVariants != null) {
            bundleVariants.forEach(this::initDiscountValue);
        }
        List<Variant> variants = null;
        if (bundleVariants != null) {
            variants = bundleVariants.stream().map(bundleVariantToVariant).collect(Collectors.<Variant>toList());
        }
        return variants;
    }

    private List<Variant> setMNPVariantForBundleForProduct(List<MNPVariant> mnpVariants) {
        if (mnpVariants != null) {
            mnpVariants.forEach(this::initDiscountValueMNP);
        }
        List<Variant> variants = null;
        if (mnpVariants != null) {
            List<MNPVariant> mnpVariants2 = new ArrayList<>();
            splitPrimaryVariant(mnpVariants, mnpVariants2);
            variants = mnpVariants2.stream().map(mnpVariantToVariant).collect(Collectors.<Variant>toList());
        }
        return variants;
    }

    private void splitPrimaryVariant(List<MNPVariant> mnpVariants, List<MNPVariant> mnpVariants2) {
        String[] primaryList = mnpVariants.get(0).getMnpVariants().split(",");
        List<String> primaryVariants = new ArrayList<>(Arrays.asList(primaryList));
        primaryVariants.forEach(p -> {
            MNPVariant mnpVariant = new MNPVariant();
            mnpVariant.setMnpVariants(p);
            mnpVariant.setDiscountFixed(mnpVariants.get(0).getDiscountFixed());
            mnpVariant.setDiscountPercent(mnpVariants.get(0).getDiscountPercent());
            mnpVariants2.add(mnpVariant);
        });
        mnpVariants.remove(0);
        mnpVariants2.addAll(mnpVariants);
    }

    private void initDiscountValue(BundleVariant bundleVariant) {
        if (bundleVariant.getDiscountPercent() == null) {
            bundleVariant.setDiscountPercent(0.0);
        } else {
            bundleVariant.setDiscountFixed(0.0);
        }
    }

    private void initDiscountValueMNP(MNPVariant mnpVariant) {
        if (mnpVariant.getDiscountPercent() == null) {
            mnpVariant.setDiscountPercent(0.0);
        } else {
            mnpVariant.setDiscountFixed(0.0);
        }
    }

    public void setPromotionForProductWm(PromotionProduct promotionProduct) {
        if (promotionProduct.getPromotionIdList() != null) {
            promotionProduct.getPromotionIdList().forEach(promotionId -> {
                PromotionWM promotion = promotionWmRepo.findOne(Long.valueOf(promotionId));
                promotion.setPromotionCondition(
                        JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
                if (promotionProduct.getPromotionForProducts() == null) {
                    promotionProduct.setPromotionForProducts(new ArrayList<>());
                    promotionProduct.getPromotionForProducts().add(
                            promotionToPromotionForProductFunctionWm.apply(promotion));
                } else {
                    promotionProduct.getPromotionForProducts().add(
                            promotionToPromotionForProductFunctionWm.apply(promotion));
                }
            });
        } else {
            promotionProduct.setPromotionForProducts(new ArrayList<>());
        }
    }

    public void setPromotionForProductITM(PromotionProduct promotionProduct) {
        if (promotionProduct.getPromotionIdList() != null) {
            promotionProduct.getPromotionIdList().forEach(promotionId -> {
                Promotion promotion = promotionItmRepo.findOne(Long.valueOf(promotionId));
                promotion.setPromotionCondition(
                        JSONUtil.parseToPromotionCondition(promotion.getConditionData()));
                promotion.setPromotionData(
                        JSONUtil.parseToPromotionData(promotion.getDetailData()));
                if (promotionProduct.getPromotionForProducts() == null) {
                    promotionProduct.setPromotionForProducts(new ArrayList<>());
                    promotionProduct.getPromotionForProducts().add(
                            promotionToPromotionForProductFunctionITM.apply(promotion));
                } else {
                    promotionProduct.getPromotionForProducts().add(
                            promotionToPromotionForProductFunctionITM.apply(promotion));
                }
            });
        } else {
            promotionProduct.setPromotionForProducts(new ArrayList<>());
        }
    }

    public PromotionProductCode setPromotionForProductCode(PromotionProduct promotionProduct) {
        PromotionProductCode promotionProductCode =
                promotionProductPromotionForProductCodeFunction.apply(promotionProduct);

        if (promotionProductCode.getPromotionIdList() != null) {
            promotionProductCode.getPromotionIdList().forEach(promotionId -> {
                Optional<PromotionWM> promotion = Optional.ofNullable(
                        promotionWmRepo.findOne(Long.valueOf(promotionId)));
                if (promotion.isPresent()) {
                    promotion.get().setPromotionCondition(
                            JSONUtil.parseToPromotionCondition(promotion.get().getConditionData()));
                    if (CampaignEnum.WM_DISCOUNT_BY_CODE.getContent().equalsIgnoreCase(promotion.get().getType())) {
                        setShowPromotionCode(promotion, promotionProductCode);
                    }
                }
            });
        } else {
            promotionProductCode.setPromotionForProductsCode(new ArrayList<>());
        }
        return promotionProductCode;
    }

    private void setShowPromotionCode(Optional<PromotionWM> promotion, PromotionProductCode promotionProductCode) {
        if (CampaignEnum.VARIANT.getContent().equalsIgnoreCase(
                promotion.get().getPromotionCondition().getCriteriaType())) {
            Optional<DiscountCodeCriteriaValue> discountCodeCriteriaValues =
                    promotion.get().getPromotionCondition().getDiscountCodeCriteriaValue().stream()
                            .filter(x -> x.getVariantId().equalsIgnoreCase(promotionProductCode
                                    .getProductVariant())).findFirst();
            if (discountCodeCriteriaValues.isPresent()
                    && discountCodeCriteriaValues.get().getShow() != null
                    && discountCodeCriteriaValues.get().getShow()) {
                setPromotionForProductsCode(promotion.get(), promotionProductCode);
            } else {
                setPromotionForProductNewListIfNull(promotionProductCode);
            }
        } else {
            if (promotion.get().getPromotionCondition().getShowCode() != null
                    && promotion.get().getPromotionCondition().getShowCode()) {
                setPromotionForProductsCode(promotion.get(), promotionProductCode);
            } else {
                setPromotionForProductNewListIfNull(promotionProductCode);
            }
        }
    }

    private void setPromotionForProductNewListIfNull(PromotionProductCode promotionProductCode) {
        if (promotionProductCode.getPromotionForProductsCode() == null) {
            promotionProductCode.setPromotionForProductsCode(new ArrayList<>());
        }
    }

    private void setPromotionForProductsCode(PromotionWM promotion, PromotionProductCode promotionProductCode) {
        Comparator<PromotionForProductCode> byPromotionId = (e1, e2) -> Integer.compare(
                Integer.valueOf(e2.getPromotionId()), Integer.valueOf(e1.getPromotionId()));
        Long codeGroupId = promotion.getPromotionCondition().getCodeGroupId();
        Optional<Code> code = Optional.ofNullable(codeRepo.findById(codeGroupId));

        if (code.isPresent()) {
            CodeDetail codeDetail = codeDetailRepo.findOne(code.get().getCodeDetail());
            if (CampaignEnum.SINGLE.getContent().equalsIgnoreCase(codeDetail.getCodeType())) {
                List<Code> codes = codeRepo.findByCodeDetail(code.get().getCodeDetail());
                if (promotionProductCode.getPromotionForProductsCode() == null) {
                    promotionProductCode.setPromotionForProductsCode(new ArrayList<>());
                    promotionProductCode.getPromotionForProductsCode()
                            .add(addPromotionForProductAndCode(promotion, codes));
                } else {
                    promotionProductCode.getPromotionForProductsCode()
                            .add(addPromotionForProductAndCode(promotion, codes));
                }
                promotionProductCode.getPromotionForProductsCode().stream().sorted(byPromotionId);
            }
        } else {
            promotionProductCode.setPromotionForProductsCode(new ArrayList<>());
        }

    }

    private PromotionForProductCode addPromotionForProductAndCode(PromotionWM promotion, List<Code> codes) {
        PromotionForProductCode promotionForProductcode = new PromotionForProductCode();
        promotionForProductcode.setPromotionId(String.valueOf(promotion.getId()));
        promotionForProductcode.setPromotionName(promotion.getName());
        promotionForProductcode.setPromotionDescription(promotion.getDescription());
        promotionForProductcode.setShortDescription(promotion.getShortDescription());
        promotionForProductcode.setImgWeb(promotion.getImgUrl());
        promotionForProductcode.setImgMobile(promotion.getImgUrlEn());
        promotionForProductcode.setImgWebTranslation(promotion.getImgThmUrl());
        promotionForProductcode.setImgMobileTranslation(promotion.getImgThmUrlEn());
        promotionForProductcode.setPromotionNameTranslation(promotion.getNameEn());
        promotionForProductcode.setPromotionDescriptionTranslation(promotion.getDescriptionEn());
        promotionForProductcode.setPromotionShortDescriptionTranslation(promotion.getShortDescriptionEn());
        Optional<String> codeListString = codes.stream().map(Code::getCode).findFirst();
        promotionForProductcode.setSingleCode(codeListString.get());

        return promotionForProductcode;
    }

    public PromotionProduct setProductDetail(String product) {
        PDSJson pdsJson = externalService.getPDSData(product).orElseThrow(PDSServiceException::new);
        PromotionProduct promotionProduct = new PromotionProduct();
        promotionProduct.setProductVariant(product);
        promotionProduct.setBrandVariant(pdsJson.getData().getBrand());
        promotionProduct.setCategory(pdsJson.getData().getCategory());
        return promotionProduct;
    }
}
