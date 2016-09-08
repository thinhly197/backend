package com.ascend.campaign.controllers;


import com.ascend.campaign.models.Calculation;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.VariantCalculation;
import com.ascend.campaign.services.DroolsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PriceCalculationControllerTest {
    @InjectMocks
    PriceCalculationController controller;

    MockMvc mvc;

    @Mock
    DroolsService droolsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnCalculationCartITMWhenCalculationPrice() throws Exception {
        Calculation calculation = new Calculation();
        calculation.setTotalFlatDiscount(800D);

        VariantCalculation calculation1 = new VariantCalculation();
        calculation1.setPercentDiscount(20D);
        calculation1.setFinalPrice(20D);
        calculation1.setNormalPrice(1000D);

        VariantCalculation calculation2 = new VariantCalculation();
        calculation2.setPercentDiscount(20D);
        calculation2.setFinalPrice(20D);
        calculation2.setNormalPrice(1000D);

        ArrayList<VariantCalculation> variants = new ArrayList<>();
        variants.add(calculation1);
        variants.add(calculation2);
        calculation.setPromotionForProducts(variants);

        when(droolsService.calculationCartITM(any(Cart.class))).thenReturn(calculation);
        mvc.perform(post("/api/v1/itm/prices/calculation")
                .content("{\"test\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(droolsService).calculationCartITM(any(Cart.class));
    }

    @Test
    public void shouldReturnCalculationCartWMWhenCalculationPrice() throws Exception {
        Calculation calculation = new Calculation();
        calculation.setTotalFlatDiscount(800D);

        VariantCalculation calculation1 = new VariantCalculation();
        calculation1.setPercentDiscount(20D);
        calculation1.setFinalPrice(20D);
        calculation1.setNormalPrice(1000D);

        VariantCalculation calculation2 = new VariantCalculation();
        calculation2.setPercentDiscount(20D);
        calculation2.setFinalPrice(20D);
        calculation2.setNormalPrice(1000D);

        ArrayList<VariantCalculation> variants = new ArrayList<>();
        variants.add(calculation1);
        variants.add(calculation2);
        calculation.setPromotionForProducts(variants);

        when(droolsService.calculationCartWM(any(Cart.class))).thenReturn(calculation);
        mvc.perform(post("/api/v1/wm/prices/calculation")
                .content("{\"test\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(droolsService).calculationCartWM(any(Cart.class));
    }

    @Test
    public void shouldReturnCalculationCartITMWhenReCalculationPrice() throws Exception {
        Calculation calculation = new Calculation();
        calculation.setTotalFlatDiscount(800D);

        VariantCalculation calculation1 = new VariantCalculation();
        calculation1.setPercentDiscount(20D);
        calculation1.setFinalPrice(20D);
        calculation1.setNormalPrice(1000D);

        VariantCalculation calculation2 = new VariantCalculation();
        calculation2.setPercentDiscount(20D);
        calculation2.setFinalPrice(20D);
        calculation2.setNormalPrice(1000D);

        ArrayList<VariantCalculation> variants = new ArrayList<>();
        variants.add(calculation1);
        variants.add(calculation2);
        calculation.setPromotionForProducts(variants);

        when(droolsService.reCalculationCart(any(Calculation.class))).thenReturn(calculation);
        mvc.perform(post("/api/v1/itm/prices/recalculation")
                .content("{\"test\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(droolsService).reCalculationCart(any(Calculation.class));
    }


    @Test
    public void shouldReturnCalculationCartWMWhenReCalculationPrice() throws Exception {
        Calculation calculation = new Calculation();
        calculation.setTotalFlatDiscount(800D);

        VariantCalculation calculation1 = new VariantCalculation();
        calculation1.setPercentDiscount(20D);
        calculation1.setFinalPrice(20D);
        calculation1.setNormalPrice(1000D);

        VariantCalculation calculation2 = new VariantCalculation();
        calculation2.setPercentDiscount(20D);
        calculation2.setFinalPrice(20D);
        calculation2.setNormalPrice(1000D);

        ArrayList<VariantCalculation> variants = new ArrayList<>();
        variants.add(calculation1);
        variants.add(calculation2);
        calculation.setPromotionForProducts(variants);

        when(droolsService.reCalculationCart(any(Calculation.class))).thenReturn(calculation);
        mvc.perform(post("/api/v1/wm/prices/recalculation")
                .content("{\"test\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(droolsService).reCalculationCart(any(Calculation.class));
    }

}
