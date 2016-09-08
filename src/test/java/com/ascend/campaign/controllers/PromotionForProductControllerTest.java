package com.ascend.campaign.controllers;

import com.ascend.campaign.models.BundleForProduct;
import com.ascend.campaign.models.BundleForProductWM;
import com.ascend.campaign.models.FreebieForBatchVariant;
import com.ascend.campaign.models.MNPForProduct;
import com.ascend.campaign.services.DroolsService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PromotionForProductControllerTest {
    @InjectMocks
    PromotionForProductController controller;

    MockMvc mvc;

    @Mock
    DroolsService droolsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnListBundlePromotionWhenGetBundlePromotionWMByProductSKU() throws Exception {
        List<BundleForProductWM> bundleForProducts = Lists.newArrayList();

        doNothing().when(droolsService).buildDrlWM(anyBoolean());
        when(droolsService.getBundlePromotionWM(anyString())).thenReturn(bundleForProducts);

        mvc.perform(get("/api/v1/wm/promotions/bundles/")
                .param("productVariant", "product1"))
                .andExpect(status().isOk());

        verify(droolsService).getBundlePromotionWM(anyString());
    }


    @Test
    public void shouldReturnListBundlePromotionWhenGetBundlePromotionITMByProductSKU() throws Exception {
        List<BundleForProduct> bundleForProducts = Lists.newArrayList();

        when(droolsService.getBundlePromotionITM(anyString())).thenReturn(bundleForProducts);

        mvc.perform(get("/api/v1/promotions/bundles/")
                .param("productVariant", "product1"))
                .andExpect(status().isOk());

        verify(droolsService).getBundlePromotionITM(anyString());
    }

    @Test
    public void shouldReturnListBundlePromotionWhenGetBundlePromotionITMNewPathByProductSKU() throws Exception {
        List<BundleForProduct> bundleForProducts = Lists.newArrayList();

        when(droolsService.getBundlePromotionITM(anyString())).thenReturn(bundleForProducts);

        mvc.perform(get("/api/v1/itm/promotions/bundles/")
                .param("productVariant", "product1"))
                .andExpect(status().isOk());

        verify(droolsService).getBundlePromotionITM(anyString());
    }

    @Test
    public void shouldReturnBadRequestWhenGetBundlePromotionWithNoParameters() throws Exception {

        mvc.perform(get("/api/v1/itm/promotions/bundles/"))
                .andExpect(status().isBadRequest());

        verify(droolsService, never()).getBundlePromotionITM(anyString());
    }

    @Test
    public void shouldReturnListMNPPromotionWhenGetMNPPromotionITMNewPathByProductSKU() throws Exception {
        List<MNPForProduct> mnpForProducts = Lists.newArrayList();

        when(droolsService.getMNPPromotionITM(anyString())).thenReturn(mnpForProducts);

        mvc.perform(get("/api/v1/itm/promotions/mnp/")
                .param("productVariant", "product1"))
                .andExpect(status().isOk());

        verify(droolsService).getMNPPromotionITM(anyString());
    }

    @Test
    public void shouldReturnListFreebiePromotionWhenGetFreebiePromotionByBatchVariants() throws Exception {
        String variantsForGetPromotion = "variant1,variant2,variants3";
        FreebieForBatchVariant freebieForBatchVariant1 = new FreebieForBatchVariant();
        FreebieForBatchVariant freebieForBatchVariant2 = new FreebieForBatchVariant();
        List<FreebieForBatchVariant> freebieForBatchVariants =
                Arrays.asList(freebieForBatchVariant1, freebieForBatchVariant2);

        when(droolsService.getFreebiePromotionITM(anyString())).thenReturn(freebieForBatchVariants);

        mvc.perform(get("/api/v1/itm/promotions/freebie/")
                .param("productVariant", variantsForGetPromotion))
                .andDo(print())
                .andExpect(status().isOk());

        verify(droolsService).getFreebiePromotionITM(anyString());
    }

    @Test
    public void shouldReturnListFreebiePromotionWhenGetFreebiePromotionBySingleVariants() throws Exception {
        String variantsForGetPromotion = "variant1";
        FreebieForBatchVariant freebieForBatchVariant1 = new FreebieForBatchVariant();
        FreebieForBatchVariant freebieForBatchVariant2 = new FreebieForBatchVariant();
        List<FreebieForBatchVariant> freebieForBatchVariants =
                Arrays.asList(freebieForBatchVariant1, freebieForBatchVariant2);

        when(droolsService.getFreebiePromotionITM(anyString())).thenReturn(freebieForBatchVariants);

        mvc.perform(get("/api/v1/itm/promotions/freebie/")
                .param("productVariant", variantsForGetPromotion))
                .andDo(print())
                .andExpect(status().isOk());

        verify(droolsService).getFreebiePromotionITM(anyString());
    }
}