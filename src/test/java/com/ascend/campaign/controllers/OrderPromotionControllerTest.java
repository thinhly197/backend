package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.HoldTypeEnum;
import com.ascend.campaign.entities.OrderPromotion;
import com.ascend.campaign.exceptions.CampaignException;
import com.ascend.campaign.models.CustomerPromotion;
import com.ascend.campaign.services.OrderPromotionService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderPromotionControllerTest {
    @InjectMocks
    private OrderPromotionController controller;

    @Mock
    OrderPromotionService orderPromotionService;
    private MockMvc mvc;

    private OrderPromotion orderPromotion;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        orderPromotion = new OrderPromotion();
        orderPromotion.setOrderId("1");
        orderPromotion.setCustomerId("1");
        orderPromotion.setPromotionId(1L);
        orderPromotion.setDiscountValue(1.00);
    }

    @Test
    public void shouldCreatedWhenCreateWithValidJsonData() throws Exception {
        when(orderPromotionService.createOrderPromotion(any(OrderPromotion.class))).thenReturn(orderPromotion);

        mvc.perform(post("/api/v1/orders/promotions")
                .content("{\"order_id\":\"1\", \"customer_id\":\"1\", \"promotion_id\":1, \"discount_value\":1}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.order_id", is("1")))
                .andExpect(status().isCreated());

        verify(orderPromotionService).createOrderPromotion(any(OrderPromotion.class));
    }

    @Test
    public void shouldCreatedOrderPromotionWhenValidCustomerPromotionApply() throws Exception {
        List<OrderPromotion> orderPromotions = Lists.newArrayList(orderPromotion);

        when(orderPromotionService.applyCustomerPromotion(any(CustomerPromotion.class))).thenReturn(orderPromotions);

        mvc.perform(post("/api/v1/orders/customers")
                .content("{\"order_id\":\"1\", \"customer_id\":\"1\", \"promotion_id\":1, \"discount_value\":1}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[0].order_id", is("1")))
                .andExpect(status().isCreated());

        verify(orderPromotionService).applyCustomerPromotion(any(CustomerPromotion.class));
    }

    @Test
    public void shouldHoldOrderPromotionWhenHoldExistingOrderPromotion() throws Exception {
        orderPromotion.setHoldType(HoldTypeEnum.ONLINE.getContent());

        when(orderPromotionService.holdOrderPromotion(anyString(), anyLong(), anyString(), anyLong()))
                .thenReturn(orderPromotion);

        mvc.perform(put("/api/v1/orders/1/promotions/1/online/2"))
                .andExpect(jsonPath("$.data.order_id", is("1")))
                .andExpect(jsonPath("$.data.promotion_id", is(1)))
                .andExpect(jsonPath("$.data.hold_type", is("online")))
                .andExpect(status().isOk());

        verify(orderPromotionService).holdOrderPromotion(anyString(), anyLong(), anyString(), anyLong());
    }

    @Test
    public void shouldBadRequestWhenInvalidHoldType() throws Exception {
        mvc.perform(put("/api/v1/orders/1/promotions/1/invalid/2"))
                .andExpect(status().isBadRequest());

        verify(orderPromotionService, never()).holdOrderPromotion(anyString(), anyLong(), anyString(), anyLong());
    }

    @Test
    public void shouldUnholdOrderPromotionWhenUnholdExistingOrderPromotion() throws Exception {
        orderPromotion.setHoldType(CampaignEnum.UNHOLD.getContent());

        when(orderPromotionService.unholdOrderPromotion(anyString(), anyLong(), anyObject()))
                .thenReturn(orderPromotion);

        mvc.perform(put("/api/v1/orders/1/promotions/1/unhold"))
                .andExpect(jsonPath("$.data.order_id", is("1")))
                .andExpect(jsonPath("$.data.promotion_id", is(1)))
                .andExpect(jsonPath("$.data.hold_type", is("unhold")))
                .andExpect(status().isOk());

        verify(orderPromotionService).unholdOrderPromotion(anyString(), anyLong(), anyObject());
    }

    @Test
    public void shouldBadRequestWhenUnholdExpiredOrNotFoundOrderPromotion() throws Exception {
        when(orderPromotionService.unholdOrderPromotion(anyString(), anyLong(), anyObject()))
                .thenThrow(new CampaignException(Errors.ORDER_PROMOTION_00));

        mvc.perform(put("/api/v1/orders/1/promotions/1/unhold"))
                .andExpect(status().isBadRequest());

        verify(orderPromotionService).unholdOrderPromotion(anyString(), anyLong(), anyObject());
    }
}