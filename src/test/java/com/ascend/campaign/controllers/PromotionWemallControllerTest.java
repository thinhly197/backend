package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.CartCampaign;
import com.ascend.campaign.models.PromotionForProduct;
import com.ascend.campaign.services.DroolsService;
import com.ascend.campaign.services.PromotionService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PromotionWemallControllerTest {
    @InjectMocks
    PromotionWemallController controller;

    MockMvc mvc;

    @Mock
    PromotionService promotionService;

    @Mock
    DroolsService droolsService;

    private PromotionWM promotion1;
    private PromotionWM promotion2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        promotion1 = new PromotionWM();
        promotion1.setId(1L);
        promotion1.setName("PROMOTION1_NAME");
        promotion1.setType("PROMOTION1_TYPE");

        promotion2 = new PromotionWM();
        promotion2.setId(2L);
        promotion2.setName("PROMOTION2_NAME");
        promotion2.setType("PROMOTION2_TYPE");
    }

    @Test
    public void shouldCreatePromotionWhenCreateNewPromotionSuccessfully() throws Exception {
        when(promotionService.createPromotionWemall(any(PromotionWM.class))).thenReturn(promotion1);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(post("/api/v1/wm/promotions")
                .content("{\"name\":\"PROMOTION1_NAME\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.name_local", is(promotion1.getName())))
                .andExpect(status().isCreated());

        verify(promotionService).createPromotionWemall(any(PromotionWM.class));
    }

    @Test
    public void shouldReturnPromotionListWhenGetAllExistingPromotion() throws Exception {
        List<PromotionWM> promotions = Lists.newArrayList(promotion1, promotion2);
        Page expectedPage = new PageImpl(promotions);

        when(promotionService.getAllPromotionsWM(
                anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(),
                anyString(), anyBoolean(), anyBoolean(), anyString())).thenReturn(expectedPage);

        mvc.perform(get("/api/v1/wm/promotions"))
                .andExpect(jsonPath("$.data.content[0].name_local", is(promotion1.getName())))
                .andExpect(jsonPath("$.data.content[1].name_local", is(promotion2.getName())))
                .andExpect(status().isOk());

        verify(promotionService).getAllPromotionsWM(
                anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(),
                anyString(), anyBoolean(), anyBoolean(), anyString());
    }

    @Test
    public void shouldReturnPromotionWhenGetExistingPromotionById() throws Exception {
        when(promotionService.getPromotionWm(anyLong())).thenReturn(promotion1);

        mvc.perform(get("/api/v1/wm/promotions/1"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name_local", is(promotion1.getName())))
                .andExpect(status().isOk());

        verify(promotionService).getPromotionWm(anyLong());
    }

    @Test
    public void shouldReturnPromotionUpdatedWhenUpdateExistingPromotionById() throws Exception {
        promotion1.setName("UPDATE1");

        when(promotionService.updatePromotionWm(anyLong(), any(PromotionWM.class))).thenReturn(promotion1);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(put("/api/v1/wm/promotions/1")
                .content("{\"name\":\"UPDATE1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name_local", is(promotion1.getName())))
                .andExpect(status().isOk());

        verify(promotionService).updatePromotionWm(anyLong(), any(PromotionWM.class));
    }

    @Test
    public void shouldReturnNotFoundWhenUpdateNonExistingPromotionById() throws Exception {
        doThrow(PromotionNotFoundException.class).when(promotionService)
                .updatePromotionWm(anyLong(), any(PromotionWM.class));
        when(droolsService.buildDrlPromotion(anyBoolean())).thenReturn(0);

        mvc.perform(put("/api/v1/wm/promotions/1")
                .content("{\"name\":\"UPDATE1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(promotionService).updatePromotionWm(anyLong(), any(PromotionWM.class));
    }

    @Test
    public void shouldDeletePromotionWhenDeleteExistingPromotionById() throws Exception {
        when(promotionService.deletePromotionWm(anyLong())).thenReturn(promotion1);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(delete("/api/v1/wm/promotions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(status().isOk());

        verify(promotionService).deletePromotionWm(anyLong());
    }

    @Test
    public void shouldReturnNotFoundWhenDeleteNonExistingPromotionById() throws Exception {
        doThrow(PromotionNotFoundException.class).when(promotionService).deletePromotionWm(anyLong());
        when(droolsService.buildDrlPromotion(anyBoolean())).thenReturn(0);

        mvc.perform(delete("/api/v1/wm/promotions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(promotionService).deletePromotionWm(anyLong());
    }

    @Test
    public void shouldDuplicatePromotionWhenDuplicateExistingPromotionById() throws Exception {
        when(promotionService.duplicatePromotionWm(anyLong())).thenReturn(promotion2);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(post("/api/v1/wm/promotions/1/duplication"))
                .andExpect(jsonPath("$.data.id", is(2)))
                .andExpect(status().isOk());

        verify(promotionService).duplicatePromotionWm(anyLong());
    }

    @Test
    public void shouldEnablePromotionWhenEnabledExistingPromotionById() throws Exception {
        promotion1.setEnable(true);

        when(promotionService.enablePromotionWm(anyLong())).thenReturn(promotion1);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(put("/api/v1/wm/promotions/1/enabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(true)))
                .andExpect(status().isOk());

        verify(promotionService).enablePromotionWm(anyLong());
    }

    @Test
    public void shouldDisablePromotionWhenDisabledExistingPromotionById() throws Exception {
        promotion1.setEnable(false);

        when(promotionService.disablePromotionWm(anyLong())).thenReturn(promotion1);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(put("/api/v1/wm/promotions/1/disabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(false)))
                .andExpect(status().isOk());

        verify(promotionService).disablePromotionWm(anyLong());
    }

    @Test
    public void shouldReturnPromotionForProductWhenGetPromotionForProductByProductSKU() throws Exception {
        List<PromotionForProduct> promotions = Lists.newArrayList();

        doNothing().when(droolsService).buildDrlWM(anyBoolean());
        when(droolsService.getProductPromotionWM(anyString(), anyString())).thenReturn(promotions);

        mvc.perform(get("/api/v1/wm/promotions/products/SKU1")
                .param("brandSKU", "productBrand1"))
                .andExpect(status().isOk());

        verify(droolsService).getProductPromotionWM(anyString(), anyString());
    }

    @Test
    public void shouldBatchEnablePromotionWhenEnabledExistingPromotionById() throws Exception {
        promotion1.setEnable(true);

        when(promotionService.enablePromotionWm(anyLong())).thenReturn(promotion1);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(put("/api/v1/wm/promotions/enabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(promotionService, times(4)).enablePromotionWm(anyLong());
    }

    @Test
    public void shouldBatchDisablePromotionWhenDisabledExistingPromotionById() throws Exception {
        promotion1.setEnable(false);

        when(promotionService.disablePromotionWm(anyLong())).thenReturn(promotion1);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(put("/api/v1/wm/promotions/disabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(promotionService, times(4)).disablePromotionWm(anyLong());
    }

    @Test
    public void shouldReturnCartPromotionWhenGetPromotionByCart() throws Exception {
        CartCampaign cartCampaign = new CartCampaign();

        when(droolsService.executeCartPromotionRuleWM(any(Cart.class))).thenReturn(cartCampaign);
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(post("/api/v1/wm/promotions/cart")
                .content("{\"test\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(droolsService).executeCartPromotionRuleWM(any(Cart.class));
    }

    @Test
    public void shouldReturnPromotionCodesWhenGetExistingPromotionBySKU() throws Exception {
        when(droolsService.getProductPromotionCodeWM(anyString(), anyString())).thenReturn(Lists.newArrayList());
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(get("/api/v1/wm/promotions/code/1SKU"))
                .andExpect(status().isOk());

        verify(droolsService).getProductPromotionCodeWM(anyString(), anyString());
    }

    @Test
    public void shouldReturnBadRequestWhenDisabledExistingPromotionWithNoParameters() throws Exception {
        doNothing().when(droolsService).buildDrlWM(anyBoolean());

        mvc.perform(put("/api/v1/wm/promotions/disabled"))
                .andExpect(status().isBadRequest());

        verify(promotionService, never()).disablePromotionWm(anyLong());
    }
}