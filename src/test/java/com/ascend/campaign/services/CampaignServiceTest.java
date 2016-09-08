package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.PendingPromotion;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.models.CampaignResponse;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.VariantDuplicateFreebie;
import com.ascend.campaign.repositories.CampaignRepo;
import com.ascend.campaign.repositories.PendingPromotionItmRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.utils.DroolsUtil;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CampaignServiceTest {
    @Autowired
    CampaignService campaignService;

    @Mock
    CampaignRepo campaignRepo;

    @Mock
    PromotionItmRepo promotionItmRepo;

    @Mock
    DroolsUtil droolsUtil;

    @Mock
    PromotionUtil promotionUtil;

    @Mock
    PendingPromotionItmRepo pendingPromotionItmRepo;


    private Campaign campaign;
    private Campaign campaign2;
    private Promotion promotion;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        campaignService = new CampaignService(campaignRepo, promotionItmRepo,
                droolsUtil, promotionUtil, pendingPromotionItmRepo);

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("NAME1");

        campaign2 = new Campaign();
        campaign2.setId(2L);
        campaign2.setName("NAME2");

        promotion = new Promotion();
        promotion.setCampaign(campaign);
        promotion.setName("promotionName");


    }

    @Test
    public void shouldCreateNewCampaignITMSuccessfullyWhenCreateNonExistingCampaignInDb() {
        when(campaignRepo.saveAndFlush(any(Campaign.class))).thenReturn(campaign);
        Campaign campaignCreated = campaignService.createCampaign(campaign);

        assertThat(campaignCreated, notNullValue());

        verify(campaignRepo).saveAndFlush(any(Campaign.class));
    }

    @Test
    public void shouldReturnAllCampaignITMWhenGetAllExistingCampaignInDb() {
        List<Campaign> campaigns = Lists.newArrayList(campaign, campaign2);
        Page expectedPage = new PageImpl(campaigns);
        when(campaignRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);

        Page<Campaign> result = campaignService.getAllCampaignITM(
                1, 5, Sort.Direction.ASC, "id", null, null, null, null, null, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));

        verify(campaignRepo).findAll(any(), any(PageRequest.class));
    }


    @Test
    public void shouldReturnAllCampaignWithCriteriaWhenGetAllExistingCampaignInDb() {
        List<Campaign> campaigns = Lists.newArrayList(campaign, campaign2);
        Page expectedPage = new PageImpl(campaigns);
        when(campaignRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);

        Page<Campaign> result = campaignService.getAllCampaignITM(
                1, 5, Sort.Direction.ASC, "id", null, null, null, null, null, null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));

        verify(campaignRepo).findAll(any(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnCampaignWhenGetExistingITMCampaignByIdInDb() {
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        Promotion promotion = new Promotion();
        promotion.setStartPeriod(new Date());
        promotion.setEndPeriod(new Date());
        List<Promotion> promotionList = Arrays.asList(promotion);
        when(promotionItmRepo.findByCampaignId(anyLong())).thenReturn(promotionList);
        CampaignResponse campaignResponse = campaignService.getCampaignItm(1L);
        assertThat(campaignResponse.getId(), is(1L));

        verify(campaignRepo).findOne(anyLong());
        verify(promotionItmRepo).findByCampaignId(anyLong());
    }

    @Test(expected = CampaignNotFoundException.class)
    public void shouldReturnCampaignNotFoundExceptionWhenGetNonExistingITMCampaignByIdInDb() {
        when(campaignRepo.findOne(anyLong())).thenReturn(null);

        campaignService.getCampaignItm(1L);

        verify(campaignRepo).findOne(anyLong());
    }


    @Test
    public void shouldReturnCampaignUpdatedITMWhenUpdateExistingCampaignByIdInDb() throws Exception {
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        when(campaignRepo.saveAndFlush(any(Campaign.class))).thenReturn(campaign);

        assertThat(campaignService.updateCampaignItm(1L, campaign).getId(), is(1L));

        verify(campaignRepo).findOne(anyLong());
        verify(campaignRepo).saveAndFlush(any(Campaign.class));

    }

    @Test(expected = CampaignNotFoundException.class)
    public void shouldReturnCampaignNotFoundExceptionWhenUpdateNonExistingCampaignITM() throws Exception {
        when(campaignRepo.findOne(anyLong())).thenReturn(null);

        campaignService.updateCampaignItm(1L, campaign);

        verify(campaignRepo).findOne(anyLong());
        verify(campaignRepo, never()).saveAndFlush(any(Campaign.class));
    }

    @Test
    public void shouldDeleteCampaignITMWhenDeleteExistingCampaignByIdInDb() throws Exception {
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        doNothing().when(campaignRepo).delete(anyLong());

        assertThat(campaignService.deleteCampaignItm(1L).getId(), is(1L));

        verify(campaignRepo).findOne(anyLong());
    }

    @Test(expected = CampaignNotFoundException.class)
    public void shouldReturnCampaignNotFoundExceptionWhenDeleteNonExistingCampaignITM() throws Exception {
        when(campaignRepo.findOne(anyLong())).thenReturn(null);

        campaignService.deleteCampaignItm(1L);

        verify(campaignRepo).findOne(anyLong());
        verify(campaignRepo, never()).delete(anyLong());
    }

    @Test
    public void shouldDuplicateCampaignITMWhenDuplicateExistingCampaignById() {
        Promotion promotion = new Promotion();
        Promotion promotion2 = new Promotion();
        List<Promotion> promotionList = Arrays.asList(promotion, promotion2);
        when(campaignRepo.saveAndFlush(any(Campaign.class))).thenReturn(campaign2);
        when(promotionItmRepo.findByCampaignId(anyLong())).thenReturn(promotionList);
        when(promotionItmRepo.save(anyListOf(Promotion.class))).thenReturn(Arrays.asList(promotion, promotion2));

        Campaign duplicateCampaign = campaignService.duplicateCampaignItm(1L, campaign2);
        assertThat(duplicateCampaign.getId(), is(not(1L)));

        verify(campaignRepo).saveAndFlush(any(Campaign.class));
        verify(promotionItmRepo).findByCampaignId(anyLong());
        verify(promotionItmRepo).save(anyListOf(Promotion.class));
    }

    @Test
    public void shouldDuplicateCampaignITMWithPromotionAndPendingPromotionWhenDuplicateExistingCampaignById() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        Promotion promotion2 = new Promotion();
        promotion2.setId(2L);
        PendingPromotion pendingPromotion = new PendingPromotion();
        List<Promotion> promotionList = Arrays.asList(promotion, promotion2);
        pendingPromotion.setPromotionId(1L);
        when(campaignRepo.saveAndFlush(any(Campaign.class))).thenReturn(campaign2);
        when(promotionItmRepo.findByCampaignId(anyLong())).thenReturn(promotionList);
        when(promotionItmRepo.saveAndFlush(any(Promotion.class))).thenReturn(promotion);
        when(pendingPromotionItmRepo.findByCampaignId(anyLong())).thenReturn(Arrays.asList(pendingPromotion));
        when(promotionItmRepo.save(anyListOf(Promotion.class))).thenReturn(Arrays.asList(promotion, promotion2));

        Campaign duplicateCampaign = campaignService.duplicateCampaignItm(1L, campaign2);
        assertThat(duplicateCampaign.getId(), is(not(1L)));

        verify(campaignRepo).saveAndFlush(any(Campaign.class));
        verify(promotionItmRepo).saveAndFlush(any(Promotion.class));
        verify(promotionItmRepo).findByCampaignId(anyLong());
        verify(pendingPromotionItmRepo).findByCampaignId(anyLong());
        verify(promotionItmRepo).save(anyListOf(Promotion.class));
    }

    @Test
    public void shouldEnableCampaignITMWhenEnabledExistingCampaignById() {
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        campaign.setEnable(true);
        when(campaignRepo.saveAndFlush(any(Campaign.class))).thenReturn(campaign);

        Campaign enabledCampaign = campaignService.enableCampaignItm(1L);
        assertThat(enabledCampaign.getId(), is(1L));
        assertThat(enabledCampaign.getEnable(), is(true));

        verify(campaignRepo).findOne(anyLong());
        verify(campaignRepo).saveAndFlush(any(Campaign.class));
    }


    @Test
    public void shouldDisableCampaignITMWhenDisabledExistingCampaignById() {
        when(campaignRepo.findOne(anyLong())).thenReturn(campaign);
        campaign.setEnable(false);
        when(campaignRepo.saveAndFlush(any(Campaign.class))).thenReturn(campaign);

        Campaign disabledCampaign = campaignService.disableCampaignItm(1L);
        assertThat(disabledCampaign.getId(), is(1L));
        assertThat(disabledCampaign.getEnable(), is(false));

        verify(campaignRepo).findOne(anyLong());
        verify(campaignRepo).saveAndFlush(any(Campaign.class));
    }

    @Test
    public void shouldBooleanWhenWhenCheckCampaignNameDuplicateCorrectly() {
        Campaign campaign = new Campaign();
        Campaign campaign1 = new Campaign();
        List<Campaign> campaigns = Arrays.asList(campaign, campaign1);
        when(campaignRepo.findByName(anyString())).thenReturn(campaigns);

        Boolean result = campaignService.checkCampaignNameItm("test");
        assertThat(result, is(true));

        verify(campaignRepo).findByName(anyString());
    }

    @Test
    public void shouldBooleanWhenWhenCheckCampaignNameNotDuplicateCorrectly() {
        List<Campaign> campaigns = new ArrayList<>();
        when(campaignRepo.findByName(anyString())).thenReturn(campaigns);

        Boolean result = campaignService.checkCampaignNameItm("test");

        assertThat(result, is(false));
        verify(campaignRepo).findByName(anyString());
    }

    @Test
    public void shouldReturnVariantDuplicateFreebieWhenCheckDuplicateCriteriaFreebieAndFoundExistingDuplicate() {
        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setCriteriaType(CampaignEnum.VARIANT.getContent());
        promotionCondition.setCriteriaValue(Arrays.asList("freebie"));
        promotion.setPromotionCondition(promotionCondition);
        promotion.setType(PromotionTypeEnum.ITM_FREEBIE.getContent());
        promotion.setConditionData(JSONUtil.toString(promotionCondition));
        Date startPeriod = DateTime.now().toDate();
        Date endPeriod = DateTime.now().plusHours(2).toDate();
        promotion.setStartPeriod(startPeriod);
        promotion.setEndPeriod(endPeriod);
        when(promotionItmRepo.findByCampaignId(anyLong())).thenReturn(Arrays.asList(promotion));
        when(promotionItmRepo.findPromotionsByDateTime(anyString(), anyString())).thenReturn(Arrays.asList(promotion));

        List<VariantDuplicateFreebie> checkDuplicateCriteriaFreebie =
                campaignService.checkDuplicateCriteriaFreebie(1L, startPeriod.getTime(), endPeriod.getTime());
        assertThat(checkDuplicateCriteriaFreebie.size(), is(1));
        assertThat(checkDuplicateCriteriaFreebie.get(0).getVariantId(), is("freebie"));

        verify(promotionItmRepo).findByCampaignId(anyLong());
        verify(promotionItmRepo).findPromotionsByDateTime(anyString(), anyString());
    }

    @Test
    public void shouldReturnEmptyListWhenCheckDuplicateCriteriaFreebieAndNotFoundDuplicate() {
        Date startPeriod = DateTime.now().toDate();
        Date endPeriod = DateTime.now().plusHours(2).toDate();
        when(promotionItmRepo.findByCampaignId(anyLong())).thenReturn(new ArrayList<>());

        List<VariantDuplicateFreebie> checkDuplicateCriteriaFreebie =
                campaignService.checkDuplicateCriteriaFreebie(1L, startPeriod.getTime(), endPeriod.getTime());
        assertThat(checkDuplicateCriteriaFreebie.isEmpty(), is(true));

        verify(promotionItmRepo).findByCampaignId(anyLong());
    }

    @Test
    public void shouldReturnTrueWhenCheckCampaignNameEditAndFoundDuplicateCampaignName() {
        campaign.setId(1L);
        when(campaignRepo.findByName(anyString())).thenReturn(Arrays.asList(campaign, campaign));
        Boolean duplicateResult = campaignService.checkCampaignNameEditItm("campaignDup", 1L);

        assertThat(duplicateResult, is(true));

        verify(campaignRepo).findByName(anyString());
    }

    @Test
    public void shouldReturnFalseWhenCheckCampaignNameEditAndNotFoundDuplicateCampaignName() {
        when(campaignRepo.findByName(anyString())).thenReturn(Arrays.asList(campaign));
        Boolean duplicateResult = campaignService.checkCampaignNameEditItm("campaignDup", 1L);

        assertThat(duplicateResult, is(false));

        verify(campaignRepo).findByName(anyString());
    }
}
