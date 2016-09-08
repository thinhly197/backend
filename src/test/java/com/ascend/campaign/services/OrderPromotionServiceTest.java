package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.HoldTypeEnum;
import com.ascend.campaign.entities.OrderPromotion;
import com.ascend.campaign.entities.User;
import com.ascend.campaign.entities.UserPromotionKey;
import com.ascend.campaign.exceptions.CampaignException;
import com.ascend.campaign.models.CampaignSuggestion;
import com.ascend.campaign.models.CustomerPromotion;
import com.ascend.campaign.models.PromotionAction;
import com.ascend.campaign.models.Variant;
import com.ascend.campaign.repositories.OrderPromotionRepo;
import com.ascend.campaign.repositories.UserRepo;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class OrderPromotionServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    OrderPromotionService orderPromotionService;

    @Mock
    OrderPromotionRepo orderPromotionRepo;

    @Mock
    UserRepo userRepo;

    @Mock
    UserPromotionService userPromotionService;

    OrderPromotion orderPromotion;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        orderPromotionService = new OrderPromotionService(orderPromotionRepo, userRepo, userPromotionService);
        orderPromotion = new OrderPromotion();
        orderPromotion.setOrderId("1");
        orderPromotion.setCustomerId("1");
        orderPromotion.setPromotionId(1L);
        orderPromotion.setDiscountValue(1.00);
    }

    @Test
    public void shouldReturnOrderPromotionCreatedWhenCreateNonExistingOrderPromotion() {
        when(orderPromotionRepo.saveAndFlush(any(OrderPromotion.class))).thenReturn(orderPromotion);

        OrderPromotion orderPromotionCreated = orderPromotionService.createOrderPromotion(orderPromotion);

        assertThat(orderPromotionCreated, is(notNullValue()));

        verify(orderPromotionRepo).saveAndFlush(any(OrderPromotion.class));
    }

    @Test
    public void shouldCreateOrderPromotionAndCreateUserWhenNonExistingCustomerApplyPromotionSuccessfully() {
        List<OrderPromotion> orderPromotions = Arrays.asList(orderPromotion);
        when(orderPromotionRepo.save(anyList())).thenReturn(orderPromotions);
        doNothing().when(orderPromotionRepo).flush();
        when(userRepo.findByCustomerId(anyString())).thenReturn(null);
        when(userRepo.saveAndFlush(any(User.class))).thenReturn(new User());

        Variant variant = new Variant();
        variant.setDiscountValue(1d);

        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setVariants(Arrays.asList(variant));
        CampaignSuggestion campaignSuggestion = new CampaignSuggestion();
        campaignSuggestion.setPromotionAction(Arrays.asList(promotionAction));

        campaignSuggestion.setPromotionId("1");
        campaignSuggestion.setPromotionAction(Arrays.asList(promotionAction));

        CustomerPromotion customerPromotion = new CustomerPromotion();
        customerPromotion.setCustomerId("1");
        customerPromotion.setCustomerType("user");
        customerPromotion.setOrderId("1");
        customerPromotion.setPromotions(Arrays.asList(campaignSuggestion));

        orderPromotions = orderPromotionService.applyCustomerPromotion(customerPromotion);

        assertThat(orderPromotions.size(), is(1));

        verify(orderPromotionRepo).save(anyList());
        verify(userRepo).findByCustomerId(anyString());
        verify(userRepo).saveAndFlush(any(User.class));
    }

    @Test
    public void shouldCreateOrderPromotionButNotCreateUserWhenExistingCustomerApplyPromotionSuccessfully() {
        List<OrderPromotion> orderPromotions = Arrays.asList(orderPromotion);
        when(orderPromotionRepo.save(anyList())).thenReturn(orderPromotions);
        doNothing().when(orderPromotionRepo).flush();
        when(userRepo.findByCustomerId(anyString())).thenReturn(new User());

        Variant variant = new Variant();
        variant.setDiscountValue(1d);

        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setVariants(Arrays.asList(variant));

        CampaignSuggestion campaignSuggestion = new CampaignSuggestion();
        campaignSuggestion.setPromotionId("1");
        campaignSuggestion.setPromotionAction(Arrays.asList(promotionAction));

        CustomerPromotion customerPromotion = new CustomerPromotion();
        customerPromotion.setCustomerId("1");
        customerPromotion.setCustomerType("user");
        customerPromotion.setOrderId("1");
        customerPromotion.setPromotions(Arrays.asList(campaignSuggestion));

        orderPromotions = orderPromotionService.applyCustomerPromotion(customerPromotion);

        assertThat(orderPromotions.size(), is(1));

        verify(orderPromotionRepo).save(anyList());
        verify(userRepo).findByCustomerId(anyString());
        verify(userRepo, never()).saveAndFlush(any(User.class));
    }

    @Test
    public void shouldCreateOrderPromotionButNotCreateUserWhenNonUserApplyPromotionSuccessfully() {
        List<OrderPromotion> orderPromotions = Arrays.asList(orderPromotion);
        when(orderPromotionRepo.save(anyList())).thenReturn(orderPromotions);
        doNothing().when(orderPromotionRepo).flush();

        Variant variant = new Variant();
        variant.setDiscountValue(1d);

        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setVariants(Arrays.asList(variant));

        CampaignSuggestion campaignSuggestion = new CampaignSuggestion();
        campaignSuggestion.setPromotionId("1");
        campaignSuggestion.setPromotionAction(Arrays.asList(promotionAction));

        CustomerPromotion customerPromotion = new CustomerPromotion();
        customerPromotion.setCustomerId("1");
        customerPromotion.setCustomerType("non-user");
        customerPromotion.setOrderId("1");
        customerPromotion.setPromotions(Arrays.asList(campaignSuggestion));

        orderPromotions = orderPromotionService.applyCustomerPromotion(customerPromotion);

        assertThat(orderPromotions.size(), is(1));

        verify(orderPromotionRepo).save(anyList());
        verify(userRepo, never()).findByCustomerId(anyString());
        verify(userRepo, never()).saveAndFlush(any(User.class));
    }

    @Test
    public void shouldHoldOrderPromotionWhenHoldExistingOrderPromotionById() {
        when(orderPromotionRepo.findByOrderIdAndPromotionId(anyString(), anyLong())).thenReturn(orderPromotion);

        orderPromotion.setHoldType(HoldTypeEnum.ONLINE.getContent());

        when(orderPromotionRepo.saveAndFlush(any(OrderPromotion.class))).thenReturn(orderPromotion);

        OrderPromotion result = orderPromotionService.holdOrderPromotion("1", 1L, HoldTypeEnum.ONLINE.getContent(), 2L);

        assertThat(result, is(notNullValue()));
        assertThat(result.getHoldType(), is("online"));

        verify(orderPromotionRepo).findByOrderIdAndPromotionId(anyString(), anyLong());
        verify(orderPromotionRepo).saveAndFlush(any(OrderPromotion.class));
    }

    @Test
    public void shouldUnholdOrderPromotionWhenUnholdExistingOrderPromotionByIdAndValidHoldTime() throws Exception {
        DateTime now = DateTime.now();
        DateTime nowPlusTwoDay = now.plusDays(2);

        orderPromotion.setHoldTime(nowPlusTwoDay.toDate());
        orderPromotion.setHoldType(HoldTypeEnum.ONLINE.getContent());
        when(orderPromotionRepo.findByOrderIdAndPromotionId(anyString(), anyLong())).thenReturn(orderPromotion);
        when(userPromotionService.isUpdateExecuteTime(any(UserPromotionKey.class))).thenReturn(true);
        when(orderPromotionRepo.saveAndFlush(any(OrderPromotion.class))).thenReturn(orderPromotion);

        OrderPromotion result = orderPromotionService.unholdOrderPromotion("1", 1L, now);

        assertThat(result, is(notNullValue()));
        assertThat(result.getHoldType(), is("unhold"));

        verify(orderPromotionRepo).findByOrderIdAndPromotionId(anyString(), anyLong());
        verify(userPromotionService).isUpdateExecuteTime(any(UserPromotionKey.class));
        verify(orderPromotionRepo).saveAndFlush(any(OrderPromotion.class));
    }

    @Test
    public void shouldThrownExceptionOrderPromotionExpiredWhenUnholdExistingOrderPromotionByIdAndInvalidHoldTime()
            throws Exception {
        thrown.expect(CampaignException.class);
        thrown.expectMessage(Errors.ORDER_PROMOTION_00.name());

        DateTime now = DateTime.now();
        DateTime nowMinusTwoDay = now.minusDays(2);

        orderPromotion.setHoldType(HoldTypeEnum.ONLINE.getContent());
        orderPromotion.setHoldTime(nowMinusTwoDay.toDate());
        when(orderPromotionRepo.findByOrderIdAndPromotionId(anyString(), anyLong())).thenReturn(orderPromotion);

        orderPromotionService.unholdOrderPromotion("1", 1L, now);

        verify(orderPromotionRepo).findByOrderIdAndPromotionId(anyString(), anyLong());
        verify(userPromotionService, never()).isUpdateExecuteTime(any(UserPromotionKey.class));
        verify(orderPromotionRepo, never()).saveAndFlush(any(OrderPromotion.class));
    }

    @Test
    public void shouldThrownExceptionOrderPromotionNotFoundWhenUnholdNonExistingOrderPromotion()
            throws Exception {
        thrown.expect(CampaignException.class);
        thrown.expectMessage(Errors.ORDER_PROMOTION_01.name());

        when(orderPromotionRepo.findByOrderIdAndPromotionId(anyString(), anyLong())).thenReturn(null);

        orderPromotionService.unholdOrderPromotion("1", 1L, DateTime.now());

        verify(orderPromotionRepo).findByOrderIdAndPromotionId(anyString(), anyLong());
        verify(userPromotionService, never()).isUpdateExecuteTime(any(UserPromotionKey.class));
        verify(orderPromotionRepo, never()).saveAndFlush(any(OrderPromotion.class));
    }

    @Test
    public void shouldThrownExceptionCannotHoldOverLimitWhenUnholdExistingOrderPromotionByIdAndInvalidHoldTime()
            throws Exception {
        thrown.expect(CampaignException.class);
        thrown.expectMessage(Errors.ORDER_PROMOTION_02.name());

        DateTime now = DateTime.now();
        DateTime nowPlusTwoDay = now.plusDays(2);

        orderPromotion.setHoldTime(nowPlusTwoDay.toDate());
        orderPromotion.setHoldType(HoldTypeEnum.ONLINE.getContent());
        when(orderPromotionRepo.findByOrderIdAndPromotionId(anyString(), anyLong())).thenReturn(orderPromotion);
        when(userPromotionService.isUpdateExecuteTime(any(UserPromotionKey.class))).thenReturn(false);

        orderPromotionService.unholdOrderPromotion("1", 1L, now);

        verify(orderPromotionRepo).findByOrderIdAndPromotionId(anyString(), anyLong());
        verify(userPromotionService).isUpdateExecuteTime(any(UserPromotionKey.class));
        verify(orderPromotionRepo, never()).saveAndFlush(any(OrderPromotion.class));
    }

    @Test
    public void shouldThrownExceptionAlreadyUnholdWhenUnholdExistingUnholdOrderPromotion()
            throws Exception {
        thrown.expect(CampaignException.class);
        thrown.expectMessage(Errors.ORDER_PROMOTION_03.name());

        DateTime now = DateTime.now();

        orderPromotion.setHoldType(CampaignEnum.UNHOLD.getContent());
        when(orderPromotionRepo.findByOrderIdAndPromotionId(anyString(), anyLong())).thenReturn(orderPromotion);

        orderPromotionService.unholdOrderPromotion("1", 1L, now);

        verify(orderPromotionRepo).findByOrderIdAndPromotionId(anyString(), anyLong());
        verify(userPromotionService, never()).isUpdateExecuteTime(any(UserPromotionKey.class));
        verify(orderPromotionRepo, never()).saveAndFlush(any(OrderPromotion.class));
    }
}
