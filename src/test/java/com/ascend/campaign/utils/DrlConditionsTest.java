package com.ascend.campaign.utils;

import com.ascend.campaign.constants.DrlOperator;
import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.MNPVariant;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.PromotionParams;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DrlConditionsTest {
    private DrlConditions drlConditions;

    @Before
    public void setUp() {
        drlConditions = new DrlConditions();
    }

    @Test
    public void shouldGenerateConditionCorrectly() {
        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");

        assertEquals("variantId == \"SKU1\" || variantId == \"SKU2\" || variantId == \"SKU3\"",
                drlConditions.generateCondition(
                        skuList,
                        DrlOperator.DRL_OPERATOR_OR,
                        "variantId"));
    }

    @Test
    public void shouldNotGenerateConditionWithNullProduct() {
        List<String> skuList = null;

        assertEquals("",
                drlConditions.generateCondition(
                        skuList,
                        DrlOperator.DRL_OPERATOR_OR,
                        "variantId"));
    }

    @Test
    public void shouldGenerateNotConditionCorrectly() {
        List<String> skuList = Lists.newArrayList("SKU1", "SKU2", "SKU3");
        assertEquals("variantId != \"SKU1\" || variantId != \"SKU2\" || variantId != \"SKU3\"",
                drlConditions.generateNotCondition(
                        skuList,
                        DrlOperator.DRL_OPERATOR_OR,
                        "variantId"));
    }

    @Test
    public void shouldNotGenerateNotConditionWithNullProduct() {
        assertEquals("",
                drlConditions.generateNotCondition(
                        null,
                        DrlOperator.DRL_OPERATOR_OR,
                        "variantId"));
    }

    @Test
    public void shouldGenerateNameFunctionCorrectly() {
        long name = 10;
        assertEquals("buildPromotion10", drlConditions.generateNameFunction(name));
    }

    @Test
    public void shouldGenerateUserTypeCorrectly() {
        boolean user = true;
        boolean nonUser = true;

        assertEquals("customerType == \"user\" || customerType == \"non-user\" ,",
                drlConditions.generateUserType(user, nonUser));

        boolean user2 = true;
        boolean nonUser2 = false;

        assertEquals("customerType == \"user\",",
                drlConditions.generateUserType(user2, nonUser2));

        boolean user3 = false;
        boolean nonUser3 = true;

        assertEquals("customerType == \"non-user\",",
                drlConditions.generateUserType(user3, nonUser3));

    }

    @Test
    public void shouldGeneratePromotionCodeCorrectly() {
        String promotionCode = "codeAAA";
        assertEquals("eval(\"codeAAA\".equals($promotionCode))",
                drlConditions.generateBundleTypeOrPromotionCode(promotionCode, "$promotionCode"));


    }

    @Test
    public void shouldGenerateTimePromotion4Correctly() {
        String startTime = "20:00";
        String endTime = "23:00";
        assertEquals("eval($cart.getEpochCurrentTime() > 72000 && $cart.getEpochCurrentTime()< 82800)\n",
                drlConditions.generateTimePromotion4(startTime, endTime));
    }

    @Test
    public void shouldGenerateRuleNotProductConditionCorrectly() {
        List<String> excludedSkus = Arrays.asList("SKU1", "SKU2", "SKU3");
        assertEquals("eval( !\"SKU1\".equals($productVariant) && !\"SKU2\".equals($productVariant) &&"
                        + " !\"SKU3\".equals($productVariant))",
                drlConditions.generateRuleNotProductCondition(excludedSkus,
                        DrlOperator.DRL_OPERATOR_AND, "$productVariant"));
    }

    @Test
    public void shouldNotGenerateRuleNotProductConditionWhenListIsEmpty() {
        assertEquals("",
                drlConditions.generateRuleNotProductCondition(null,
                        DrlOperator.DRL_OPERATOR_AND, "$productVariant"));
    }

    @Test
    public void shouldNotGenerateRuleProductConditionWhenListIsEmpty() {
        assertEquals("",
                drlConditions.generateEqualsConditionInDrlTemplate(null,
                        DrlOperator.DRL_OPERATOR_AND, "$productVariant"));
    }

    @Test
    public void shouldGenerateBundleSkuConditionCorrectly() {
        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("AA");
        bundleVariant1.setDiscountPercent(30.0);
        bundleVariant1.setDiscountFixed(0.0);

        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");
        bundleVariant2.setDiscountPercent(0.0);
        bundleVariant2.setDiscountFixed(15.0);

        BundleVariant bundleVariant3 = new BundleVariant();
        bundleVariant3.setBundleVariant("DD");
        bundleVariant3.setDiscountPercent(0.0);
        bundleVariant3.setDiscountFixed(25.0);

        bundleVariantArrayList.add(bundleVariant1);
        bundleVariantArrayList.add(bundleVariant2);
        bundleVariantArrayList.add(bundleVariant3);

        assertEquals("variantId contains \"BB\" && variantId contains \"DD\"",
                drlConditions.generateBundleSkuCondition(bundleVariantArrayList,
                        DrlOperator.DRL_OPERATOR_AND, "variantId"));
    }

    @Test
    public void shouldGenerateArrayBundleConditionCorrectly() {
        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("AA");

        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");

        BundleVariant bundleVariant3 = new BundleVariant();
        bundleVariant3.setBundleVariant("DD");

        bundleVariantArrayList.add(bundleVariant1);
        bundleVariantArrayList.add(bundleVariant2);
        bundleVariantArrayList.add(bundleVariant3);


        assertEquals("variantId == \"AA\" || variantId == \"BB\" || variantId == \"DD\"",
                drlConditions.generateArrayBundleCondition(bundleVariantArrayList, "variantId"));
    }

    @Test
    public void shouldGenerateBundleTypeCorrectly() {
        String bundleType = "truemoveH";
        assertEquals("eval(\"truemoveH\".equals($promotionType))",
                drlConditions.generateBundleTypeOrPromotionCode(bundleType, "$promotionType"));

        String pricePlanCode2 = "";
        assertEquals("",
                drlConditions.generateBundleTypeOrPromotionCode(pricePlanCode2, "$promotionType"));
    }

    @Test
    public void shouldGeneratePricePlanCodeCorrectly() {
        PromotionParams promotionParams = new PromotionParams();
        promotionParams.setPricePlanCode("PricePlanCode");
        assertEquals("eval(\"PricePlanCode\".equals($promotionParams.getPricePlanCode()))",
                drlConditions.generateBundlePricePlanCode(promotionParams,
                        "$promotionParams.getPricePlanCode()"));

        String pricePlanCode2 = "";
        assertEquals("",
                drlConditions.generateBundleTypeOrPromotionCode(pricePlanCode2,
                        "$promotionParams.getPricePlanCode()"));

        assertEquals("",
                drlConditions.generateBundlePricePlanCode(null,
                        "$promotionParams.getPricePlanCode()"));


    }

    @Test
    public void shouldGenerateRuleProductBundleConditionCorrectly() {
        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant1 = new BundleVariant();
        bundleVariant1.setBundleVariant("BA");

        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("NA");

        BundleVariant bundleVariant3 = new BundleVariant();
        bundleVariant3.setBundleVariant("NAA");

        bundleVariantArrayList.add(bundleVariant1);
        bundleVariantArrayList.add(bundleVariant2);
        bundleVariantArrayList.add(bundleVariant3);


        assertEquals(" \"BA\".equals($productVariant) || \"NA\".equals($productVariant) || \"NAA\".equals"
                        + "($productVariant)",
                drlConditions.generateRuleProductBundleCondition(bundleVariantArrayList,
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
    }

    @Test
    public void shouldGenerateStringCriteriaConditionCorrectly() {

        List<String> brandList = Arrays.asList("brand1", "brand2", "brand3");
        List<String> variantList = Arrays.asList("variant1", "variant2", "variant3");
        List<String> categoryList = Arrays.asList("category1", "category2", "category3");
        String brand = "brand";
        String variant = "variant";
        String category = "category";

        assertEquals("brandCode == \"brand1\" ||brandCode == \"brand2\" ||brandCode == \"brand3\"",
                drlConditions.generateCriteriaCondition(brand, brandList));

        assertEquals("variantId == \"variant1\" ||variantId == \"variant2\" ||variantId == \"variant3\"",
                drlConditions.generateCriteriaCondition(variant, variantList));

        assertEquals("categoryCode == \"category1\" ||categoryCode == \"category2\" ||categoryCode == \"category3\"",
                drlConditions.generateCriteriaCondition(category, categoryList));
    }

    @Test
    public void shouldGenerateRuleProductMNPConditionCorrectly() {
        PromotionCondition promotionCondition = new PromotionCondition();
        MNPVariant mnpVariant = new MNPVariant();
        mnpVariant.setMnpVariants("A,B,C");
        MNPVariant mnpVariant2 = new MNPVariant();
        mnpVariant2.setMnpVariants("D");
        promotionCondition.setMnpVariants(Arrays.asList(mnpVariant, mnpVariant2));


        assertEquals(" \"A\".equals($productVariant) || \"B\".equals($productVariant) ||"
                        + " \"C\".equals($productVariant) || \"D\".equals($productVariant)",
                drlConditions.generateRuleProductMNPCondition(promotionCondition,
                        DrlOperator.DRL_OPERATOR_OR, "$productVariant"));
    }

    @Test
    public void shouldGenerateMNPBundleConditionCorrectly() {

        List<MNPVariant> mnpList = new ArrayList<>();
        MNPVariant mnpVariant = new MNPVariant();
        mnpVariant.setMnpVariants("A");
        mnpList.add(mnpVariant);

        assertEquals("", drlConditions.generateMNPBundleCondition(mnpList));
    }


}
