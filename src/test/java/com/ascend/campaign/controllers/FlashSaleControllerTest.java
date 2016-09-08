package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.AppId;
import com.ascend.campaign.entities.FlashSale;
import com.ascend.campaign.entities.FlashSaleProduct;
import com.ascend.campaign.entities.FlashSaleVariant;
import com.ascend.campaign.exceptions.FlashSaleNotFoundException;
import com.ascend.campaign.exceptions.PolicyNotFoundException;
import com.ascend.campaign.exceptions.WowBannerException;
import com.ascend.campaign.exceptions.WowExtraProductNotFoundException;
import com.ascend.campaign.models.FlashSaleCondition;
import com.ascend.campaign.models.FlashSaleProductAvailable;
import com.ascend.campaign.models.FlashSaleVariantAvailable;
import com.ascend.campaign.models.Policy;
import com.ascend.campaign.models.WowBannerProduct;
import com.ascend.campaign.models.WowBannerProductResponse;
import com.ascend.campaign.models.WowExtra;
import com.ascend.campaign.models.WowExtraProduct;
import com.ascend.campaign.services.FlashSaleService;
import com.ascend.campaign.utils.JSONUtil;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FlashSaleControllerTest {
    @InjectMocks
    FlashSaleController controller;

    MockMvc mvc;

    @Mock
    FlashSaleService flashSaleService;

    private FlashSale flashSale1;
    private FlashSale flashSale2;
    private WowBannerProductResponse wowBannerProductResponse;
    private AppId appId;
    private WowExtra wowExtra;

    @Before

    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        flashSale1 = new FlashSale();
        flashSale1.setId(1L);
        flashSale1.setName("flashSaleName1");
        flashSale1.setStartPeriod(new Date());
        flashSale1.setEndPeriod(new Date());
        flashSale1.setName("fs_name");
        flashSale1.setType("wow_banner");
        flashSale1.setNonMember(true);
        flashSale1.setEnable(true);
        flashSale1.setMember(true);
        final FlashSaleCondition flashSaleCondition = new FlashSaleCondition();
        FlashSaleVariant flashSaleVariant = new FlashSaleVariant();
        flashSaleVariant.setDiscountPercent(9D);
        flashSaleVariant.setPromotionPrice(9D);
        flashSaleVariant.setLimitQuantity(5L);
        flashSaleVariant.setVariantId("variantId");
        FlashSaleProduct flashSaleProduct = new FlashSaleProduct();
        flashSaleProduct.setProductKey("pdid1");
        flashSaleProduct.setCategoryIds(Arrays.asList("category"));
        flashSaleProduct.setFlashsaleVariants(Arrays.asList(flashSaleVariant));
        flashSaleCondition.setFlashSaleProduct(flashSaleProduct);
        flashSaleCondition.setLimitItem(5);
        flashSale1.setFlashSaleCondition(flashSaleCondition);
        appId = new AppId();
        appId.setName("app_id");
        flashSale1.setAppId(appId);
        flashSale2 = new FlashSale();
        flashSale2.setId(2L);
        flashSale2.setName("flashSaleName2");
        wowBannerProductResponse = new WowBannerProductResponse();
        wowBannerProductResponse.setCurrentWow(new WowBannerProduct());
        wowBannerProductResponse.setNextWow(new WowBannerProduct());
        wowBannerProductResponse.setIncomingWow(new WowBannerProduct());

        wowExtra = new WowExtra();
        wowExtra.setVariantId("wowExtra");
        wowExtra.setPartner("Line");
        wowExtra.setPromotionPrice(99D);
        wowExtra.setCategoryId("category");

    }

    @Test
    public void shouldReturnPageFlashSaleWhenGetAllFlashSaleExistingInDb() throws Exception {
        Page expectedPage = new PageImpl(Arrays.asList(flashSale1, flashSale2));
        when(flashSaleService.getAllFlashSale(anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(),
                anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong(), anyLong(), anyString()))
                .thenReturn(expectedPage);

        mvc.perform(get("/api/v1/flashsales").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content[0].name", is(flashSale1.getName())))
                .andExpect(jsonPath("$.data.content[1].name", is(flashSale2.getName())))
                .andExpect(status().isOk());

        verify(flashSaleService).getAllFlashSale(anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyLong(),
                anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong(), anyLong(), anyString());
    }

    @Test
    public void shouldCreatedFlashSaleWhenCreateNewFlashSaleSuccessfully() throws Exception {
        when(flashSaleService.createFlashSale(any(FlashSale.class))).thenReturn(flashSale1);

        mvc.perform(post("/api/v1/flashsales")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(flashSaleService).createFlashSale(any(FlashSale.class));

    }

    @Test
    public void shouldReturnErrorWhenCreateNewFlashSaleWithJsonNotValid() throws Exception {
        flashSale1.setType("wow_banner_invalid");

        mvc.perform(post("/api/v1/flashsales")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void shouldReturnFlashSaleUpdatedWhenUpdateExistingFlashSaleById() throws Exception {

        when(flashSaleService.updateFlashSale(anyLong(), any(FlashSale.class))).thenReturn(flashSale1);

        mvc.perform(put("/api/v1/flashsales/1")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(status().isOk());

        verify(flashSaleService).updateFlashSale(anyLong(), any(FlashSale.class));
    }


    @Test
    public void shouldReturnErrorMessageWhenUpdateFlashSaleWithJsonNotValid() throws Exception {
        flashSale1.setName("UpdateName");
        flashSale1.setId(1L);
        flashSale1.setType("type_not_valid");

        mvc.perform(put("/api/v1/flashsales/1")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }


    @Test
    public void shouldDeleteDealWhenDeleteExistingDealById() throws Exception {
        when(flashSaleService.deleteFlashSale(anyLong())).thenReturn(flashSale1);

        mvc.perform(delete("/api/v1/flashsales/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(status().isOk());

        verify(flashSaleService).deleteFlashSale(anyLong());
    }

    @Test
    public void shouldNotDeleteFlashSaleWhenDeleteNonExistingFlashSaleById() throws Exception {
        doThrow(FlashSaleNotFoundException.class).when(flashSaleService).deleteFlashSale(anyLong());

        mvc.perform(delete("/api/v1/flashsales/1"))
                .andExpect(status().isNotFound());

        verify(flashSaleService).deleteFlashSale(anyLong());
    }

    @Test
    public void shouldReturnFlashSaleWhenGetExistingFlashSaleById() throws Exception {
        when(flashSaleService.getFlashSaleById(1L)).thenReturn(flashSale1);

        mvc.perform(get("/api/v1/flashsales/1"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is(flashSale1.getName())))
                .andExpect(status().isOk());

        verify(flashSaleService).getFlashSaleById(anyLong());
    }

    @Test
    public void shouldReturnNotFoundFlashSaleWhenGetNonExistingFlashSaleById() throws Exception {
        doThrow(FlashSaleNotFoundException.class).when(flashSaleService).getFlashSaleById(anyLong());

        mvc.perform(get("/api/v1/flashsales/1"))
                .andExpect(status().isNotFound());

        verify(flashSaleService).getFlashSaleById(anyLong());
    }

    @Test
    public void shouldReturnWowBannerPromotionWhenGetExistingWowBannerByDate() throws Exception {
        Long currentDate = DateTime.now().getMillis();
        wowBannerProductResponse.getCurrentWow().setName("currentWowName");
        wowBannerProductResponse.getNextWow().setName("nextWowName");
        when(flashSaleService.getWowBanner(currentDate)).thenReturn(wowBannerProductResponse);

        mvc.perform(get("/api/v1/flashsales/wowBanner")
                .param("current_time", currentDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.current_wow.name", is(wowBannerProductResponse.getCurrentWow().getName())))
                .andExpect(jsonPath("$.data.next_wow.name", is(wowBannerProductResponse.getNextWow().getName())))
                .andExpect(status().isOk());
        verify(flashSaleService).getWowBanner(currentDate);
    }

    @Test
    public void shouldReturnWowExtraPromotionWhenGetExistingWowExtraByDate() throws Exception {
        Page expectedPage = new PageImpl(Arrays.asList(wowExtra));
        when(flashSaleService.getWowExtra(anyInt(), anyInt(), any(Sort.Direction.class), anyString(),
                anyString())).thenReturn(expectedPage);

        mvc.perform(get("/api/v1/flashsales/wowExtra").contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(jsonPath("$.data.content[0].variant_id", is(wowExtra.getVariantId())))
                .andExpect(status().isOk());
        verify(flashSaleService).getWowExtra(anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyString());
    }

    @Test
    public void shouldReturnAppIdsListWhenGetAllExistingAppIdInDb() throws Exception {
        AppId appId = new AppId();
        appId.setName("itm");
        when(flashSaleService.getAppIds()).thenReturn(Arrays.asList(appId));

        mvc.perform(get("/api/v1/flashsales/appId"))
                .andExpect(jsonPath("$.data[0].name", is("itm")));
        verify(flashSaleService).getAppIds();
    }

    @Test
    public void shouldReturnEmptyAppIdListWhenGetNotExistingAppIdInDb() throws Exception {
        when(flashSaleService.getAppIds()).thenReturn(new ArrayList<>());

        mvc.perform(get("/api/v1/flashsales/appId"))
                .andExpect(jsonPath("$.data", is(new ArrayList())));
        verify(flashSaleService).getAppIds();
    }

    @Test
    public void shouldReturnAppIdWhenCreateAppIdSuccessfully() throws Exception {
        when(flashSaleService.createAppId(any(AppId.class))).thenReturn(appId);

        mvc.perform(post("/api/v1/flashsales/appId")
                .content(JSONUtil.toString(appId))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("app_id")));

        verify(flashSaleService).createAppId(any(AppId.class));
    }

    @Test
    public void shouldEnableFlashSaleWhenEnabledExistingFlashSaleById() throws Exception {
        flashSale1.setEnable(true);
        when(flashSaleService.enableFlashSale(anyLong())).thenReturn(flashSale1);

        mvc.perform(put("/api/v1/flashsales/1/enabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(true)))
                .andExpect(status().isOk());

        verify(flashSaleService).enableFlashSale(anyLong());
    }

    @Test
    public void shouldReturnFlashSaleExceptionWhenEnabledNonExistingFlashSaleInDb() throws Exception {
        flashSale1.setEnable(true);
        doThrow(FlashSaleNotFoundException.class).when(flashSaleService).enableFlashSale(anyLong());

        mvc.perform(put("/api/v1/flashsales/1/enabled"))
                .andExpect(status().is4xxClientError());

        verify(flashSaleService).enableFlashSale(anyLong());
    }

    @Test
    public void shouldEnableFlashSaleWhenDisabledExistingFlashSaleById() throws Exception {
        flashSale1.setEnable(false);
        when(flashSaleService.disabledFlashSale(anyLong())).thenReturn(flashSale1);

        mvc.perform(put("/api/v1/flashsales/1/disabled"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.enable", is(false)))
                .andExpect(status().isOk());

        verify(flashSaleService).disabledFlashSale(anyLong());
    }

    @Test
    public void shouldReturnFlashSaleExceptionWhenDisabledNonExistingFlashSaleInDb() throws Exception {
        flashSale1.setEnable(false);
        doThrow(FlashSaleNotFoundException.class).when(flashSaleService).disabledFlashSale(anyLong());

        mvc.perform(put("/api/v1/flashsales/1/disabled"))
                .andExpect(status().is4xxClientError());

        verify(flashSaleService).disabledFlashSale(anyLong());
    }

    @Test
    public void shouldBatchEnableCampaignWhenEnabledExistingCampaignById() throws Exception {
        flashSale1.setEnable(true);
        when(flashSaleService.enableFlashSale(anyLong())).thenReturn(flashSale1);

        mvc.perform(put("/api/v1/flashsales/enabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(flashSaleService, times(4)).enableFlashSale(anyLong());
    }

    @Test
    public void shouldBatchDisableCampaignWhenDisabledExistingCampaignById() throws Exception {
        flashSale1.setEnable(false);
        when(flashSaleService.disabledFlashSale(anyLong())).thenReturn(flashSale1);

        mvc.perform(put("/api/v1/flashsales/disabled").param("ids", "1,2,3,4"))
                .andExpect(status().isOk());

        verify(flashSaleService, times(4)).disabledFlashSale(anyLong());
    }

    @Test
    public void shouldReturnAlertMessageWhenCreateWowBannerPeriodTimeCoverExistingWowBanner() throws Exception {
        doThrow(new WowBannerException("Wow Banner period cover  any other Wow Banner Promotion !!!"))
                .when(flashSaleService).createFlashSale(any(FlashSale.class));

        mvc.perform(post("/api/v1/flashsales")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Wow Banner period cover  any other Wow Banner Promotion !!!")))
                .andExpect(status().is4xxClientError());

        verify(flashSaleService).createFlashSale(any(FlashSale.class));
    }

    @Test
    public void shouldReturnErrorMessageWhenCreateWowBannerWithJsonNotValid() throws Exception {
        doThrow(new InvalidDataAccessApiUsageException("Json is no volid"))
                .when(flashSaleService).createFlashSale(any(FlashSale.class));

        mvc.perform(post("/api/v1/flashsales")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Json is no volid")))
                .andExpect(status().is4xxClientError());

        verify(flashSaleService).createFlashSale(any(FlashSale.class));
    }

    @Test
    public void shouldReturnErrorMessageConstraintViolationExceptionWhenCreateWowBannerWithJsonNotValid()
            throws Exception {
        doThrow(ConstraintViolationException.class)
                .when(flashSaleService).createFlashSale(any(FlashSale.class));

        mvc.perform(post("/api/v1/flashsales")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("javax.validation.ConstraintViolationException")))
                .andExpect(status().is4xxClientError());

        verify(flashSaleService).createFlashSale(any(FlashSale.class));
    }

    @Test
    public void shouldReturnFlashSaleLimitItemPerCartImageWhenGetLimitItemPerCartImageCorrectly() throws Exception {
        Policy policy1 = new Policy();
        policy1.setPolicy(1L);
        policy1.setPolicyImg("number1.jpg");

        when(flashSaleService.getFlashSalePolicyImage(anyLong())).thenReturn(policy1);

        mvc.perform(get("/api/v1/flashsales/policies/images/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.policy_number", is(1)))
                .andExpect(jsonPath("$.data.policy_img", is("number1.jpg")))
                .andExpect(status().isOk());

        verify(flashSaleService).getFlashSalePolicyImage(anyLong());
    }

    @Test
    public void shouldReturnPolicyNotFoundExceptionWhenGetNotExistingPolicyInYMLFile()
            throws Exception {
        doThrow(PolicyNotFoundException.class)
                .when(flashSaleService).getFlashSalePolicyImage(anyLong());

        mvc.perform(get("/api/v1/flashsales/policies/images/99")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Policy not found !!")))
                .andExpect(status().is4xxClientError());

        verify(flashSaleService).getFlashSalePolicyImage(anyLong());
    }

    @Test
    public void shouldReturnWowProductDetailWhenGetExistingWowProductByProductKey() throws Exception {
        WowExtraProduct wowProduct = new WowExtraProduct();
        wowProduct.setProductKey("pdk111");
        when(flashSaleService.getFlashSaleProductByProductKey(anyString())).thenReturn(wowProduct);

        mvc.perform(get("/api/v1/flashsales/products/pdk111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.product_key", is("pdk111")))
                .andExpect(status().isOk());

        verify(flashSaleService).getFlashSaleProductByProductKey(anyString());
    }

    @Test
    public void shouldReturnWowExtraProductNotFoundExceptionWhenGetNotExistingWowExtraProduct()
            throws Exception {
        doThrow(WowExtraProductNotFoundException.class)
                .when(flashSaleService).getFlashSaleProductByProductKey(anyString());

        mvc.perform(get("/api/v1/flashsales/products/pdk12345")
                .content(JSONUtil.toString(flashSale1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Wow Product not found !!")))
                .andExpect(status().is4xxClientError());

        verify(flashSaleService).getFlashSaleProductByProductKey(anyString());
    }

    @Test
    public void shouldReturnFlashSaleProductAvailableListWhenUpdateProductFlashSaleStatus()
            throws Exception {

        List<FlashSaleProductAvailable> productAvailableList = Arrays.asList(new FlashSaleProductAvailable());
        when(flashSaleService.updateFlashSaleProductStatus(anyListOf(FlashSaleProductAvailable.class)))
                .thenReturn(productAvailableList);

        mvc.perform(put("/api/v1/flashsales/products/status")
                .content(JSONUtil.toString(productAvailableList))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(flashSaleService).updateFlashSaleProductStatus(anyListOf(FlashSaleProductAvailable.class));
    }

    @Test
    public void shouldReturnVariantAvailableListWhenUpdateVariantFlashsaleStatus() throws Exception {
        List<FlashSaleVariantAvailable> variantAvailableList = Arrays.asList(new FlashSaleVariantAvailable());
        when(flashSaleService.updateFlashSaleVariantStatus(anyListOf(FlashSaleVariantAvailable.class)))
                .thenReturn(variantAvailableList);

        mvc.perform(put("/api/v1/flashsales/variants/status")
                .content(JSONUtil.toString(variantAvailableList))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(flashSaleService).updateFlashSaleVariantStatus(anyListOf(FlashSaleVariantAvailable.class));
    }

    @Test
    public void shouldReturnProductFlashSaleDuplicateListWhenCreateFlashSaleDuplicateProductInTheSamePeriod()
            throws Exception {
        when(flashSaleService.checkProductDuplicate(anyString(), anyLong(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList());

        mvc.perform(get("/api/v1/flashsales/isProductDuplicate")
                .param("products", "pdk1,pdk2")
                .param("start_period", String.valueOf(DateTime.now().minusMinutes(5).getMillis()))
                .param("end_period", String.valueOf(DateTime.now().plusMinutes(5).getMillis()))
                .param("flashsale_id", "1"))
                .andExpect(status().is2xxSuccessful());

        verify(flashSaleService).checkProductDuplicate(anyString(), anyLong(), anyLong(), anyLong());
    }
}
