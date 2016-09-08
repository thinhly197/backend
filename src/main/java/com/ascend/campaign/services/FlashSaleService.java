package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.FlashSaleEnum;
import com.ascend.campaign.constants.FlashSaleTypeEnum;
import com.ascend.campaign.entities.AppId;
import com.ascend.campaign.entities.BaseEntity;
import com.ascend.campaign.entities.FlashSale;
import com.ascend.campaign.entities.FlashSaleCategory;
import com.ascend.campaign.entities.FlashSaleProduct;
import com.ascend.campaign.entities.FlashSaleVariant;
import com.ascend.campaign.exceptions.AppIdNotFoundException;
import com.ascend.campaign.exceptions.FlashSaleException;
import com.ascend.campaign.exceptions.FlashSaleNotFoundException;
import com.ascend.campaign.exceptions.PolicyNotFoundException;
import com.ascend.campaign.exceptions.WowBannerException;
import com.ascend.campaign.exceptions.WowExtraProductNotFoundException;
import com.ascend.campaign.models.FlashSaleProductAvailable;
import com.ascend.campaign.models.FlashSaleVariantAvailable;
import com.ascend.campaign.models.Policy;
import com.ascend.campaign.models.ProductDuplicateFlashSale;
import com.ascend.campaign.models.WowBannerProduct;
import com.ascend.campaign.models.WowBannerProductResponse;
import com.ascend.campaign.models.WowExtraProduct;
import com.ascend.campaign.repositories.AppIdRepo;
import com.ascend.campaign.repositories.FlashSaleProductRepo;
import com.ascend.campaign.repositories.FlashSaleRepo;
import com.ascend.campaign.repositories.FlashSaleVariantRepo;
import com.ascend.campaign.utils.JSONUtil;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class FlashSaleService {
    @NonNull
    private final FlashSaleRepo flashSaleRepo;

    @NonNull
    private final AppIdRepo appIdRepo;

    @NonNull
    private final FlashSaleProductRepo flashSaleProductRepo;

    @NonNull
    private final FlashSaleVariantRepo flashSaleVariantRepo;

    @NonNull
    private final ConfigurationService configurationService;


    @Autowired
    public FlashSaleService(FlashSaleRepo flashSaleRepo,
                            AppIdRepo appIdRepo,
                            ConfigurationService configurationService,
                            FlashSaleProductRepo flashSaleProductRepo,
                            FlashSaleVariantRepo flashSaleVariantRepo) {
        this.flashSaleRepo = flashSaleRepo;
        this.appIdRepo = appIdRepo;
        this.configurationService = configurationService;
        this.flashSaleProductRepo = flashSaleProductRepo;
        this.flashSaleVariantRepo = flashSaleVariantRepo;
    }

    public FlashSale createFlashSale(FlashSale flashSaleCreate) {
        if (FlashSaleTypeEnum.WOW_BANNER.getContent().equalsIgnoreCase(flashSaleCreate.getType())) {
            checkPeriod(flashSaleCreate.getStartPeriod(), flashSaleCreate.getEndPeriod());
        }
        parseFlashSaleConditionToConditionData(flashSaleCreate);

        flashSaleCreate.setAppId(getFlashSaleAppId(flashSaleCreate.getAppId().getId()));

        FlashSale flashSaleResult = flashSaleRepo.saveAndFlush(flashSaleCreate);
        saveFlashSaleProduct(flashSaleCreate, flashSaleResult);
        parseConditionDataToFlashSaleCondition(flashSaleResult);
        log.warn("content={\"activity\":\"Create flashSale\", \"msg\":{}}", JSONUtil.toString(flashSaleResult));
        return flashSaleResult;
    }

    private void parseFlashSaleConditionToConditionData(FlashSale flashSaleCreate) {
        flashSaleCreate.setConditionData(JSONUtil.toString(flashSaleCreate.getFlashSaleCondition()));
        flashSaleCreate.setBannerImagesData(JSONUtil.toString(flashSaleCreate.getBannerImages()));
    }

    private void parseConditionDataToFlashSaleCondition(FlashSale flashSaleResult) {
        flashSaleResult.setFlashSaleCondition(JSONUtil.parseToFlashSaleCondition(flashSaleResult.getConditionData()));
        flashSaleResult.setBannerImages(JSONUtil.parseToBannerImages(flashSaleResult.getBannerImagesData()));
    }

    private AppId getFlashSaleAppId(Long appId) {
        return Optional.ofNullable(appIdRepo.findOne(appId)).orElseThrow(AppIdNotFoundException::new);
    }

    private void saveFlashSaleProduct(FlashSale flashSaleCreate, FlashSale flashSaleRusult) {
        if (FlashSaleTypeEnum.WOW_BANNER.getContent().equalsIgnoreCase(flashSaleCreate.getType())) {
            Optional<FlashSaleProduct> flashSaleProductOptional =
                    Optional.ofNullable(flashSaleCreate.getFlashSaleCondition().getFlashSaleProduct());
            if (flashSaleProductOptional.isPresent()) {
                List<FlashSaleCategory> flashSaleCategories = getFlashSaleCategories(flashSaleProductOptional.get());
                flashSaleProductOptional.get().setFlashsaleCategories(flashSaleCategories);
                flashSaleProductOptional.get().setFlashSale(flashSaleRusult);
                setDiscountPercentAndPromotionPriceToFlashSaleProduct(flashSaleProductOptional.get());
                saveFlashSaleProductToDB(flashSaleProductOptional.get());
            }

        } else if (FlashSaleTypeEnum.WOW_EXTRA.getContent().equalsIgnoreCase(flashSaleCreate.getType())) {
            Optional<List<FlashSaleProduct>> flashSaleProductsOptional =
                    Optional.ofNullable(flashSaleCreate.getFlashSaleCondition().getFlashSaleProducts());
            if (flashSaleProductsOptional.isPresent()) {

                List<FlashSaleProduct> flashSaleProducts = flashSaleProductsOptional.get().stream()
                        .map(flashsaleProduct -> {
                            flashsaleProduct.setFlashSale(flashSaleRusult);
                            setDiscountPercentAndPromotionPriceToFlashSaleProduct(flashsaleProduct);
                            List<FlashSaleCategory> flashSaleCategories = getFlashSaleCategories(flashsaleProduct);
                            flashsaleProduct.setFlashsaleCategories(flashSaleCategories);
                            return flashsaleProduct;
                        }).collect(Collectors.toList());
                flashSaleProducts.forEach(this::saveFlashSaleProductToDB);
            }
        } else {
            throw new FlashSaleException(Errors.FLASH_SALE_TYPE_IS_NOT_VALID.getErrorDesc());
        }
    }

    private void setDiscountPercentAndPromotionPriceToFlashSaleProduct(FlashSaleProduct flashSaleProduct) {
        Double minDiscountPercent = flashSaleProduct.getFlashsaleVariants().stream()
                .mapToDouble(FlashSaleVariant::getDiscountPercent).min().getAsDouble();
        Double maxDiscountPercent = flashSaleProduct.getFlashsaleVariants().stream()
                .mapToDouble(FlashSaleVariant::getDiscountPercent).max().getAsDouble();
        Double minPromotionPrice = flashSaleProduct.getFlashsaleVariants().stream()
                .mapToDouble(FlashSaleVariant::getPromotionPrice).min().getAsDouble();
        Double maxPromotionPrice = flashSaleProduct.getFlashsaleVariants().stream()
                .mapToDouble(FlashSaleVariant::getPromotionPrice).max().getAsDouble();

        flashSaleProduct.setMinDiscountPercent(minDiscountPercent);
        flashSaleProduct.setMaxDiscountPercent(maxDiscountPercent);
        flashSaleProduct.setMinPromotionPrice(minPromotionPrice);
        flashSaleProduct.setMaxPromotionPrice(maxPromotionPrice);
    }

    private List<FlashSaleCategory> getFlashSaleCategories(FlashSaleProduct flashSaleProduct) {
        return flashSaleProduct.getCategoryIds()
                .stream().map(p -> {
                    FlashSaleCategory flashSaleCategory = new FlashSaleCategory();
                    flashSaleCategory.setCategoryId(p);
                    flashSaleCategory.setFlashSaleProduct(flashSaleProduct);
                    return flashSaleCategory;
                }).collect(Collectors.toList());
    }

    private void saveFlashSaleProductToDB(FlashSaleProduct flashSaleProduct) {
        flashSaleProduct.getFlashsaleVariants().forEach(p -> p.setProductKey(flashSaleProduct));
        flashSaleProductRepo.saveAndFlush(flashSaleProduct);

    }


    public FlashSale updateFlashSale(Long flashsaleId, FlashSale flashSaleUpdate) {
        log.warn("content={\"activity\":\"Update flashSale\", \"msg\":{}}", JSONUtil.toString(flashSaleUpdate));
        if (FlashSaleTypeEnum.WOW_BANNER.getContent().equalsIgnoreCase(flashSaleUpdate.getType())) {
            checkPeriodWhenUpdateFlashSale(flashsaleId, flashSaleUpdate.getStartPeriod(),
                    flashSaleUpdate.getEndPeriod());
        }

        FlashSale flashSale = Optional.ofNullable(flashSaleRepo.findOne(flashsaleId))
                .orElseThrow(FlashSaleNotFoundException::new);
        setFlashSaleUpdateToFlashSale(flashSaleUpdate, flashSale);
        List<FlashSaleProduct> flashSaleProductsOld = flashSaleProductRepo.findByFlashSale(flashSale);
        if (FlashSaleTypeEnum.WOW_BANNER.getContent().equalsIgnoreCase(flashSale.getType())
                && Optional.ofNullable(flashSaleUpdate.getFlashSaleCondition().getFlashSaleProduct()).isPresent()) {
            if (Optional.ofNullable(flashSaleProductsOld.get(0)).isPresent()) {

                List<FlashSaleCategory> flashSaleCategories = getFlashSaleCategoriesForUpdate(flashSaleUpdate
                        .getFlashSaleCondition().getFlashSaleProduct(), flashSaleProductsOld.get(0));
                flashSaleUpdate.getFlashSaleCondition().getFlashSaleProduct()
                        .setFlashsaleCategories(flashSaleCategories);

                updateProductCategories(flashSaleProductsOld.get(0).getFlashsaleCategories(), flashSaleCategories);
                flashSaleProductsOld.get(0).setProductKey(flashSaleUpdate.getFlashSaleCondition()
                        .getFlashSaleProduct().getProductKey());

                updateProductVariant(flashSaleUpdate.getFlashSaleCondition().getFlashSaleProduct(),
                        flashSaleProductsOld.get(0));
                setDiscountPercentAndPromotionPriceToFlashSaleProduct(flashSaleProductsOld.get(0));
                updateFlashSaleProductToDB(flashSaleProductsOld.get(0));

            }
        } else if (FlashSaleTypeEnum.WOW_EXTRA.getContent().equalsIgnoreCase(flashSale.getType())
                && Optional.ofNullable(flashSaleUpdate.getFlashSaleCondition().getFlashSaleProducts()).isPresent()) {
            updateFlashSaleProductWowExtra(flashSaleUpdate, flashSale, flashSaleProductsOld);
            flashSale.setPartner(flashSaleUpdate.getPartner());

        }
        flashSale.setUpdatedAt(null);
        FlashSale flashSaleUpdated = flashSaleRepo.saveAndFlush(flashSale);
        parseConditionDataToFlashSaleCondition(flashSaleUpdated);

        return flashSaleUpdated;
    }

    private void updateProductVariant(FlashSaleProduct flashSaleProductUpdate, FlashSaleProduct flashSaleProductsOld) {
        List<FlashSaleVariant> flashSaleVariants = getFlashSaleVariantsFromFlashsaleProductUpdate(flashSaleProductsOld,
                flashSaleProductUpdate);

        List<String> flashSaleVariantsFromUpdate = flashSaleVariants.stream().map(FlashSaleVariant::getVariantId)
                .collect(Collectors.toList());

        List<FlashSaleVariant> flashSaleVariantsForDelete = flashSaleProductsOld.getFlashsaleVariants()
                .stream().filter(c -> !flashSaleVariantsFromUpdate.contains(c.getVariantId()))
                .collect(Collectors.toList());

        if (!flashSaleVariantsForDelete.isEmpty()) {
            flashSaleProductsOld.getFlashsaleVariants().removeAll(flashSaleVariantsForDelete);
        }

        List<String> flashsaleVariantsString = flashSaleProductsOld.getFlashsaleVariants().stream()
                .map(FlashSaleVariant::getVariantId).collect(Collectors.toList());

        flashSaleVariants = flashSaleVariants.stream()
                .filter(p -> !flashsaleVariantsString.contains(p.getVariantId()))
                .collect(Collectors.toList());

        updateDetailOldVariants(flashSaleProductsOld.getFlashsaleVariants(),
                flashSaleProductUpdate, flashSaleVariants);

        flashSaleProductsOld.getFlashsaleVariants().addAll(flashSaleVariants);

    }

    private void updateDetailOldVariants(List<FlashSaleVariant> flashSaleVariantsOld,
                                         FlashSaleProduct flashSaleProductUpdate,
                                         List<FlashSaleVariant> flashSaleVariants) {
        List<String> variantIdList = flashSaleVariants.stream().map(FlashSaleVariant::getVariantId)
                .collect(Collectors.toList());
        flashSaleVariantsOld.forEach(p -> {
            if (!variantIdList.contains(p.getVariantId())) {
                Optional<FlashSaleVariant> flashSaleVariantOptional = flashSaleProductUpdate.getFlashsaleVariants()
                        .stream().filter(o -> p.getVariantId().equalsIgnoreCase(o.getVariantId())).findFirst();
                if (flashSaleVariantOptional.isPresent()) {
                    p.setDiscountPercent(flashSaleVariantOptional.get().getDiscountPercent());
                    p.setPromotionPrice(flashSaleVariantOptional.get().getPromotionPrice());
                    p.setLimitQuantity(flashSaleVariantOptional.get().getLimitQuantity());
                }
            }
        });
    }

    private List<FlashSaleVariant> getFlashSaleVariantsFromFlashsaleProductUpdate(
            FlashSaleProduct flashSaleProductsOld, FlashSaleProduct flashSaleProductUpdate) {
        List<FlashSaleVariant> flashSaleVariants = flashSaleProductUpdate.getFlashsaleVariants().stream()
                .map(p -> {
                    p.setProductKey(flashSaleProductsOld);
                    return p;
                }).collect(Collectors.toList());
        flashSaleProductUpdate.setFlashsaleVariants(flashSaleVariants);
        return flashSaleVariants;
    }

    private List<FlashSaleCategory> getFlashSaleCategoriesForUpdate(FlashSaleProduct flashSaleProductUpdate,
                                                                    FlashSaleProduct flashSaleProduct) {
        return flashSaleProductUpdate.getCategoryIds()
                .stream().map(p -> {
                    FlashSaleCategory flashSaleCategory = new FlashSaleCategory();
                    flashSaleCategory.setCategoryId(p);
                    flashSaleCategory.setFlashSaleProduct(flashSaleProduct);
                    return flashSaleCategory;
                }).collect(Collectors.toList());
    }

    private void updateProductCategories(List<FlashSaleCategory> flashSaleCategoriesResult,
                                         List<FlashSaleCategory> flashSaleCategories) {
        List<String> cateFromUpdate = flashSaleCategories.stream().map(FlashSaleCategory::getCategoryId)
                .collect(Collectors.toList());
        List<FlashSaleCategory> flashSaleForDelete = flashSaleCategoriesResult
                .stream().filter(c -> !cateFromUpdate.contains(c.getCategoryId())).collect(Collectors.toList());
        List<String> flashsaleCategoriesString = flashSaleCategoriesResult.stream()
                .map(FlashSaleCategory::getCategoryId).collect(Collectors.toList());
        flashSaleCategories = flashSaleCategories.stream()
                .filter(p -> !flashsaleCategoriesString.contains(p.getCategoryId()))
                .collect(Collectors.toList());
        flashSaleCategoriesResult.addAll(flashSaleCategories);
        if (!flashSaleForDelete.isEmpty()) {
            flashSaleCategoriesResult.removeAll(flashSaleForDelete);
        }
    }

    private void setFlashSaleUpdateToFlashSale(FlashSale flashSaleUpdate, FlashSale flashSale) {
        flashSale.setAppId(flashSaleUpdate.getAppId());
        flashSale.setName(flashSaleUpdate.getName());
        flashSale.setNameTranslation(flashSaleUpdate.getNameTranslation());
        flashSale.setStartPeriod(flashSaleUpdate.getStartPeriod());
        flashSale.setEndPeriod(flashSaleUpdate.getEndPeriod());
        flashSale.setEnable(flashSaleUpdate.getEnable());
        flashSale.setMember(flashSaleUpdate.getMember());
        flashSale.setNonMember(flashSaleUpdate.getNonMember());
        flashSale.setShortDescription(flashSaleUpdate.getShortDescription());
        flashSale.setShortDescriptionTranslation(flashSaleUpdate.getShortDescriptionTranslation());
        flashSale.setConditionData(JSONUtil.toString(flashSaleUpdate.getFlashSaleCondition()));
        flashSale.setFlashSaleCondition(flashSaleUpdate.getFlashSaleCondition());
        if (FlashSaleTypeEnum.WOW_BANNER.getContent().equalsIgnoreCase(flashSale.getType())) {
            flashSale.setBannerImagesData(JSONUtil.toString(flashSaleUpdate.getBannerImages()));
        }
    }

    private void updateFlashSaleProductWowExtra(FlashSale flashSaleUpdate, FlashSale flashSale,
                                                List<FlashSaleProduct> flashSaleProductsResult) {

        List<String> productKeyName = flashSaleProductsResult.stream().map(FlashSaleProduct::getProductKey)
                .collect(Collectors.toList());

        List<String> variantsNameFlashSale = flashSaleUpdate.getFlashSaleCondition().getFlashSaleProducts().stream()
                .map(FlashSaleProduct::getProductKey).collect(Collectors.toList());

        List<FlashSaleProduct> flashsaleProductForDelete = flashSaleProductsResult.stream()
                .filter(s -> !variantsNameFlashSale.contains(s.getProductKey())).collect(Collectors.toList());
        if (!flashsaleProductForDelete.isEmpty()) {
            flashSaleProductRepo.delete(flashsaleProductForDelete);
            flashSaleProductRepo.flush();

        }

        List<FlashSaleProduct> flashsaleProductForCreate = getFlashSaleProductsForCreate(flashSaleUpdate,
                flashSale, productKeyName);

        flashsaleProductForCreate.forEach(this::saveFlashSaleProductToDB);

        List<FlashSaleProduct> forUpdate = flashSaleProductsResult.stream()
                .filter(s -> !flashsaleProductForDelete.contains(s)).collect(Collectors.toList());

        forUpdate.forEach(flashSaleProductOld -> {
            Optional<FlashSaleProduct> flashSaleProductUpdate = flashSaleUpdate.getFlashSaleCondition()
                    .getFlashSaleProducts().stream().filter(fp -> fp.getProductKey()
                            .equalsIgnoreCase(flashSaleProductOld.getProductKey())).findFirst();
            if (flashSaleProductUpdate.isPresent()) {

                List<FlashSaleCategory> flashSaleCategories =
                        getFlashSaleCategoriesForUpdate(flashSaleProductUpdate.get(), flashSaleProductOld);
                flashSaleProductUpdate.get().setFlashsaleCategories(flashSaleCategories);

                updateProductCategories(flashSaleProductOld.getFlashsaleCategories(), flashSaleCategories);
                updateProductVariant(flashSaleProductUpdate.get(), flashSaleProductOld);

                setDiscountPercentAndPromotionPriceToFlashSaleProduct(flashSaleProductOld);
            }

            updateFlashSaleProductToDB(flashSaleProductOld);
        });
    }

    private List<FlashSaleProduct> getFlashSaleProductsForCreate(FlashSale flashSaleUpdate,
                                                                 FlashSale flashSale,
                                                                 List<String> productKeyName) {
        return flashSaleUpdate.getFlashSaleCondition()
                .getFlashSaleProducts().stream().filter(s -> !productKeyName.contains(s.getProductKey()))
                .map(fp -> {
                    List<FlashSaleCategory> flashSaleCategories = getFlashSaleCategories(fp);
                    fp.setFlashsaleCategories(flashSaleCategories);
                    fp.setFlashSale(flashSale);
                    setDiscountPercentAndPromotionPriceToFlashSaleProduct(fp);
                    return fp;
                }).collect(Collectors.toList());
    }

    private void updateFlashSaleProductToDB(FlashSaleProduct flashSaleProductsResult) {

        flashSaleProductRepo.saveAndFlush(flashSaleProductsResult);
    }

    public FlashSale deleteFlashSale(Long flashSaleId) {
        FlashSale flashSale = Optional.ofNullable(flashSaleRepo.findOne(flashSaleId))
                .orElseThrow(FlashSaleNotFoundException::new);

        List<FlashSaleProduct> flashSaleProductOptional =
                Optional.ofNullable(flashSaleProductRepo.findByFlashSale(flashSale)).get();
        if (!flashSaleProductOptional.isEmpty()) {
            flashSaleProductRepo.deleteByFlashSale(flashSale);
            flashSaleProductRepo.flush();
        }
        flashSaleRepo.delete(flashSale);
        flashSaleRepo.flush();
        log.warn("content={\"activity\":\"Delete flashSale\", \"msg\":{}}", JSONUtil.toString(flashSale));

        return flashSale;
    }

    @Transactional
    public FlashSale getFlashSaleById(Long flashSaleId) {
        FlashSale flashSaleResult = Optional.ofNullable(flashSaleRepo.findOne(flashSaleId))
                .orElseThrow(FlashSaleNotFoundException::new);

        flashSaleResult.setFlashSaleCondition(JSONUtil.parseToFlashSaleCondition(flashSaleResult.getConditionData()));

        if (FlashSaleTypeEnum.WOW_BANNER.getContent().equalsIgnoreCase(flashSaleResult.getType())) {
            flashSaleResult.setBannerImages(JSONUtil.parseToBannerImages(flashSaleResult.getBannerImagesData()));
            FlashSaleProduct flashSaleProduct = flashSaleResult.getFlashSaleProductList().stream().map(fp -> {
                List<String> categories = fp.getFlashsaleCategories().stream().map(FlashSaleCategory::getCategoryId)
                        .collect(Collectors.toList());
                fp.setCategoryIds(categories);
                return fp;
            }).findFirst().orElseThrow(FlashSaleException::new);
            flashSaleResult.getFlashSaleCondition().setFlashSaleProduct(flashSaleProduct);

        } else {
            List<FlashSaleProduct> flashSaleProductList = flashSaleResult.getFlashSaleProductList().stream().map(fp -> {
                List<String> categories = fp.getFlashsaleCategories().stream().map(FlashSaleCategory::getCategoryId)
                        .collect(Collectors.toList());
                fp.setCategoryIds(categories);
                return fp;
            }).collect(Collectors.toList());
            flashSaleResult.getFlashSaleCondition().setFlashSaleProducts(flashSaleProductList);
        }

        flashSaleResult.setLive(isLive(flashSaleResult.getEnable(),
                flashSaleResult.getStartPeriod(), flashSaleResult.getEndPeriod()));
        return flashSaleResult;
    }

    public Page<FlashSale> getAllFlashSale(Integer page, Integer perPage, Sort.Direction direction, String sort,
                                           Long searchID, String searchName, Boolean enable, Boolean disable,
                                           Boolean live, Boolean expired, Long startPeriod, Long endPeriod,
                                           String flashSaleType) {

        PageRequest pageRequest = genPageRequest(page - 1, perPage, direction, sort);
        Specifications specifications = customerSpecificationsFilterFlashSale(searchID, searchName, enable, disable,
                live, expired, startPeriod, endPeriod, flashSaleType);
        Page<FlashSale> flashSalePage = flashSaleRepo.findAll(specifications,
                pageRequest);
        flashSalePage.getContent().forEach(flashSale -> {
            parseConditionDataToFlashSaleCondition(flashSale);
            flashSale.setLive(isLive(flashSale.getEnable(), flashSale.getStartPeriod(), flashSale.getEndPeriod()));
            setFilterStatusPromotion(flashSale, DateTime.now().toDate());
        });

        return flashSalePage;
    }

    private PageRequest genPageRequest(int page, int number, Sort.Direction direction, String sort) {
        return new PageRequest(page, number, new Sort(direction, sort));
    }

    public WowBannerProductResponse getWowBanner(Long currentDateTime) {
        Date currentDate = new Date(currentDateTime);
        Specification specificationWowBanner = filterFlashSaleCriteriaWowBanner(currentDate);
        List<FlashSale> flashSalesEnabled = flashSaleRepo.findAll(specificationWowBanner);
        flashSalesEnabled = parseStringDataToObjectFlashSale(flashSalesEnabled);

        return getWowBannerProductResponse(flashSalesEnabled, currentDate);
    }

    private Specification<FlashSale> filterFlashSaleCriteriaWowBanner(Date currentDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("type"), FlashSaleTypeEnum.WOW_BANNER.getContent()));
            predicates.add(cb.equal(root.get("enable"), true));
            predicates.add(cb.greaterThanOrEqualTo(root.get("endPeriod"), currentDate));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private WowBannerProductResponse getWowBannerProductResponse(List<FlashSale> flashSalesEnabled, Date currentDate) {
        WowBannerProductResponse wowBannerProductResponse = new WowBannerProductResponse();
        wowBannerProductResponse.setCurrentWow(getCurrentWow(flashSalesEnabled, currentDate));
        if (Optional.ofNullable(wowBannerProductResponse.getCurrentWow()).isPresent()) {
            wowBannerProductResponse.setNextWow(getNextWow(flashSalesEnabled,
                    wowBannerProductResponse.getCurrentWow().getEndPeriod()));
        }
        if (Optional.ofNullable(wowBannerProductResponse.getNextWow()).isPresent()) {
            wowBannerProductResponse.setIncomingWow(getNextWow(flashSalesEnabled,
                    wowBannerProductResponse.getNextWow().getEndPeriod()));
        }
        return wowBannerProductResponse;
    }

    private List<FlashSale> parseStringDataToObjectFlashSale(List<FlashSale> flashSalesEnabled) {
        return flashSalesEnabled.stream().map(flashSale -> {
            parseConditionDataToFlashSaleCondition(flashSale);
            return flashSale;
        }).collect(Collectors.toList());
    }

    private WowBannerProduct getCurrentWow(List<FlashSale> flashSalesEnabled, Date currentDate) {
        Optional<FlashSale> currentWow = flashSalesEnabled.stream().filter(flashSale -> currentDate.after(
                flashSale.getStartPeriod()) && currentDate.before(flashSale.getEndPeriod())).min((fist, second) ->
                Double.compare(fist.getFlashSaleCondition().getFlashSaleProduct().getMinPromotionPrice(),
                        second.getFlashSaleCondition().getFlashSaleProduct().getMinPromotionPrice()));
        if (currentWow.isPresent()) {
            return getFlashSaleResponse(currentWow.get());
        } else {
            return getNextWow(flashSalesEnabled, currentDate);
        }
    }

    private WowBannerProduct getNextWow(List<FlashSale> flashSalesEnabled, Date endPeriodCurrentWow) {
        Optional<FlashSale> nextWow = flashSalesEnabled.stream().filter(flashSale -> flashSale.getStartPeriod()
                .after(endPeriodCurrentWow)
                || flashSale.getStartPeriod().compareTo(endPeriodCurrentWow) == 0
                && flashSale.getEndPeriod().after(endPeriodCurrentWow))
                .min(Comparator.comparing(FlashSale::getEndPeriod));
        if (nextWow.isPresent()) {
            return getFlashSaleResponse(nextWow.get());
        } else {
            return null;
        }
    }

    public Boolean checkPeriod(Date startPeriod, Date endPeriod) {

        List<FlashSale> flashSalesEnabled = flashSaleRepo.findByType(FlashSaleTypeEnum.WOW_BANNER.getContent());

        if (isInValidStartPeriodOrEndPeriod(startPeriod, flashSalesEnabled)
                || isInValidStartPeriodOrEndPeriod(endPeriod, flashSalesEnabled)
                || flashSalesEnabled.stream().filter(fs -> fs.getStartPeriod().compareTo(startPeriod) == 0
                || fs.getEndPeriod().compareTo(endPeriod) == 0).findFirst().isPresent()
                || flashSalesEnabled.stream().filter(fs -> fs.getStartPeriod().after(startPeriod)
                && fs.getEndPeriod().before(endPeriod)).findFirst().isPresent()) {
            throw new WowBannerException(Errors.FLASH_SALE_INVALID_PERIOD.getErrorDesc());
        } else {
            return true;
        }
    }

    public Boolean checkPeriodWhenUpdateFlashSale(Long flashsaleId, Date startPeriod, Date endPeriod) {
        List<FlashSale> flashSalesEnabled = flashSaleRepo.findByType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        flashSalesEnabled = flashSalesEnabled.stream().filter(f -> !Objects.equals(f.getId(), flashsaleId))
                .collect(Collectors.toList());
        if (isInValidStartPeriodOrEndPeriod(startPeriod, flashSalesEnabled)
                || isInValidStartPeriodOrEndPeriod(endPeriod, flashSalesEnabled)
                || flashSalesEnabled.stream().filter(fs -> fs.getStartPeriod().compareTo(startPeriod) == 0
                || fs.getEndPeriod().compareTo(endPeriod) == 0).findFirst().isPresent()
                || flashSalesEnabled.stream().filter(fs -> fs.getStartPeriod().after(startPeriod)
                && fs.getEndPeriod().before(endPeriod)).findFirst().isPresent()) {
            throw new WowBannerException(Errors.FLASH_SALE_INVALID_PERIOD.getErrorDesc());
        } else {
            return true;
        }
    }

    private boolean isInValidStartPeriodOrEndPeriod(Date startPeriod, List<FlashSale> flashSalesEnabled) {
        return flashSalesEnabled.stream().filter(fs -> fs.getStartPeriod().before(startPeriod)
                && fs.getEndPeriod().after(startPeriod)).findFirst().isPresent();
    }

    public WowBannerProduct getFlashSaleResponse(FlashSale flashSale) {
        WowBannerProduct wowBannerProduct = new WowBannerProduct();
        wowBannerProduct.setCurrentBanner(flashSale.getBannerImages().getTodayBanner());
        wowBannerProduct.setNextBanner(flashSale.getBannerImages().getTomorrowBanner());
        wowBannerProduct.setIncomingBanner(flashSale.getBannerImages().getIncomingBanner());
        wowBannerProduct.setName(flashSale.getName());
        wowBannerProduct.setNameTranslation(flashSale.getNameTranslation());
        wowBannerProduct.setShortDescription(flashSale.getShortDescription());
        wowBannerProduct.setShortDescriptionTranslation(flashSale.getShortDescriptionTranslation());
        wowBannerProduct.setStartPeriod(flashSale.getStartPeriod());
        wowBannerProduct.setEndPeriod(flashSale.getEndPeriod());
        wowBannerProduct.setFlashSaleProduct(getFlashSaleProductWowBanner(flashSale));
        return wowBannerProduct;
    }

    private FlashSaleProduct getFlashSaleProductWowBanner(FlashSale flashSale) {
        return flashSale.getFlashSaleProductList().stream().map(fp -> {
            List<String> categories = fp.getFlashsaleCategories().stream().map(FlashSaleCategory::getCategoryId)
                    .collect(Collectors.toList());
            fp.setCategoryIds(categories);
            return fp;
        }).findFirst().orElseThrow(FlashSaleException::new);

    }

    private void setFilterStatusPromotion(FlashSale flashsale, Date currentTime) {

        if (Optional.ofNullable(flashsale.getEndPeriod()).orElse(currentTime).before(currentTime)) {
            flashsale.setFilterStatus(CampaignEnum.FILTER_EXPIRED.getContent());
        } else if (Optional.ofNullable(flashsale.getLive()).orElse(false)) {
            flashsale.setFilterStatus(CampaignEnum.FILTER_LIVE.getContent());
        } else if (Optional.ofNullable(flashsale.getEnable()).orElse(false)) {
            flashsale.setFilterStatus(CampaignEnum.FILTER_ENABLED.getContent());
        } else {
            flashsale.setFilterStatus(CampaignEnum.FILTER_DISABLE.getContent());
        }
    }

    public List<AppId> getAppIds() {
        return appIdRepo.findAll();
    }

    public AppId createAppId(AppId appId) {
        return appIdRepo.saveAndFlush(appId);
    }

    public FlashSale enableFlashSale(long flashSaleId) {
        FlashSale flashSale = Optional.ofNullable(flashSaleRepo.findOne(flashSaleId))
                .orElseThrow(FlashSaleNotFoundException::new);
        flashSale.setEnable(true);
        parseConditionDataToFlashSaleCondition(flashSale);
        return flashSaleRepo.saveAndFlush(flashSale);
    }

    public FlashSale disabledFlashSale(Long flashSaleId) {
        FlashSale flashSale = Optional.ofNullable(flashSaleRepo.findOne(flashSaleId))
                .orElseThrow(FlashSaleNotFoundException::new);
        flashSale.setEnable(false);
        parseConditionDataToFlashSaleCondition(flashSale);
        return flashSaleRepo.saveAndFlush(flashSale);
    }

    @Transactional
    public Page<FlashSaleProduct> getWowExtra(Integer page, Integer perPage, Sort.Direction direction,
                                              String sort, String category) {
        PageRequest pageRequest = getPageRequestWowExtra(page, perPage, direction, sort);
        Specification specification = filterFlashSaleProductCriteria(category);

        Page<FlashSaleProduct> wowExtraResult = flashSaleProductRepo.findAll(specification, pageRequest);
        List<WowExtraProduct> wowExtraProducts = mapFlashSaleProductToWowExtraProduct(wowExtraResult.getContent());
        wowExtraProducts = filterUniqueWowExtraProductByLowerPromotionPrice(wowExtraProducts);
        return new PageImpl(wowExtraProducts, pageRequest, wowExtraResult.getTotalElements());
    }

    private PageRequest getPageRequestWowExtra(Integer page, Integer perPage, Sort.Direction direction, String sort) {
        PageRequest pageRequest;
        if (FlashSaleEnum.SORT_DISCOUNT_PERCENT.getContent().equalsIgnoreCase(sort)) {
            if (direction.equals(Sort.Direction.ASC)) {
                pageRequest = genPageRequest(page - 1, perPage, direction,
                        FlashSaleEnum.SORT_MIN_DISCOUNT_PERCENT.getContent());
            } else {
                pageRequest = genPageRequest(page - 1, perPage, direction,
                        FlashSaleEnum.SORT_MAX_DISCOUNT_PERCENT.getContent());
            }
        } else if (FlashSaleEnum.SORT_PROMOTION_PRICE.getContent().equalsIgnoreCase(sort)) {
            if (direction.equals(Sort.Direction.ASC)) {
                pageRequest = genPageRequest(page - 1, perPage, direction,
                        FlashSaleEnum.SORT_MIN_PROMOTION_PRICE.getContent());
            } else {
                pageRequest = genPageRequest(page - 1, perPage, direction,
                        FlashSaleEnum.SORT_MAX_PROMOTION_PRICE.getContent());
            }
        } else {
            if (direction.equals(Sort.Direction.ASC)) {
                pageRequest = genPageRequest(page - 1, perPage, Sort.Direction.DESC,
                        FlashSaleEnum.SORT_LATEST_FLASHSALE.getContent());
            } else {
                pageRequest = genPageRequest(page - 1, perPage, Sort.Direction.ASC,
                        FlashSaleEnum.SORT_LATEST_FLASHSALE.getContent());
            }

        }
        return pageRequest;
    }

    private List<WowExtraProduct> filterUniqueWowExtraProductByLowerPromotionPrice(
            List<WowExtraProduct> wowExtraProducts) {
        LinkedHashMap<String, WowExtraProduct> wowExtraVariantMap = new LinkedHashMap();
        wowExtraProducts.forEach(p -> {
            if (wowExtraVariantMap.get(p.getProductKey()) == null) {
                wowExtraVariantMap.put(p.getProductKey(), p);
            } else if (p.getMinPromotionPrice() < wowExtraVariantMap.get(p.getProductKey()).getMinPromotionPrice()) {
                wowExtraVariantMap.remove(p.getProductKey());
                wowExtraVariantMap.put(p.getProductKey(), p);
            }
        });
        return new ArrayList<>(wowExtraVariantMap.values());
    }

    private List<WowExtraProduct> mapFlashSaleProductToWowExtraProduct(
            List<FlashSaleProduct> wowExtraFlashSaleProduct) {
        return wowExtraFlashSaleProduct.stream().map(flashsaleProduct -> {
            WowExtraProduct wowExtraProduct = new WowExtraProduct();
            wowExtraProduct.setProductKey(flashsaleProduct.getProductKey());
            List<String> categories = flashsaleProduct.getFlashsaleCategories().stream()
                    .map(FlashSaleCategory::getCategoryId).collect(Collectors.toList());
            wowExtraProduct.setCategorise(categories);
            wowExtraProduct.setMinPromotionPrice(flashsaleProduct.getMinPromotionPrice());
            wowExtraProduct.setMaxPromotionPrice(flashsaleProduct.getMaxPromotionPrice());
            wowExtraProduct.setMinDiscountPercent(flashsaleProduct.getMinDiscountPercent());
            wowExtraProduct.setMaxDiscountPercent(flashsaleProduct.getMaxDiscountPercent());
            wowExtraProduct.setStartPeriod(flashsaleProduct.getFlashSale().getStartPeriod());
            wowExtraProduct.setEndPeriod(flashsaleProduct.getFlashSale().getEndPeriod());
            wowExtraProduct.setPartner(flashsaleProduct.getFlashSale().getPartner());
            wowExtraProduct.setFlashSaleVariants(flashsaleProduct.getFlashsaleVariants());
            return wowExtraProduct;
        }).collect(Collectors.toList());
    }


    private Specification<FlashSaleProduct> filterFlashSaleProductCriteria(String category) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (category != null) {
                predicates.add(cb.equal(root.join("flashsaleCategories").get("categoryId"), category));
            }
            predicates.add(cb.equal(root.get("flashSale").get("enable"), true));
            predicates.add(cb.lessThanOrEqualTo(root.get("flashSale").get("startPeriod"), DateTime.now().toDate()));
            predicates.add(cb.greaterThanOrEqualTo(root.get("flashSale").get("endPeriod"), DateTime.now().toDate()));

            //query.select(root.get("productKey")).distinct(true);
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }


    public Boolean isLive(Boolean isEnable, Date start, Date end) {
        if (isEnable != null && start != null && end != null) {
            DateTime startDT = new DateTime(start);
            DateTime endDT = new DateTime(end);
            return isEnable && startDT.isBeforeNow() && endDT.isAfterNow();
        } else {
            return false;
        }
    }

    public Policy getFlashSalePolicyImage(Long policyNumber) {
        HashMap<Long, String> policiesImg = configurationService.getPolicies();
        String policyImg = Optional.ofNullable(policiesImg.get(policyNumber)).orElseThrow(PolicyNotFoundException::new);
        Policy policy1 = new Policy();
        policy1.setPolicy(policyNumber);
        policy1.setPolicyImg(policyImg);
        return policy1;
    }

    public WowExtraProduct getFlashSaleProductByProductKey(String productKey) {
        List<FlashSaleProduct> flashSaleProducts = flashSaleProductRepo.findByProductKey(productKey);
        return flashSaleProducts.stream().filter(fp ->
                isLive(fp.getFlashSale().getEnable(),
                        fp.getFlashSale().getStartPeriod(),
                        fp.getFlashSale().getEndPeriod())
        ).min((fist, second) -> Double.compare(fist.getMinPromotionPrice(),
                second.getMinPromotionPrice())).map(this::getWowExtraProduct)
                .orElseThrow(WowExtraProductNotFoundException::new);
    }

    private WowExtraProduct getWowExtraProduct(FlashSaleProduct flashSaleProduct1) {
        flashSaleProduct1.getFlashSale().setFlashSaleCondition(
                JSONUtil.parseToFlashSaleCondition(flashSaleProduct1.getFlashSale().getConditionData()));
        WowExtraProduct wowExtraProduct = new WowExtraProduct();
        wowExtraProduct.setProductKey(flashSaleProduct1.getProductKey());
        List<FlashSaleCategory> flashsaleCategories = flashSaleProduct1.getFlashsaleCategories();
        wowExtraProduct.setCategorise(flashsaleCategories.stream().map(FlashSaleCategory::getCategoryId)
                .collect(Collectors.toList()));
        wowExtraProduct.setStartPeriod(flashSaleProduct1.getFlashSale().getStartPeriod());
        wowExtraProduct.setEndPeriod(flashSaleProduct1.getFlashSale().getEndPeriod());
        wowExtraProduct.setMinDiscountPercent(flashSaleProduct1.getMinDiscountPercent());
        wowExtraProduct.setMaxDiscountPercent(flashSaleProduct1.getMaxDiscountPercent());
        wowExtraProduct.setMinPromotionPrice(flashSaleProduct1.getMinPromotionPrice());
        wowExtraProduct.setMaxPromotionPrice(flashSaleProduct1.getMaxPromotionPrice());
        wowExtraProduct.setPartner(flashSaleProduct1.getFlashSale().getPartner());
        Policy policy = getPolicy(flashSaleProduct1);
        wowExtraProduct.setPolicy(policy);
        wowExtraProduct.setFlashSaleVariants(flashSaleProduct1.getFlashsaleVariants());
        return wowExtraProduct;
    }

    private Policy getPolicy(FlashSaleProduct flashSaleProduct1) {
        Policy policy = new Policy();
        policy.setPolicy(Long.valueOf(flashSaleProduct1.getFlashSale().getFlashSaleCondition().getLimitItem()));
        policy.setPolicyImg(flashSaleProduct1.getFlashSale().getFlashSaleCondition().getLimitItemImg());
        return policy;
    }

    private Specifications customerSpecificationsFilterFlashSale(Long searchID, String searchName,
                                                                 Boolean enable, Boolean disable,
                                                                 Boolean live, Boolean expired,
                                                                 Long startPeriod, Long endPeriod,
                                                                 String flashSaleType) {
        return Specifications.where(getSpecificationsFlashSaleStatus(enable, disable, expired, live))
                .and(getSpecificationsFlashSaleFilter(searchID, searchName, startPeriod, endPeriod, flashSaleType));
    }

    private Specification<FlashSale> getSpecificationsFlashSaleStatus(Boolean enable, Boolean disabled,
                                                                      Boolean expired, Boolean live) {
        Date currentTime = DateTime.now().toDate();
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (enable) {
                Predicate enablePredicate = cb.equal(root.get("enable"), true);
                Predicate startPeriodPredicate = cb.greaterThanOrEqualTo(root.get("startPeriod"),
                        currentTime);
                Predicate endPeriodPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate enablePredicates = cb.and(enablePredicate, startPeriodPredicate,
                        endPeriodPredicate);
                predicates.add(enablePredicates);
            }
            if (disabled) {
                Predicate enablePredicate = cb.equal(root.get("enable"), false);
                Predicate endPeriodPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate disabledPredicate = cb.and(enablePredicate, endPeriodPredicate);
                predicates.add(disabledPredicate);
            }
            if (expired) {
                Predicate endpiredPredicate = cb.lessThanOrEqualTo(root.get("endPeriod"),
                        currentTime);
                Predicate startPeriodPredicate = cb.lessThanOrEqualTo(root.get("startPeriod"),
                        currentTime);
                Predicate expiredPredicates = cb.and(endpiredPredicate, startPeriodPredicate);
                predicates.add(expiredPredicates);
            }
            if (live) {
                Predicate enablePredicate = cb.equal(root.get("enable"), true);
                Predicate startPeriodPredicate = cb.lessThanOrEqualTo(root.get("startPeriod"), currentTime);
                Predicate endPeriodPredicate = cb.greaterThanOrEqualTo(root.get("endPeriod"), currentTime);
                Predicate isLivePredicate = cb.and(enablePredicate, startPeriodPredicate, endPeriodPredicate);
                predicates.add(isLivePredicate);
            }
            if (predicates.isEmpty()) {
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
            return cb.or(predicates.toArray(new Predicate[predicates.size()]));
        };

    }

    private Specification<FlashSale> getSpecificationsFlashSaleFilter(Long searchID, String searchName,
                                                                      Long startPeriod, Long endPeriod,
                                                                      String flashSaleType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (Optional.ofNullable(searchID).isPresent()) {
                predicates.add(cb.equal(root.get("id"), searchID));
            }
            if (!StringUtils.isEmpty(searchName)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + searchName.toLowerCase() + "%"));
            }
            if (startPeriod != null && endPeriod != null) {
                Date expiry = new Date(startPeriod);
                Date expiry2 = new Date(endPeriod);
                predicates.add(cb.lessThanOrEqualTo(root.get("startPeriod"), expiry2));
                predicates.add(cb.greaterThanOrEqualTo(root.get("endPeriod"), expiry));
            }
            if (!StringUtils.isEmpty(flashSaleType)) {
                predicates.add(cb.equal(root.get("type"), flashSaleType));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }


    public List<FlashSaleProductAvailable> updateFlashSaleProductStatus(List<FlashSaleProductAvailable>
                                                                                flashSaleProductAvailable) {
        log.warn("content={\"activity\":\"Update flashSale product is available status"
                + "\", \"msg\":{}}", JSONUtil.toString(flashSaleProductAvailable));

        if (Optional.ofNullable(flashSaleProductAvailable).isPresent()) {
            flashSaleProductAvailable.stream().filter(fsp -> fsp.getProductKey() != null
                    && fsp.getIsAvailable() != null)
                    .forEach(flashSaleProduct -> {
                                Optional<List<FlashSaleProduct>> flashSaleProducts =
                                        Optional.ofNullable(flashSaleProductRepo
                                                .findByProductKey(flashSaleProduct.getProductKey()));
                                if (flashSaleProducts.isPresent() && !flashSaleProducts.get().isEmpty()) {
                                    List<FlashSaleProduct> flashSaleProductList = flashSaleProducts.get().stream()
                                            .map(fsProduct -> {
                                                fsProduct.setIsAvailable(flashSaleProduct.getIsAvailable());
                                                return fsProduct;
                                            }).collect(Collectors.toList());
                                    flashSaleProductRepo.save(flashSaleProductList);
                                    flashSaleProductRepo.flush();
                                }
                            }
                    );
        }

        return flashSaleProductAvailable;
    }


    private List<FlashSaleProductAvailable> mapFlashSaleProductToFlashSaleProductAvailable(
            List<FlashSaleProduct> flashSaleProductList) {
        return flashSaleProductList.stream().map(fp -> {
            FlashSaleProductAvailable flashSaleProductAvailable = new FlashSaleProductAvailable();
            flashSaleProductAvailable.setProductKey(fp.getProductKey());
            flashSaleProductAvailable.setIsAvailable(fp.getIsAvailable());
            return flashSaleProductAvailable;
        }).collect(Collectors.toList());
    }

    public List<FlashSaleVariantAvailable> updateFlashSaleVariantStatus(
            List<FlashSaleVariantAvailable> flashSaleProductAvailables) {
        log.warn("content={\"activity\":\"Update flashSale variant is available status"
                + "\", \"msg\":{}}", JSONUtil.toString(flashSaleProductAvailables));

        if (Optional.ofNullable(flashSaleProductAvailables).isPresent()) {
            flashSaleProductAvailables.stream().filter(fsv -> fsv.getVariantId() != null
                    && fsv.getIsAvailable() != null)
                    .forEach(flashSaleVariant -> {
                                Optional<List<FlashSaleVariant>> flashSaleVariants =
                                        Optional.ofNullable(flashSaleVariantRepo
                                                .findByVariantId(flashSaleVariant.getVariantId()));
                                if (flashSaleVariants.isPresent() && !flashSaleVariants.get().isEmpty()) {
                                    List<FlashSaleVariant> flashSaleProductList = flashSaleVariants.get().stream()
                                            .map(fsVariant -> {
                                                fsVariant.setIsAvailable(flashSaleVariant.getIsAvailable());
                                                return fsVariant;
                                            }).collect(Collectors.toList());
                                    flashSaleVariantRepo.save(flashSaleProductList);
                                    flashSaleVariantRepo.flush();
                                }
                            }
                    );
        }

        return flashSaleProductAvailables;

    }

    public List<ProductDuplicateFlashSale> checkProductDuplicate(String products, Long startPeriod,
                                                                 Long endPeriod, Long flashsaleId) {
        List<FlashSale> flashsales = flashSaleRepo.findFlashSaleByDateTime(
                new DateTime(startPeriod).toString("yyyy-MM-dd HH:mm:ss"),
                new DateTime(endPeriod).toString("yyyy-MM-dd HH:mm:ss"));

        if (!flashsales.isEmpty()) {
            List<String> productKeyList = isValidateAndSplitBatchProductKey(products);
            return getCollectProductKeyDuplicateFlashSaleFromProducts(flashsales, productKeyList, flashsaleId);
        }

        return new ArrayList<>();
    }

    private List<String> isValidateAndSplitBatchProductKey(String productKeys) {
        if (isValidateBatch(productKeys)) {
            throw new FlashSaleException();
        } else {
            return Arrays.stream(productKeys.split(",")).collect(Collectors.toList());
        }
    }

    private boolean isValidateBatch(String productVariant) {
        return productVariant == null || productVariant.isEmpty();
    }

    private List<ProductDuplicateFlashSale> getCollectProductKeyDuplicateFlashSaleFromProducts(
            List<FlashSale> flashsales, List<String> variants, Long flashsaleId) {
        return variants.stream().map(productKey -> {
            ProductDuplicateFlashSale variantDuplicateFreebie = new ProductDuplicateFlashSale();
            variantDuplicateFreebie.setProductKey(productKey);
            variantDuplicateFreebie.setDuplicateFlashsaleId(setProductDuplicateFlashsaleId(
                    productKey, flashsales, flashsaleId));
            return variantDuplicateFreebie;

        }).collect(Collectors.toList());
    }

    private List<Long> setProductDuplicateFlashsaleId(String product, List<FlashSale> flashsales, Long flashsaleI) {
        return flashsales.stream().filter(flashSale ->
                flashSale.getFlashSaleProductList().stream().map(FlashSaleProduct::getProductKey)
                        .collect(Collectors.toList())
                        .contains(product) && !Objects.equals(flashSale.getId(), flashsaleI))
                .map(BaseEntity::getId).collect(Collectors.toList());
    }
}
