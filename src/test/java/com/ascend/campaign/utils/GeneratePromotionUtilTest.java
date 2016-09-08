package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.PromotionWM;
import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.DiscountCodeCriteriaValue;
import com.ascend.campaign.models.MNPVariant;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.PromotionParams;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GeneratePromotionUtilTest {
    private Promotion promotion;
    private PromotionWM promotion2;
    private GeneratePromotionUtil generatePromotionUtil;
    private PromotionCondition promotionCondition;

    @Before
    public void setUp() {
        generatePromotionUtil = new GeneratePromotionUtil();
        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        final List<String> freeSkuList = Lists.newArrayList("FREESKU1", "FREESKU2", "FREESKU3");
        List<String> brandList = Lists.newArrayList("BRAND1", "BRAND2", "BRAND3");

        final String startPeriod = "01-Jun-2015 08:00:00";
        final String endPeriod = "05-Jun-2015 08:00:00";
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
        DiscountCodeCriteriaValue discountCodeCriteriaValue2 = new DiscountCodeCriteriaValue();
        discountCodeCriteriaValue2.setVariantId("SKU2");
        DiscountCodeCriteriaValue discountCodeCriteriaValue3 = new DiscountCodeCriteriaValue();
        discountCodeCriteriaValue3.setVariantId("SKU3");
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
        promotion2.setBusinessChannel("wemall");
        promotion2.setDescriptionEn("promotionDescriptionTranslation");
        promotion2.setShortDescriptionEn("promotionShortDescriptionTranslation");

    }

    @Test
    public void shouldGenerateStringDataITMPromotionFreebieCorrectly() throws Exception {
        promotion.setType("itm-freebie");
        promotion.setMember(false);
        promotion.setNonMember(true);
        promotion.getPromotionCondition().setMinTotalValue(null);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$quantity : "
                + "quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId"
                + " != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$quantity : "
                + "quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId "
                + "!= \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$quantity : "
                + "quantity)"
                + " from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

    }

    @Test
    public void shouldGenerateStringDataITMPromotionFreebieDiscountByValueCorrectly() throws Exception {
        promotion.setType("itm-freebie");
        promotion.setMember(false);
        promotion.setNonMember(true);
        promotion.getPromotionCondition().setMinTotalValue(500);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value :"
                + " getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$sumQuantity/500) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/500),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$sumQuantity/500) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/500),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value :"
                + " getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$sumQuantity/500) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/500),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

    }

    @Test
    public void shouldGenerateStringDataWMPromotionFreebieCorrectly() throws Exception {
        promotion2.setType("wm-freebie");
        promotion2.setMember(false);
        promotion2.setNonMember(true);
        promotion2.getPromotionCondition().setMinTotalValue(null);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$quantity : "
                + "quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(false);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),"
                + "$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(true);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId"
                + " != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$quantity : "
                + "quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

    }

    @Test
    public void shouldGenerateStringDataWMPromotionFreebieDiscountByValueCorrectly() throws Exception {
        promotion2.setType("wm-freebie");
        promotion2.setMember(false);
        promotion2.setNonMember(true);
        promotion2.getPromotionCondition().setMinTotalValue(500);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$sumQuantity/500) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/500),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(false);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$sumQuantity/500) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/500),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(true);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == \"SKU2\" "
                + "||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value :"
                + " getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$sumQuantity/500) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/500),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

    }

    @Test
    public void shouldGenerateStringDataITMPromotion2WithRepeatIsNullAndQuantityIsNullCorrectly() throws Exception {
        promotion.setType("itm-discount_promotion");
        promotion.setMember(false);
        promotion.setNonMember(true);

        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        List<String> freeSkuList = Lists.newArrayList("FREESKU1", "FREESKU2", "FREESKU3");
        List<String> brandList = Lists.newArrayList("BRAND1", "BRAND2", "BRAND3");
        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setBrands(brandList);
        promotionCondition.setVariants(skuList);
        promotionCondition.setExcludedVariants(skuList);
        promotionCondition.setFreeVariants(freeSkuList);
        promotionCondition.setQuantity(null);
        promotionCondition.setDiscountPercent(80.00);
        promotionCondition.setDiscountFixed(0.0);
        promotionCondition.setMinTotalValue(10000);
        promotionCondition.setWeekDays(72L);
        promotionCondition.setMasterCardPercent(30.0);
        promotionCondition.setStartTime("18:30");
        promotionCondition.setEndTime("23:20");
        promotionCondition.setPromotionCode("AAACode");

        promotion.setPromotionCondition(promotionCondition);
        promotion.setRepeat(null);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity"
                + " ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity) from $products, "
                + "sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\"),$value : getDiscountPriceDrl() ,"
                + " $quantity : quantity) from $products, sum( $value * $quantity "
                + ") )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

    }

    @Test
    public void shouldGenerateStringDataWMPromotion2WithRepeatIsNullAndQuantityIsNullCorrectly() throws Exception {
        promotion2.setType("wm-discount_promotion");
        promotion2.setMember(false);
        promotion2.setNonMember(true);

        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        List<String> freeSkuList = Lists.newArrayList("FREESKU1", "FREESKU2", "FREESKU3");
        List<String> brandList = Lists.newArrayList("BRAND1", "BRAND2", "BRAND3");
        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setBrands(brandList);
        promotionCondition.setVariants(skuList);
        promotionCondition.setExcludedVariants(skuList);
        promotionCondition.setFreeVariants(freeSkuList);
        promotionCondition.setQuantity(null);
        promotionCondition.setDiscountPercent(80.00);
        promotionCondition.setDiscountFixed(0.0);
        promotionCondition.setMinTotalValue(10000);
        promotionCondition.setWeekDays(72L);
        promotionCondition.setMasterCardPercent(30.0);
        promotionCondition.setStartTime("18:30");
        promotionCondition.setEndTime("23:20");
        promotionCondition.setPromotionCode("AAACode");
        promotionCondition.setCriteriaType("brand");
        promotionCondition.setCriteriaValue(brandList);

        promotion2.setPromotionCondition(promotionCondition);
        promotion2.setRepeat(null);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity"
                + " ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(false);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value :"
                + " getDiscountPriceDrl() , $quantity : quantity) from $products, "
                + "sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(true);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() ,"
                + " $quantity : quantity) from $products, sum( $value * $quantity "
                + ") )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

    }

    @Test
    public void shouldGenerateStringDataWMDiscountPromotionCorrectly() throws Exception {
        promotion2.setType("wm-discount_promotion");
        promotion2.setMember(false);
        promotion2.setNonMember(true);

        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        List<String> freeSkuList = Lists.newArrayList("FREESKU1", "FREESKU2", "FREESKU3");
        List<String> brandList = Lists.newArrayList("BRAND1", "BRAND2", "BRAND3");
        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setBrands(brandList);
        promotionCondition.setVariants(skuList);
        promotionCondition.setExcludedVariants(skuList);
        promotionCondition.setFreeVariants(freeSkuList);
        promotionCondition.setQuantity(null);
        promotionCondition.setDiscountPercent(80.00);
        promotionCondition.setDiscountFixed(0.0);
        promotionCondition.setMinTotalValue(10000);
        promotionCondition.setWeekDays(72L);
        promotionCondition.setMasterCardPercent(30.0);
        promotionCondition.setStartTime("18:30");
        promotionCondition.setEndTime("23:20");
        promotionCondition.setPromotionCode("AAACode");
        promotionCondition.setCriteriaType(CampaignEnum.BRAND.getContent());
        promotionCondition.setCriteriaValue(brandList);

        promotion2.setPromotionCondition(promotionCondition);
        promotion2.setRepeat(null);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity"
                + " ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotionCondition.setCriteriaType(CampaignEnum.VARIANT.getContent());
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"BRAND1\" || variantId == "
                + "\"BRAND2\" || variantId == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity"
                + " ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotionCondition.setCriteriaType(CampaignEnum.COLLECTION.getContent());
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((collection == \"BRAND1\" || collection == "
                + "\"BRAND2\" || collection == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity"
                + " ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotionCondition.setCriteriaType(CampaignEnum.CATEGORY.getContent());
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((categoryCode == "
                + "\"BRAND1\" || categoryCode == "
                + "\"BRAND2\" || categoryCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity"
                + " ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));


    }

    @Test
    public void shouldGenerateStringDataPromotion2WithZeroRepeatCorrectly() throws Exception {
        promotion.setType("itm-discount_promotion");
        promotion.setMember(false);
        promotion.setNonMember(true);
        Promotion customPromotion = promotion;
        customPromotion.setRepeat(0);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity) from $products, "
                + "sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(customPromotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity) from $products, "
                + "sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(customPromotion));

        promotion.setMember(true);
        promotion.setNonMember(true);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != "
                + "\"SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity) from $products,"
                + " sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),"
                + Integer.MAX_VALUE + "));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(customPromotion));

    }

    @Test
    public void shouldGenerateStringDataPromotion2Correctly() throws Exception {
        promotion.setType("itm-discount_promotion");
        promotion.setMember(false);
        promotion.setNonMember(true);
        assertEquals("\n"
                +
                "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && "
                + "variantId != \""
                + "SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity)"
                + " from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n"
                +
                "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" &&"
                + " variantId != \""
                + "SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity)"
                + " from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);
        assertEquals("\n"
                +
                "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  "
                + "$products : products)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((brandCode == \"BRAND1\" || brandCode == "
                + "\"BRAND2\" || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" &&"
                + " variantId != \""
                + "SKU3\"),$value : getDiscountPriceDrl() , $quantity : quantity)"
                + " from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($minTotalValue/10000),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

    }

    @Test
    public void shouldGenerateStringDataITMPromotion3Correctly() throws Exception {
        promotion.setType("itm-option_to_buy");
        promotion.setMember(false);
        promotion.setNonMember(true);
        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product(variantId == \"SKU1\" || variantId == \"SKU2\" "
                + "|| variantId == \"SKU3\",$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product(variantId == \"SKU1\" || variantId == \"SKU2\" "
                + "|| variantId == \"SKU3\",$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);

        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product(variantId == \"SKU1\" || variantId == \"SKU2\" "
                + "|| variantId == \"SKU3\",$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

    }

    @Test
    public void shouldGenerateStringDataWMPromotion3Correctly() throws Exception {
        promotion2.setType("wm-option_to_buy");
        promotion2.setMember(false);
        promotion2.setNonMember(true);
        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product(variantId == \"SKU1\" || variantId == \"SKU2\" "
                + "|| variantId == \"SKU3\",$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(false);

        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product(variantId == \"SKU1\" || variantId == \"SKU2\" "
                + "|| variantId == \"SKU3\",$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(true);

        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" , "
                + " $products : products)\n"
                + "    $sumQuantity : Double()  from accumulate(Product(variantId == \"SKU1\" || variantId == \"SKU2\" "
                + "|| variantId == \"SKU3\",$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval(Math.floor($sumQuantity/1) > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",Math.min((int)Math.floor($sumQuantity/1),10));\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

    }

    @Test
    public void shouldGenerateStringDataITMPromotion4Correctly() throws Exception {
        promotion.setType("itm-specific_time");
        promotion.setMember(false);
        promotion.setNonMember(true);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products,$promotionCode "
                + ": promotionCode,$cardType : cardType)\n"
                + "    eval(($cart.getDayOfWeek() & 72) > 0)\n"
                + "    eval(\"AAACode\".equals($promotionCode))\n"
                + "    eval($cart.getEpochCurrentTime() > 75600 && $cart.getEpochCurrentTime()< 84600)\n"
                + "\n"
                + "    $count : Long() from accumulate(Product((brandCode == \"BRAND1\" || brandCode == \"BRAND2\" ||"
                + " brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\"),$value :"
                + " getDiscountPriceDrl() ) from $products, count( $value ) )\n"
                + "    eval($count > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products,$promotionCode :"
                + " promotionCode,$cardType : cardType)\n"
                + "    eval(($cart.getDayOfWeek() & 72) > 0)\n"
                + "    eval(\"AAACode\".equals($promotionCode))\n"
                + "    eval($cart.getEpochCurrentTime() > 75600 && $cart.getEpochCurrentTime()< 84600)\n"
                + "\n"
                + "    $count : Long() from accumulate(Product((brandCode == \"BRAND1\" || brandCode == \"BRAND2\""
                + " || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\")"
                + ",$value : getDiscountPriceDrl() ) from $products, count( $value ) )\n"
                + "    eval($count > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : products,"
                + "$promotionCode : promotionCode,$cardType : cardType)\n"
                + "    eval(($cart.getDayOfWeek() & 72) > 0)\n"
                + "    eval(\"AAACode\".equals($promotionCode))\n"
                + "    eval($cart.getEpochCurrentTime() > 75600 && $cart.getEpochCurrentTime()< 84600)\n"
                + "\n"
                + "    $count : Long() from accumulate(Product((brandCode == \"BRAND1\" || brandCode == \"BRAND2\" ||"
                + " brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\"),$value :"
                + " getDiscountPriceDrl() ) from $products, count( $value ) )\n"
                + "    eval($count > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));
    }

    @Test
    public void shouldGenerateStringDataWMPromotion4Correctly() throws Exception {
        promotion2.setType("wm-specific_time");
        promotion2.setMember(false);
        promotion2.setNonMember(true);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products,$promotionCode "
                + ": promotionCode,$cardType : cardType)\n"
                + "    eval(($cart.getDayOfWeek() & 72) > 0)\n"
                + "    eval(\"AAACode\".equals($promotionCode))\n"
                + "    eval($cart.getEpochCurrentTime() > 75600 && $cart.getEpochCurrentTime()< 84600)\n"
                + "\n"
                + "    $count : Long() from accumulate(Product((brandCode == \"BRAND1\" || brandCode == \"BRAND2\" ||"
                + " brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\"),$value :"
                + " getDiscountPriceDrl() ) from $products, count( $value ) )\n"
                + "    eval($count > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(false);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products,$promotionCode :"
                + " promotionCode,$cardType : cardType)\n"
                + "    eval(($cart.getDayOfWeek() & 72) > 0)\n"
                + "    eval(\"AAACode\".equals($promotionCode))\n"
                + "    eval($cart.getEpochCurrentTime() > 75600 && $cart.getEpochCurrentTime()< 84600)\n"
                + "\n"
                + "    $count : Long() from accumulate(Product((brandCode == \"BRAND1\" || brandCode == \"BRAND2\""
                + " || brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\")"
                + ",$value : getDiscountPriceDrl() ) from $products, count( $value ) )\n"
                + "    eval($count > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(true);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : products,"
                + "$promotionCode : promotionCode,$cardType : cardType)\n"
                + "    eval(($cart.getDayOfWeek() & 72) > 0)\n"
                + "    eval(\"AAACode\".equals($promotionCode))\n"
                + "    eval($cart.getEpochCurrentTime() > 75600 && $cart.getEpochCurrentTime()< 84600)\n"
                + "\n"
                + "    $count : Long() from accumulate(Product((brandCode == \"BRAND1\" || brandCode == \"BRAND2\" ||"
                + " brandCode == \"BRAND3\") && (variantId != \"SKU1\" && variantId != \"SKU2\" && variantId != "
                + "\"SKU3\"),$value :"
                + " getDiscountPriceDrl() ) from $products, count( $value ) )\n"
                + "    eval($count > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataITMPromotionForProduct1Correctly() throws Exception {
        promotion.setType("itm-freebie");
        promotion.getPromotionCondition().setMinTotalValue(null);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant "
                + ",$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"SKU1\".equals($productVariant) || \"SKU2\".equals($productVariant) "
                + "|| \"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmProductPromotion(promotion));

        promotion.getPromotionCondition().setCriteriaType(CampaignEnum.BRAND.getContent());

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant "
                + ",$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"SKU1\".equals($brandVariant) || \"SKU2\".equals($brandVariant) "
                + "|| \"SKU3\".equals($brandVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmProductPromotion(promotion));

        promotion.getPromotionCondition().setCriteriaType("");

        assertEquals("", generatePromotionUtil.generateStringDataItmProductPromotion(promotion));
    }

    @Test
    public void shouldGenerateStringDataWMPromotionForProduct1Correctly() throws Exception {
        promotion2.setType("wm-freebie");
        promotion2.getPromotionCondition().setMinTotalValue(null);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant "
                + ",$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"SKU1\".equals($productVariant) || \"SKU2\".equals($productVariant) "
                + "|| \"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataWMPromotionForProductDiscountByCodeCorrectly() throws Exception {
        promotion2.setType("wm-discount_by_code");

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant "
                + ",$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"SKU1\".equals($productVariant) || \"SKU2\".equals($productVariant) "
                + "|| \"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataITMPromotionForProduct2Correctly() throws Exception {
        promotion.setType("itm-discount_promotion");
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant , $brandVariant : "
                + "brandVariant)\n"
                + "    eval( \"BRAND1\".equals($brandVariant) || \"BRAND2\".equals($brandVariant) "
                + "|| \"BRAND3\".equals($brandVariant))\n"
                + "    eval( !\"SKU1\".equals($productVariant) && !\"SKU2\".equals($productVariant) && "
                + "!\"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmProductPromotion(promotion));
    }

    @Test
    public void shouldGenerateStringDataWMPromotionForProduct2Correctly() throws Exception {
        promotionCondition = new PromotionCondition();
        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        promotionCondition.setExcludedVariants(skuList);
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
        promotionCondition.setCriteriaType("brand");
        promotionCondition.setCriteriaValue(Lists.newArrayList("BRAND1", "BRAND2", "BRAND3"));
        promotionCondition.setFreeQuantity(1);
        promotion2.setType("wm-discount_promotion");
        promotion2.setPromotionCondition(promotionCondition);
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant , $brandVariant : "
                + "brandVariant)\n"
                + "    eval( \"BRAND1\".equals($brandVariant) || \"BRAND2\".equals($brandVariant) "
                + "|| \"BRAND3\".equals($brandVariant))\n"
                + "    eval( !\"SKU1\".equals($productVariant) && !\"SKU2\".equals($productVariant) && "
                + "!\"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataWMProductFreebieBrandTypeCorrectly() throws Exception {
        promotionCondition = new PromotionCondition();
        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        promotionCondition.setExcludedVariants(skuList);
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
        promotionCondition.setCriteriaType(CampaignEnum.BRAND.getContent());
        promotionCondition.setCriteriaValue(Lists.newArrayList("BRAND1", "BRAND2", "BRAND3"));
        promotionCondition.setFreeQuantity(1);
        promotion2.setType("wm-freebie");
        promotion2.setPromotionCondition(promotionCondition);
        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : "
                + "productVariant ,$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"BRAND1\".equals($brandVariant) || \"BRAND2\"."
                + "equals($brandVariant) || \"BRAND3\".equals($brandVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataWMProductFreebieCollectionTypeCorrectly() throws Exception {
        promotionCondition = new PromotionCondition();
        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        promotionCondition.setExcludedVariants(skuList);
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
        promotionCondition.setCriteriaType(CampaignEnum.COLLECTION.getContent());
        promotionCondition.setCriteriaValue(Lists.newArrayList("col1", "col2", "col3"));
        promotionCondition.setFreeQuantity(1);
        promotion2.setType("wm-freebie");
        promotion2.setPromotionCondition(promotionCondition);
        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : "
                + "productVariant ,$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"col1\".equals($collection) || \"col2\"."
                + "equals($collection) || \"col3\".equals($collection))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataWMProductFreebieCategoryTypeCorrectly() throws Exception {
        promotionCondition = new PromotionCondition();
        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        promotionCondition.setExcludedVariants(skuList);
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
        promotionCondition.setCriteriaType(CampaignEnum.CATEGORY.getContent());
        promotionCondition.setCriteriaValue(Lists.newArrayList("cat1", "cat2", "cat3"));
        promotionCondition.setFreeQuantity(1);
        promotion2.setType("wm-freebie");
        promotion2.setPromotionCondition(promotionCondition);
        assertEquals("\n\nrule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : "
                + "productVariant ,$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"cat1\".equals($category) || \"cat2\"."
                + "equals($category) || \"cat3\".equals($category))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataWMProductFreebieNotMatchTypeTypeCorrectly() throws Exception {
        promotionCondition = new PromotionCondition();
        promotionCondition.setCriteriaType("test");
        promotion2.setType("wm-freebie");
        promotion2.setPromotionCondition(promotionCondition);
        assertEquals("",
                generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataITMPromotionForProduct3Correctly() throws Exception {
        promotion.setType("itm-option_to_buy");
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant "
                + ",$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"SKU1\".equals($productVariant) || \"SKU2\".equals($productVariant) "
                + "|| \"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmProductPromotion(promotion));
    }

    @Test
    public void shouldGenerateStringDataWMPromotionForProduct3Correctly() throws Exception {
        promotion2.setType("wm-option_to_buy");
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant ,"
                + "$brandVariant : brandVariant ,$collection : collection ,$category : category)\n"
                + "    eval( \"SKU1\".equals($productVariant) || \"SKU2\".equals($productVariant) "
                + "|| \"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataITMPromotionForProduct4Correctly() throws Exception {
        promotion.setType("itm-specific_time");
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant , $brandVariant : "
                + "brandVariant)\n"
                + "    eval( \"BRAND1\".equals($brandVariant) || \"BRAND2\".equals($brandVariant) "
                + "|| \"BRAND3\".equals($brandVariant))\n"
                + "    eval( !\"SKU1\".equals($productVariant) && !\"SKU2\".equals($productVariant) &&"
                + " !\"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmProductPromotion(promotion));
    }

    @Test
    public void shouldGenerateStringDataWMPromotionForProduct4Correctly() throws Exception {
        promotion2.setType("wm-specific_time");
        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $promotionProduct : PromotionProduct($productVariant : productVariant , $brandVariant : "
                + "brandVariant)\n"
                + "    eval( \"BRAND1\".equals($brandVariant) || \"BRAND2\".equals($brandVariant) "
                + "|| \"BRAND3\".equals($brandVariant))\n"
                + "    eval( !\"SKU1\".equals($productVariant) && !\"SKU2\".equals($productVariant) &&"
                + " !\"SKU3\".equals($productVariant))\n"
                + "then\n"
                + "    $promotionProduct.addPromotionsIdList(1);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductPromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataITMPromotion5Correctly() throws Exception {
        promotion.setType("itm-bundle");
        promotion.setMember(false);
        promotion.setNonMember(true);

        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("AA");
        bundleVariant1.setDiscountPercent(30.0);


        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");

        bundleVariant2.setDiscountFixed(15.0);

        BundleVariant bundleVariant3 = new BundleVariant();
        bundleVariant3.setBundleVariant("DD");
        bundleVariant3.setDiscountPercent(0.0);
        bundleVariant3.setDiscountFixed(25.0);

        bundleVariantArrayList.add(bundleVariant1);
        bundleVariantArrayList.add(bundleVariant2);
        bundleVariantArrayList.add(bundleVariant3);

        PromotionParams promotionParams = new PromotionParams();
        promotionParams.setPricePlanCode("PricePlanCode");

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setBundleVariant(bundleVariantArrayList);
        promotionCondition.setNote("note");
        promotionCondition.setNoteEn("noteEn");

        promotion.setPromotionCondition(promotionCondition);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products , $promotionType "
                + ": promotionType ,$promotionParams : promotionParams)\n"
                + "    $countPrimary : Double() from accumulate(Product(variantId == \"AA\",$quantity : quantity) "
                + "from $products, sum( $quantity ) )\n"
                + "    eval($countPrimary > 0)\n"
                + "    $variant : List() from accumulate(Product($variantId : variantId)from $products,collectList"
                + "($variantId))\n"
                + "    $countBundleSku : Double() from accumulate(Product($variant contains \"BB\" && $variant "
                + "contains \"DD\","
                + "$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval($countBundleSku > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end", generatePromotionUtil.generateStringDataItmPromotion(promotion));

    }

    @Test
    public void shouldGenerateStringDataWMPromotion5Correctly() throws Exception {
        promotion2.setType("wm-bundle");
        promotion2.setMember(false);
        promotion2.setNonMember(true);

        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("AA");
        bundleVariant1.setDiscountPercent(30.0);


        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");

        bundleVariant2.setDiscountFixed(15.0);

        BundleVariant bundleVariant3 = new BundleVariant();
        bundleVariant3.setBundleVariant("DD");
        bundleVariant3.setDiscountPercent(0.0);
        bundleVariant3.setDiscountFixed(25.0);

        bundleVariantArrayList.add(bundleVariant1);
        bundleVariantArrayList.add(bundleVariant2);
        bundleVariantArrayList.add(bundleVariant3);

        PromotionParams promotionParams = new PromotionParams();
        promotionParams.setPricePlanCode("PricePlanCode");

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setBundleVariant(bundleVariantArrayList);
        promotionCondition.setNote("note");
        promotionCondition.setNoteEn("noteEn");

        promotion2.setPromotionCondition(promotionCondition);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products , $promotionType "
                + ": promotionType ,$promotionParams : promotionParams)\n"
                + "    $countPrimary : Double() from accumulate(Product(variantId == \"AA\",$quantity : quantity) "
                + "from $products, sum( $quantity ) )\n"
                + "    eval($countPrimary > 0)\n"
                + "    $variant : List() from accumulate(Product($variantId : variantId)from $products,collectList"
                + "($variantId))\n"
                + "    $countBundleSku : Double() from accumulate(Product($variant contains \"BB\" && $variant "
                + "contains \"DD\","
                + "$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval($countBundleSku > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

    }

    @Test
    public void shouldGenerateStringDataITMPromotionForProductBundleCorrectly() throws Exception {
        promotion.setType("itm-bundle");
        promotion.setMember(false);
        promotion.setNonMember(true);

        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("AA");
        bundleVariant1.setDiscountPercent(30.0);


        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");

        bundleVariant2.setDiscountFixed(15.0);

        BundleVariant bundleVariant3 = new BundleVariant();
        bundleVariant3.setBundleVariant("DD");
        bundleVariant3.setDiscountPercent(0.0);
        bundleVariant3.setDiscountFixed(25.0);

        bundleVariantArrayList.add(bundleVariant1);
        bundleVariantArrayList.add(bundleVariant2);
        bundleVariantArrayList.add(bundleVariant3);

        PromotionParams promotionParams = new PromotionParams();
        promotionParams.setPricePlanCode("PricePlanCode");

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setBundleVariant(bundleVariantArrayList);
        promotionCondition.setNote("bundleNote");
        promotionCondition.setNoteEn("bundleNoteTranslation");
        promotion.setPromotionCondition(promotionCondition);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $bundleProduct : BundleProduct( $productVariant : productVariant  )\n"
                + "      eval( \"AA\".equals($productVariant) || \"BB\".equals($productVariant) "
                + "|| \"DD\".equals($productVariant))\n"
                + "then\n"
                + "    $bundleProduct.addPromotionId(\"1\");\n"
                + "end\n", generatePromotionUtil.generateStringDataItmProductBundlePromotion(promotion));
    }

    @Test
    public void shouldGenerateStringDataITMPromotionForProductMNPCorrectly() throws Exception {
        promotion.setType("itm-mnp");
        promotion.setMember(false);
        promotion.setNonMember(true);

        final ArrayList<MNPVariant> mnpVariantArrayList = new ArrayList<>();
        MNPVariant mnpVariant1 = new MNPVariant();
        mnpVariant1.setMnpVariants("AA");
        mnpVariant1.setDiscountPercent(30.0);


        MNPVariant mnpVariant2 = new MNPVariant();
        mnpVariant2.setMnpVariants("BB");
        mnpVariant2.setDiscountFixed(15.0);

        MNPVariant mmnpVariant3 = new MNPVariant();
        mmnpVariant3.setMnpVariants("DD");
        mmnpVariant3.setDiscountPercent(0.0);
        mmnpVariant3.setDiscountFixed(25.0);

        mnpVariantArrayList.add(mnpVariant1);
        mnpVariantArrayList.add(mnpVariant2);
        mnpVariantArrayList.add(mmnpVariant3);



        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setMnpVariants(mnpVariantArrayList);
        promotionCondition.setNote("mnpNote");
        promotionCondition.setNoteEn("mnpNoteTranslation");
        promotion.setPromotionCondition(promotionCondition);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $mnpProduct : MNPProduct( $productVariant : productVariant  )\n"
                + "      eval( \"AA\".equals($productVariant) || \"BB\".equals($productVariant) "
                + "|| \"DD\".equals($productVariant))\n"
                + "then\n"
                + "    $mnpProduct.addPromotionId(\"1\");\n"
                + "end\n", generatePromotionUtil.generateStringDataItmProductMNPPromotion(promotion));
    }

    @Test
    public void shouldGenerateStringDataWMPromotionForProductBundleCorrectly() throws Exception {
        promotion2.setType("wm-bundle");
        promotion2.setMember(false);
        promotion2.setNonMember(true);

        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("AA");
        bundleVariant1.setDiscountPercent(30.0);


        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");

        bundleVariant2.setDiscountFixed(15.0);

        BundleVariant bundleVariant3 = new BundleVariant();
        bundleVariant3.setBundleVariant("DD");
        bundleVariant3.setDiscountPercent(0.0);
        bundleVariant3.setDiscountFixed(25.0);

        bundleVariantArrayList.add(bundleVariant1);
        bundleVariantArrayList.add(bundleVariant2);
        bundleVariantArrayList.add(bundleVariant3);

        PromotionParams promotionParams = new PromotionParams();
        promotionParams.setPricePlanCode("PricePlanCode");

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setBundleVariant(bundleVariantArrayList);
        promotionCondition.setNote("bundleNote");
        promotionCondition.setNoteEn("bundleNoteTranslation");
        promotion2.setPromotionCondition(promotionCondition);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $bundleProduct : BundleProduct( $productVariant : productVariant  )\n"
                + "      eval( \"AA\".equals($productVariant) || \"BB\".equals($productVariant) "
                + "|| \"DD\".equals($productVariant))\n"
                + "then\n"
                + "    $bundleProduct.addPromotionId(\"1\");\n"
                + "end\n", generatePromotionUtil.generateStringDataWmProductBundlePromotion(promotion2));
    }

    @Test
    public void shouldGenerateStringDataITMPromotion6Correctly() throws Exception {
        promotion.setType("itm-discount_by_code");
        promotion.setMember(false);
        promotion.setNonMember(true);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products "
                + ",$promotionCode : promotionCode ,$codeGroupId"
                +
                " :"
                + " codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId !="
                + " \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection "
                + "!= \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(false);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products "
                + ",$promotionCode : promotionCode ,$codeGroupId : codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId =="
                + " \"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection "
                + "!= \"\"),$value : getDiscountPriceDrl() "
                + ", $quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : "
                + "products ,$promotionCode : promotionCode "
                + ",$codeGroupId"
                + " : codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection "
                + "!= \"\"),$value : getDiscountPriceDrl() , "
                + "$quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.setMember(true);
        promotion.setNonMember(true);
        promotionCondition.setMaxDiscountValue(null);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : "
                + "products ,$promotionCode : promotionCode "
                + ",$codeGroupId"
                + " : codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection"
                + " != \"\"),$value : getDiscountPriceDrl() , "
                + "$quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));

        promotion.getPromotionCondition().setCodeGroupId(3L);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : "
                + "products ,$promotionCode : promotionCode "
                + ",$codeGroupId"
                + " : codeGroupId)\n"
                + "    eval(3 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" && collection"
                + " != \"\"),$value : getDiscountPriceDrl() , "
                + "$quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataItmPromotion(promotion));


    }

    @Test
    public void shouldGenerateStringDataWMPromotion6Correctly() throws Exception {
        promotion2.setType("wm-discount_by_code");
        promotion2.setMember(false);
        promotion2.setNonMember(true);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products "
                + ", $promotionCode : promotionCode ,$codeGroupId"
                +
                " :"
                + " codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||"
                + "variantId == \"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId !="
                + " \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" &&"
                + " collection != \"\"),$value : "
                + "getDiscountPriceDrl() , $quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(false);

        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\",  $products : products "
                + ", $promotionCode : promotionCode ,$codeGroupId : codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\""
                + " && collection != \"\"),$value : getDiscountPriceDrl() "
                + ", $quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(true);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : "
                + "products , $promotionCode : promotionCode "
                + ",$codeGroupId"
                + " : codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" "
                + "&& collection != \"\"),$value : getDiscountPriceDrl() , "
                + "$quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion2.setMember(true);
        promotion2.setNonMember(true);
        promotionCondition.setMaxDiscountValue(null);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : "
                + "products , $promotionCode : promotionCode"
                + " ,$codeGroupId"
                + " : codeGroupId)\n"
                + "    eval(0 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" "
                + "&& collection != \"\"),$value : getDiscountPriceDrl() , "
                + "$quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));

        promotion.getPromotionCondition().setCodeGroupId(3L);
        assertEquals("\n"
                + "\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"user\" || customerType == \"non-user\" ,  $products : "
                + "products , $promotionCode : promotionCode"
                + " ,$codeGroupId"
                + " : codeGroupId)\n"
                + "    eval(3 == $codeGroupId)\n"
                + "    $minTotalValue : Double()  from accumulate(Product((variantId == \"SKU1\" ||variantId == "
                + "\"SKU2\" ||variantId == \"SKU3\") && (variantId != \"SKU1\" && variantId "
                + "!= \"SKU2\" && variantId != \"SKU3\" && brandCode != \"\" && categoryCode != \"\" "
                + "&& collection != \"\"),$value : getDiscountPriceDrl() , "
                + "$quantity : quantity) from $products, sum( $value * $quantity ) )\n"
                + "    eval(Math.floor((double)$minTotalValue/10000 )> 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end\n", generatePromotionUtil.generateStringDataWmPromotion(promotion2));


    }

    @Test
    public void shouldGenerateStringDataITMPromotionMNPCorrectly() throws Exception {
        promotion.setType(PromotionTypeEnum.ITM_MNP.getContent());
        promotion.setMember(false);
        promotion.setNonMember(true);

        final ArrayList<MNPVariant> mnpVariants = new ArrayList<>();
        MNPVariant mnpVariant = new MNPVariant();
        mnpVariant.setMnpVariants("P1,P2");
        mnpVariant.setDiscountPercent(30.0);

        MNPVariant mnpVariant2 = new MNPVariant();
        mnpVariant2.setMnpVariants("AA");
        mnpVariant2.setDiscountPercent(30.0);

        MNPVariant mnpVariant3 = new MNPVariant();
        mnpVariant3.setMnpVariants("BB");
        mnpVariant3.setDiscountPercent(30.0);

        mnpVariants.add(mnpVariant);
        mnpVariants.add(mnpVariant2);
        mnpVariants.add(mnpVariant3);

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setMnpVariants(mnpVariants);

        promotion.setPromotionCondition(promotionCondition);

        assertEquals("\n\n"
                + "rule \"promotion_1\"\n"
                + "date-effective \"01-Jun-2015 08:00:00\"\n"
                + "date-expires \"05-Jun-2015 08:00:00\"\n"
                + "when\n"
                + "    $cart : Cart(customerType == \"non-user\",  $products : products , $promotionType "
                + ": promotionType ,$promotionParams : promotionParams)\n"
                + "    $countPrimary : Double() from accumulate(Product(variantId == \"P1\" || variantId == \"P2\""
                + ",$quantity : quantity) "
                + "from $products, sum( $quantity ) )\n"
                + "    eval($countPrimary > 0)\n"
                + "    $variant : List() from accumulate(Product($variantId : variantId)from $products,collectList"
                + "($variantId))\n"
                + "    $countMNPSku : Double() from accumulate(Product($variant contains \"AA\" && $variant "
                + "contains \"BB\","
                + "$quantity : quantity) from $products, sum( $quantity ) )\n"
                + "    eval($countMNPSku > 0)\n"
                + "then\n"
                + "    $cart.addPromotion(\"1\",null);\n"
                + "end", generatePromotionUtil.generateStringDataItmPromotion(promotion));

    }
}

