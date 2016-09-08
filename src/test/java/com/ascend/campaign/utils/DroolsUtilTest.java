package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Code;
import com.ascend.campaign.entities.CodeDetail;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.models.BundleForProduct;
import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.CampaignApplied;
import com.ascend.campaign.models.Cart;
import com.ascend.campaign.models.CartCampaign;
import com.ascend.campaign.models.DiscountCodeCriteriaValue;
import com.ascend.campaign.models.FreebieForProduct;
import com.ascend.campaign.models.MNPForProduct;
import com.ascend.campaign.models.MNPVariant;
import com.ascend.campaign.models.PDSJson;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.PromotionProduct;
import com.ascend.campaign.models.PromotionProductCode;
import com.ascend.campaign.models.VariantDealDetail;
import com.ascend.campaign.repositories.CodeDetailRepo;
import com.ascend.campaign.repositories.CodeRepo;
import com.ascend.campaign.repositories.PromotionItmRepo;
import com.ascend.campaign.repositories.PromotionWMRepo;
import com.ascend.campaign.services.ExternalService;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DroolsUtilTest {
    @Autowired
    DroolsUtil droolsUtil;
    @Mock
    PromotionItmRepo promotionItmRepo;

    @Mock
    PromotionWMRepo promotionWmRepo;

    @Mock
    CodeRepo codeRepo;

    @Mock
    CodeDetailRepo codeDetailRepo;

    @Mock
    ExternalService externalService;

    private Promotion promotion;
    private PromotionWM promotion2;
    private PromotionCondition promotionCondition;
    private Cart cart;

    @Before
    public void setUp() {
        droolsUtil = new DroolsUtil(promotionItmRepo, promotionWmRepo, new GenerateActionPromotionUtil(),
                codeRepo, codeDetailRepo, externalService);
        List<String> skuList = Lists.newArrayList("Variant1", "Variant2", "Variant3");
        final List<String> freeSkuList = Lists.newArrayList("FREESKU1", "FREESKU2", "FREESKU3");
        List<String> brandList = Lists.newArrayList("BRAND1", "BRAND2", "BRAND3");


        final String startPeriod = "01-Jun-2017 08:00:00";
        final String endPeriod = "05-Jun-2017 08:00:00";
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MMM-yyyy HH:mm:ss");
        final DateTime startDateTime = dtf.parseDateTime(startPeriod);
        final DateTime endDateTime = dtf.parseDateTime(endPeriod);

        promotionCondition = new PromotionCondition();
        promotionCondition.setBrands(brandList);
        promotionCondition.setVariants(skuList);
        promotionCondition.setExcludedVariants(skuList);
        promotionCondition.setFreeVariants(freeSkuList);
        promotionCondition.setQuantity(1);
        promotionCondition.setDiscountPercent(80.00);
        promotionCondition.setDiscountFixed(0.0);
        promotionCondition.setMinTotalValue(10000);
        promotionCondition.setWeekDays(72L);
        promotionCondition.setMasterCardPercent(30.0);
        promotionCondition.setStartTime("21:00");
        promotionCondition.setEndTime("23:30");
        promotionCondition.setPromotionCode("AAACode");
        promotionCondition.setMaxDiscountValue(200.0);
        promotionCondition.setCriteriaType("variant");
        promotionCondition.setCriteriaValue(Lists.newArrayList("SKU1", "SKU2", "SKU3"));
        promotionCondition.setFreeQuantity(1);
        DiscountCodeCriteriaValue discountCodeCriteriaValue = new DiscountCodeCriteriaValue();
        discountCodeCriteriaValue.setVariantId("SKU1");
        discountCodeCriteriaValue.setShow(true);
        DiscountCodeCriteriaValue discountCodeCriteriaValue2 = new DiscountCodeCriteriaValue();
        discountCodeCriteriaValue2.setVariantId("SKU2");
        discountCodeCriteriaValue2.setShow(true);
        DiscountCodeCriteriaValue discountCodeCriteriaValue3 = new DiscountCodeCriteriaValue();
        discountCodeCriteriaValue3.setVariantId("SKU3");
        discountCodeCriteriaValue3.setShow(true);
        promotionCondition.setDiscountCodeCriteriaValue(Arrays.asList(
                discountCodeCriteriaValue, discountCodeCriteriaValue2, discountCodeCriteriaValue3));

        promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("product name");
        promotion.setStartPeriod(startDateTime.toDate());
        promotion.setEndPeriod(endDateTime.toDate());
        promotion.setDescription("description");
        promotion.setShortDescription("short Description");
        promotion.setRepeat(10);
        promotion.setPromotionCondition(promotionCondition);
        promotion.setImgThmUrl("pic1");
        promotion.setImgUrl("pic2");
        promotion.setAppId("[ \"APP1\",\"APP2\",\"APP3\" ]");
        promotion.setBusinessChannel("itruemart");
        promotion.setDescriptionEn("promotionDescriptionTranslation");
        promotion.setShortDescriptionEn("promotionShortDescriptionTranslation");

        promotion2 = new PromotionWM();
        promotion2.setId(1L);
        promotion2.setName("product name");
        promotion2.setStartPeriod(startDateTime.toDate());
        promotion2.setEndPeriod(endDateTime.toDate());
        promotion2.setDescription("description");
        promotion2.setShortDescription("short Description");
        promotion2.setRepeat(10);
        promotion2.setPromotionCondition(promotionCondition);
        promotion2.setImgThmUrl("pic1");
        promotion2.setImgUrl("pic2");
        promotion2.setAppId("[ \"APP1\",\"APP2\",\"APP3\" ]");
        promotion2.setBusinessChannel("itruemart");
        promotion2.setDescriptionEn("promotionDescriptionTranslation");
        promotion2.setShortDescriptionEn("promotionShortDescriptionTranslation");

        Product product1 = new Product();
        product1.setVariantId("AA");
        product1.setBrandCode("BC1");
        product1.setCategoryCode("cat1");
        product1.setCollection("col1");
        product1.setNormalPrice(1000D);
        product1.setNormalPrice(1000.0);
        product1.setQuantity(2);

        Product product2 = new Product();
        product2.setVariantId("BB");
        product2.setBrandCode("BC2");
        product2.setCategoryCode("cat2");
        product2.setCollection("col2");
        product2.setNormalPrice(1000.0);
        product2.setNormalPrice(1000.0);
        product2.setQuantity(3);
        final ArrayList<Product> products = new ArrayList<>();
        final HashMap<String, Integer> promotionIdList = new HashMap<>();
        promotionIdList.put("1", 1);
        products.add(product1);
        products.add(product2);
        cart = new Cart();
        cart.setProducts(products);
        cart.setPromotionIdMatchPromotionList(promotionIdList);
    }

    @Test
    public void addCartPromotionsBundleITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":null,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,"
                + "\"excluded_variants\":null,\"discount_mastercard\":null,\"free_variants\":null,"
                + "\"discount_percent\":null,\"discount_fixed\":null,\"min_total_value\":null,\"start_time\":null,"
                + "\"end_time\":null,\"week_days\":null,\"primary_variant\":null,\"bundle_conditions\":"
                + "[{\"bundle_variant\":\"AA\",\"discount_percent\":null,\"discount_fixed\":2400.0},"
                + "{\"bundle_variant\":\"BB\",\"discount_percent\":100.0,\"discount_fixed\":null}],"
                + "\"criteria_type\":null,\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,"
                + "\"note_en\":\"\",\"note_local\":\"\",\"free_quantity\":null}";
        promotion.setType("itm-bundle");
        promotion.setConditionData(conditionData);
        CampaignApplied campaignApplied = new CampaignApplied();
        campaignApplied.setPromotionId(2L);
        cart.setCampaignApplied(Arrays.asList(campaignApplied));
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_bundle"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(2));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(1).getVariantId(), is("BB"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsBundleWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":null,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,"
                + "\"excluded_variants\":null,\"discount_mastercard\":null,\"free_variants\":null,"
                + "\"discount_percent\":null,\"discount_fixed\":null,\"min_total_value\":null,\"start_time\":null,"
                + "\"end_time\":null,\"week_days\":null,\"primary_variant\":null,\"bundle_conditions\":"
                + "[{\"bundle_variant\":\"AA\",\"discount_percent\":null,\"discount_fixed\":2400.0},"
                + "{\"bundle_variant\":\"BB\",\"discount_percent\":100.0,\"discount_fixed\":null}],"
                + "\"criteria_type\":null,\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,"
                + "\"note_en\":\"\",\"note_local\":\"\",\"free_quantity\":null}";
        promotion2.setType("wm-bundle");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_bundle"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(2));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(1).getVariantId(), is("BB"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsFreebieITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":[\"FREEBIG\"],\"discount_percent\":null,"
                + "\"discount_fixed\":null,\"min_total_value\":null,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"variant\","
                + "\"criteria_values\":[\"AA\"],\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion.setType("itm-freebie");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("free"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("FREEBIG"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(1).getVariantId(), is("FREEBIG"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsFreebieWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":[\"FREEBIG\"],\"discount_percent\":null,"
                + "\"discount_fixed\":null,\"min_total_value\":null,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"variant\","
                + "\"criteria_values\":[\"AA\"],\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion2.setType("wm-freebie");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("free"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("FREEBIG"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(1).getVariantId(), is("FREEBIG"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void deleteUnSuggestedCorrectlyWhenHaveCampaignApplied() {
        final List<CampaignApplied> campaignApplieds = new ArrayList<>();
        CampaignApplied campaignApplied = new CampaignApplied();
        campaignApplied.setPromotionId(1L);
        campaignApplied.setLimit(1);
        campaignApplied.setPromotionName("test");
        campaignApplied.setVariantId(Arrays.asList("AA", "BB"));

        CampaignApplied campaignApplied2 = new CampaignApplied();
        campaignApplied2.setPromotionId(1L);
        campaignApplied2.setLimit(1);
        campaignApplied2.setPromotionName("test");
        campaignApplied2.setVariantId(Arrays.asList("AA", "BB"));
        campaignApplieds.add(campaignApplied);
        campaignApplieds.add(campaignApplied2);
        cart.setCampaignApplied(campaignApplieds);
        droolsUtil.deleteUnSuggested(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), is(nullValue()));
    }

    @Test
    public void deleteUnSuggestedCorrectlyWhenNotHaveCampaignApplied() {
        final List<CampaignApplied> campaignApplieds = new ArrayList<>();
        CampaignApplied campaignApplied = new CampaignApplied();
        campaignApplied.setPromotionId(1L);
        campaignApplied.setLimit(1);
        campaignApplied.setPromotionName("test");
        campaignApplied.setVariantId(Arrays.asList("AA", "BB"));

        CampaignApplied campaignApplied2 = new CampaignApplied();
        campaignApplied2.setPromotionId(2L);
        campaignApplied2.setLimit(1);
        campaignApplied2.setPromotionName("test2");
        campaignApplied2.setVariantId(Arrays.asList("CC", "DD"));
        campaignApplieds.add(campaignApplied2);
        cart.setCampaignApplied(campaignApplieds);
        cart.setCartCampaign(new CartCampaign());

        droolsUtil.deleteUnSuggested(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), is(nullValue()));
        assertThat(cart.getCartCampaign().getCampaignUnSuggestion().get(0).getPromotionId(), is(2L));
    }

    @Test
    public void setEmptyCampaignSuggestionOrCampaignUnSuggestionToNull() {
        Cart cart = new Cart();
        cart.setCartCampaign(new CartCampaign());
        cart.getCartCampaign();
        cart.getCartCampaign().setCampaignSuggestion(new ArrayList<>());
        droolsUtil.setEmptyCampaignSuggestionOrCampaignUnSuggestionToNull(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), is(nullValue()));
        cart.getCartCampaign().setCampaignSuggestion(null);
        cart.getCartCampaign().setCampaignUnSuggestion(new ArrayList<>());
        droolsUtil.setEmptyCampaignSuggestionOrCampaignUnSuggestionToNull(cart);
        assertThat(cart.getCartCampaign().getCampaignUnSuggestion(), is(nullValue()));

    }

    @Test
    public void setBundleForProductCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":null,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,"
                + "\"excluded_variants\":null,\"discount_mastercard\":null,\"free_variants\":null,"
                + "\"discount_percent\":null,\"discount_fixed\":null,\"min_total_value\":null,\"start_time\":null,"
                + "\"end_time\":null,\"week_days\":null,\"primary_variant\":null,\"bundle_conditions\":"
                + "[{\"bundle_variant\":\"AA\",\"discount_percent\":null,\"discount_fixed\":2400.0},"
                + "{\"bundle_variant\":\"BB\",\"discount_percent\":100.0,\"discount_fixed\":null}],"
                + "\"criteria_type\":null,\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,"
                + "\"note_en\":\"\",\"note_local\":\"\",\"free_quantity\":null}";
        promotion.setConditionData(conditionData);
        List<BundleVariant> bundleVariants = new ArrayList<>();
        BundleVariant bundleVariant = new BundleVariant();
        bundleVariant.setBundleVariant("test");
        bundleVariant.setDiscountFixed(50D);
        bundleVariants.add(bundleVariant);

        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("test");
        bundleVariant1.setDiscountPercent(10D);
        bundleVariants.add(bundleVariant1);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        List<BundleForProduct> bundleForProducts = droolsUtil.setBundleForProductItm(Arrays.asList("1"));
        assertThat(bundleForProducts.get(0).getPromotionId(), is("1"));
        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test
    public void addCartPromotionsDiscountPromotionITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"variant\","
                + "\"criteria_values\":[\"AA\"],\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion.setType("itm-discount_promotion");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsDiscountPromotionWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"variant\","
                + "\"criteria_values\":[\"AA\"],\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion2.setType("wm-discount_promotion");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsOptionToBuyPromotionITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":[\"AA\"],\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":null,"
                + "\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion.setType("itm-option_to_buy");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("suggest_discount"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("BB"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsOptionToBuyPromotionWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":[\"AA\"],\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":null,"
                + "\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion2.setType("wm-option_to_buy");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("suggest_discount"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("BB"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsSpecificTimePromotionITMCorrectly() {
        String conditionData = "{\"brands\":[\"BC1\"],\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":null,"
                + "\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion.setType("itm-specific_time");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionsSpecificTimePromotionWMCorrectly() {
        String conditionData = "{\"brands\":[\"BC1\"],\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":null,"
                + "\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion2.setType("wm-specific_time");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetVariantPromotionITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"variant\","
                + "\"criteria_values\":[\"AA\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion.setType("itm-discount_by_code");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        cart.setCartCampaign(new CartCampaign());
        cart.getCartCampaign().setCampaignSuggestion(new ArrayList<>());
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());

        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo, times(2)).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetVariantPromotionWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"variant\","
                + "\"criteria_values\":[\"AA\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion2.setType("wm-discount_by_code");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetBrandPromotionITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"brand\","
                + "\"criteria_values\":[\"BC1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion.setType("itm-discount_by_code");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetBrandPromotionWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"brand\","
                + "\"criteria_values\":[\"BC2\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion2.setType("wm-discount_by_code");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("BB"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetCategoryPromotionITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"Category\","
                + "\"criteria_values\":[\"cat1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion.setType("itm-discount_by_code");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetCategoryPromotionWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"Category\","
                + "\"criteria_values\":[\"cat1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion2.setType("wm-discount_by_code");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetCollectionPromotionITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"Collection\","
                + "\"criteria_values\":[\"col1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion.setType("itm-discount_by_code");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetCollectionPromotionWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"Collection\","
                + "\"criteria_values\":[\"col1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion2.setType("wm-discount_by_code");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionWmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetCartPromotionITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"cart\","
                + "\"criteria_values\":[\"col1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion.setType("itm-discount_by_code");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_per_cart"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void addCartPromotionDiscountByCodeSetCartPromotionWMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":[\"BB\"],"
                + "\"excluded_variants\""
                + ":null,\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"cart\","
                + "\"criteria_values\":[\"col1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";
        promotion2.setType("wm-discount_by_code");
        promotion2.setConditionData(conditionData);
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.addCartPromotionsWm(cart);
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_per_cart"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionWmRepo).findOne(anyLong());

    }


    @Test
    public void addCartPromotionDiscountByCodeSetCartAndExcludedVariantPromotionCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,"
                + "\"excluded_variants\""
                + ":[\"BB\"],\"excluded_brands\":[\"BC2\"],\"excluded_collections\":[\"col2\"],"
                + "\"excluded_categories\":[\"cat2\"],\"discount_mastercard\":null,\"free_variants\":null,"
                + "\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\""
                + "week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"cart\","
                + "\"criteria_values\":[\"col1\"],\"max_discount_value\":null,\"code_group_id\":\"15\",\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":null}";

        promotion.setType("itm-discount_by_code");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_per_cart"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void setPromotionForProductCodeCorrectly() {
        promotion2.setType(CampaignEnum.WM_DISCOUNT_BY_CODE.getContent());
        promotion2.setConditionData("{\n"
                + "      \"criteria_type\":\"variant\",\n"
                + "      \"criteria_values\":[],\n"
                + "      \"excluded_categories\":[\n"
                + "         \"\"\n"
                + "      ],\n"
                + "      \"excluded_brands\":[\n"
                + "         \"\"\n"
                + "      ],\n"
                + "      \"excluded_collections\":[\n"
                + "         \"\"\n"
                + "      ],\n"
                + "      \"excluded_variants\":[\n"
                + "         \"\"\n"
                + "      ],\n"
                + "      \"discount_code_criteria_value\":[\n"
                + "         {\n"
                + "            \"variant_id\":\"SKU1\",\n"
                + "            \"show\":true\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"SKU2\",\n"
                + "            \"show\":true\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"SKU3\",\n"
                + "            \"show\":true\n"
                + "         }\n"
                + "      ],\n"
                + "      \"code_group_id\":\"4\",\n"
                + "      \"min_total_value\":4,\n"
                + "      \"discount_percent\":4,\n"
                + "      \"max_discount_value\":4\n"
                + "   }");
        Code code = new Code();
        code.setCode("testSingleCode");


        CodeDetail codeDetail = new CodeDetail();
        codeDetail.setCodeType(CampaignEnum.SINGLE.getContent());


        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        when(codeRepo.findById(anyLong())).thenReturn(code);
        when(codeRepo.findByCodeDetail(anyLong())).thenReturn(Arrays.asList(code));
        when(codeDetailRepo.findOne(anyLong())).thenReturn(codeDetail);

        PromotionProduct promotionProduct = new PromotionProduct();
        promotionProduct.setProductVariant("SKU1");
        promotionProduct.setPromotionIdList(Arrays.asList(1, 2));
        PromotionProductCode promotionProductCode = droolsUtil.setPromotionForProductCode(promotionProduct);
        assertThat(promotionProductCode.getPromotionForProductsCode().get(0).getSingleCode(), is("testSingleCode"));

        PromotionProduct promotionProduct2 = new PromotionProduct();
        PromotionProductCode promotionProductCode3 = droolsUtil.setPromotionForProductCode(promotionProduct2);
        assertThat(promotionProductCode3.getPromotionForProductsCode(), is(new ArrayList<>()));

        verify(promotionWmRepo, times(2)).findOne(anyLong());
        verify(codeRepo, times(2)).findById(anyLong());
        verify(codeRepo, times(2)).findByCodeDetail(anyLong());
        verify(codeDetailRepo, times(2)).findOne(anyLong());

    }

    @Test
    public void setPromotionForProductCodeIfNotFoundCodeCorrectly() {
        promotion2.setType("wm-discount_by_code");

        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);

        PromotionProduct promotionProduct = new PromotionProduct();
        promotionProduct.setPromotionIdList(Arrays.asList(1));
        PromotionProductCode promotionProductCode = droolsUtil.setPromotionForProductCode(promotionProduct);

        assertThat(promotionProductCode.getPromotionForProductsCode(), is(new ArrayList<>()));

        verify(promotionWmRepo).findOne(anyLong());
    }


    @Test
    public void setBundleForProductItmCorrectly() {
        List<String> promotionIds = Arrays.asList("1", "2", "3");
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        List<BundleForProduct> bundleForProducts = droolsUtil.setBundleForProductItm(promotionIds);
        assertThat(bundleForProducts.size(), is(3));
        assertThat(bundleForProducts.get(0).getPromotionId(), is("1"));
        verify(promotionItmRepo, times(3)).findOne(anyLong());
    }

    @Test
    public void setBundleForProductWmCorrectly() {
        List<String> promotionIds = Arrays.asList("1", "2", "3");
        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        List<BundleForProduct> bundleForProducts = droolsUtil.setBundleForProductWm(promotionIds);
        assertThat(bundleForProducts.size(), is(3));
        assertThat(bundleForProducts.get(0).getPromotionId(), is("1"));
        verify(promotionWmRepo, times(3)).findOne(anyLong());
    }

    @Test
    public void setPromotionForProductWmCorrectly() {
        List<Integer> promotionIds = Arrays.asList(1, 2, 3);
        PromotionProduct promotionProduct = new PromotionProduct();
        promotionProduct.setPromotionIdList(promotionIds);

        when(promotionWmRepo.findOne(anyLong())).thenReturn(promotion2);
        droolsUtil.setPromotionForProductWm(promotionProduct);

        assertThat(promotionProduct.getPromotionForProducts().size(), is(3));
        assertThat(promotionProduct.getPromotionForProducts().get(0).getPromotionId(), is("1"));

        promotionProduct.setPromotionIdList(null);
        promotionProduct.setPromotionForProducts(null);
        droolsUtil.setPromotionForProductWm(promotionProduct);

        assertThat(promotionProduct.getPromotionForProducts(), is(new ArrayList<>()));

        verify(promotionWmRepo, times(3)).findOne(anyLong());
    }

    @Test
    public void shouldGetProductListWhenExcludedCollectionsCorrectly() {

        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null"
                + ",\"excluded_variants\":null,\"excluded_collections\":[\"col2\"],"
                + "\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"variant\","
                + "\"criteria_values\":[\"AA\",\"BB\"],\"max_discount_value\":null,\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion.setType("itm-discount_promotion");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test
    public void shouldGetProductListWhenExcludedBrandsCorrectly() {

        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null"
                + ",\"excluded_variants\":null,\"excluded_brands\":[\"BC2\"],"
                + "\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"collection\","
                + "\"criteria_values\":[\"col1\",\"col2\"],\"max_discount_value\":null,\"code_group_id\":null,"
                + "\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion.setType("itm-discount_promotion");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void shouldGetProductListWhenExcludedCategorysCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null"
                + ",\"excluded_variants\":null,\"excluded_categories\":[\"cat2\"],"
                + "\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"category\","
                + "\"criteria_values\":[\"cat1\",\"cat2\"],\"max_discount_value\":null,"
                + "\"code_group_id\":null,\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion.setType("itm-discount_promotion");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void shouldGetProductListWhenExcludedVariantsCorrectly() {

        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":2,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null"
                + ",\"excluded_variants\":null,\"excluded_variants\":[\"BB\"],"
                + "\"discount_mastercard\":null,\"free_variants\":null,\"discount_percent\":50,"
                + "\"discount_fixed\":null,\"min_total_value\":500,\"start_time\":null,\"end_time\":null,\"week_days\""
                + ":null,\"primary_variant\":null,\"bundle_conditions\":null,\"criteria_type\":\"brand\","
                + "\"criteria_values\":[\"BC1\",\"BC2\"],\"max_discount_value\":null,\"code_group_id\":null,"
                + "\"note_en\":"
                + "null,\"note_local\":null,\"free_quantity\":2}";
        promotion.setType("itm-discount_promotion");
        promotion.setConditionData(conditionData);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_item"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(1));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));

        verify(promotionItmRepo).findOne(anyLong());

    }


    @Test
    public void shouldGetProductDeatailFromPdsCorrectly() {

        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setVariant("A");
        variantDealDetail.setBrand("B");
        variantDealDetail.setCategory("C");
        variantDealDetail.setCollection(Arrays.asList("D"));
        variantDealDetail.setProduct("P");
        PDSJson pdsJson = new PDSJson();
        pdsJson.setData(variantDealDetail);
        Optional pdsOptional = Optional.of(pdsJson);

        when(externalService.getPDSData(anyString())).thenReturn(pdsOptional);
        PromotionProduct promotionProduct = droolsUtil.setProductDetail("A");
        assertThat(promotionProduct.getProductVariant(), is("A"));

        verify(externalService).getPDSData(anyString());

    }

    @Test
    public void addCartPromotionsMNPITMCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":null,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,"
                + "\"excluded_variants\":null,\"discount_mastercard\":null,\"free_variants\":null,"
                + "\"discount_percent\":null,\"discount_fixed\":null,\"min_total_value\":null,\"start_time\":null,"
                + "\"end_time\":null,\"week_days\":null,\"primary_variant\":null,\"mnp_conditions\":"
                + "[{\"mnp_variant\":\"AA\",\"discount_percent\":null,\"discount_fixed\":2400.0},"
                + "{\"mnp_variant\":\"BB\",\"discount_percent\":100.0,\"discount_fixed\":null}],"
                + "\"criteria_type\":null,\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,"
                + "\"note_en\":\"\",\"note_local\":\"\",\"free_quantity\":null}";
        promotion.setType("itm-mnp");
        promotion.setConditionData(conditionData);
        CampaignApplied campaignApplied = new CampaignApplied();
        campaignApplied.setPromotionId(2L);
        cart.setCampaignApplied(Arrays.asList(campaignApplied));
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        droolsUtil.addCartPromotionsItm(cart, CampaignEnum.ITM_V1.getContent());
        assertThat(cart.getCartCampaign().getCampaignSuggestion(), notNullValue());
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0).getPromotionId(), is("1"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getCommand(), is("discount_mnp"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getLimit(), is(2));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(0).getVariantId(), is("AA"));
        assertThat(cart.getCartCampaign().getCampaignSuggestion().get(0)
                .getPromotionAction().get(0).getVariants().get(1).getVariantId(), is("BB"));

        verify(promotionItmRepo).findOne(anyLong());

    }

    @Test
    public void setMNPForProductCorrectly() {
        String conditionData = "{\"brands\":null,\"variants\":null,\"quantity\":null,\"discount\":null,"
                + "\"mastercard_percent\":null,\"promotion_code\":null,\"option_variants\":null,"
                + "\"excluded_variants\":null,\"discount_mastercard\":null,\"free_variants\":null,"
                + "\"discount_percent\":null,\"discount_fixed\":null,\"min_total_value\":null,\"start_time\":null,"
                + "\"end_time\":null,\"week_days\":null,\"primary_variant\":null,\"mnp_conditions\":"
                + "[{\"mnp_variant\":\"AA\",\"discount_percent\":null,\"discount_fixed\":2400.0},"
                + "{\"mnp_variant\":\"BB\",\"discount_percent\":100.0,\"discount_fixed\":null}],"
                + "\"criteria_type\":null,\"criteria_values\":null,\"max_discount_value\":null,\"code_group_id\":null,"
                + "\"note_en\":\"\",\"note_local\":\"\",\"free_quantity\":null}";
        promotion.setConditionData(conditionData);
        List<MNPVariant> mnpVariants = new ArrayList<>();
        MNPVariant mnpVariant = new MNPVariant();
        mnpVariant.setMnpVariants("test");
        mnpVariant.setDiscountFixed(50D);
        mnpVariants.add(mnpVariant);

        MNPVariant mnpVariant1 = new MNPVariant();
        mnpVariant1.setMnpVariants("test");
        mnpVariant1.setDiscountPercent(10D);
        mnpVariants.add(mnpVariant1);
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        List<MNPForProduct> mnpForProducts = droolsUtil.setMNPForProductItm(Arrays.asList("1"));
        assertThat(mnpForProducts.get(0).getPromotionId(), is("1"));
        verify(promotionItmRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnFreebieForProductListWhenGetFreebieForProductListByPromotionIdList() {
        final List<String> promotionIdList = Arrays.asList("1", "2");
        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setFreeVariantsSelectable(false);
        promotion.setConditionData(JSONUtil.toString(promotionCondition));
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        List<FreebieForProduct> freebieForProducts = droolsUtil.setFreeForProductItm(promotionIdList);
        assertThat(freebieForProducts.get(0).getPromotionId(), is("1"));
    }

    @Test
    public void shouldReturnFreebieForProductListWhenGetFreebieForProductListWithVariantSelectAble() {
        final List<String> promotionIdList = Arrays.asList("1", "2");
        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setFreeVariantsSelectable(true);
        promotion.setConditionData(JSONUtil.toString(promotionCondition));
        when(promotionItmRepo.findOne(anyLong())).thenReturn(promotion);
        List<FreebieForProduct> freebieForProducts = droolsUtil.setFreeForProductItm(promotionIdList);
        assertThat(freebieForProducts.get(0).getPromotionId(), is("1"));
    }
}
