package com.ascend.campaign.utils;


import com.ascend.campaign.models.BundleVariant;
import com.ascend.campaign.models.Product;
import com.ascend.campaign.models.PromotionAction;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.models.Variant;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GenerateActionPromotionUtilTest {
    private GenerateActionPromotionUtil generateActionPromotionUtil;

    @Before
    public void setUp() {
        generateActionPromotionUtil = new GenerateActionPromotionUtil();
    }

    @Test
    public void shouldReturnPromotionActionWhenGenerateActionPromotion1() {
        List<String> freeSkus = Arrays.asList("SKU1", "SKU2", "SKU3");
        PromotionAction actualResult = generateActionPromotionUtil.generateActionPromotionFreebie(freeSkus, 5);
        assertThat(actualResult.getCommand(), is("free"));
        assertThat(actualResult.getLimit(), is(5));
        assertThat(actualResult.getVariants().size(), is(3));
        assertThat(actualResult.getVariants().get(0).getQuantity(), is(1));
        assertThat(actualResult.getVariants().get(0).getVariantId(), is("SKU1"));
        assertThat(actualResult.getVariants().get(1).getVariantId(), is("SKU2"));
        assertThat(actualResult.getVariants().get(2).getVariantId(), is("SKU3"));
    }

    @Test
    public void shouldReturnPromotionActionWhenGenerateActionPromotionDiscountByBrand() {
        final int quantity = 5;
        ArrayList<Product> sku = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("SKU1");
        Product product2 = new Product();
        product1.setVariantId("SKU2");
        sku.add(product1);
        sku.add(product2);

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setDiscountPercent(80.0);
        PromotionAction actualResult = generateActionPromotionUtil.generateActionPromotionDiscountByBrand(sku, quantity,
                promotionCondition);

        assertThat(actualResult.getVariants().size(), is(2));
        assertThat(actualResult.getLimit(), is(1));
        assertThat(actualResult.getCommand(), is("discount_item"));
        assertThat(actualResult.getVariants().get(0).getDiscountValue(), is(80.0));
        assertThat(actualResult.getVariants().get(0).getQuantity(), is(1));
        assertThat(actualResult.getVariants().get(0).getDiscountType(), is("percent"));

        promotionCondition.setDiscountPercent(null);
        promotionCondition.setDiscountFixed(50.0);
        PromotionAction actualResult2 = generateActionPromotionUtil.generateActionPromotionDiscountByBrand(sku,
                quantity, promotionCondition);

        assertThat(actualResult2.getVariants().get(0).getDiscountValue(), is(50.0));
        assertThat(actualResult2.getVariants().get(0).getQuantity(), is(1));
    }


    @Test
    public void shouldReturnPromotionActionWhenGenerateActionPromotion3() {
        List<String> optionSku = Arrays.asList("SKU1", "SKU2", "SKU3");
        final int quantity = 5;
        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setDiscountPercent(60.0);
        promotionCondition.setOptionVariants(optionSku);

        PromotionAction actualResult = generateActionPromotionUtil.generateActionPromotionOptionToBuy(
                quantity,
                promotionCondition);
        assertEquals(3, actualResult.getVariants().size());
        assertThat(actualResult.getVariants().get(0).getDiscountValue(), is(60.0));
        assertThat(actualResult.getCommand(), is("suggest_discount"));
        assertThat(actualResult.getLimit(), is(5));
        assertThat(actualResult.getVariants().get(0).getQuantity(), is(1));
        assertThat(actualResult.getVariants().get(0).getVariantId(), is("SKU1"));
        assertEquals("percent", actualResult.getVariants().get(0).getDiscountType());

        promotionCondition.setDiscountPercent(null);
        promotionCondition.setDiscountFixed(40.0);

        PromotionAction actualResult2 = generateActionPromotionUtil.generateActionPromotionOptionToBuy(quantity,
                promotionCondition);

        assertThat(actualResult2.getVariants().get(0).getDiscountValue(), is(40.0));
        assertThat(actualResult2.getVariants().get(0).getQuantity(), is(1));
    }

    @Test
    public void shouldReturnPromotionActionWhenGenerateActionPromotionSpecificTime() {
        final ArrayList<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("SKU1");
        product1.setDiscountPrice(5000.0);
        Product product2 = new Product();
        product2.setVariantId("SKU2");
        product2.setDiscountPrice(1000.0);
        products.add(product1);
        products.add(product2);

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setDiscountPercent(20.0);
        promotionCondition.setMasterCardPercent(0.0);

        PromotionAction actualResult2 = generateActionPromotionUtil.generateActionPromotionSpecificTime(products,
                promotionCondition);

        assertThat(actualResult2.getVariants().size(), is(2));
        assertThat(actualResult2.getCommand(), is("discount_item"));
        assertThat(actualResult2.getLimit(), is(1));
        assertThat(String.format("%.2f", actualResult2.getVariants().get(0).getDiscountValue()), is("20.00"));
        assertEquals("percent", actualResult2.getVariants().get(0).getDiscountType());
        assertThat(actualResult2.getVariants().get(0).getQuantity(), is(1));
        assertThat(actualResult2.getVariants().get(0).getVariantId(), is("SKU1"));
        assertThat(actualResult2.getVariants().get(1).getVariantId(), is("SKU2"));

        promotionCondition.setMasterCardPercent(5.0);
        PromotionAction actualResult3 = generateActionPromotionUtil.generateActionPromotionSpecificTime(products,
                promotionCondition);

        assertThat(actualResult3.getVariants().get(0).getDiscountValue(), is(24.0));
        assertThat(actualResult3.getVariants().get(1).getDiscountValue(), is(24.0));

        promotionCondition.setMasterCardPercent(null);
        PromotionAction actualResult4 = generateActionPromotionUtil.generateActionPromotionSpecificTime(products,
                promotionCondition);
        assertThat(actualResult4.getVariants().get(0).getDiscountValue(), is(19.999999999999996));
        assertThat(actualResult4.getVariants().get(1).getDiscountValue(), is(19.999999999999996));
    }

    @Test
    public void shouldReturnPromotionActionWhenGenerateActionPromotion5() {
        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant = new BundleVariant();
        bundleVariant.setBundleVariant("AA");
        bundleVariant.setDiscountPercent(30.0);
        bundleVariant.setDiscountFixed(0.0);

        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");
        bundleVariant2.setDiscountPercent(0.0);
        bundleVariant2.setDiscountFixed(15.0);

        bundleVariantArrayList.add(bundleVariant);
        bundleVariantArrayList.add(bundleVariant2);

        final ArrayList<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("AA");
        product1.setDiscountPrice(5000.0);
        product1.setQuantity(30);

        Product product2 = new Product();
        product2.setVariantId("BB");
        product2.setDiscountPrice(1000.0);
        product2.setQuantity(20);

        products.add(product1);
        products.add(product2);


        PromotionAction actualResult = generateActionPromotionUtil.generateActionPromotionBundle(products,
                bundleVariantArrayList);

        assertThat(actualResult.getVariants().size(), is(2));
        assertThat(actualResult.getVariants().get(0).getDiscountValue(), is(30.0));
        assertThat(actualResult.getVariants().get(1).getDiscountValue(), is(15.0));
        assertThat(actualResult.getCommand(), is("discount_bundle"));
        assertThat(actualResult.getLimit(), is(20));

        assertEquals("percent", actualResult.getVariants().get(0).getDiscountType());
        assertEquals("total", actualResult.getVariants().get(1).getDiscountType());


    }

    @Test
    public void shouldReturnBundleSkuListForBundlePromotionForProduct() {

        final ArrayList<BundleVariant> bundleVariantArrayList = new ArrayList<>();
        BundleVariant bundleVariant = new BundleVariant();
        bundleVariant.setBundleVariant("AA");
        bundleVariant.setDiscountPercent(30.0);

        BundleVariant bundleVariant2 = new BundleVariant();
        bundleVariant2.setBundleVariant("BB");
        bundleVariant2.setDiscountFixed(15.0);

        bundleVariantArrayList.add(bundleVariant);
        bundleVariantArrayList.add(bundleVariant2);


        List<Variant> variantList = generateActionPromotionUtil
                .generateBundleSkuListForBundlePromotionForProduct(bundleVariantArrayList);
        assertThat(variantList.get(0).getVariantId(), is("AA"));
        assertThat(variantList.get(0).getDiscountType(), is("percent"));
        assertThat(variantList.get(0).getDiscountValue(), is(30.0));

        assertThat(variantList.get(1).getVariantId(), is("BB"));
        assertThat(variantList.get(1).getDiscountType(), is("total"));
        assertThat(variantList.get(1).getDiscountValue(), is(15.0));

        assertThat(variantList.size(), is(2));


    }

    @Test
    public void shouldReturnPromotionActionWhenGenerateActionPromotionDiscountByCode() {

        List<Product> productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("SKU1");
        Product product2 = new Product();
        product1.setVariantId("SKU2");
        productList.add(product1);
        productList.add(product2);

        PromotionCondition promotionCondition = new PromotionCondition();
        promotionCondition.setDiscountPercent(80.0);
        promotionCondition.setMaxDiscountValue(300.0);
        PromotionAction actualResult = generateActionPromotionUtil.generateActionPromotionDiscountByCode(
                productList, promotionCondition);

        assertThat(actualResult.getVariants().size(), is(2));
        assertThat(actualResult.getLimit(), is(1));
        assertThat(actualResult.getCommand(), is("discount_item"));
        assertThat(actualResult.getVariants().get(0).getDiscountMaximum(), is(300.0));
        assertThat(actualResult.getVariants().get(0).getDiscountValue(), is(80.0));
        assertThat(actualResult.getVariants().get(0).getQuantity(), is(1));
        assertThat(actualResult.getVariants().get(0).getDiscountType(), is("percent"));

        promotionCondition.setDiscountFixed(50.0);
        promotionCondition.setDiscountPercent(null);
        PromotionAction actualResult2 = generateActionPromotionUtil.generateActionPromotionDiscountByCode(
                productList, promotionCondition);

        assertThat(actualResult2.getVariants().get(0).getDiscountMaximum(), is(300.0));
        assertThat(actualResult2.getVariants().get(0).getDiscountValue(), is(50.0));
        assertThat(actualResult2.getVariants().get(0).getQuantity(), is(1));
    }


}
