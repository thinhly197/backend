package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.exceptions.BuildDroolsException;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.CartCampaign;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.PromotionForProduct;
import com.ascend.campaign.models.VariantDuplicateFreebie;
import com.ascend.campaign.services.DroolsService;
import com.ascend.campaign.services.PendingPromotionService;
import com.ascend.campaign.services.PromotionService;
import com.ascend.campaign.utils.JSONUtil;
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
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PromotionITMControllerTest {
    @InjectMocks
    PromotionITMController controller;

    MockMvc mvc;

    @Mock
    PromotionService promotionService;

    @Mock
    PendingPromotionService pendingPromotionService;

    @Mock
    DroolsService droolsService;

    private Promotion promotion1;
    private Promotion promotion2;
    private PromotionCondition promotionCondition;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        promotion1 = new Promotion();
        promotion1.setId(1L);
        promotion1.setName("PROMOTION1_NAME");
        promotion1.setType("itm-freebie");
        promotion1.setMember(true);
        promotion1.setNonMember(true);
        promotion1.setEnable(true);
        promotion1.setShortDescription("Short");
        promotion1.setStartPeriod(new Date());
        promotion1.setEndPeriod(new Date());
        promotion2 = new Promotion();
        promotion2.setId(2L);
        promotion2.setName("PROMOTION2_NAME");
        promotion2.setType("itm-freebie");
        promotionCondition = new PromotionCondition();
        promotion1.setPromotionCondition(promotionCondition);


    }

    @Test
    public void shouldCreatePromotionWhenCreateNewPromotionSuccessfully() throws Exception {
        when(pendingPromotionService.createPromotionItruemart(any(Promotion.class))).thenReturn(promotion1);

        mvc.perform(post("/api/v1/itm/promotions")
                .content(JSONUtil.toString(promotion1))
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(jsonPath("$.data.name_local", is(promotion1.getName())))
                .andExpect(status().isCreated());

        verify(pendingPromotionService).createPromotionItruemart(any(Promotion.class));
    }

    @Test
    public void shouldReturnErrorMessageWhenCreateNewPromotionWithJsonIsNotValid() throws Exception {
        promotion1.setType("");
        mvc.perform(post("/api/v1/itm/promotions")
                .content(JSONUtil.toString(promotion1))
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                //.andExpect(jsonPath("$.message", is("promotion type Value specified is not valid")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnCampaignNotFoundExceptionWhenCreateNewPromotionWithNotExistingCampaign() throws Exception {
        doThrow(CampaignNotFoundException.class).when(pendingPromotionService)
                .createPromotionItruemart(any(Promotion.class));

        mvc.perform(post("/api/v1/itm/promotions")
                .content(JSONUtil.toString(promotion1))
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(jsonPath("$.message", is("Campaign not found !!")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnPromotionListWhenGetAllExistingPromotion() throws Exception {
        List<Promotion> promotions = Lists.newArrayList(promotion1, promotion2);
        Page expectedPage = new PageImpl(promotions);

        when(promotionService.getAllPromotionsITM(anyInt(), anyInt(), any(Sort.Direction.class),
                anyString(), anyLong(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyString(), anyString(), anyLong())).thenReturn(expectedPage);

        mvc.perform(get("/api/v1/itm/promotions"))
                .andExpect(jsonPath("$.data.content[0].name_local", is(promotion1.getName())))
                .andExpect(jsonPath("$.data.content[1].name_local", is(promotion2.getName())))
                .andExpect(status().isOk());

        verify(promotionService).getAllPromotionsITM(
                anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyString(), anyString(), anyLong());
    }

    @Test
    public void shouldReturnPromotionWhenGetExistingPromotionById() throws Exception {
        when(promotionService.getPromotionItm(anyLong())).thenReturn(promotion1);

        mvc.perform(get("/api/v1/itm/promotions/1"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name_local", is(promotion1.getName())))
                .andExpect(status().isOk());

        verify(promotionService).getPromotionItm(anyLong());
    }

    @Test
    public void shouldReturnPromotionUpdatedWhenUpdateExistingPromotionById() throws Exception {
        promotion1.setName("UPDATE1");

        when(pendingPromotionService.updatePromotionItm(anyLong(), any(Promotion.class))).thenReturn(promotion1);

        mvc.perform(put("/api/v1/itm/promotions/1")
                .content("{\"name\":\"UPDATE1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name_local", is(promotion1.getName())))
                .andExpect(status().isOk());

        verify(pendingPromotionService).updatePromotionItm(anyLong(), any(Promotion.class));
    }

    @Test
    public void shouldReturnNotFoundWhenUpdateNonExistingPromotionById() throws Exception {
        doThrow(PromotionNotFoundException.class).when(pendingPromotionService)
                .updatePromotionItm(anyLong(), any(Promotion.class));

        mvc.perform(put("/api/v1/itm/promotions/1")
                .content("{\"name\":\"UPDATE1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(pendingPromotionService).updatePromotionItm(anyLong(), any(Promotion.class));
    }

    @Test
    public void shouldDeletePromotionWhenDeleteExistingPromotionById() throws Exception {
        when(pendingPromotionService.deletePromotionItm(anyLong())).thenReturn(promotion1);

        mvc.perform(delete("/api/v1/itm/promotions/1")
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(status().isOk());

        verify(pendingPromotionService).deletePromotionItm(anyLong());
    }

    @Test
    public void shouldReturnNotFoundWhenDeleteNonExistingPromotionById() throws Exception {
        doThrow(PromotionNotFoundException.class).when(pendingPromotionService).deletePromotionItm(anyLong());

        mvc.perform(delete("/api/v1/itm/promotions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(pendingPromotionService).deletePromotionItm(anyLong());
    }

    @Test
    public void shouldDuplicatePromotionWhenDuplicateExistingPromotionById() throws Exception {
        when(pendingPromotionService.duplicatePromotionItm(anyLong())).thenReturn(promotion2);

        mvc.perform(post("/api/v1/itm/promotions/1/duplication"))
                .andExpect(jsonPath("$.data.id", is(2)))
                .andExpect(status().isOk());

        verify(pendingPromotionService).duplicatePromotionItm(anyLong());
    }

    @Test
    public void shouldReturnCartPromotionWhenGetPromotionByCart() throws Exception {
        CartCampaign cartCampaign = new CartCampaign();

        when(droolsService.executeCartPromotionRuleITM(any(Cart.class), anyString())).thenReturn(cartCampaign);
        when(droolsService.buildDrlPromotion(anyBoolean())).thenReturn(0);

        mvc.perform(post("/api/v1/itm/promotions/cart")
                .content("{\"test\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(droolsService).executeCartPromotionRuleITM(any(Cart.class), anyString());
    }

    @Test
    public void shouldReturnCartPromotionWhenGetPromotionByCartV2() throws Exception {
        CartCampaign cartCampaign = new CartCampaign();

        when(droolsService.executeCartPromotionRuleITM(any(Cart.class), anyString())).thenReturn(cartCampaign);
        when(droolsService.buildDrlPromotion(anyBoolean())).thenReturn(0);

        mvc.perform(post("/api/v2/itm/promotions/cart")
                .content("{\"test\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(droolsService).executeCartPromotionRuleITM(any(Cart.class), anyString());
    }

    @Test
    public void shouldEnablePromotionWhenEnabledExistingPromotionById() throws Exception {
        promotion1.setEnable(true);

        when(promotionService.enablePromotionItm(anyLong())).thenReturn(promotion1);

        mvc.perform(put("/api/v1/itm/promotions/1/enabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(true)))
                .andExpect(status().isOk());

        verify(promotionService).enablePromotionItm(anyLong());
    }

    @Test
    public void shouldDisablePromotionWhenDisabledExistingPromotionById() throws Exception {
        promotion1.setEnable(false);

        when(promotionService.disablePromotionItm(anyLong())).thenReturn(promotion1);

        mvc.perform(put("/api/v1/itm/promotions/1/disabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(false)))
                .andExpect(status().isOk());

        verify(promotionService).disablePromotionItm(anyLong());
    }

    @Test
    public void shouldReturnPromotionForProductWhenGetPromotionForProductByProductSKU() throws Exception {
        List<PromotionForProduct> promotions = Lists.newArrayList();

        when(droolsService.getProductPromotionITM(anyString(), anyString())).thenReturn(promotions);

        mvc.perform(get("/api/v1/itm/promotions/products/SKU1")
                .param("brandSKU", "productBrand1"))
                .andExpect(status().isOk());

        verify(droolsService).getProductPromotionITM(anyString(), anyString());
    }

    @Test
    public void shouldBatchEnablePromotionWhenEnabledExistingPromotionById() throws Exception {
        promotion1.setEnable(true);

        when(promotionService.getPromotionItm(anyLong())).thenReturn(promotion1);

        mvc.perform(put("/api/v1/itm/promotions/enabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(promotionService, times(4)).getPromotionItm(anyLong());
        verify(pendingPromotionService, times(4)).updatePromotionItm(anyLong(), any(Promotion.class));
    }

    @Test
    public void shouldBatchDisablePromotionWhenDisabledExistingPromotionById() throws Exception {
        promotion1.setEnable(false);

        when(promotionService.getPromotionItm(anyLong())).thenReturn(promotion1);

        mvc.perform(put("/api/v1/itm/promotions/disabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(promotionService, times(4)).getPromotionItm(anyLong());
        verify(pendingPromotionService, times(4)).updatePromotionItm(anyLong(), any(Promotion.class));
    }

    @Test
    public void shouldReturnBadRequestWhenDisabledExistingPromotionWithNoParameters() throws Exception {

        mvc.perform(put("/api/v1/itm/promotions/disabled"))
                .andExpect(status().isBadRequest());

        verify(promotionService, never()).disablePromotionItm(anyLong());
    }

    @Test
    public void shouldReturnBuildResponseCorrectlyWhenBuildExistingPendingPromotionInDB() throws Exception {
        promotion1.setEnable(true);

        when(droolsService.buildDrlPromotion(anyBoolean())).thenReturn(5);

        mvc.perform(put("/api/v1/itm/promotions/build"))
                .andExpect(jsonPath("$.data.promotion_built", is(5)))
                .andExpect(status().isOk());

        verify(droolsService).buildDrlPromotion(anyBoolean());
    }

    @Test
    public void shouldReturnBuildResponseCorrectlyWhenBuildNotExistingPendingPromotionInDB() throws Exception {
        promotion1.setEnable(true);

        when(droolsService.buildDrlPromotion(anyBoolean())).thenReturn(0);

        mvc.perform(put("/api/v1/itm/promotions/build"))
                .andExpect(jsonPath("$.data.promotion_built", is(0)))
                .andExpect(status().isOk());

        verify(droolsService).buildDrlPromotion(anyBoolean());
    }

    @Test
    public void shouldReturnBuildDroolsExceptionWhenBuildDroolsAndFoundSomeThingError() throws Exception {
        promotion1.setEnable(true);

        doThrow(BuildDroolsException.class).when(droolsService).buildDrlPromotion(anyBoolean());

        mvc.perform(put("/api/v1/itm/promotions/build"))
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Build drools fail !!")))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldReturnListVariantDuplicateFreebieWhenCheckFreeBiePromotionDuplicateAndFoundFreeBieDuplicate()
            throws Exception {
        VariantDuplicateFreebie variantDuplicateFreebie = new VariantDuplicateFreebie();
        variantDuplicateFreebie.setDuplicatePromotionId(Arrays.asList(1L, 2L, 3L));
        variantDuplicateFreebie.setVariantId("VA1");
        when(promotionService.checkDuplicateCriteriaFreebie(anyString(), anyLong(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList(variantDuplicateFreebie));

        mvc.perform(get("/api/v1/itm/promotions/isFreebieCriteriaDuplicate")
                .param("variants", "VA1,VA2")
                .param("start_period", "1001200099")
                .param("end_period", "10012000193")
                .param("promotion_id", "5"))
                .andExpect(jsonPath("$.data[0].variant_id", is("VA1")))
                .andExpect(status().isOk());

        verify(promotionService).checkDuplicateCriteriaFreebie(anyString(), anyLong(), anyLong(), anyLong());
    }


    @Test
    public void shouldReturnEmptyListWhenCheckFreeBiePromotionDuplicateAndNotFoundFreeBieDuplicate()
            throws Exception {
        when(promotionService.checkDuplicateCriteriaFreebie(anyString(), anyLong(), anyLong(), anyLong()))
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/api/v1/itm/promotions/isFreebieCriteriaDuplicate")
                .param("variants", "VA1,VA2")
                .param("start_period", "1001200099")
                .param("end_period", "10012000193")
                .param("promotion_id", "5"))
                .andExpect(jsonPath("$.data", is(Collections.emptyList())))
                .andExpect(status().isOk());

        verify(promotionService).checkDuplicateCriteriaFreebie(anyString(), anyLong(), anyLong(), anyLong());
    }
}