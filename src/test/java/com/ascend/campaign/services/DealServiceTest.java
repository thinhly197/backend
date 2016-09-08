package com.ascend.campaign.services;

import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.entities.PromotionTask;
import com.ascend.campaign.exceptions.DealNotFoundException;
import com.ascend.campaign.exceptions.PDSServiceException;
import com.ascend.campaign.exceptions.PricingServiceException;
import com.ascend.campaign.models.PDSJson;
import com.ascend.campaign.models.PricingJson;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.SuperDeal;
import com.ascend.campaign.models.VariantCalculation;
import com.ascend.campaign.models.VariantDeal;
import com.ascend.campaign.models.VariantDealDetail;
import com.ascend.campaign.models.VariantDealResponse;
import com.ascend.campaign.repositories.DealRepo;
import com.ascend.campaign.repositories.PromotionTaskRepo;
import com.ascend.campaign.utils.DealUtil;
import com.google.common.collect.Lists;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DealServiceTest {
    @Autowired
    DealService dealService;

    @Mock
    private DealRepo dealRepo;

    @Mock
    private ExternalService externalService;

    @Mock
    private DealUtil dealUtil;

    @Mock
    private PromotionTaskRepo promotionTaskRepo;

    private Deal deal1;
    private Deal deal2;
    private SuperDeal superDeal1;
    private SuperDeal superDeal2;
    private PromotionTask promotionTask;

    @Before
    public void setUp() {
        dealService = new DealService(dealRepo, externalService, dealUtil, promotionTaskRepo);
        final DateTime today = new DateTime().withTimeAtStartOfDay();
        final DateTime tomorrow = today.plusDays(1).withTimeAtStartOfDay();
        final Date startDate = today.toDate();
        final Date endDate = tomorrow.toDate();

        deal1 = new Deal();
        deal1.setId(1L);
        deal1.setName("variant1Id");
        deal1.setStartPeriod(startDate);
        deal1.setEndPeriod(endDate);


        deal2 = new Deal();
        deal2.setId(2L);
        deal2.setName("deal2");

        deal2.setStartPeriod(startDate);
        deal2.setEndPeriod(endDate);

        superDeal1 = new SuperDeal();
        superDeal1.setVariantId("va1");
        superDeal2 = new SuperDeal();
        superDeal2.setVariantId("va2");

        promotionTask = new PromotionTask();
        promotionTask.setPromotionId(1L);
        promotionTask.setIsStart(false);
    }

    @Test
    public void shouldCreateNewVariantSuccessfullyWhenCreateNonExistingVariantInDb() {
        when(dealRepo.saveAndFlush(any(Deal.class))).thenReturn(deal1);
        when(promotionTaskRepo.saveAndFlush(any(PromotionTask.class))).thenReturn(promotionTask);

        Deal dealCreated = dealService.createDeal(deal1);

        assertThat(dealCreated, notNullValue());

        verify(dealRepo).saveAndFlush(any(Deal.class));
        verify(promotionTaskRepo, times(2)).saveAndFlush(any(PromotionTask.class));
    }

    @Test
    public void shouldReturnAllVariantWhenGetAllExistingVariantInDb() {
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);
        Page expectedPage = new PageImpl(dealList);

        when(dealRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);

        assertThat(dealService.getAllDeal(
                1, 5, Sort.Direction.ASC, "id", null, null, null, null, null, null, null, null)
                .getContent().size(), is(2));

        verify(dealRepo).findAll(any(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnVariantUpdatedWhenUpdateExistingVariantByIdInDb() {
        when(dealRepo.findOne(anyLong())).thenReturn(deal1);
        when(dealRepo.saveAndFlush(any(Deal.class))).thenReturn(deal1);
        when(promotionTaskRepo.findByPromotionIdAndIsStart(anyLong(), anyBoolean())).thenReturn(promotionTask);
        when(promotionTaskRepo.saveAndFlush(any(PromotionTask.class))).thenReturn(promotionTask);

        assertThat(dealService.updateDeal(1L, deal1).getId(), is(1L));

        verify(dealRepo).findOne(anyLong());
        verify(dealRepo).saveAndFlush(any(Deal.class));
        verify(promotionTaskRepo, times(2)).findByPromotionIdAndIsStart(anyLong(), anyBoolean());
        verify(promotionTaskRepo, times(2)).saveAndFlush(any(PromotionTask.class));
    }

    @Test
    public void shouldReturnVariantUpdatedWhenUpdateExistingVariantByIdInDbWithNoScheduleTask() {
        when(dealRepo.findOne(anyLong())).thenReturn(deal1);
        when(dealRepo.saveAndFlush(any(Deal.class))).thenReturn(deal1);
        when(promotionTaskRepo.findByPromotionIdAndIsStart(anyLong(), anyBoolean())).thenReturn(null);
        when(promotionTaskRepo.saveAndFlush(any(PromotionTask.class))).thenReturn(promotionTask);

        assertThat(dealService.updateDeal(1L, deal1).getId(), is(1L));

        verify(dealRepo).findOne(anyLong());
        verify(dealRepo).saveAndFlush(any(Deal.class));
        verify(promotionTaskRepo, times(2)).findByPromotionIdAndIsStart(anyLong(), anyBoolean());
        verify(promotionTaskRepo, times(2)).saveAndFlush(any(PromotionTask.class));
    }

    @Test
    public void shouldReturnSuperDealWhenGetAllExistingSuperDealInDb() {
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);
        when(dealRepo.findBySuperDeal(true)).thenReturn(dealList);

        assertThat(dealService.getSuperDeals().size(), is(2));

        verify(dealRepo).findBySuperDeal(true);
    }

    @Test
    public void shouldReturnTomorrowDealWhenGetAllExistingTomorrowDealInDb() {
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);
        List<SuperDeal> superDealList = Lists.newArrayList(superDeal1, superDeal2);
        when(dealRepo.findDealsByDateTime(anyString())).thenReturn(dealList);
        when(dealUtil.getAllSuperDealByTomorrowDeal(anyList())).thenReturn(superDealList);

        assertThat(dealService.getTomorrowDeals().size(), is(2));

        verify(dealRepo).findDealsByDateTime(anyString());
        verify(dealUtil).getAllSuperDealByTomorrowDeal(anyList());
    }

    @Test
    public void shouldReturnToDayDealWhenGetAllExistingTodayDealInDb() {
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);
        List<SuperDeal> superDealList = Lists.newArrayList(superDeal1, superDeal2);
        when(dealRepo.findDealsByDateTime(anyString())).thenReturn(dealList);
        when(dealUtil.getAllSuperDealByTodayDeal(anyList())).thenReturn(superDealList);

        assertThat(dealService.getTodayDeals().size(), is(2));

        verify(dealRepo).findDealsByDateTime(anyString());
        verify(dealUtil).getAllSuperDealByTodayDeal(anyList());
    }

    @Test
    public void shouldDeleteDealWhenDeleteExistingDealByIdInDb() {
        when(dealRepo.findOne(anyLong())).thenReturn(deal1);
        doNothing().when(dealRepo).delete(anyLong());
        when(promotionTaskRepo.deleteByPromotionId(anyLong())).thenReturn(anyLong());

        assertThat(dealService.deleteDeal(1L).getId(), is(1L));

        verify(dealRepo).findOne(anyLong());
    }

    @Test(expected = DealNotFoundException.class)
    public void shouldNotDeleteDealWhenDeleteNonExistingDealByIdInDb() {
        when(dealRepo.findOne(anyLong())).thenReturn(null);
        doNothing().when(dealRepo).delete(anyLong());

        dealService.deleteDeal(1L);

        verify(dealRepo).findOne(anyLong());
        verify(promotionTaskRepo, never()).deleteByPromotionId(anyLong());
    }

    @Test
    public void shouldEnableDealWhenEnabledExistingDealById() {
        when(dealRepo.findOne(anyLong())).thenReturn(deal1);

        deal1.setEnable(true);
        when(dealRepo.saveAndFlush(any(Deal.class))).thenReturn(deal1);

        Deal duplicateDeal = dealService.enableDeal(1L);
        assertThat(duplicateDeal.getId(), is(1L));
        assertThat(duplicateDeal.getEnable(), is(true));

        verify(dealRepo).findOne(anyLong());
        verify(dealRepo).saveAndFlush(any(Deal.class));
    }

    @Test
    public void shouldDisableDealWhenDisabledExistingDealById() {
        when(dealRepo.findOne(anyLong())).thenReturn(deal1);

        deal1.setEnable(false);
        when(dealRepo.saveAndFlush(any(Deal.class))).thenReturn(deal1);

        Deal duplicateDeal = dealService.disableDeal(1L);
        assertThat(duplicateDeal.getId(), is(1L));
        assertThat(duplicateDeal.getEnable(), is(false));

        verify(dealRepo).findOne(anyLong());
        verify(dealRepo).saveAndFlush(any(Deal.class));
    }

    @Test
    public void shouldReturnDealWhenGetExistingDealById() {
        when(dealRepo.findOne(anyLong())).thenReturn(deal1);
        when(dealUtil.isLive(anyBoolean(), anyObject(), anyObject())).thenReturn(true);

        assertThat(dealService.getDeal(1L).getId(), is(1L));

        verify(dealRepo).findOne(anyLong());
        verify(dealUtil).isLive(anyBoolean(), anyObject(), anyObject());
    }

    @Test(expected = DealNotFoundException.class)
    public void shouldNullWhenGetNonExistingDealById() {
        when(dealRepo.findOne(anyLong())).thenReturn(null);

        dealService.getDeal(1L);

        verify(dealRepo).findOne(anyLong());
    }

    @Test
    public void shouldDeleteSuperDealWhenDeleteSuperDealByProductListWithHaveNormalPrice() {
        List<Product> productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("Variant1");
        product1.setNormalPrice(1000D);
        productList.add(product1);

        Product product2 = new Product();
        product2.setVariantId("Variant2");
        product2.setNormalPrice(1000D);
        productList.add(product2);

        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setVariant("Variant1");

        PDSJson pdsJson = new PDSJson();
        pdsJson.setData(variantDealDetail);

        List<Deal> dealList = new ArrayList<>();
        Deal deal1 = new Deal();
        deal1.setId(1L);
        deal1.setName("Variant1");

        dealList.add(deal1);

        VariantDeal variantDeal = new VariantDeal();
        variantDeal.setPromotionPrice(100D);

        when(dealRepo.findDealsByDateTime(anyString())).thenReturn(dealList);
        when(externalService.getPDSData(anyString())).thenReturn(Optional.of(pdsJson));
        when(dealUtil.isLive(anyBoolean(), any(), any())).thenReturn(true);
        when(dealUtil.setPromotionPrice(anyList(), anyObject(), anyDouble())).thenReturn(variantDeal);

        List<Product> superDeal = new ArrayList<>();
        List<Product> test = dealService.setSuperDealAndDeleteSuperDealFromProductCart(productList, superDeal);
        assertThat(test.size(), is(0));

        variantDeal.setPromotionPrice(null);
        when(dealUtil.setPromotionPrice(anyList(), anyObject(), anyDouble())).thenReturn(variantDeal);
        List<Product> test2 = dealService.setSuperDealAndDeleteSuperDealFromProductCart(productList, superDeal);
        assertThat(test2.size(), is(2));

        verify(dealRepo, times(2)).findDealsByDateTime(anyString());
        verify(externalService, times(4)).getPDSData(anyString());
        verify(dealUtil, times(2)).isLive(anyBoolean(), any(), any());
        verify(dealUtil, times(4)).setPromotionPrice(anyList(), anyObject(), anyDouble());
    }

    @Test
    public void shouldDeleteSuperDealWhenDeleteSuperDealByProductListWithHaveNotNormalPrice() {
        final String variantId = "variant1";
        List<Product> productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId(variantId);
        productList.add(product1);

        Product product2 = new Product();
        product2.setVariantId("Variant2");
        productList.add(product2);

        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setVariant(variantId);

        PDSJson pdsJson = new PDSJson();
        pdsJson.setData(variantDealDetail);

        List<Deal> dealList = new ArrayList<>();
        Deal deal1 = new Deal();
        deal1.setId(1L);
        deal1.setName(variantId);
        dealList.add(deal1);

        when(dealRepo.findDealsByDateTime(anyString())).thenReturn(dealList);
        when(externalService.getPDSData(anyString())).thenReturn(Optional.of(pdsJson));
        when(dealUtil.setPromotionPrice(anyList(), anyObject(), anyDouble())).thenReturn(new VariantDeal());
        List<Product> products = dealService.setSuperDealAndDeleteSuperDealFromProductCart(productList, anyList());
        assertThat(products.size(), is(2));

        verify(dealRepo).findDealsByDateTime(anyString());
        verify(externalService, times(2)).getPDSData(anyString());
        verify(dealUtil, times(2)).setPromotionPrice(anyList(), anyObject(), anyDouble());
    }

    @Test
    public void shouldReturnVariantDealWhenFindPromotionPriceByVariantIdWithHavePrice() {
        String variantId = "variant1";
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);
        when(dealRepo.findDealsByDateTime(anyString())).thenReturn(dealList);

        VariantCalculation variantCalculation = new VariantCalculation();
        variantCalculation.setVariantId(variantId);
        variantCalculation.setNormalPrice(1d);
        variantCalculation.setNormalPriceString("100.00");

        PricingJson pricingJson = new PricingJson();
        pricingJson.setMessage("pricing");
        pricingJson.setData(variantCalculation);
        when(externalService.getPricingData(anyString())).thenReturn(Optional.of(pricingJson));

        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setVariant(variantId);

        PDSJson pdsJson = new PDSJson();
        pdsJson.setData(variantDealDetail);
        when(externalService.getPDSData(anyString())).thenReturn(Optional.of(pdsJson));

        VariantDeal variantDeal = new VariantDeal();
        variantDeal.setPromotionId(1L);
        variantDeal.setVariantID(variantId);
        variantDeal.setPromotionPrice(1d);
        when(dealUtil.setPromotionPrice(anyList(), any(VariantDealDetail.class), anyDouble())).thenReturn(variantDeal);

        VariantDealResponse result = dealService.findPromotionPrice(variantId);

        assertThat(result.getPromotionPrice(), is("1.0"));

        verify(dealRepo).findDealsByDateTime(anyString());
        verify(externalService).getPricingData(anyString());
        verify(externalService).getPDSData(anyString());
        verify(dealUtil).setPromotionPrice(anyList(), any(VariantDealDetail.class), anyDouble());
    }

    @Test
    public void shouldReturnVariantDealWhenFindPromotionPriceByVariantIdWithHaveNotPrice() {
        final String variantId = "variant1";
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);
        when(dealRepo.findDealsByDateTime(anyString())).thenReturn(dealList);

        PricingJson pricingJson = new PricingJson();
        pricingJson.setMessage("pricing");
        VariantCalculation variantCalculation = new VariantCalculation();
        variantCalculation.setNormalPriceString("100.00");
        pricingJson.setData(variantCalculation);
        when(externalService.getPricingData(anyString())).thenReturn(Optional.of(pricingJson));

        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setVariant(variantId);

        PDSJson pdsJson = new PDSJson();
        pdsJson.setData(variantDealDetail);
        when(externalService.getPDSData(anyString())).thenReturn(Optional.of(pdsJson));

        VariantDeal variantDeal = new VariantDeal();
        variantDeal.setPromotionId(1L);
        variantDeal.setVariantID(variantId);
        variantDeal.setPromotionPrice(1d);
        when(dealUtil.setPromotionPrice(anyList(), any(VariantDealDetail.class), anyDouble())).thenReturn(variantDeal);

        VariantDealResponse result = dealService.findPromotionPrice(variantId);

        assertThat(result.getPromotionPrice(), is("1.0"));

        verify(dealRepo).findDealsByDateTime(anyString());
        verify(externalService).getPricingData(anyString());
        verify(externalService).getPDSData(anyString());
        verify(dealUtil).setPromotionPrice(anyList(), any(VariantDealDetail.class), anyDouble());
    }

    @Test(expected = PricingServiceException.class)
    public void shouldReturnVariantDealWhenFindPromotionPriceByVariantIdWithHaveNotPriceFromPricing() {
        final String variantId = "variant1";

        when(externalService.getPricingData(anyString())).thenReturn(Optional.empty());

        dealService.findPromotionPrice(variantId);

        verify(dealRepo, never()).findDealsByDateTime(anyString());
        verify(externalService).getPricingData(anyString());
        verify(externalService, never()).getPDSData(anyString());
        verify(dealUtil, never()).setPromotionPrice(anyList(), any(VariantDealDetail.class), anyDouble());
    }

    @Test(expected = PDSServiceException.class)
    public void shouldReturnVariantDealWhenFindPromotionPriceByVariantIdWithNotPromotionPrice() {
        final String variantId = "variant1";
        PricingJson pricingJson = new PricingJson();
        VariantCalculation variantCalculation = new VariantCalculation();
        variantCalculation.setNormalPrice(1d);

        pricingJson.setData(variantCalculation);

        when(externalService.getPricingData(anyString())).thenReturn(Optional.of(pricingJson));
        when(externalService.getPDSData(anyString())).thenReturn(Optional.empty());

        dealService.findPromotionPrice(variantId);

        verify(dealRepo, never()).findDealsByDateTime(anyString());
        verify(externalService).getPricingData(anyString());
        verify(externalService).getPDSData(anyString());
        verify(dealUtil, never()).setPromotionPrice(anyList(), any(VariantDealDetail.class), anyDouble());
    }
}