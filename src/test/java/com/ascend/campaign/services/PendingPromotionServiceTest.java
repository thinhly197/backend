package com.ascend.campaign.services;

import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionTask;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.repositories.CampaignRepo;
import com.ascend.campaign.repositories.PendingPromotionItmRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.repositories.UserRepo;
import com.ascend.campaign.utils.DealUtil;
import com.ascend.campaign.utils.DecimalUtil;
import com.ascend.campaign.utils.JSONUtil;
import com.ascend.campaign.utils.PromotionUtil;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PendingPromotionServiceTest {
    @Autowired
    PendingPromotionService pendingPromotionService;

    @Mock
    PromotionItmRepo promotionItmRepo;


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
    private PendingPromotion pendingPromotion;
    private PendingPromotion pendingPromotion2;
    private Campaign campaign;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pendingPromotionService = new PendingPromotionService(pendingPromotionItmRepo, campaignRepo, promotionUtil,
                promotionItmRepo);
        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("camp");

        promotion1 = new Promotion();
        promotion1.setId(1L);
        promotion1.setName("PROMOTION1_NAME");
        promotion1.setType("itm-freebie");
        promotion1.setConditionData("{\"setFlatDiscountData\":\"test1\"}");
        promotion1.setCampaign(campaign);
        promotion1.setMember(true);
        promotion1.setRepeat(1);
        promotion1.setEnable(true);
        promotion1.setStartPeriod(new DateTime(2016, 1, 1, 10, 0, 0, 0).toDate());
        promotion1.setEndPeriod(new DateTime(2016, 1, 1, 12, 0, 0, 0).toDate());

        promotion2 = new Promotion();
        promotion2.setId(2L);
        promotion2.setName("PROMOTION2_NAME");
        promotion2.setType("itm-bundle");
        promotion2.setConditionData("{\"setFlatDiscountData\":\"test2\"}");
        promotion2.setCampaign(campaign);
        promotion2.setStartPeriod(new DateTime(2016, 1, 1, 8, 0, 0, 0).toDate());
        promotion2.setEndPeriod(new DateTime(2016, 1, 1, 9, 0, 0, 0).toDate());

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

        pendingPromotion = new PendingPromotion();
        pendingPromotion2 = new PendingPromotion();

    }

    @Test
    public void shouldCreateNewPromotionITMSuccessfullyWhenCreateNonExistingPromotionInDb() {
        PromotionCondition promotionCondition = new PromotionCondition();
        promotion1.setPromotionCondition(promotionCondition);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        Promotion promotionCreated = pendingPromotionService.createPromotionItruemart(promotion1);

        assertThat(promotionCreated, notNullValue());

        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(campaignRepo).findOne(anyLong());
    }

    @Test
    public void shouldCreateNewPromotionAndPendingPromotionITMSuccessfullyWhenCreateNonExistingPromotionInDb() {
        final PromotionCondition promotionCondition = new PromotionCondition();
        promotion1.setStartPeriod(DateTime.now().plusHours(1).toDate());
        promotion1.setEndPeriod(DateTime.now().plusHours(2).toDate());
        promotion1.setPromotionCondition(promotionCondition);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(pendingPromotionItmRepo.saveAndFlush(any(PendingPromotion.class))).thenReturn(pendingPromotion);
        Promotion promotionCreated = pendingPromotionService.createPromotionItruemart(promotion1);

        assertThat(promotionCreated, notNullValue());

        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(campaignRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnPendingPromotionListWhenGetExistingPromotionInDb() {
        List<PendingPromotion> pendingPromotions = Arrays.asList(pendingPromotion, pendingPromotion2);
        when(pendingPromotionItmRepo.findAll()).thenReturn(pendingPromotions);

        List<PendingPromotion> expect = pendingPromotionService.getAllPromotionDrools();
        assertThat(expect, Matchers.not(0));
        verify(pendingPromotionItmRepo).findAll();
    }

    @Test
    public void shouldDeletePendingPromotionListWhenDeleteExistingPromotionInDb() {
        pendingPromotion.setId(1L);
        when(pendingPromotionItmRepo.findOne(anyLong())).thenReturn(pendingPromotion);
        doNothing().when(pendingPromotionItmRepo).delete(anyLong());

        pendingPromotionService.deletePendingPromotion(1L);

        verify(pendingPromotionItmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnPromotionWhenUpdatePromotionAndConditionDataNotChangeCorrectly() {
        pendingPromotion.setPromotionId(1L);
        promotion1.setId(1L);
        promotion1.setPromotionCondition(new PromotionCondition());
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);

        assertThat(pendingPromotionService.updatePromotionItm(1L, promotion1).getId(), is(1L));

        verify(promotionItmRepo).findOne(anyLong());
        verify(campaignRepo).findOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));

    }

    @Test
    public void shouldReturnPromotionWhenUpdateNotExpirePromotionToExpiredPromotionCorrectly() {
        pendingPromotion.setPromotionId(1L);
        promotion1.setId(1L);
        promotion1.setStartPeriod(DateTime.now().plusHours(1).toDate());
        promotion1.setEndPeriod(DateTime.now().plusHours(21).toDate());
        promotion2.setStartPeriod(DateTime.now().minusHours(2).toDate());
        promotion2.setEndPeriod(DateTime.now().minusHours(1).toDate());

        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(pendingPromotionItmRepo.findByPromotionId(anyLong())).thenReturn(pendingPromotion);
        doNothing().when(pendingPromotionItmRepo).delete(anyLong());
        assertThat(pendingPromotionService.updatePromotionItm(1L, promotion2).getId(), is(1L));

        verify(promotionItmRepo).findOne(anyLong());
        verify(campaignRepo).findOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(pendingPromotionItmRepo).findByPromotionId(anyLong());

    }

    @Test
    public void shouldReturnPromotionWhenUpdateLivePromotionWhenExistingPendingPromotionPromotionCorrectly() {
        pendingPromotion.setPromotionId(1L);
        promotion1.setId(1L);
        promotion1.setStartPeriod(DateTime.now().plusHours(1).toDate());
        promotion1.setEndPeriod(DateTime.now().plusHours(21).toDate());
        promotion2.setStartPeriod(DateTime.now().plusHours(1).toDate());
        promotion2.setEndPeriod(DateTime.now().plusHours(21).toDate());
        promotion2.setEnable(true);

        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(pendingPromotionItmRepo.findByPromotionId(anyLong())).thenReturn(pendingPromotion);

        assertThat(pendingPromotionService.updatePromotionItm(1L, promotion2).getId(), is(1L));

        verify(promotionItmRepo).findOne(anyLong());
        verify(campaignRepo).findOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(pendingPromotionItmRepo).findByPromotionId(anyLong());
    }

    @Test
    public void shouldReturnPromotionWhenUpdateLivePromotionAndNotRequestBuildPromotionCorrectly() {
        final Date startPeriod = DateTime.now().plusHours(1).toDate();
        final Date endPeriod = DateTime.now().plusHours(2).toDate();
        pendingPromotion.setPromotionId(1L);
        promotion1.setId(1L);
        promotion1.setEnable(true);
        promotion1.setRepeat(2);
        promotion1.setMember(true);
        promotion1.setStartPeriod(startPeriod);
        promotion1.setConditionData(JSONUtil.toString(new PromotionCondition()));
        promotion1.setPromotionCondition(new PromotionCondition());
        promotion1.setEndPeriod(endPeriod);
        promotion2.setStartPeriod(startPeriod);
        promotion2.setEndPeriod(endPeriod);
        promotion2.setEnable(true);
        promotion2.setRepeat(2);
        promotion2.setMember(true);
        promotion2.setConditionData(JSONUtil.toString(new PromotionCondition()));
        promotion2.setPromotionCondition(new PromotionCondition());

        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(pendingPromotionItmRepo.findByPromotionId(anyLong())).thenReturn(pendingPromotion);

        assertThat(pendingPromotionService.updatePromotionItm(1L, promotion2).getId(), is(1L));

        verify(promotionItmRepo).findOne(anyLong());
        verify(campaignRepo).findOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(pendingPromotionItmRepo).findByPromotionId(anyLong());
    }


    @Test
    public void shouldReturnPromotionWhenUpdatePromotionDisabledAndConditionDataCorrectly() {
        pendingPromotion.setPromotionId(1L);
        pendingPromotion.setId(1L);
        pendingPromotion.setName("testUpdate");
        promotion2.setName("testUpdate");
        promotion1.setId(1L);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(pendingPromotionItmRepo.findByPromotionId(anyLong())).thenReturn(pendingPromotion);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        doNothing().when(pendingPromotionItmRepo).delete(anyLong());
        assertThat(pendingPromotionService.updatePromotionItm(1L, promotion2).getName(),
                is(pendingPromotion.getName()));

        verify(promotionItmRepo).findOne(anyLong());
        verify(campaignRepo).findOne(anyLong());
        verify(pendingPromotionItmRepo).findByPromotionId(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));

    }

    @Test
    public void shouldUpdatePromotionToPendingPromotionWhenExistingPromotionInPendingPromotion() {
        pendingPromotion.setPromotionId(1L);
        pendingPromotion.setId(1L);
        pendingPromotion.setName("testUpdate");
        promotion2.setName("testUpdate");

        pendingPromotion.setPromotionId(1L);
        promotion1.setId(1L);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(pendingPromotionItmRepo.findByPromotionId(anyLong())).thenReturn(pendingPromotion);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        doNothing().when(pendingPromotionItmRepo).delete(anyLong());

        assertThat(pendingPromotionService.updatePromotionItm(1L, promotion2).getName(),
                is(pendingPromotion.getName()));

        verify(promotionItmRepo).findOne(anyLong());
        verify(campaignRepo).findOne(anyLong());
        verify(pendingPromotionItmRepo).findByPromotionId(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));

    }

    @Test
    public void shouldReturnPromotionWhenDuplicatePromotion() {
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        pendingPromotion.setName("testDuplicate");
        pendingPromotion.setPromotionId(1L);
        when(pendingPromotionItmRepo.saveAndFlush(any(PendingPromotion.class))).thenReturn(pendingPromotion);

        assertThat(pendingPromotionService.duplicatePromotionItm(1L).getName(), is(pendingPromotion.getName()));
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(pendingPromotionItmRepo).saveAndFlush(any(PendingPromotion.class));
        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test
    public void shouldDeletePromotionWhenDeletePromotion() {
        pendingPromotion.setName("testDelete");
        pendingPromotion.setPromotionId(1L);

        when(pendingPromotionItmRepo.findByPromotionId(anyLong())).thenReturn(pendingPromotion);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(pendingPromotionItmRepo.saveAndFlush(any(PendingPromotion.class))).thenReturn(pendingPromotion);

        assertThat(pendingPromotionService.deletePromotionItm(1L).getName(), is(pendingPromotion.getName()));

        verify(pendingPromotionItmRepo).findByPromotionId(anyLong());
        verify(promotionItmRepo).findOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(pendingPromotionItmRepo).saveAndFlush(any(PendingPromotion.class));
    }

    @Test
    public void shouldDeletePromotionWhenNotExistPromotionInPendingPromotion() {
        pendingPromotion.setName("testDelete");
        pendingPromotion.setPromotionId(2L);

        when(pendingPromotionItmRepo.findByPromotionId(anyLong())).thenReturn(new PendingPromotion());
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion1);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion1);
        when(pendingPromotionItmRepo.saveAndFlush(any(PendingPromotion.class))).thenReturn(pendingPromotion);

        assertThat(pendingPromotionService.deletePromotionItm(1L).getName(), is(pendingPromotion.getName()));

        verify(pendingPromotionItmRepo).findByPromotionId(anyLong());
        verify(promotionItmRepo).findOne(anyLong());
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(pendingPromotionItmRepo).saveAndFlush(any(PendingPromotion.class));
    }
}
