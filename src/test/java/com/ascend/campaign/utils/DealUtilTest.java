package com.ascend.campaign.utils;


import com.ascend.campaign.entities.Deal;
import com.ascend.campaign.models.VariantDeal;
import com.ascend.campaign.models.VariantDealDetail;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class DealUtilTest {
    @Autowired
    DealUtil dealUtil;

    @Before
    public void setUp() {
        dealUtil = new DealUtil(new DecimalUtil());
    }

    @Test
    public void shouldReturnVariantDealWhenSetPromotionPriceByDataCorrectly() {
        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setBrand("test1");
        variantDealDetail.setCategory("vv");
        variantDealDetail.setCollection(Arrays.asList("col1", "col2"));
        variantDealDetail.setVariant("iphone12");
        variantDealDetail.setProduct("iphone");

        List<Deal> dealList = new ArrayList<>();
        Deal deal1 = new Deal();
        deal1.setConditionData("{\"criteria_type\":\"brand\",\"criteria_values\":[\"test1\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":null,\"excluded_criteria_values\":[\"iphone\"],\"limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":null,\"fixed_price\":200.0}");
        dealList.add(deal1);

        Deal deal2 = new Deal();
        deal2.setConditionData("{\"criteria_type\":\"category\",\"criteria_values\":[\"vv\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":null,\"excluded_criteria_values\":[\"iphone\"],\"limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":null,\"fixed_price\":22.0}");
        dealList.add(deal2);

        Deal deal3 = new Deal();
        deal3.setConditionData("{\"criteria_type\":\"collection\",\"criteria_values\":[\"col1\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":null,\"excluded_criteria_values\":[\"iphone\"],\"limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":null,\"fixed_price\":5.0}");
        dealList.add(deal3);

        Deal deal4 = new Deal();
        deal4.setConditionData("{\"criteria_type\":\"product\",\"criteria_values\":[\"iphone\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":null,\"excluded_criteria_values\":[\"iphone\"],\"limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":null,\"fixed_price\":10.0}");
        dealList.add(deal4);


        Deal deal6 = new Deal();
        deal6.setConditionData("{\"criteria_type\":\"variant\",\"criteria_values\":[\"iphone12\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":null,\"excluded_criteria_values\":[\"iphone\"],\"limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":null,\"fixed_price\":8.0}");
        dealList.add(deal6);

        Deal deal7 = new Deal();
        deal7.setConditionData("{\"criteria_type\":\"variant\",\"criteria_values\":[\"iphone12\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":null,\"excluded_criteria_values\":[\"iphone\"],\"limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":null,\"fixed_price\":4.0}");
        dealList.add(deal7);

        Double normalPrice = 800D;

        VariantDeal variantDeal = dealUtil.setPromotionPrice(dealList, variantDealDetail, normalPrice);

        assertThat(variantDeal.getVariantID(), is("iphone12"));
        assertThat(variantDeal.getPromotionPrice(), is(4.0));
    }

    @Test
    public void shouldReturnVariantDealWithNullPromotionPriceWhenDataExcluded_criteria() {
        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setBrand("test1");
        variantDealDetail.setCategory("vv");
        variantDealDetail.setCollection(Arrays.asList("col1", "col2"));
        variantDealDetail.setVariant("iphone12");
        variantDealDetail.setProduct("iphone");

        final Double normalPrice = 800D;
        List<Deal> dealList1 = new ArrayList<>();
        Deal dealEx = new Deal();
        dealEx.setConditionData("{\"criteria_type\":\"collection\",\"criteria_values\":[\"col1\",\"col2\",\"col3\"],"
                + "\"excluded_criteria_type\":\"variant\",\"excluded_criteria_values\":[\"iphone12\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx);

        Deal dealEx1 = new Deal();
        dealEx1.setConditionData("{\"criteria_type\":\"category\",\"criteria_values\":[\"vv\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"product\",\"excluded_criteria_values\":[\"iphone\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx1);

        Deal dealEx3 = new Deal();
        dealEx3.setConditionData("{\"criteria_type\":\"variant\",\"criteria_values\":[\"test12\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"variant\",\"excluded_criteria_values\":[\"iphone12\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx3);

        Deal dealEx4 = new Deal();
        dealEx4.setConditionData("{\"criteria_type\":\"category\",\"criteria_values\":[\"vv\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"collection\",\"excluded_criteria_values\":[\"col1\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx4);

        Deal dealEx5 = new Deal();
        dealEx5.setConditionData("{\"criteria_type\":\"category\",\"criteria_values\":[\"vv\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"brand\",\"excluded_criteria_values\":[\"test1\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx5);

        Deal dealEx6 = new Deal();
        dealEx6.setConditionData("{\"criteria_type\":\"brand\",\"criteria_values\":[\"test1\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"category\",\"excluded_criteria_values\":[\"vv\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx6);

        Deal dealEx7 = new Deal();
        dealEx7.setConditionData("{\"criteria_type\":\"product\",\"criteria_values\":[\"col1\",\"col2\",\"col3\"],"
                + "\"excluded_criteria_type\":\"product\",\"excluded_criteria_values\":[\"iphone\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx7);

        VariantDeal variantDeal = dealUtil.setPromotionPrice(dealList1, variantDealDetail, normalPrice);
        assertThat(variantDeal.getVariantID(), is("iphone12"));
        assertThat(variantDeal.getPromotionPrice(), nullValue());


        List<Deal> dealList4 = new ArrayList<>();
        Deal dealE1 = new Deal();
        dealE1.setConditionData("{\"criteria_type\":\"product\",\"criteria_values\":[\"iphone\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"product\",\"excluded_criteria_values\":[\"iph6one\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":30,\"fixed_price\":null}");
        dealList4.add(dealE1);


        Deal dealE5 = new Deal();
        dealE5.setConditionData("{\"criteria_type\":\"product\",\"criteria_values\":[\"iphone\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"product\",\"excluded_criteria_values\":[\"iph6one\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList4.add(dealE5);

        Deal dealE6 = new Deal();
        dealE6.setConditionData("{\"criteria_type\":\"product\",\"criteria_values\":[\"iphone\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":\"product\",\"excluded_criteria_values\":[\"iph6one\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":40,\"fixed_price\":null}");
        dealList4.add(dealE6);


        Deal dealE2 = new Deal();

        dealE2.setConditionData("{\"criteria_type\":\"brand\",\"criteria_values\":[\"test1\",\"test2\",\"test3\"],"
                + "\"excluded_criteria_type\":null,\"excluded_criteria_values\":[\"ipho6ne\"],\"limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":null,\"fixed_price\":200.0}");
        dealList4.add(dealE2);

        VariantDeal variantDeal3 = dealUtil.setPromotionPrice(dealList4, variantDealDetail, normalPrice);
        assertThat(variantDeal3.getVariantID(), is("iphone12"));
        assertThat(variantDeal3.getPromotionPrice(), is(8.0));
    }


    @Test
    public void shouldReturnVariantDealWhenSetSuperDealCorrectly() {
        VariantDealDetail variantDealDetail = new VariantDealDetail();
        variantDealDetail.setBrand("test1");
        variantDealDetail.setCategory("vv");
        variantDealDetail.setCollection(Arrays.asList("col1", "col2"));
        variantDealDetail.setVariant("iphone12");
        variantDealDetail.setProduct("iphone");

        final Double normalPrice = 800D;
        List<Deal> dealList1 = new ArrayList<>();
        Deal dealEx = new Deal();
        Deal dealEx3 = new Deal();
        dealEx3.setConditionData("{\"criteria_type\":\"variant\",\"variants\":[  {\n"
                + "            \"variant_id\":\"iphone12\",\n"
                + "            \"recommended\":false,\n"
                + "             \"promotion_price\":500\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"col2\",\n"
                + "            \"recommended\":false,\n"
                + "             \"promotion_price\":500\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"col3\",\n"
                + "            \"recommended\":false\n"
                + "         }],"
                + "\"excluded_criteria_type\":\"variant\",\"excluded_criteria_values\":[\"iphon5e12\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx3);


        VariantDeal variantDeal = dealUtil.setPromotionPrice(dealList1, variantDealDetail, normalPrice);
        assertThat(variantDeal.getVariantID(), is("iphone12"));
        assertThat(variantDeal.getPromotionPrice(), is(500.0));

        final Double normalPrice2 = 400D;
        Deal dealEx4 = new Deal();
        dealEx4.setConditionData("{\"criteria_type\":\"variant\",\"variants\":[  {\n"
                + "            \"variant_id\":\"iphone12\",\n"
                + "            \"recommended\":false,\n"
                + "             \"promotion_price\":500\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"col2\",\n"
                + "            \"recommended\":false,\n"
                + "             \"promotion_price\":500\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"col3\",\n"
                + "            \"recommended\":false\n"
                + "         }],"
                + "\"excluded_criteria_type\":\"variant\",\"excluded_criteria_values\":[\"iphon5e12\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx3);


        VariantDeal variantDeal2 = dealUtil.setPromotionPrice(dealList1, variantDealDetail, normalPrice2);
        assertThat(variantDeal2.getVariantID(), is("iphone12"));
        assertThat(variantDeal2.getPromotionPrice(), is(nullValue()));

        final Double normalPrice3 = 400D;
        Deal dealEx5 = new Deal();
        dealEx5.setConditionData("{\"criteria_type\":\"variant\",\"variants\":[  {\n"
                + "            \"variant_id\":\"iphone12\",\n"
                + "            \"recommended\":false,\n"
                + "             \"promotion_price\":500\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"col2\",\n"
                + "            \"recommended\":false,\n"
                + "             \"promotion_price\":500\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"col3\",\n"
                + "            \"recommended\":false\n"
                + "         }],"
                + "\"excluded_criteria_type\":\"variant\",\"excluded_criteria_values\":[\"iphon5e12\"],\""
                + "limit_account\":4,"
                + "\"limit_item_per_cart\":4,\"discount_percent\":99,\"fixed_price\":null}");
        dealList1.add(dealEx3);


        VariantDeal variantDeal3 = dealUtil.setPromotionPrice(dealList1, variantDealDetail, normalPrice3);
        assertThat(variantDeal3.getVariantID(), is("iphone12"));
        assertThat(variantDeal3.getPromotionPrice(), is(nullValue()));

    }

    @Test
    public void shouldTrueWhenIsEnableAndValidDate() {
        assertThat(dealUtil.isLive(true, DateTime.now().minusDays(1).toDate(),
                DateTime.now().plusDays(1).toDate()), is(true));
    }

    @Test
    public void shouldFalseWhenIsNotEnableAndValidDate() {
        assertThat(dealUtil.isLive(false, DateTime.now().minusDays(1).toDate(),
                DateTime.now().plusDays(1).toDate()), is(false));
    }

    @Test
    public void shouldFalseWhenIsEnableAndInValidDate() {
        assertThat(dealUtil.isLive(true, DateTime.now().toDate(), DateTime.now().minusDays(1).toDate()), is(false));
    }

    @Test
    public void shouldFalseWhenNullInputValue() {
        assertThat(dealUtil.isLive(null, null, null), is(false));
    }
}
