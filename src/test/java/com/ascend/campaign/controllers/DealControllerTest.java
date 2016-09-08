package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.exceptions.DealNotFoundException;
import com.ascend.campaign.exceptions.PDSServiceException;
import com.ascend.campaign.exceptions.PricingServiceException;
import com.ascend.campaign.models.SuperDeal;
import com.ascend.campaign.models.VariantDealResponse;
import com.ascend.campaign.services.DealService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DealControllerTest {
    @InjectMocks
    DealController controller;

    MockMvc mvc;

    @Mock
    DealService dealService;

    private Deal deal1;
    private Deal deal2;
    private SuperDeal superDeal1;
    private SuperDeal superDeal2;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        final DateTime today = new DateTime().withTimeAtStartOfDay();
        final DateTime tomorrow = today.plusDays(1).withTimeAtStartOfDay();
        final Date startDate = today.toDate();
        final Date endDate = tomorrow.toDate();

        deal1 = new Deal();
        deal1.setId(1L);
        deal1.setName("variant1Id");
        deal1.setSuperDeal(true);
        deal1.setStartPeriod(startDate);
        deal1.setEndPeriod(endDate);

        deal2 = new Deal();
        deal2.setId(2L);
        deal2.setName("variant2Id");
        deal2.setSuperDeal(true);
        deal2.setStartPeriod(startDate);
        deal2.setEndPeriod(endDate);

        superDeal1 = new SuperDeal();
        superDeal1.setVariantId("va1");
        superDeal2 = new SuperDeal();
        superDeal2.setVariantId("va2");
    }

    @Test
    public void shouldCreatedVariantWhenCreateNewDealSuccessfully() throws Exception {
        when(dealService.createDeal(any(Deal.class))).thenReturn(deal1);

        mvc.perform(post("/api/v1/deals")
                .content("{\"variantId\":\"deal1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(dealService).createDeal(any(Deal.class));

    }

    @Test
    public void shouldReturnVariantListWhenGetAllExistingVariant() throws Exception {
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);
        Page expectedPage = new PageImpl(dealList);

        when(dealService.getAllDeal(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(expectedPage);

        mvc.perform(get("/api/v1/deals"))
                .andExpect(jsonPath("$.data.content[0].name_local", is(deal1.getName())))
                .andExpect(jsonPath("$.data.content[1].name_local", is(deal2.getName())))
                .andExpect(status().isOk());

        verify(dealService).getAllDeal(any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    public void shouldReturnVariantUpdatedWhenUpdateExistingVariantById() throws Exception {
        deal1.setName("UpdateVaId");

        when(dealService.updateDeal(anyLong(), any(Deal.class))).thenReturn(deal1);

        mvc.perform(put("/api/v1/deals/1")
                .content("{\"variant_id\":\"UpdateVaId\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(status().isOk());

        verify(dealService).updateDeal(anyLong(), any(Deal.class));
    }

    @Test
    public void shouldReturnSuperDealWhenGetExistingSuperDealInDB() throws Exception {
        List<Deal> dealList = Lists.newArrayList(deal1, deal2);

        when(dealService.getSuperDeals()).thenReturn(dealList);

        mvc.perform(get("/api/v1/deals/superDeal"))
                .andExpect(jsonPath("$.data.[*].name_local",
                        hasItems(endsWith(deal1.getName()), endsWith(deal2.getName()))))
                .andExpect(status().isOk());

        verify(dealService).getSuperDeals();
    }

    @Test
    public void shouldReturnTomorrowDealWhenGetTomorrowDeal() throws Exception {
        List<SuperDeal> dealList = Lists.newArrayList(superDeal1, superDeal2);

        when(dealService.getTomorrowDeals()).thenReturn(dealList);

        mvc.perform(get("/api/v1/deals/tomorrow"))
                .andExpect(jsonPath("$.data.[*].variant_id",
                        hasItems(endsWith(superDeal1.getVariantId()), endsWith(superDeal2.getVariantId()))))
                .andExpect(status().isOk());

        verify(dealService).getTomorrowDeals();
    }

    @Test
    public void shouldReturnTodayDealWhenGetTodayDeal() throws Exception {
        List<SuperDeal> dealList = Lists.newArrayList(superDeal1, superDeal2);

        when(dealService.getTodayDeals()).thenReturn(dealList);

        mvc.perform(get("/api/v1/deals/today"))
                .andExpect(jsonPath("$.data.[*].variant_id",
                        hasItems(endsWith(superDeal1.getVariantId()), endsWith(superDeal2.getVariantId()))))
                .andExpect(status().isOk());

        verify(dealService).getTodayDeals();
    }

    @Test
    public void shouldDeleteDealWhenDeleteExistingDealById() throws Exception {
        when(dealService.deleteDeal(anyLong())).thenReturn(deal1);

        mvc.perform(delete("/api/v1/deals/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(status().isOk());

        verify(dealService).deleteDeal(anyLong());
    }

    @Test
    public void shouldNotDeleteDealWhenDeleteNonExistingDealById() throws Exception {
        doThrow(DealNotFoundException.class).when(dealService).deleteDeal(anyLong());

        mvc.perform(delete("/api/v1/deals/1"))
                .andExpect(status().isNotFound());

        verify(dealService).deleteDeal(anyLong());
    }

    @Test
    public void shouldEnableDealWhenEnabledExistingDealById() throws Exception {
        deal1.setEnable(true);

        when(dealService.enableDeal(anyLong())).thenReturn(deal1);

        mvc.perform(put("/api/v1/deals/1/enabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(true)))
                .andExpect(status().isOk());

        verify(dealService).enableDeal(anyLong());
    }

    @Test
    public void shouldDisableDealWhenDisabledExistingDealById() throws Exception {
        deal1.setEnable(false);

        when(dealService.disableDeal(anyLong())).thenReturn(deal1);

        mvc.perform(put("/api/v1/deals/1/disabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(false)))
                .andExpect(status().isOk());

        verify(dealService).disableDeal(anyLong());
    }

    @Test
    public void shouldReturnDealWhenGetExistingDealById() throws Exception {
        when(dealService.getDeal(1L)).thenReturn(deal1);

        mvc.perform(get("/api/v1/deals/1"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name_local", is(deal1.getName())))
                .andExpect(status().isOk());

        verify(dealService).getDeal(anyLong());
    }

    @Test
    public void shouldReturnNotFoundDealWhenGetNonExistingDealById() throws Exception {
        doThrow(DealNotFoundException.class).when(dealService).getDeal(anyLong());

        mvc.perform(get("/api/v1/deals/1"))
                .andExpect(status().isNotFound());

        verify(dealService).getDeal(anyLong());
    }

    @Test
    public void shouldReturnNotFoundDealWhenGetNonExistingDealById1() throws Exception {
        VariantDealResponse variantDealResponse = new VariantDealResponse();
        variantDealResponse.setVariantID("variant");

        when(dealService.findPromotionPrice(anyString())).thenReturn(variantDealResponse);

        mvc.perform(get("/api/v1/deals/price/variant"))
                .andExpect(jsonPath("$.data.variant_id", is("variant")))
                .andExpect(status().isOk());

        verify(dealService).findPromotionPrice(anyString());
    }

    @Test
    public void shouldReturnInternalServerErrorDealWhenConnotGetDataFromPDS() throws Exception {
        VariantDealResponse variantDealResponse = new VariantDealResponse();
        variantDealResponse.setVariantID("variant");

        doThrow(PDSServiceException.class).when(dealService).findPromotionPrice(anyString());

        mvc.perform(get("/api/v1/deals/price/variant"))
                .andExpect(status().isInternalServerError());

        verify(dealService).findPromotionPrice(anyString());
    }

    @Test
    public void shouldReturnInternalServerErrorDealWhenConnotGetDataFromPricing() throws Exception {
        VariantDealResponse variantDealResponse = new VariantDealResponse();
        variantDealResponse.setVariantID("variant");

        doThrow(PricingServiceException.class).when(dealService).findPromotionPrice(anyString());

        mvc.perform(get("/api/v1/deals/price/variant"))
                .andExpect(status().isInternalServerError());

        verify(dealService).findPromotionPrice(anyString());
    }
}
