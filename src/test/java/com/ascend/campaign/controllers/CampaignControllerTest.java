package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.Campaign;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.exceptions.CampaignNotFoundException;
import com.ascend.campaign.exceptions.DeleteException;
import com.ascend.campaign.exceptions.DuplicateException;
import com.ascend.campaign.exceptions.PromotionNotFoundException;
import com.ascend.campaign.models.CampaignResponse;
import com.ascend.campaign.models.VariantDuplicateFreebie;
import com.ascend.campaign.services.CampaignService;
import com.ascend.campaign.services.DroolsService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CampaignControllerTest {
    @InjectMocks
    CampaignController controller;

    MockMvc mvc;

    @Mock
    CampaignService campaignService;

    @Mock
    DroolsService droolsService;

    private Campaign campaign;
    private Campaign campaign2;
    private Promotion promotion;
    private Promotion promotion2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("name");
        campaign.setNameTranslation("name_tran");

        campaign2 = new Campaign();
        campaign2.setId(2L);
        campaign2.setName("name");
        campaign2.setNameTranslation("name_tran");

        promotion = new Promotion();
        promotion.setId(1L);

        promotion2 = new Promotion();
        promotion2.setId(2L);
    }

    @Test
    public void shouldCreateCampaignWhenCreateNewCampaignSuccessfully() throws Exception {
        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaign);

        mvc.perform(post("/api/v1/itm/campaigns")
                .content("{\"name\":\"name\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.name", is(campaign.getName())))
                .andExpect(status().isCreated());

        verify(campaignService).createCampaign(any(Campaign.class));
    }

    @Test
    public void shouldCreateCampaignWhenCreateNewCampaignAndCheckNameSuccessfully() throws Exception {
        when(campaignService.createCampaign(any(Campaign.class))).thenReturn(campaign);
        when(campaignService.checkCampaignNameItm(anyString())).thenReturn(false);

        mvc.perform(post("/api/v1/itm/campaigns?check_name=true")
                .content("{\"name\":\"name\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.name", is(campaign.getName())))
                .andExpect(status().isCreated());

        verify(campaignService).createCampaign(any(Campaign.class));
        verify(campaignService).checkCampaignNameItm(anyString());
    }

    @Test
    public void shouldCreateCampaignWhenCreateNewCampaignAndCheckNameNotSuccessfully() throws Exception {
        when(campaignService.checkCampaignNameItm(anyString())).thenReturn(true);

        mvc.perform(post("/api/v1/itm/campaigns?check_name=true")
                .content("{\"name\":\"name\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(campaignService).checkCampaignNameItm(anyString());
    }


    @Test
    public void shouldReturnCampaignListWhenGetAllExistingCampaign() throws Exception {
        List<Campaign> campaigns = Lists.newArrayList(campaign, campaign2);

        when(campaignService.getAllCampaignITM(
                anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(),
                anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong(),
                anyLong(), anyBoolean())).thenReturn(new PageImpl(campaigns));

        mvc.perform(get("/api/v1/itm/campaigns"))
                .andExpect(jsonPath("$.data.content[0].name", is(campaign.getName())))
                .andExpect(jsonPath("$.data.content[1].name", is(campaign2.getName())))
                .andExpect(status().isOk());

        verify(campaignService).getAllCampaignITM(
                anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(),
                anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void shouldReturnPromotionWhenGetExistingPromotionById() throws Exception {
        CampaignResponse campaignResponse = new CampaignResponse();
        campaignResponse.setId(campaign.getId());
        campaignResponse.setName(campaign.getName());
        when(campaignService.getCampaignItm(anyLong())).thenReturn(campaignResponse);

        mvc.perform(get("/api/v1/itm/campaigns/1"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is(campaign.getName())))
                .andExpect(status().isOk());

        verify(campaignService).getCampaignItm(anyLong());
    }

    @Test
    public void shouldReturnCampaignUpdatedWhenUpdateExistingCampaignById() throws Exception {
        campaign.setName("UPDATE1");

        when(campaignService.updateCampaignItm(anyLong(), any(Campaign.class))).thenReturn(campaign);

        mvc.perform(put("/api/v1/itm/campaigns/1")
                .content("{\"name\":\"UPDATE1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is(campaign.getName())))
                .andExpect(status().isOk());

        verify(campaignService).updateCampaignItm(anyLong(), any(Campaign.class));
    }


    @Test
    public void shouldReturnCampaignUpdatedWhenUpdateExistingCampaignByIdAndCheckName() throws Exception {
        campaign.setName("UPDATE1");

        when(campaignService.updateCampaignItm(anyLong(), any(Campaign.class))).thenReturn(campaign);

        mvc.perform(put("/api/v1/itm/campaigns/1")
                .content("{\"name\":\"UPDATE1\"}")
                .param("check_name", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is(campaign.getName())))
                .andExpect(status().isOk());

        verify(campaignService).updateCampaignItm(anyLong(), any(Campaign.class));
    }

    @Test
    public void shouldReturnErrorMessageWhenUpdateExistingDuplicateNameCampaignByIdAndCheckName() throws Exception {
        campaign.setName("UPDATE1");
        when(campaignService.checkCampaignNameEditItm(anyString(), anyLong())).thenReturn(true);

        mvc.perform(put("/api/v1/itm/campaigns/1")
                .content("{\"name\":\"UPDATE1\"}")
                .param("check_name", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", is("Duplicate Name !!")))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(status().isBadRequest());

        verify(campaignService).checkCampaignNameEditItm(anyString(), anyLong());
    }

    @Test
    public void shouldReturnNotFoundWhenUpdateNonExistingCampaignById() throws Exception {
        doThrow(CampaignNotFoundException.class).when(campaignService)
                .updateCampaignItm(anyLong(), any(Campaign.class));

        mvc.perform(put("/api/v1/itm/campaigns/1")
                .content("{\"name\":\"UPDATE1\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(campaignService).updateCampaignItm(anyLong(), any(Campaign.class));
    }

    @Test
    public void shouldDeleteCampaignWhenDeleteExistingCampaignById() throws Exception {
        when(campaignService.deleteCampaignItm(anyLong())).thenReturn(campaign);

        mvc.perform(delete("/api/v1/itm/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(status().isOk());

        verify(campaignService).deleteCampaignItm(anyLong());
    }

    @Test
    public void shouldReturnNotFoundWhenDeleteNonExistingCampaignById() throws Exception {
        doThrow(CampaignNotFoundException.class).when(campaignService).deleteCampaignItm(anyLong());

        mvc.perform(delete("/api/v1/itm/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(campaignService).deleteCampaignItm(anyLong());
    }

    @Test
    public void shouldReturnDeleteWhenDeleteNonExistingCampaignById() throws Exception {
        doThrow(DeleteException.class).when(campaignService).deleteCampaignItm(anyLong());

        mvc.perform(delete("/api/v1/itm/campaigns/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(campaignService).deleteCampaignItm(anyLong());
    }

    @Test
    public void shouldDuplicateCampaignsWhenDuplicateExistingCampaignById() throws Exception {
        when(campaignService.duplicateCampaignItm(anyLong(), any(Campaign.class))).thenReturn(campaign2);

        mvc.perform(post("/api/v1/itm/campaigns/1/duplication")
                .content("{\"name\":\"dup\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(2)))
                .andExpect(status().isOk());

        verify(campaignService).duplicateCampaignItm(anyLong(), any(Campaign.class));
    }

    @Test
    public void shouldReturnDuplicateExceptionWhenDuplicateExistingCampaignByIdAndSomethingWrong() throws Exception {
        doThrow(DuplicateException.class).when(campaignService).duplicateCampaignItm(anyLong(), any(Campaign.class));

        mvc.perform(post("/api/v1/itm/campaigns/1/duplication")
                .content("{\"name\":\"dup\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Duplicate failed !!")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void shouldReturnCreateCampaignSuccessfullyWhenDuplicateExistingCampaignByIdAndCheckName() throws Exception {
        when(campaignService.duplicateCampaignItm(anyLong(), any(Campaign.class))).thenReturn(campaign2);

        mvc.perform(post("/api/v1/itm/campaigns/1/duplication")
                .content("{\"name\":\"dup\"}")
                .param("check_name", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(2)))
                .andExpect(status().isCreated());

        verify(campaignService).duplicateCampaignItm(anyLong(), any(Campaign.class));
    }

    @Test
    public void shouldReturnErrorMessageWhenDuplicateExistingDuplicateNameCampaignByIdAndCheckName() throws Exception {
        when(campaignService.checkCampaignNameItm(anyString())).thenReturn(true);

        mvc.perform(post("/api/v1/itm/campaigns/1/duplication")
                .content("{\"name\":\"UPDATE1\"}")
                .param("check_name", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", is("Duplicate Name !!")))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(status().isBadRequest());

        verify(campaignService).checkCampaignNameItm(anyString());
    }


    @Test
    public void shouldDuplicateCampaignsWhenCheckNameDuplicateExistingCampaignById() throws Exception {
        when(campaignService.duplicateCampaignItm(anyLong(), any(Campaign.class))).thenReturn(campaign2);

        mvc.perform(post("/api/v1/itm/campaigns/1/duplication")
                .content("{\"name\":\"dup\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(2)))
                .andExpect(status().isOk());

        verify(campaignService).duplicateCampaignItm(anyLong(), any(Campaign.class));
    }

    @Test
    public void shouldEnableCampaignWhenEnabledExistingCampaignById() throws Exception {
        campaign.setEnable(true);

        when(campaignService.enableCampaignItm(anyLong())).thenReturn(campaign);
        //doNothing().when(droolsService).buildDrlITM(anyBoolean());

        mvc.perform(put("/api/v1/itm/campaigns/1/enabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(true)))
                .andExpect(status().isOk());

        verify(campaignService).enableCampaignItm(anyLong());
    }

    @Test
    public void shouldDisableCampaignWhenDisabledExistingCampaignById() throws Exception {
        campaign.setEnable(false);

        when(campaignService.disableCampaignItm(anyLong())).thenReturn(campaign);
        //doNothing().when(droolsService).buildDrlITM(anyBoolean());

        mvc.perform(put("/api/v1/itm/campaigns/1/disabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(false)))
                .andExpect(status().isOk());

        verify(campaignService).disableCampaignItm(anyLong());
    }

    @Test
    public void shouldBatchEnableCampaignWhenEnabledExistingCampaignById() throws Exception {
        campaign.setEnable(true);

        when(campaignService.enableCampaignItm(anyLong())).thenReturn(campaign);
        // doNothing().when(droolsService).buildDrlITM(anyBoolean());

        mvc.perform(put("/api/v1/itm/campaigns/enabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(campaignService, times(4)).enableCampaignItm(anyLong());
    }

    @Test
    public void shouldBatchDisableCampaignWhenDisabledExistingCampaignById() throws Exception {
        campaign.setEnable(false);

        when(campaignService.disableCampaignItm(anyLong())).thenReturn(campaign);
        //doNothing().when(droolsService).buildDrlITM(anyBoolean());

        mvc.perform(put("/api/v1/itm/campaigns/disabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(campaignService, times(4)).disableCampaignItm(anyLong());
    }


    @Test
    public void shouldPromotionListWhenGetExistingPromotionListByCampaignById() throws Exception {
        promotion.setName("name1");
        promotion2.setName("name2");
        List<Promotion> promotionList = Arrays.asList(promotion, promotion2);

        when(campaignService.getCampaignPromotion(
                anyLong(), anyInt(), anyInt(), any(Sort.Direction.class), anyString(),
                anyLong(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString()))
                .thenReturn(new PageImpl(promotionList));
        //doNothing().when(droolsService).buildDrlITM(anyBoolean());

        mvc.perform(get("/api/v1/itm/campaigns/1/promotions"))
                .andExpect(jsonPath("$.data.content[0].name_local", is(promotion.getName())))
                .andExpect(jsonPath("$.data.content[1].name_local", is(promotion2.getName())))
                .andExpect(status().isOk());

        verify(campaignService).getCampaignPromotion(
                anyLong(), anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(),
                anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString());
    }

    @Test
    public void shouldReturnListVariantDuplicateFreebieWhenCheckFreeBieCampaignDuplicateAndFoundFreeBieDuplicate()
            throws Exception {
        VariantDuplicateFreebie variantDuplicateFreebie = new VariantDuplicateFreebie();
        variantDuplicateFreebie.setDuplicatePromotionId(Arrays.asList(1L, 2L, 3L));
        variantDuplicateFreebie.setVariantId("VA1");
        when(campaignService.checkDuplicateCriteriaFreebie(anyLong(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList(variantDuplicateFreebie));

        mvc.perform(get("/api/v1/itm/campaigns/1/isFreebieCriteriaDuplicate")
                .param("start_period", "1001200099")
                .param("end_period", "10012000193"))
                .andExpect(jsonPath("$.data[0].variant_id", is("VA1")))
                .andExpect(status().isOk());

        verify(campaignService).checkDuplicateCriteriaFreebie(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void shouldReturnErrorMessageWhenDuplicateExistingCampaignByIdAndSomeThingWrongWhenGetPromotionInDb()
            throws Exception {
        campaign.setName("UPDATE1");
        doThrow(PromotionNotFoundException.class).when(campaignService)
                .duplicateCampaignItm(anyLong(), any(Campaign.class));
        mvc.perform(post("/api/v1/itm/campaigns/1/duplication")
                .content("{\"name\":\"dup\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Promotion not found !!")))
                .andExpect(status().isNotFound());


    }
}