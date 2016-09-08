package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionTask;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.exceptions.CampaignException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.VariantDuplicateFreebie;
import com.ascend.campaign.repositories.CampaignRepo;
import com.ascend.campaign.repositories.PendingPromotionItmRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.repositories.PromotionWMRepo;
import com.ascend.campaign.repositories.UserRepo;
import com.ascend.campaign.utils.DealUtil;
import com.ascend.campaign.utils.DecimalUtil;
import com.ascend.campaign.utils.JSONUtil;
import com.ascend.campaign.utils.PromotionUtil;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PromotionServiceTest {
    @Autowired
    PromotionService promotionService;

    @Mock
    PromotionItmRepo promotionItmRepo;

    @Mock
    PromotionWMRepo promotionWmRepo;

    @Mock
    UserRepo userRepo;

    @Mock
    UserPromotionService userPromotionService;

    @Mock
    ConfigurationService configurationService;

    @Mock
    CampaignRepo campaignRepo;

    @Mock
    DealUtil dealUtil;

    @Mock
    PendingPromotionItmRepo pendingPromotionItmRepo;

    DecimalUtil decimalUtil = new DecimalUtil();

    PromotionUtil promotionUtil = new PromotionUtil(decimalUtil);

    private Promotion promotion1;
    private Promotion promotion2;
    private PromotionWM promotion3;
    private PromotionWM promotion4;
    private PromotionTask promotionTask;
    private Campaign campaign;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        promotionService = new PromotionService(promotionItmRepo, promotionWmRepo, userRepo, userPromotionService,
                configurationService, promotionUtil, campaignRepo, pendingPromotionItmRepo);
        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("camp");

        promotion1 = new Promotion();
        promotion1.setId(1L);
        promotion1.setName("PROMOTION1_NAME");
        promotion1.setType("itm-freebie");
        promotion1.setConditionData("{\"setFlatDiscountData\":\"test1\"}");
        promotion1.setCampaign(campaign);
        PromotionCondition pc = new PromotionCondition();
        promotion1.setPromotionCondition(pc);

        promotion2 = new Promotion();
        promotion2.setId(2L);
        promotion2.setName("PROMOTION2_NAME");
        promotion2.setType("itm-bundle");
        promotion2.setConditionData("{\"setFlatDiscountData\":\"test2\"}");
        promotion2.setCampaign(campaign);

        promotion3 = new PromotionWM();
        promotion3.setId(1L);
        promotion3.setName("PROMOTION1_NAME");
        promotion3.setType("wm-freebie");
        promotion3.setConditionData("{\"setFlatDiscountData\":\"test1\"}");

        promotion4 = new PromotionWM();
        promotion4.setId(2L);
        promotion4.setName("PROMOTION2_NAME");
        promotion4.setType("wm-bundle");
        promotion4.setConditionData("{\"setFlatDiscountData\":\"test2\"}");

        promotionTask = new PromotionTask();
        promotionTask.setPromotionId(1L);
        promotionTask.setIsStart(false);

    }

    @Test
    public void shouldCreateNewPromotionITMSuccessfullyWhenCreateNonExistingPromotionInDb() {
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        Promotion promotionCreated = promotionService.createPromotionItruemart(promotion1);

        assertThat(promotionCreated, notNullValue());

        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(campaignRepo).findOne(anyLong());
    }

    @Test(expected = CampaignException.class)
    public void shouldReturnInvalidPromotionTypeWhenCreatePromotionWithInvalidType() {
        promotion1.setType("invalid");
        promotionService.createPromotionItruemart(promotion1);
    }

    @Test
    public void shouldCreateNewPromotionWMSuccessfullyWhenCreateNonExistingPromotionInDb() {
        when(promotionWmRepo.saveAndFlush(any(PromotionWM.class))).thenReturn(promotion3);

        PromotionWM promotionCreated = promotionService.createPromotionWemall(promotion3);

        assertThat(promotionCreated, notNullValue());

        verify(promotionWmRepo).saveAndFlush(any(PromotionWM.class));
    }

    @Test(expected = CampaignException.class)
    public void shouldReturnInvalidPromotionTypeWemallWhenCreatePromotionWithInvalidType() {
        promotion3.setType("invalid");
        promotionService.createPromotionWemall(promotion3);
    }


    @Test
    public void shouldReturnAllPromotionITMWhenGetAllExistingPromotionInDb() {
        List<Promotion> promotions = Lists.newArrayList(promotion1, promotion2);
        Page expectedPage = new PageImpl(promotions);

        when(promotionItmRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);

        Page<Promotion> result = promotionService.getAllPromotionsITM(
                1, 5, Sort.Direction.ASC, "id", null, null, null, null, null, false, null, "campaignName", null);

        assertThat(result.getTotalElements(), Matchers.not(0));

        verify(promotionItmRepo).findAll(any(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnAllPromotionWMWhenGetAllExistingPromotionInDb() {
        List<PromotionWM> promotions = Lists.newArrayList(promotion3, promotion4);
        Page expectedPage = new PageImpl(promotions);

        when(promotionWmRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);

        Page<PromotionWM> result = promotionService.getAllPromotionsWM(
                1, 5, Sort.Direction.ASC, "id", null, null, null, null, null);

        assertThat(result.getTotalElements(), Matchers.not(0));

        verify(promotionWmRepo).findAll(any(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnAllPromotionWMWithCriteriaWhenGetAllExistingPromotionInDb() {
        List<PromotionWM> promotions = Lists.newArrayList(promotion3, promotion4);
        promotion3.setLive(false);
        promotion4.setLive(false);
        Page expectedPage = new PageImpl(promotions);

        when(promotionWmRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);

        Page<PromotionWM> result = promotionService.getAllPromotionsWM(
                1, 5, Sort.Direction.ASC, "id", null, null, null, false, null);

        assertThat(result.getTotalElements(), Matchers.not(0));

        verify(promotionWmRepo).findAll(any(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnPromotionWhenGetExistingITMPromotionByIdInDb() {
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);

        assertThat(promotionService.getPromotionItm(1L).getId(), is(1L));

        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test(expected = PromotionNotFoundException.class)
    public void shouldReturnPromotionNotFoundExceptionWhenGetNonExistingITMPromotionByIdInDb() {
        when(promotionItmRepo.findOne(anyLong())).thenReturn(null);

        promotionService.getPromotionItm(1L);

        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnPromotionWhenGetExistingWMPromotionByIdInDb() {
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion3);

        assertThat(promotionService.getPromotionWm(1L).getId(), is(1L));

        verify(promotionWmRepo).findOne(anyLong());
    }

    @Test(expected = PromotionNotFoundException.class)
    public void shouldReturnPromotionNotFoundExceptionWhenGetNonExistingWMPromotionByIdInDb() {
        when(promotionWmRepo.findOne(anyLong())).thenReturn(null);

        promotionService.getPromotionWm(1L);

        verify(promotionWmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnLivePromotionWhenGetExistingITMPromotionById() {
        promotion1.setEnable(true);
        promotion1.setStartPeriod(DateTime.now().minusDays(1).toDate());
        promotion1.setEndPeriod(DateTime.now().plusDays(1).toDate());
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(dealUtil.isLive(anyBoolean(), anyObject(), anyObject())).thenReturn(true);

        assertThat(promotionService.getPromotionItm(1L).getLive(), is(true));

        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnLivePromotionWhenGetExistingWMPromotionById() {
        promotion3.setEnable(true);
        promotion3.setStartPeriod(DateTime.now().minusDays(1).toDate());
        promotion3.setEndPeriod(DateTime.now().plusDays(1).toDate());
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion3);
        when(dealUtil.isLive(anyBoolean(), anyObject(), anyObject())).thenReturn(true);

        assertThat(promotionService.getPromotionWm(1L).getLive(), is(true));

        verify(promotionWmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnNonLivePromotionWhenGetExistingITMPromotionById() {
        promotion1.setEnable(false);
        promotion1.setStartPeriod(DateTime.now().minusDays(1).toDate());
        promotion1.setEndPeriod(DateTime.now().plusDays(1).toDate());
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);

        assertThat(promotionService.getPromotionItm(1L).getLive(), is(false));

        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnNonLivePromotionWhenGetExistingWMPromotionById() {
        promotion3.setEnable(false);
        promotion3.setStartPeriod(DateTime.now().minusDays(1).toDate());
        promotion3.setEndPeriod(DateTime.now().plusDays(1).toDate());
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion3);

        assertThat(promotionService.getPromotionWm(1L).getLive(), is(false));

        verify(promotionWmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnPromotionUpdatedITMWhenUpdateExistingPromotionByIdInDb() throws Exception {
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);

        assertThat(promotionService.updatePromotionItm(1L, promotion1).getId(), is(1L));

        verify(promotionItmRepo).findOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(campaignRepo).findOne(anyLong());

    }

    @Test(expected = PromotionNotFoundException.class)
    public void shouldReturnPromotionNotFoundExceptionWhenUpdateNonExistingPromotionITM() throws Exception {
        when(promotionItmRepo.findOne(anyLong())).thenReturn(null);

        promotionService.updatePromotionItm(1L, promotion1);

        verify(promotionItmRepo).findOne(anyLong());
        verify(promotionItmRepo, never()).saveAndFlush(any(Promotion.class));
    }

    @Test
    public void shouldReturnPromotionUpdatedWMWhenUpdateExistingPromotionByIdInDb() throws Exception {
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion3);
        when(promotionWmRepo.saveAndFlush(any(PromotionWM.class))).thenReturn(promotion3);

        assertThat(promotionService.updatePromotionWm(1L, promotion3).getId(), is(1L));

        verify(promotionWmRepo).findOne(anyLong());
        verify(promotionWmRepo).saveAndFlush(any(PromotionWM.class));

    }

    @Test(expected = PromotionNotFoundException.class)
    public void shouldReturnPromotionNotFoundExceptionWhenUpdateNonExistingPromotionWM() throws Exception {
        when(promotionWmRepo.findOne(anyLong())).thenReturn(null);

        promotionService.updatePromotionWm(1L, promotion3);

        verify(promotionWmRepo).findOne(anyLong());
        verify(promotionWmRepo, never()).saveAndFlush(any(PromotionWM.class));
    }

    @Test
    public void shouldDeletePromotionITMWhenDeleteExistingPromotionByIdInDb() throws Exception {
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        doNothing().when(promotionItmRepo).delete(anyLong());

        assertThat(promotionService.deletePromotionItm(1L).getId(), is(1L));

        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test(expected = PromotionNotFoundException.class)
    public void shouldReturnPromotionNotFoundExceptionWhenDeleteNonExistingPromotionITM() throws Exception {
        when(promotionItmRepo.findOne(anyLong())).thenReturn(null);

        promotionService.deletePromotionItm(1L);

        verify(promotionItmRepo).findOne(anyLong());
        verify(promotionItmRepo, never()).delete(anyLong());
    }

    @Test
    public void shouldDeletePromotionWMWhenDeleteExistingPromotionByIdInDb() throws Exception {
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion3);
        doNothing().when(promotionWmRepo).delete(anyLong());

        assertThat(promotionService.deletePromotionWm(1L).getId(), is(1L));

        verify(promotionWmRepo).findOne(anyLong());
    }

    @Test(expected = PromotionNotFoundException.class)
    public void shouldReturnPromotionNotFoundExceptionWhenDeleteNonExistingPromotionWM() throws Exception {
        when(promotionWmRepo.findOne(anyLong())).thenReturn(null);

        promotionService.deletePromotionWm(1L);

        verify(promotionWmRepo).findOne(anyLong());
        verify(promotionWmRepo, never()).delete(anyLong());
    }

    @Test
    public void shouldDuplicatePromotionITMWhenDuplicateExistingPromotionById() {
        when(promotionItmRepo.getOne(anyLong())).thenReturn(promotion1);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion2);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);

        Promotion duplicatePromotion = promotionService.duplicatePromotionItm(1L);
        assertThat(duplicatePromotion.getId(), is(not(1L)));

        verify(promotionItmRepo).getOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(campaignRepo).findOne(anyLong());
    }

    @Test
    public void shouldDuplicatePromotionWmWhenDuplicateExistingPromotionById() {
        when(promotionWmRepo.getOne(anyLong())).thenReturn(promotion3);
        when(promotionWmRepo.saveAndFlush(any(PromotionWM.class))).thenReturn(promotion4);

        PromotionWM duplicatePromotion = promotionService.duplicatePromotionWm(1L);
        assertThat(duplicatePromotion.getId(), is(not(1L)));

        verify(promotionWmRepo).getOne(anyLong());
        verify(promotionWmRepo).saveAndFlush(any(PromotionWM.class));
    }

    @Test
    public void shouldEnablePromotionITMWhenEnabledExistingPromotionById() {
        when(promotionItmRepo.getOne(anyLong())).thenReturn(promotion1);

        promotion1.setEnable(true);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);

        Promotion duplicatePromotion = promotionService.enablePromotionItm(1L);
        assertThat(duplicatePromotion.getId(), is(1L));
        assertThat(duplicatePromotion.getEnable(), is(true));

        verify(promotionItmRepo).getOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
    }

    @Test
    public void shouldEnablePromotionWMWhenEnabledExistingPromotionById() {
        when(promotionWmRepo.getOne(anyLong())).thenReturn(promotion3);

        promotion3.setEnable(true);
        when(promotionWmRepo.saveAndFlush(any(PromotionWM.class))).thenReturn(promotion3);

        PromotionWM duplicatePromotion = promotionService.enablePromotionWm(1L);
        assertThat(duplicatePromotion.getId(), is(1L));
        assertThat(duplicatePromotion.getEnable(), is(true));

        verify(promotionWmRepo).getOne(anyLong());
        verify(promotionWmRepo).saveAndFlush(any(PromotionWM.class));
    }

    @Test
    public void shouldDisablePromotionITMWhenDisabledExistingPromotionById() {
        when(promotionItmRepo.getOne(anyLong())).thenReturn(promotion1);

        promotion1.setEnable(false);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);

        Promotion duplicatePromotion = promotionService.disablePromotionItm(1L);
        assertThat(duplicatePromotion.getId(), is(1L));
        assertThat(duplicatePromotion.getEnable(), is(false));

        verify(promotionItmRepo).getOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
    }

    @Test
    public void shouldDisablePromotionWMWhenDisabledExistingPromotionById() {
        when(promotionWmRepo.getOne(anyLong())).thenReturn(promotion3);

        promotion3.setEnable(false);
        when(promotionWmRepo.saveAndFlush(any(PromotionWM.class))).thenReturn(promotion3);

        PromotionWM duplicatePromotion = promotionService.disablePromotionWm(1L);
        assertThat(duplicatePromotion.getId(), is(1L));
        assertThat(duplicatePromotion.getEnable(), is(false));

        verify(promotionWmRepo).getOne(anyLong());
        verify(promotionWmRepo).saveAndFlush(any(PromotionWM.class));
    }

    @Test
    public void shouldReturnAllEnablePromotionWhenGetAllExistingEnabledITMPromotionInDb() {
        promotion1.setEnable(true);
        promotion2.setEnable(true);
        Campaign campaign = new Campaign();
        campaign.setEnable(true);
        promotion1.setCampaign(campaign);
        promotion1.setEndPeriod(DateTime.now().plusMinutes(1).toDate());

        promotion2.setCampaign(campaign);
        promotion2.setEndPeriod(DateTime.now().plusMinutes(5).toDate());
        List<Promotion> promotions = Lists.newArrayList(promotion1, promotion2);

        when(promotionItmRepo.findByEnable(anyBoolean())).thenReturn(promotions);

        assertThat(promotionService.getAllActivePromotionBusinessChannelItm().size(), is(2));

        verify(promotionItmRepo).findByEnable(anyBoolean());
    }

    @Test
    public void shouldReturnAllEnablePromotionWhenGetAllExistingEnabledWMPromotionInDb() {
        promotion3.setEnable(true);
        promotion4.setEnable(true);
        List<PromotionWM> promotions = Lists.newArrayList(promotion3, promotion4);

        when(promotionWmRepo.findByEnable(anyBoolean())).thenReturn(promotions);

        assertThat(promotionService.getAllActivePromotionBusinessChannelWm().size(), is(2));

        verify(promotionWmRepo).findByEnable(anyBoolean());
    }

    @Test
    public void shouldReturnCheckDuplicateFreebieWhenGetCheckDuplicateFreebieByVariantsAndPromotionPeriod()
            throws Exception {

        final String variants = "variant1,variant2";
        final Long startPeriod = DateTime.now().minusMinutes(5).getMillis();
        final Long endPeriod = DateTime.now().plusMinutes(5).getMillis();

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setCriteriaType(CampaignEnum.VARIANT.getContent());
        promotionCondition.setCriteriaValue(Collections.singletonList("variant1"));

        Promotion promotionFreebie = new Promotion();
        promotionFreebie.setId(1L);
        promotionFreebie.setType(PromotionTypeEnum.ITM_FREEBIE.getContent());
        promotionFreebie.setStartPeriod(DateTime.now().minusMinutes(10).toDate());
        promotionFreebie.setEndPeriod(DateTime.now().plusMinutes(10).toDate());
        promotionFreebie.setConditionData(JSONUtil.toString(promotionCondition));

        when(promotionItmRepo.findPromotionsByDateTime(anyString(), anyString()))
                .thenReturn(Collections.singletonList(promotionFreebie));

        List<VariantDuplicateFreebie> variantDuplicateFreebies = promotionService.checkDuplicateCriteriaFreebie(
                variants, startPeriod, endPeriod, null);

        assertTrue(variantDuplicateFreebies.get(0).getDuplicatePromotionId().contains(1L));
        assertThat(variantDuplicateFreebies.get(0).getVariantId(), is("variant1"));

        verify(promotionItmRepo).findPromotionsByDateTime(anyString(), anyString());


    }
}
