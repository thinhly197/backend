package com.ascend.campaign.services;

import com.ascend.campaign.constants.FlashSaleTypeEnum;
import com.ascend.campaign.entities.AppId;
import com.ascend.campaign.entities.FlashSale;
import com.ascend.campaign.entities.FlashSaleCategory;
import com.ascend.campaign.entities.FlashSaleProduct;
import com.ascend.campaign.entities.FlashSaleVariant;
import com.ascend.campaign.exceptions.FlashSaleNotFoundException;
import com.ascend.campaign.exceptions.PolicyNotFoundException;
import com.ascend.campaign.exceptions.WowBannerException;
import com.ascend.campaign.exceptions.WowExtraProductNotFoundException;
import com.ascend.campaign.models.BannerImages;
import com.ascend.campaign.models.FlashSaleCondition;
import com.ascend.campaign.models.FlashSaleProductAvailable;
import com.ascend.campaign.models.FlashSaleVariantAvailable;
import com.ascend.campaign.models.Policy;
import com.ascend.campaign.models.ProductDuplicateFlashSale;
import com.ascend.campaign.models.WowBannerProductResponse;
import com.ascend.campaign.models.WowExtraProduct;
import com.ascend.campaign.repositories.AppIdRepo;
import com.ascend.campaign.repositories.FlashSaleProductRepo;
import com.ascend.campaign.repositories.FlashSaleRepo;
import com.ascend.campaign.repositories.FlashSaleVariantRepo;
import com.ascend.campaign.utils.JSONUtil;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FlashSaleServiceTest {
    @Autowired
    FlashSaleService flashSaleService;

    @Mock
    private FlashSaleRepo flashSaleRepo;

    @Mock
    private AppIdRepo appIdRepo;

    @Mock
    private FlashSaleProductRepo flashSaleProductRepo;

    @Mock
    private FlashSaleVariantRepo flashSaleVariantRepo;

    @Mock
    private ConfigurationService configurationService;

    private FlashSale flashSale1;
    private FlashSale flashSale2;
    private FlashSale flashSale;


    private AppId appId;

    private Long currentDate = DateTime.now().plusMillis(30).getMillis();

    @Before
    public void setUp() {
        flashSaleService = new FlashSaleService(flashSaleRepo,
                appIdRepo,
                configurationService,
                flashSaleProductRepo,
                flashSaleVariantRepo);


        flashSale1 = new FlashSale();
        flashSale1.setId(1L);
        flashSale1.setName("flashSaleName1");
        flashSale2 = new FlashSale();
        flashSale2.setId(2L);
        flashSale2.setName("flashSaleName2");

        flashSale = new FlashSale();
        flashSale.setName("current");

        FlashSaleVariant flashSaleCriteriaValue = new FlashSaleVariant();
        flashSaleCriteriaValue.setVariantId("variantId1");
        flashSaleCriteriaValue.setPromotionPrice(100D);
        final BannerImages bannerImages = new BannerImages();
        FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("PD111");
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleCriteriaValue));
        flashSaleCondition.setFlashSaleProducts(Arrays.asList(flashSaleProduct));
        flashSale.setConditionData(JSONUtil.toString(flashSaleCondition));
        flashSale.setBannerImagesData(JSONUtil.toString(flashSale.getBannerImages()));
        flashSale.setStartPeriod(DateTime.now().toDate());
        flashSale.setEndPeriod(DateTime.now().plusHours(1).toDate());
        flashSale.setBannerImages(bannerImages);
        flashSale.setBannerImagesData(JSONUtil.toString(bannerImages));
        flashSale.setFlashSaleCondition(flashSaleCondition);

        appId = new AppId();
        appId.setName("itm");

    }

    @Test
    public void shouldReturnPageFlashSaleWhenGetExistingAllFlashSaleInDb() throws Exception {
        FlashSale flashSale3 = new FlashSale();
        flashSale3.setFlashSaleCondition(flashSale.getFlashSaleCondition());
        flashSale3.setConditionData(flashSale.getConditionData());
        flashSale3.setEndPeriod(DateTime.now().minusHours(1).toDate());
        flashSale3.setEnable(false);
        FlashSale flashSale4 = new FlashSale();
        flashSale4.setFlashSaleCondition(flashSale.getFlashSaleCondition());
        flashSale4.setConditionData(flashSale.getConditionData());
        flashSale4.setEnable(true);
        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale1, flashSale2, flashSale3, flashSale4)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, null,
                null, null, null, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaEnableDisableExpirdAndActiveCorrectlyWhenGetExistingFlashSale()
            throws Exception {

        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, true,
                true, true, true, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaEnableDisableAndExpiredCorrectlyWhenGetExistingFlashSale()
            throws Exception {

        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, true,
                true, null, true, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaEnableAndDisableCorrectlyWhenGetExistingFlashSale()
            throws Exception {
        FlashSale flashSale3 = new FlashSale();
        flashSale3.setFlashSaleCondition(flashSale.getFlashSaleCondition());
        flashSale3.setConditionData(flashSale.getConditionData());
        flashSale3.setBannerImagesData(flashSale.getBannerImagesData());
        flashSale3.setEndPeriod(DateTime.now().minusHours(1).toDate());
        flashSale3.setEnable(false);
        flashSale3.setLive(false);
        FlashSale flashSale4 = new FlashSale();
        flashSale4.setFlashSaleCondition(flashSale.getFlashSaleCondition());
        flashSale4.setConditionData(flashSale.getConditionData());
        flashSale4.setBannerImagesData(flashSale.getBannerImagesData());
        flashSale4.setEnable(true);
        flashSale4.setEndPeriod(DateTime.now().toDate());
        flashSale4.setLive(false);

        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale, flashSale3, flashSale4)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, true,
                true, null, null, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaEnableCorrectlyWhenGetExistingFlashSale()
            throws Exception {
        flashSale.setLive(true);
        flashSale.setEnable(true);
        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, true,
                null, null, null, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaDisableCorrectlyWhenGetExistingFlashSale()
            throws Exception {
        flashSale.setLive(true);
        flashSale.setEnable(true);
        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, null,
                true, null, null, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaActiveCorrectlyWhenGetExistingFlashSale()
            throws Exception {
        flashSale.setLive(true);
        flashSale.setEnable(true);
        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, null,
                null, true, null, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaExpiredCorrectlyWhenGetExistingFlashSale()
            throws Exception {
        flashSale.setLive(true);
        flashSale.setEnable(true);
        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, null,
                null, null, true, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }


    @Test
    public void shouldFilterFlashSaleListByCriteriaDisableAndExpiredCorrectlyWhenGetExistingFlashSale()
            throws Exception {
        flashSale.setLive(true);
        flashSale.setEnable(true);
        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, null,
                true, null, true, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldFilterFlashSaleListByCriteriaEnableAndExpiredCorrectlyWhenGetExistingFlashSale()
            throws Exception {
        flashSale.setLive(true);
        flashSale.setEnable(true);
        when(flashSaleRepo.findAll(any(), any(PageRequest.class))).thenReturn(
                new PageImpl(Arrays.asList(flashSale)));
        Page<FlashSale> result = flashSaleService.getAllFlashSale(1, 5, Sort.Direction.ASC, "id", null, null, true,
                null, null, true, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));
        verify(flashSaleRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldCreateNewFlashSaleWowBannerSuccessfullyWhenCreateNonExistingFlashSaleInDb() {
        AppId appId = new AppId();
        appId.setName("app_id");
        flashSale1.setAppId(appId);
        final FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setDiscountPercent(20D);
        flashSaleVariant.setPromotionPrice(90D);
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("pdid");
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));
        flashSaleCondition.setFlashSaleProduct(flashSaleProduct);
        flashSale1.setFlashSaleCondition(flashSaleCondition);
        flashSale1.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());

        when(appIdRepo.findOne(anyLong())).thenReturn(appId);
        when(flashSaleRepo.saveAndFlush(any(FlashSale.class))).thenReturn(flashSale1);
        when(flashSaleProductRepo.saveAndFlush(any(FlashSaleProduct.class))).thenReturn(flashSaleProduct);


        FlashSale flashSaleCreated = flashSaleService.createFlashSale(flashSale1);

        assertThat(flashSaleCreated, notNullValue());

        verify(appIdRepo).findOne(anyLong());
        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));
        verify(flashSaleProductRepo).saveAndFlush(any(FlashSaleProduct.class));
    }

    @Test
    public void shouldCreateNewFlashSaleWowExtraSuccessfullyWhenCreateNonExistingFlashSaleInDb() {
        AppId appId = new AppId();
        appId.setName("app_id");
        when(appIdRepo.findOne(anyLong())).thenReturn(appId);
        flashSale1.setAppId(appId);
        final FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setPromotionPrice(90D);
        flashSaleVariant.setDiscountPercent(50D);
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));
        flashSaleCondition.setFlashSaleProduct(flashSaleProduct);
        flashSaleCondition.setFlashSaleProducts(Arrays.asList(flashSaleProduct));
        flashSale1.setFlashSaleCondition(flashSaleCondition);
        flashSale1.setType("wow_extra");
        flashSale1.setConditionData(JSONUtil.toString(flashSale1.getFlashSaleCondition()));

        when(flashSaleRepo.saveAndFlush(any(FlashSale.class))).thenReturn(flashSale1);

        FlashSale flashSaleCreated = flashSaleService.createFlashSale(flashSale1);

        assertThat(flashSaleCreated, notNullValue());

        verify(appIdRepo).findOne(anyLong());
        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));

    }

    @Test
    public void shouldReturnFlashSaleUpdatedWhenUpdateExistingFlashSaleWowBannerByIdInDb() throws Exception {
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setVariantId("V1");
        flashSaleVariant.setPromotionPrice(999D);
        flashSaleVariant.setDiscountPercent(50D);
        FlashSaleVariant flashSaleVariant2 = new FlashSaleVariant();
        flashSaleVariant2.setVariantId("V2");
        flashSaleVariant2.setPromotionPrice(999D);
        flashSaleVariant2.setDiscountPercent(50D);
        flashSale.setId(1L);
        flashSale.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("pdid");
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant, flashSaleVariant2));
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));
        flashSale.getFlashSaleCondition().setFlashSaleProduct(flashSaleProduct);
        flashSale.setConditionData(JSONUtil.toString(flashSale.getFlashSaleCondition()));
        flashSale1.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());

        when(flashSaleRepo.findOne(anyLong())).thenReturn(flashSale1);
        when(flashSaleRepo.saveAndFlush(any(FlashSale.class))).thenReturn(flashSale);
        when(flashSaleProductRepo.findByFlashSale(any(FlashSale.class))).thenReturn(Arrays.asList(flashSaleProduct));

        flashSale.getFlashSaleCondition().setFlashSaleProduct(flashSaleProduct);

        FlashSale fs = new FlashSale();
        fs.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        fs.setId(1L);
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant2));
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));
        fs.setFlashSaleCondition(flashSale.getFlashSaleCondition());
        fs.getFlashSaleCondition().setFlashSaleProduct(flashSaleProduct);

        FlashSale flashSaleUpdated = flashSaleService.updateFlashSale(1L, fs);

        assertThat(flashSaleUpdated.getId(), is(1L));
        //assertThat(flashSaleUpdated.getFlashSaleCondition().getFlashSaleVariant().getVariantId(), is("V1"));

        verify(flashSaleRepo).findOne(anyLong());
        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));
        verify(flashSaleProductRepo).findByFlashSale(any(FlashSale.class));
    }

    @Test
    public void shouldReturnFlashSaleUpdatedWhenUpdateExistingFlashSaleWowExtraByIdInDb() throws Exception {
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setVariantId("V1");
        flashSaleVariant.setPromotionPrice(90D);
        flashSaleVariant.setDiscountPercent(50D);
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("flashSaleProductUpdate");
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));

        FlashSaleProduct flashSaleProductResult = new FlashSaleProduct();
        flashSaleProductResult.setProductKey("flashSaleProductResult");
        flashSaleProductResult.setCategoryIds(Arrays.asList("category"));
        flashSaleProductResult.setFlashsaleVariants(Arrays.asList(flashSaleVariant));

        flashSale.setId(1L);
        flashSale.setType(FlashSaleTypeEnum.WOW_EXTRA.getContent());
        flashSale.getFlashSaleCondition().setFlashSaleProducts(Arrays.asList(flashSaleProduct));
        flashSale.setConditionData(JSONUtil.toString(flashSale.getFlashSaleCondition()));
        flashSale1.setType(FlashSaleTypeEnum.WOW_EXTRA.getContent());

        when(flashSaleRepo.findOne(anyLong())).thenReturn(flashSale1);
        when(flashSaleRepo.saveAndFlush(any(FlashSale.class))).thenReturn(flashSale);
        when(flashSaleProductRepo.findByFlashSale(any(FlashSale.class)))
                .thenReturn(Arrays.asList(flashSaleProductResult));

        FlashSale flashSaleUpdated = flashSaleService.updateFlashSale(1L, flashSale);

        assertThat(flashSaleUpdated.getId(), is(1L));
        // assertThat(flashSaleUpdated.getFlashSaleCondition().getFlashSaleVariant().getVariantId(), is("V1"));

        verify(flashSaleRepo).findOne(anyLong());
        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));
        verify(flashSaleProductRepo).findByFlashSale(any(FlashSale.class));
    }

    @Test
    public void shouldReturnWowExtraFlashSaleUpdatedWhenUpdateExistingFlashSaleByIdInDb() throws Exception {
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setVariantId("V1");
        flashSaleVariant.setDiscountPercent(90D);
        flashSaleVariant.setPromotionPrice(99D);
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("pdid");
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));
        flashSale.setId(1L);
        flashSale.setType(FlashSaleTypeEnum.WOW_EXTRA.getContent());
        flashSale1.setType(FlashSaleTypeEnum.WOW_EXTRA.getContent());
        flashSale.getFlashSaleCondition().setFlashSaleProducts(Arrays.asList(flashSaleProduct));
        flashSale.setConditionData(JSONUtil.toString(flashSale.getFlashSaleCondition()));

        when(flashSaleRepo.findOne(anyLong())).thenReturn(flashSale1);
        when(flashSaleRepo.saveAndFlush(any(FlashSale.class))).thenReturn(flashSale);
        when(flashSaleProductRepo.findByFlashSale(any(FlashSale.class))).thenReturn(Arrays.asList(flashSaleProduct));

        FlashSale flashSaleUpdated = flashSaleService.updateFlashSale(1L, flashSale);

        assertThat(flashSaleUpdated.getId(), is(1L));
        //assertThat(flashSaleUpdated.getFlashSaleCondition().getFlashSaleVariants().get(0).getVariantId(), is("V1"));

        verify(flashSaleRepo).findOne(anyLong());
        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));
        verify(flashSaleProductRepo).findByFlashSale(any(FlashSale.class));
    }

    @Test
    public void shouldDeleteFlashSaleWhenDeleteExistingFlashSaleByIdInDb() {
        when(flashSaleRepo.findOne(anyLong())).thenReturn(flashSale1);
        doNothing().when(flashSaleRepo).delete(anyLong());

        assertThat(flashSaleService.deleteFlashSale(1L).getId(), is(1L));

        verify(flashSaleRepo).findOne(anyLong());
    }

    @Test(expected = FlashSaleNotFoundException.class)
    public void shouldNotDeleteFlashSaleWhenDeleteNonExistingFlashSaleByIdInDb() throws Exception {
        when(flashSaleRepo.findOne(anyLong())).thenReturn(null);
        doNothing().when(flashSaleRepo).delete(anyLong());

        flashSaleService.deleteFlashSale(1L);

        verify(flashSaleRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnFlashSaleWhenGetExistingFlashSaleById() throws Exception {
        final FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setPromotionPrice(90D);
        flashSaleVariant.setDiscountPercent(50D);
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));
        FlashSaleCategory flashSaleCategory = new FlashSaleCategory();
        flashSaleCategory.setCategoryId("category");
        flashSaleProduct.setFlashsaleCategories(Arrays.asList(flashSaleCategory));
        flashSaleProduct.setFlashsaleCategories(Arrays.asList());
        flashSaleCondition.setFlashSaleProduct(flashSaleProduct);
        flashSaleCondition.setFlashSaleProducts(Arrays.asList(flashSaleProduct));
        flashSale1.setFlashSaleCondition(flashSaleCondition);
        flashSale1.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        flashSale1.setConditionData(JSONUtil.toString(flashSale1.getFlashSaleCondition()));
        flashSale1.setBannerImagesData(JSONUtil.toString(new BannerImages()));
        flashSale1.setFlashSaleProductList(Arrays.asList(flashSaleProduct));
        when(flashSaleRepo.findOne(anyLong())).thenReturn(flashSale1);

        assertThat(flashSaleService.getFlashSaleById(1L).getId(), is(1L));

        verify(flashSaleRepo).findOne(anyLong());
    }

    @Test(expected = FlashSaleNotFoundException.class)
    public void shouldNullWhenGetNonExistingFlashSaleById() throws Exception {
        when(flashSaleRepo.findOne(anyLong())).thenReturn(null);

        flashSaleService.getFlashSaleById(1L);

        verify(flashSaleRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnWowBannerPromotionWhenGetExistingSingleWowBannerInDb() throws Exception {
        final FlashSaleCategory flashSaleCategory = new FlashSaleCategory();
        flashSaleCategory.setCategoryId("category");
        final BannerImages bannerImages = new BannerImages();
        final DateTime dateTime = DateTime.now();
        FlashSale flashSale1 = new FlashSale();
        flashSale1.setName("currentWow");
        flashSale1.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setVariantId("variantId1");
        flashSaleVariant.setPromotionPrice(100D);
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("PDID1");
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        flashSaleProduct.setFlashsaleCategories(Arrays.asList(flashSaleCategory));
        FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        flashSaleCondition.setFlashSaleProduct(flashSaleProduct);
        flashSale1.setFlashSaleProductList(Arrays.asList(flashSaleProduct));
        flashSale1.setConditionData(JSONUtil.toString(flashSaleCondition));
        flashSale1.setBannerImagesData(JSONUtil.toString(flashSale1.getBannerImages()));
        flashSale1.setStartPeriod(dateTime.toDate());
        flashSale1.setEndPeriod(dateTime.plusHours(1).toDate());
        flashSale1.setBannerImages(bannerImages);
        flashSale1.setBannerImagesData(JSONUtil.toString(bannerImages));

        when(flashSaleRepo.findAll(any(Specification.class)))
                .thenReturn(Arrays.asList(flashSale1));

        WowBannerProductResponse everyDayWow = flashSaleService.getWowBanner(dateTime.plusMillis(5).getMillis());
        assertThat(everyDayWow.getCurrentWow().getName(), is("currentWow"));

        verify(flashSaleRepo).findAll(any(Specification.class));

    }


    @Test
    public void shouldReturnWowPromotionWhenGetExistingMultiFlashSaleCorrectly() {
        DateTime current = DateTime.now();
        when(flashSaleRepo.findAll(any(Specification.class))).thenReturn(initFlashSaleList(current));
        WowBannerProductResponse wowBannerProductResponse =
                flashSaleService.getWowBanner(current.plusMinutes(1).getMillis());
        assertThat(wowBannerProductResponse.getCurrentWow().getName(), is("currentWow"));
        assertThat(wowBannerProductResponse.getNextWow().getName(), is("nextWow"));
        assertThat(wowBannerProductResponse.getIncomingWow().getName(), is("incomingWow"));
        verify(flashSaleRepo).findAll(any(Specification.class));
    }

    @Test
    public void shouldReturnWowNearStartPeriodWhenGetExistingFlashSaleButNotHaveFlashSaleLiveCurrentTimeCorrectly() {
        when(flashSaleRepo.findAll(any(Specification.class)))
                .thenReturn(initFlashSaleList(DateTime.now().plusMinutes(30)));
        WowBannerProductResponse wowBannerProductResponse = flashSaleService.getWowBanner(currentDate);
        assertThat(wowBannerProductResponse.getCurrentWow().getName(), is("currentWow"));
        assertThat(wowBannerProductResponse.getNextWow().getName(), is("nextWow"));
        assertThat(wowBannerProductResponse.getIncomingWow().getName(), is("incomingWow"));
        verify(flashSaleRepo).findAll(any(Specification.class));
    }

    @Test
    public void shouldReturnEveryDayWowWhenGetExistingFlashSaleButNotHaveFlashSaleLiveCurrentTimeCorrectly() {

        when(flashSaleRepo.findAll(any(Specification.class)))
                .thenReturn(initFlashSaleList(DateTime.now().minusHours(5)));
        WowBannerProductResponse wowBannerProductResponse = flashSaleService.getWowBanner(currentDate);
        assertNull(wowBannerProductResponse.getCurrentWow());
        assertNull(wowBannerProductResponse.getNextWow());
        assertNull(wowBannerProductResponse.getIncomingWow());
        verify(flashSaleRepo).findAll(any(Specification.class));
    }

    @Test
    public void shouldReturnAppIdsListWhenGetAllExistingAppIdInDbCorrectly() {

        when(appIdRepo.findAll()).thenReturn(Arrays.asList(appId));
        List<AppId> appIds = flashSaleService.getAppIds();

        assertThat(appIds.get(0).getName(), is("itm"));

        verify(appIdRepo).findAll();

    }

    @Test
    public void shouldReturnEmptyAppIdsListWhenGetAllNotExistingAppIdInDbCorrectly() {
        when(appIdRepo.findAll()).thenReturn(new ArrayList<>());
        List<AppId> appIds = flashSaleService.getAppIds();

        assertThat(appIds, is(new ArrayList<>()));

        verify(appIdRepo).findAll();
    }

    @Test
    public void shouldCreateNewAppIdSuccessfullyWhenCreateNonExistingAppIdInDb() {
        when(appIdRepo.saveAndFlush(any(AppId.class))).thenReturn(appId);
        AppId appIdResult = flashSaleService.createAppId(appId);

        assertThat(appIdResult.getName(), is("itm"));

        verify(appIdRepo).saveAndFlush(any(AppId.class));
    }


    @Test
    public void shouldEnableFlashSaleWhenEnabledExistingCFlashSaleById() {
        flashSale.setId(1L);
        when(flashSaleRepo.findOne(anyLong())).thenReturn(flashSale);

        flashSale.setEnable(true);
        when(flashSaleRepo.saveAndFlush(any(FlashSale.class))).thenReturn(flashSale);

        FlashSale enabledFlashSale = flashSaleService.enableFlashSale(1L);
        assertThat(enabledFlashSale.getId(), is(1L));
        assertThat(enabledFlashSale.getEnable(), is(true));

        verify(flashSaleRepo).findOne(anyLong());
        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));
    }


    @Test
    public void shouldDisableFlashSaleWhenDisabledExistingFlashSaleById() {
        flashSale.setId(1L);
        when(flashSaleRepo.findOne(anyLong())).thenReturn(flashSale);

        flashSale.setEnable(false);
        when(flashSaleRepo.saveAndFlush(any(FlashSale.class))).thenReturn(flashSale);

        FlashSale disabledFlashSale = flashSaleService.disabledFlashSale(1L);
        assertThat(disabledFlashSale.getId(), is(1L));
        assertThat(disabledFlashSale.getEnable(), is(false));

        verify(flashSaleRepo).findOne(anyLong());
        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));
    }


    @Test
    public void shouldReturnTrueWhenCheckPeriodWithNotCoverExistingWowBanner() {

        FlashSale flashSaleReturn1 = new FlashSale();
        flashSaleReturn1.setStartPeriod(DateTime.now().plusHours(6).toDate());
        flashSaleReturn1.setEndPeriod(DateTime.now().plusHours(7).toDate());

        flashSale1.setStartPeriod(DateTime.now().toDate());
        flashSale1.setEndPeriod(DateTime.now().plusHours(3).toDate());

        when(flashSaleRepo.findByType(anyString())).thenReturn(Arrays.asList(flashSaleReturn1));

        assertTrue(flashSaleService.checkPeriod(flashSale1.getStartPeriod(), flashSale1.getEndPeriod()));


        verify(flashSaleRepo).findByType(anyString());
    }

    @Test(expected = WowBannerException.class)
    public void shouldThrowWowBannerExceptionWhenCreateWowBannerStartPeriodCoverExistingWowBanner() {

        FlashSale flashSaleReturn1 = new FlashSale();
        flashSaleReturn1.setStartPeriod(DateTime.now().toDate());
        flashSaleReturn1.setEndPeriod(DateTime.now().plusHours(2).toDate());

        flashSale1.setStartPeriod(DateTime.now().plusHours(1).toDate());
        flashSale1.setEndPeriod(DateTime.now().plusHours(3).toDate());

        when(flashSaleRepo.findByType(anyString())).thenReturn(Arrays.asList(flashSaleReturn1));

        flashSale1.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        flashSaleService.createFlashSale(flashSale1);


        verify(flashSaleRepo).saveAndFlush(any(FlashSale.class));
    }

    @Test(expected = WowBannerException.class)
    public void shouldThrowWowBannerExceptionWhenCheckEndPeriodCoverExistingWowBanner() {

        FlashSale flashSaleReturn1 = new FlashSale();
        flashSaleReturn1.setStartPeriod(DateTime.now().plusHours(2).toDate());
        flashSaleReturn1.setEndPeriod(DateTime.now().plusHours(4).toDate());

        flashSale1.setStartPeriod(DateTime.now().toDate());
        flashSale1.setEndPeriod(DateTime.now().plusHours(3).toDate());

        when(flashSaleRepo.findByType(anyString())).thenReturn(Arrays.asList(flashSaleReturn1));

        flashSaleService.checkPeriod(flashSale1.getStartPeriod(), flashSale1.getEndPeriod());


        verify(flashSaleRepo).findByType(anyString());
    }

    @Test(expected = WowBannerException.class)
    public void shouldThrowWowBannerExceptionWhenStartPeriodAndEndPeriodEqualExistingWowBanner() {
        Date start = DateTime.now().plusHours(1).toDate();
        Date end = DateTime.now().plusHours(2).toDate();
        FlashSale flashSaleReturn1 = new FlashSale();
        flashSaleReturn1.setStartPeriod(start);
        flashSaleReturn1.setEndPeriod(end);

        flashSale1.setStartPeriod(start);
        flashSale1.setEndPeriod(end);

        when(flashSaleRepo.findByType(anyString())).thenReturn(Arrays.asList(flashSaleReturn1));

        flashSaleService.checkPeriod(flashSale1.getStartPeriod(), flashSale1.getEndPeriod());


        verify(flashSaleRepo).findByType(anyString());
    }

    @Test(expected = WowBannerException.class)
    public void shouldReturnAlertMessageWhenCheckStartPeriodCoverExistingWowBanner() {

        FlashSale flashSaleReturn1 = new FlashSale();
        flashSaleReturn1.setStartPeriod(DateTime.now().plusHours(2).toDate());
        flashSaleReturn1.setEndPeriod(DateTime.now().plusHours(4).toDate());

        flashSale1.setStartPeriod(DateTime.now().plusHours(3).toDate());
        flashSale1.setEndPeriod(DateTime.now().plusHours(5).toDate());

        when(flashSaleRepo.findByType(anyString())).thenReturn(Arrays.asList(flashSaleReturn1));

        flashSaleService.checkPeriod(flashSale1.getStartPeriod(), flashSale1.getEndPeriod());

        verify(flashSaleRepo).findByType(anyString());
    }


    public List<FlashSale> initFlashSaleList(DateTime dateTime) {
        final FlashSaleCategory flashSaleCategory = new FlashSaleCategory();
        flashSaleCategory.setCategoryId("category");
        final BannerImages bannerImages = new BannerImages();
        FlashSale flashSale1 = new FlashSale();
        flashSale1.setName("currentWow");
        flashSale1.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setVariantId("variantId1");
        flashSaleVariant.setPromotionPrice(100D);
        flashSaleVariant.setDiscountPercent(90D);
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("PDID1");
        flashSaleProduct.setFlashsaleCategories(Arrays.asList(flashSaleCategory));

        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        flashSaleCondition.setFlashSaleProduct(flashSaleProduct);
        flashSale1.setConditionData(JSONUtil.toString(flashSaleCondition));
        flashSale1.setBannerImagesData(JSONUtil.toString(flashSale1.getBannerImages()));
        flashSale1.setStartPeriod(dateTime.toDate());
        flashSale1.setEndPeriod(dateTime.plusHours(1).toDate());
        flashSale1.setFlashSaleProductList(Arrays.asList(flashSaleProduct));
        flashSale1.setBannerImages(bannerImages);
        flashSale1.setBannerImagesData(JSONUtil.toString(bannerImages));
        FlashSale flashSale2 = new FlashSale();
        flashSale2.setName("nextWow");
        flashSale2.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        FlashSaleVariant fsFlashSale2 = new FlashSaleVariant();
        fsFlashSale2.setVariantId("variantId2");
        fsFlashSale2.setPromotionPrice(100D);
        FlashSaleProduct flashSaleProduct2 = new FlashSaleProduct();
        flashSaleProduct2.setProductKey("PDID2");
        flashSaleProduct2.setFlashsaleCategories(Arrays.asList(flashSaleCategory));
        flashSaleProduct2.setFlashsaleVariants(Arrays.asList(fsFlashSale2));
        FlashSaleCondition fsConditionFs2 = new FlashSaleCondition();
        fsConditionFs2.setFlashSaleProduct(flashSaleProduct2);
        flashSale2.setFlashSaleProductList(Arrays.asList(flashSaleProduct2));
        flashSale2.setConditionData(JSONUtil.toString(fsConditionFs2));
        flashSale2.setBannerImagesData(JSONUtil.toString(flashSale2.getBannerImages()));
        flashSale2.setStartPeriod(dateTime.plusHours(1).plusMinutes(1).toDate());
        flashSale2.setEndPeriod(dateTime.plusHours(2).toDate());
        flashSale2.setBannerImages(bannerImages);
        flashSale2.setBannerImagesData(JSONUtil.toString(bannerImages));
        FlashSale flashSale3 = new FlashSale();
        flashSale3.setType(FlashSaleTypeEnum.WOW_BANNER.getContent());
        flashSale3.setName("incomingWow");
        FlashSaleVariant fsCriteriaValueFs2 = new FlashSaleVariant();
        fsCriteriaValueFs2.setVariantId("variantId1");
        fsCriteriaValueFs2.setPromotionPrice(100D);
        FlashSaleProduct flashSaleProduct3 = new FlashSaleProduct();
        flashSaleProduct3.setProductKey("PDID3");
        flashSaleProduct3.setFlashsaleCategories(Arrays.asList(flashSaleCategory));
        flashSaleProduct3.setFlashsaleVariants(Arrays.asList(fsCriteriaValueFs2));
        flashSale3.setFlashSaleProductList(Arrays.asList(flashSaleProduct3));
        FlashSaleCondition fsConditionFs3 = new FlashSaleCondition();
        fsConditionFs3.setFlashSaleProduct(flashSaleProduct3);
        flashSale3.setConditionData(JSONUtil.toString(fsConditionFs3));
        flashSale3.setBannerImagesData(JSONUtil.toString(flashSale3.getBannerImages()));
        flashSale3.setStartPeriod(dateTime.plusHours(2).plusMinutes(1).toDate());
        flashSale3.setEndPeriod(dateTime.plusHours(3).toDate());
        flashSale3.setBannerImages(bannerImages);
        flashSale3.setBannerImagesData(JSONUtil.toString(bannerImages));

        return Arrays.asList(flashSale1, flashSale2, flashSale3);
    }

    @Test
    public void shouldReturnWowExtraProductPaginationWhenGetWowExtraCorrectly() {

        List<FlashSaleProduct> initFlashSaleProductResult = initFlashSaleProductWowExtra();
        when(flashSaleProductRepo.findAll(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl(initFlashSaleProductResult));

        Page flashSaleResult = flashSaleService.getWowExtra(1, 30, Sort.Direction.ASC,
                "id", null);

        assertThat(flashSaleResult.getTotalElements(), Matchers.not(0));

        verify(flashSaleProductRepo).findAll(any(), any(PageRequest.class));

    }

    @Test
    public void shouldReturnWowExtraProductLowerPriceWhenFindSameWowExtraProductMoreThanOne() {

        List<FlashSaleProduct> initFlashSaleProductResult = initFlashSaleProductWowExtra();
        when(flashSaleProductRepo.findAll(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl(initFlashSaleProductResult));

        Page flashSaleResult = flashSaleService.getWowExtra(1, 30, Sort.Direction.ASC, "id", null);

        List<WowExtraProduct> wowExtraProducts = flashSaleResult.getContent();
        Optional<Double> minPromotionPrice = wowExtraProducts.stream()
                .filter(w -> w.getProductKey().equalsIgnoreCase("flashSaleProductDup"))
                .map(WowExtraProduct::getMinPromotionPrice).findFirst();

        assertThat(minPromotionPrice.get(), is(500D));
        assertThat(flashSaleResult.getTotalElements(), is(2L));

        verify(flashSaleProductRepo).findAll(any(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnWowExtraProductWhenGetExistingWowExtraProductByIdAndProductMustBeUnderLiveFlashSale() {
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("productkey12345");
        flashSaleProduct.setFlashsaleCategories(new ArrayList<>());
        FlashSale flashSale = new FlashSale();
        flashSale.setEnable(true);
        flashSale.setStartPeriod(DateTime.now().minusHours(1).toDate());
        flashSale.setEndPeriod(DateTime.now().plusHours(1).toDate());
        flashSaleProduct.setFlashSale(flashSale);
        FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        flashSaleCondition.setPaymentType(Arrays.asList("payment"));
        flashSaleCondition.setLimitItem(5);
        flashSaleCondition.setLimitItemImg("img_url");
        flashSale.setConditionData(JSONUtil.toString(flashSaleCondition));
        when(flashSaleProductRepo.findByProductKey(anyString())).thenReturn(Arrays.asList(flashSaleProduct));
        WowExtraProduct wowExtraProduct = flashSaleService.getFlashSaleProductByProductKey("productkey12345");
        assertThat(wowExtraProduct.getProductKey(), is("productkey12345"));
        verify(flashSaleProductRepo).findByProductKey(anyString());
    }


    @Test
    public void shouldReturnWowExtraProductCheapestPromotionPriceWhenGetExistingWowExtraProductById() {
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("productkey12345");
        flashSaleProduct.setFlashsaleCategories(new ArrayList<>());
        flashSaleProduct.setMinPromotionPrice(500D);
        FlashSale flashSale = new FlashSale();
        flashSale.setEnable(true);
        flashSale.setStartPeriod(DateTime.now().minusHours(1).toDate());
        flashSale.setEndPeriod(DateTime.now().plusHours(1).toDate());
        flashSaleProduct.setFlashSale(flashSale);
        FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        flashSaleCondition.setPaymentType(Arrays.asList("payment"));
        flashSaleCondition.setLimitItem(5);
        flashSaleCondition.setLimitItemImg("img_url");
        flashSale.setConditionData(JSONUtil.toString(flashSaleCondition));


        FlashSaleProduct flashSaleProduct2 = new FlashSaleProduct();
        flashSaleProduct2.setProductKey("productkey12345");
        flashSaleProduct2.setFlashsaleCategories(new ArrayList<>());
        flashSaleProduct2.setMinPromotionPrice(100D);
        flashSaleProduct2.setFlashSale(flashSale);
        when(flashSaleProductRepo.findByProductKey(anyString())).thenReturn(
                Arrays.asList(flashSaleProduct, flashSaleProduct2));
        WowExtraProduct wowExtraProduct = flashSaleService.getFlashSaleProductByProductKey("productkey12345");
        assertThat(wowExtraProduct.getProductKey(), is("productkey12345"));
        assertThat(wowExtraProduct.getMinPromotionPrice(), is(100D));
        verify(flashSaleProductRepo).findByProductKey(anyString());
    }

    @Test(expected = WowExtraProductNotFoundException.class)
    public void shouldReturnWowExtraProductNotFoundExceptopnWhenGetNotExistingProductWowExtra() {
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("productkey12345");
        flashSaleProduct.setFlashsaleCategories(new ArrayList<>());
        FlashSale flashSale = new FlashSale();
        flashSale.setEnable(false);
        flashSale.setStartPeriod(DateTime.now().minusHours(1).toDate());
        flashSale.setEndPeriod(DateTime.now().plusHours(1).toDate());
        flashSaleProduct.setFlashSale(flashSale);
        FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        flashSaleCondition.setPaymentType(Arrays.asList("payment"));
        flashSaleCondition.setLimitItem(5);
        flashSaleCondition.setLimitItemImg("img_url");
        flashSale.setConditionData(JSONUtil.toString(flashSaleCondition));
        when(flashSaleProductRepo.findByProductKey(anyString())).thenReturn(Arrays.asList(flashSaleProduct));
        flashSaleService.getFlashSaleProductByProductKey("productkey12345");
        verify(flashSaleProductRepo).findByProductKey(anyString());
    }

    @Test
    public void shouldReturnPolicyImageWhenGetExistingPolicyImageInConfigurationFile() {
        HashMap<Long, String> policyHashMap = new HashMap<>();
        policyHashMap.put(1L, "img_url_1");
        when(configurationService.getPolicies()).thenReturn(policyHashMap);

        Policy policy = flashSaleService.getFlashSalePolicyImage(1L);
        assertThat(policy.getPolicy(), is(1L));
        verify(configurationService).getPolicies();
    }

    @Test(expected = PolicyNotFoundException.class)
    public void shouldReturnPolicyNotFounfExceptionGetNotExistingPolicyImageInConfigurationFile() {
        HashMap<Long, String> policyHashMap = new HashMap<>();
        policyHashMap.put(1L, "img_url_1");
        when(configurationService.getPolicies()).thenReturn(policyHashMap);

        flashSaleService.getFlashSalePolicyImage(2L);

        verify(configurationService).getPolicies();
    }

    @Test
    public void shouldReturnFlashSaleProductAvailableWhenUpdateStatusExistingFlashSaleProductInDb() {
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setIsAvailable(false);

        FlashSaleProductAvailable flashSaleProductAvailable = new FlashSaleProductAvailable();
        flashSaleProductAvailable.setProductKey("A1");
        flashSaleProductAvailable.setIsAvailable(true);

        FlashSaleProductAvailable flashSaleProductAvailable2 = new FlashSaleProductAvailable();
        flashSaleProductAvailable2.setProductKey("A2");
        flashSaleProductAvailable2.setIsAvailable(false);

        List<FlashSaleProductAvailable> productAvailableList = Arrays.asList(flashSaleProductAvailable,
                flashSaleProductAvailable2);
        when(flashSaleProductRepo.findByProductKey(anyString())).thenReturn(Arrays.asList(flashSaleProduct));
        List<FlashSaleProductAvailable> productAvailableResponse = flashSaleService.updateFlashSaleProductStatus(
                productAvailableList);
        assertThat(productAvailableResponse.get(0).getIsAvailable(), is(true));
        assertThat(productAvailableResponse.get(1).getIsAvailable(), is(false));
        verify(flashSaleProductRepo, times(2)).findByProductKey(anyString());
    }

    @Test
    public void shouldReturnFlashSaleVariantAvailableWhenUpdateStatusExistingFlashSaleVariantInDb() {
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setIsAvailable(false);

        FlashSaleVariantAvailable flashSaleVariantAvailable = new FlashSaleVariantAvailable();
        flashSaleVariantAvailable.setVariantId("A1");
        flashSaleVariantAvailable.setIsAvailable(true);

        FlashSaleVariantAvailable flashSaleVariantAvailable2 = new FlashSaleVariantAvailable();
        flashSaleVariantAvailable2.setVariantId("A2");
        flashSaleVariantAvailable2.setIsAvailable(false);

        List<FlashSaleVariantAvailable> variantAvailableList = Arrays.asList(flashSaleVariantAvailable,
                flashSaleVariantAvailable2);
        when(flashSaleVariantRepo.findByVariantId(anyString())).thenReturn(Arrays.asList(flashSaleVariant));
        List<FlashSaleVariantAvailable> productAvailableResponse = flashSaleService.updateFlashSaleVariantStatus(
                variantAvailableList);
        assertThat(productAvailableResponse.get(0).getIsAvailable(), is(true));
        assertThat(productAvailableResponse.get(1).getIsAvailable(), is(false));
        verify(flashSaleVariantRepo, times(2)).findByVariantId(anyString());
    }

    @Test
    public void shouldReturnCheckDuplicateProductFlashSaleWhenGetCheckDuplicateFreebieByVariantsAndPromotionPeriod()
            throws Exception {
        final String products = "product1,product2";
        final Long startPeriod = DateTime.now().minusMinutes(5).getMillis();
        final Long endPeriod = DateTime.now().plusMinutes(5).getMillis();

        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("product1");

        FlashSale flashSale = new FlashSale();
        flashSale.setId(2L);
        flashSale.setFlashSaleProductList(Arrays.asList(flashSaleProduct));

        when(flashSaleRepo.findFlashSaleByDateTime(anyString(), anyString()))
                .thenReturn(Arrays.asList(flashSale));

        List<ProductDuplicateFlashSale> productDuplicateFlashSale = flashSaleService.checkProductDuplicate(
                products, startPeriod, endPeriod, null);

        assertTrue(productDuplicateFlashSale.get(0).getDuplicateFlashsaleId().contains(2L));
        assertThat(productDuplicateFlashSale.get(0).getProductKey(), is("product1"));

        verify(flashSaleRepo).findFlashSaleByDateTime(anyString(), anyString());
    }

    @Test
    public void shouldReturnEmptyListWhenGetCheckDuplicateFreebieByVariantsAndPromotionPeriodButNotFoundDuplicate()
            throws Exception {
        final String products = "product1,product2";
        final Long startPeriod = DateTime.now().minusMinutes(5).getMillis();
        final Long endPeriod = DateTime.now().plusMinutes(5).getMillis();

        when(flashSaleRepo.findFlashSaleByDateTime(anyString(), anyString()))
                .thenReturn(Arrays.asList());

        List<ProductDuplicateFlashSale> productDuplicateFlashSale = flashSaleService.checkProductDuplicate(
                products, startPeriod, endPeriod, null);

        assertThat(productDuplicateFlashSale, is(Arrays.asList()));

        verify(flashSaleRepo).findFlashSaleByDateTime(anyString(), anyString());
    }

    private List<FlashSaleProduct> initFlashSaleProductWowExtra() {
        FlashSale flashSaleWowExtra = new FlashSale();
        flashSaleWowExtra.setType(FlashSaleTypeEnum.WOW_EXTRA.getContent());
        flashSaleWowExtra.setStartPeriod(DateTime.now().minusHours(1).toDate());
        flashSaleWowExtra.setEndPeriod(DateTime.now().plusHours(1).toDate());
        flashSaleWowExtra.setLive(true);
        flashSaleWowExtra.setEnable(true);

        FlashSale flashSaleWowExtra2 = new FlashSale();
        flashSaleWowExtra2.setType(FlashSaleTypeEnum.WOW_EXTRA.getContent());
        flashSaleWowExtra2.setStartPeriod(DateTime.now().minusHours(5).toDate());
        flashSaleWowExtra2.setEndPeriod(DateTime.now().plusHours(1).toDate());
        flashSaleWowExtra2.setLive(true);
        flashSaleWowExtra2.setEnable(true);
        FlashSaleCategory flashSaleCategory1 = new FlashSaleCategory();
        flashSaleCategory1.setCategoryId("category1");
        FlashSaleCategory flashSaleCategory2 = new FlashSaleCategory();
        flashSaleCategory2.setCategoryId("category2");
        FlashSaleVariant flashSaleVariant1 = new FlashSaleVariant();
        flashSaleVariant1.setDiscountPercent(10D);
        flashSaleVariant1.setLimitQuantity(100L);
        flashSaleVariant1.setPromotionPrice(999D);
        flashSaleVariant1.setVariantId("variantsid1");
        FlashSaleVariant flashSaleVariant2 = new FlashSaleVariant();
        flashSaleVariant2.setDiscountPercent(50D);
        flashSaleVariant2.setLimitQuantity(5L);
        flashSaleVariant2.setPromotionPrice(500D);
        flashSaleVariant2.setVariantId("variantsid2");
        FlashSaleVariant flashSaleVariant3 = new FlashSaleVariant();
        flashSaleVariant3.setDiscountPercent(99D);
        flashSaleVariant3.setLimitQuantity(600L);
        flashSaleVariant3.setPromotionPrice(1000D);
        flashSaleVariant3.setVariantId("variantsid3");

        FlashSaleProduct flashSaleProduct1 = new FlashSaleProduct();
        flashSaleProduct1.setProductKey("flashSaleProductDup");
        flashSaleProduct1.setFlashsaleCategories(Arrays.asList(flashSaleCategory1, flashSaleCategory2));
        flashSaleProduct1.setMinDiscountPercent(10D);
        flashSaleProduct1.setMaxDiscountPercent(50D);
        flashSaleProduct1.setMinPromotionPrice(500D);
        flashSaleProduct1.setMaxPromotionPrice(999D);
        flashSaleProduct1.setFlashSale(flashSaleWowExtra2);
        flashSaleProduct1.setFlashsaleVariants(Arrays.asList(flashSaleVariant1, flashSaleVariant2));

        FlashSaleProduct flashSaleProduct2 = new FlashSaleProduct();
        flashSaleProduct2.setFlashsaleCategories(Arrays.asList(flashSaleCategory1));
        flashSaleProduct2.setProductKey("flashSaleProductDup");
        flashSaleProduct2.setMinDiscountPercent(10D);
        flashSaleProduct2.setMaxDiscountPercent(99D);
        flashSaleProduct2.setMinPromotionPrice(999D);
        flashSaleProduct2.setMaxPromotionPrice(1000D);
        flashSaleProduct2.setFlashSale(flashSaleWowExtra);
        flashSaleProduct2.setFlashsaleVariants(Arrays.asList(flashSaleVariant1, flashSaleVariant3));

        FlashSaleProduct flashSaleProduct3 = new FlashSaleProduct();
        flashSaleProduct3.setProductKey("flashSaleProduct3");
        flashSaleProduct3.setFlashsaleCategories(Arrays.asList(flashSaleCategory1, flashSaleCategory2));
        flashSaleProduct3.setMinDiscountPercent(10D);
        flashSaleProduct3.setMaxDiscountPercent(99D);
        flashSaleProduct3.setMinPromotionPrice(500D);
        flashSaleProduct3.setMaxPromotionPrice(1000D);
        flashSaleProduct3.setFlashSale(flashSaleWowExtra);
        flashSaleProduct3.setFlashsaleVariants(Arrays.asList(flashSaleVariant1, flashSaleVariant2, flashSaleVariant3));
        return Arrays.asList(flashSaleProduct1, flashSaleProduct2, flashSaleProduct3);
    }
}
