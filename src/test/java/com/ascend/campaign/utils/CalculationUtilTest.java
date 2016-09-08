package com.ascend.campaign.utils;

import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore
public class CalculationUtilTest {

    @Autowired
    private CalculationUtil calculationUtil;

    @Before
    public void setUp() {
        calculationUtil = new CalculationUtil(new DecimalUtil());
    }

/*    @Test
    public void shouldReturnCalculationDataCorrectly() {
        final double discountPerCart = 400.0;
        final ArrayList<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("AA");
        product1.setNormalPrice(1000D);
        product1.setNormalPrice(1000.0);

        Product product2 = new Product();
        product2.setVariantId("BB");
        product2.setNormalPrice(1000.0);
        product2.setNormalPrice(1000.0);

        Product product4 = new Product();
        product4.setVariantId("BB");
        product4.setNormalPrice(1000.0);
        product4.setNormalPrice(1000.0);

        Product product3 = new Product();
        product3.setVariantId("CC");
        product3.setNormalPrice(1000.0);
        product3.setNormalPrice(1000.0);

        Product product5 = new Product();
        product5.setVariantId("DD");
        product5.setNormalPrice(1000.0);
        product5.setNormalPrice(1000.0);

        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);

        Variant variant1 = new Variant();
        variant1.setDiscountType("percent");
        variant1.setDiscountValue(20D);
        variant1.setVariantId("AA");

        Variant variant2 = new Variant();
        variant2.setDiscountType("percent");
        variant2.setDiscountValue(30D);
        variant2.setVariantId("BB");


        Variant variant4 = new Variant();
        variant4.setDiscountType("percent");
        variant4.setDiscountValue(100D);
        variant4.setVariantId("CC");

        ArrayList<Variant> variants1 = new ArrayList<>();
        ArrayList<Variant> variants2 = new ArrayList<>();
        ArrayList<Variant> variants3 = new ArrayList<>();
        variants1.add(variant1);
        variants2.add(variant2);
        variants3.add(variant4);


        CampaignApplied campaignApplied = new CampaignApplied();
        campaignApplied.setLimit(1);
        campaignApplied.setPromotionId(1L);
        List<CampaignApplied> campaignApplieds = new ArrayList<>();
        List<String> stringList = Collections.singletonList("AA");
        campaignApplied.setVariantId(stringList);
        campaignApplieds.add(campaignApplied);

        CampaignApplied campaignApplied2 = new CampaignApplied();
        campaignApplied2.setLimit(1);
        campaignApplied2.setPromotionId(2L);
        List<String> stringList2 = Collections.singletonList("CC");
        campaignApplied2.setVariantId(stringList2);
        campaignApplieds.add(campaignApplied2);

        CampaignApplied campaignApplied3 = new CampaignApplied();
        campaignApplied3.setLimit(2);
        campaignApplied3.setPromotionId(3L);
        List<String> stringList3 = Collections.singletonList("BB");
        campaignApplied3.setVariantId(stringList3);

        campaignApplieds.add(campaignApplied3);


        Cart cart = new Cart();
        cart.setCampaignApplied(campaignApplieds);

        CampaignSuggestion campaignSuggestion1 = new CampaignSuggestion();
        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setVariants(variants1);
        promotionAction.setLimit(1);
        campaignSuggestion1.addPromotionAction(promotionAction);
        campaignSuggestion1.setPromotionId("1");
        List<CampaignSuggestion> campaignSuggestion = new ArrayList<>();
        campaignSuggestion.add(campaignSuggestion1);

        PromotionAction promotionAction2 = new PromotionAction();
        promotionAction2.setVariants(variants2);
        promotionAction2.setLimit(2);
        CampaignSuggestion campaignSuggestion2 = new CampaignSuggestion();
        campaignSuggestion2.addPromotionAction(promotionAction2);
        campaignSuggestion2.setPromotionId("3");
        campaignSuggestion.add(campaignSuggestion2);

        PromotionAction promotionAction3 = new PromotionAction();
        promotionAction3.setVariants(variants3);
        promotionAction3.setLimit(1);
        CampaignSuggestion campaignSuggestion3 = new CampaignSuggestion();
        campaignSuggestion3.addPromotionAction(promotionAction3);
        campaignSuggestion3.setPromotionId("2");
        campaignSuggestion.add(campaignSuggestion3);

        List<Product> superDeal = new ArrayList<>();

        Calculation calculation = calculationUtil.setFlatDiscountData(products, campaignSuggestion,
                discountPerCart, campaignApplieds, superDeal);

        assertThat(calculation.getPromotionForProducts().size(), is(5));
        assertThat(calculation.getTotalFlatDiscount(), is(400.0));
        assertThat(calculation.getPromotionForProducts().get(0).getVariantId(), is("AA"));
        assertThat(calculation.getPromotionForProducts().get(0).getFlatDiscount(), is(100.0));
        assertThat(calculation.getPromotionForProducts().get(1).getVariantId(), is("BB"));
        assertThat(calculation.getPromotionForProducts().get(1).getFlatDiscount(), is(87.5));
        assertThat(calculation.getPromotionForProducts().get(2).getVariantId(), is("CC"));
        assertThat(calculation.getPromotionForProducts().get(2).getFlatDiscount(), is(0.0));
        assertThat(calculation.getPromotionForProducts().get(3).getVariantId(), is("BB"));
        assertThat(calculation.getPromotionForProducts().get(3).getFlatDiscount(), is(87.5));
        assertThat(calculation.getPromotionForProducts().get(4).getVariantId(), is("DD"));
        assertThat(calculation.getPromotionForProducts().get(4).getFlatDiscount(), is(125.0));
    }*/

 /*   @Test
    public void shouldReturnCalculationDataCorrectlyWhenCalculateWithDiscountFixed() {
        final double discountPerCart = 0.0;
        final ArrayList<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("AA");
        product1.setNormalPrice(1000D);
        product1.setNormalPrice(1000.0);

        Product product2 = new Product();
        product2.setVariantId("BB");
        product2.setNormalPrice(1000.0);
        product2.setNormalPrice(1000.0);

        Product product4 = new Product();
        product4.setVariantId("BB");
        product4.setNormalPrice(1000.0);
        product4.setNormalPrice(1000.0);

        Product product3 = new Product();
        product3.setVariantId("CC");
        product3.setNormalPrice(1000.0);
        product3.setNormalPrice(1000.0);

        Product product5 = new Product();
        product5.setVariantId("DD");
        product5.setNormalPrice(1000.0);
        product5.setNormalPrice(1000.0);

        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);

        Variant variant1 = new Variant();
        variant1.setDiscountType("total");
        variant1.setDiscountValue(20D);
        variant1.setVariantId("AA");

        Variant variant2 = new Variant();
        variant2.setDiscountType("total");
        variant2.setDiscountValue(30D);
        variant2.setVariantId("BB");


        ArrayList<Variant> variants1 = new ArrayList<>();
        ArrayList<Variant> variants2 = new ArrayList<>();
        variants1.add(variant1);
        variants2.add(variant2);


        CampaignApplied campaignApplied = new CampaignApplied();
        campaignApplied.setLimit(1);
        campaignApplied.setPromotionId(1L);
        List<CampaignApplied> campaignApplieds = new ArrayList<>();
        List<String> stringList = Collections.singletonList("AA");
        campaignApplied.setVariantId(stringList);
        campaignApplieds.add(campaignApplied);

        CampaignApplied campaignApplied2 = new CampaignApplied();
        campaignApplied2.setLimit(1);
        campaignApplied2.setPromotionId(2L);
        List<String> stringList2 = Collections.singletonList("CC");
        campaignApplied2.setVariantId(stringList2);
        campaignApplieds.add(campaignApplied2);

        CampaignApplied campaignApplied3 = new CampaignApplied();
        campaignApplied3.setLimit(2);
        campaignApplied3.setPromotionId(3L);
        List<String> stringList3 = Collections.singletonList("BB");
        campaignApplied3.setVariantId(stringList3);

        campaignApplieds.add(campaignApplied3);


        Cart cart = new Cart();
        cart.setCampaignApplied(campaignApplieds);

        CampaignSuggestion campaignSuggestion1 = new CampaignSuggestion();
        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setVariants(variants1);
        promotionAction.setLimit(1);
        campaignSuggestion1.addPromotionAction(promotionAction);
        campaignSuggestion1.setPromotionId("1");
        List<CampaignSuggestion> campaignSuggestion = new ArrayList<>();
        campaignSuggestion.add(campaignSuggestion1);

        PromotionAction promotionAction2 = new PromotionAction();
        promotionAction2.setVariants(variants2);
        promotionAction2.setLimit(2);
        CampaignSuggestion campaignSuggestion2 = new CampaignSuggestion();
        campaignSuggestion2.addPromotionAction(promotionAction2);
        campaignSuggestion2.setPromotionId("3");
        campaignSuggestion.add(campaignSuggestion2);

        List<Product> superDeal = new ArrayList<>();

        Calculation calculation = calculationUtil.setFlatDiscountData(products, campaignSuggestion,
                discountPerCart, campaignApplieds, superDeal);

        assertThat(calculation.getPromotionForProducts().size(), is(5));
        assertThat(calculation.getTotalFlatDiscount(), is(0.0));

        assertThat(calculation.getPromotionForProducts().get(0).getVariantId(), is("AA"));
        assertThat(calculation.getPromotionForProducts().get(0).getFlatDiscount(), is(0.0));
        assertThat(calculation.getPromotionForProducts().get(0).getFinalPrice(), is(980.0));
        assertThat(calculation.getPromotionForProducts().get(0).getPercentDiscount(), is(2.0));
        assertThat(calculation.getPromotionForProducts().get(1).getVariantId(), is("BB"));
        assertThat(calculation.getPromotionForProducts().get(1).getFlatDiscount(), is(0.0));
        assertThat(calculation.getPromotionForProducts().get(1).getFinalPrice(), is(970.0));
        assertThat(calculation.getPromotionForProducts().get(1).getPercentDiscount(), is(3.0));
        assertThat(calculation.getPromotionForProducts().get(2).getVariantId(), is("CC"));
        assertThat(calculation.getPromotionForProducts().get(2).getFlatDiscount(), is(0.0));
        assertThat(calculation.getPromotionForProducts().get(3).getVariantId(), is("BB"));
        assertThat(calculation.getPromotionForProducts().get(3).getFlatDiscount(), is(0.0));
        assertThat(calculation.getPromotionForProducts().get(4).getVariantId(), is("DD"));
        assertThat(calculation.getPromotionForProducts().get(4).getFlatDiscount(), is(0.0));
    }


    @Test
    public void shouldReturnCalculationDataCorrectlyWhenReCalculationWithCalculation() {
        final double discountPerCart = 800;
        VariantCalculation variantCalculation1 = new VariantCalculation();
        //variantCalculation1.setFinalPrice(4132.17);
        variantCalculation1.setFinalPriceString("4132.17");
        // variantCalculation1.setFlatDiscount(667.82);
        variantCalculation1.setFlatDiscountString("667.82");
        //variantCalculation1.setNormalPrice(5000D);
        variantCalculation1.setNormalPriceString("5000");
        //variantCalculation1.setPercentDiscount(4D);
        variantCalculation1.setPercentDiscountString("4");
        //variantCalculation1.setTotalPercentDiscount(17.36);
        variantCalculation1.setTotalPercentDiscountString("17.36");
        List<VariantCalculation> variantCalculationList = new ArrayList<>();
        variantCalculationList.add(variantCalculation1);
        Calculation calculation = calculationUtil.reCalculation(variantCalculationList, discountPerCart);
        assertThat(calculation.getPromotionForProducts().size(), is(1));
        assertThat(calculation.getPromotionForProducts().get(0).getFlatDiscount(), is(800.0));
        assertThat(calculation.getPromotionForProducts().get(0).getNormalPrice(), is(5000.0));
        assertThat(calculation.getPromotionForProducts().get(0).getFinalPrice(), is(4000.0));
        assertThat(calculation.getPromotionForProducts().get(0).getPercentDiscount(), is(4.0));
        assertThat(calculation.getPromotionForProducts().get(0).getTotalPercentDiscount(), is(20.0));
        assertThat(calculation.getTotalFlatDiscount(), is(800.0));

    }

    @Test
    public void shouldReturnTotalFlatDiscountZeroWhenCalculationWithFreeVariant() {
        final double discountPerCart = 800;
        final ArrayList<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("AA");
        product1.setNormalPrice(5000D);
        product1.setNormalPrice(5000.0);
        products.add(product1);

        Variant variant4 = new Variant();
        variant4.setDiscountType("percent");
        variant4.setDiscountValue(100D);
        variant4.setVariantId("AA");

        ArrayList<Variant> variants = new ArrayList<>();
        variants.add(variant4);

        CampaignApplied campaignApplied = new CampaignApplied();
        campaignApplied.setLimit(1);
        campaignApplied.setPromotionId(1L);
        List<String> stringList = Collections.singletonList("AA");
        campaignApplied.setVariantId(stringList);

        Cart cart = new Cart();
        List<CampaignApplied> campaignApplieds = new ArrayList<>();
        cart.setCampaignApplied(campaignApplieds);

        PromotionAction promotionAction = new PromotionAction();
        promotionAction.setVariants(variants);
        promotionAction.setLimit(1);
        CampaignSuggestion campaignSuggestion1 = new CampaignSuggestion();
        campaignSuggestion1.addPromotionAction(promotionAction);
        campaignSuggestion1.setPromotionId("1");
        List<CampaignSuggestion> campaignSuggestion = new ArrayList<>();
        campaignSuggestion.add(campaignSuggestion1);

        List<Product> superDeal = new ArrayList<>();

        Calculation calculation = calculationUtil.setFlatDiscountData(products, campaignSuggestion,
                discountPerCart, campaignApplieds, superDeal);

        assertThat(calculation.getPromotionForProducts().size(), is(1));
        assertThat(calculation.getTotalFlatDiscount(), is(0.0));
        assertThat(calculation.getPromotionForProducts().get(0).getFlatDiscount(), is(0.0));
    }

    @Test
    public void shouldReturnVariantCalculationWhenMatchProduct() {
        final ArrayList<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setVariantId("P1");
        product1.setNormalPrice(5000D);
        product1.setNormalPrice(5000.0);

        Product product2 = new Product();
        product2.setVariantId("P2");
        product2.setNormalPrice(5000D);
        product2.setNormalPrice(5000.0);

        products.add(product1);
        products.add(product2);
        List<Product> superDeal = new ArrayList<>();
        Calculation calculation = calculationUtil.setCalculationIfCartNotHavePromotion(products, superDeal);
        assertThat(calculation.getPromotionForProducts().size(), is(2));
    }*/

}
