package com.ascend.campaign.utils;

import com.ascend.campaign.models.DetailData;
import com.ascend.campaign.models.PromotionCondition;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@SuppressWarnings("unchecked")
public class JSONUtilTest {

    JSONUtil jsonUtil;

    @Before
    public void setUp() {
        this.jsonUtil = new JSONUtil();
    }

    @Test
    public void shouldReturnCartWhenParseJSONToCartWithCorrectData() {
        String data = "{"
                + "      \"channel_type\":\"channelType1\","
                + "      \"payment_channel\":\"setFlatDiscountData\","
                + "      \"current_date\":0,"
                + "      \"user_id\":\"testUser\""
                + "    }";

        assertThat(jsonUtil.parseToCart(data).getChannelType(), is("channelType1"));
        assertThat(jsonUtil.parseToCart(data).getPaymentChannel(), is("setFlatDiscountData"));
    }

    @Test
    public void shouldReturnEmptyCartWhenParseJSONToCartWithIncorrectData() {
        assertThat(jsonUtil.parseToCart("").getChannelType(), is(nullValue()));
    }

    @Test
    public void shouldReturnPromotionConditionWhenValidJSON() {
        String data = "{"
                + "      \"variants\":["
                + "         \"1\","
                + "         \"2\""
                + "      ],"
                + "      \"quantity\":10,"
                + "      \"free_variants\":[\"3\"]"
                + "   }";

        assertThat(jsonUtil.parseToPromotionCondition(data).getFreeVariants().get(0), is("3"));
        assertThat(jsonUtil.parseToPromotionCondition(data).getVariants().size(), is(2));
    }

    @Test
    public void shouldReturnDealConditionWhenValidJSON() {
        String data = "{"
                + " \"variants\":[ {\n"
                + "            \"variant_id\":\"setFlatDiscountData\",\n"
                + "            \"recommended\":false\n"
                + "         },\n"
                + "         {\n"
                + "            \"variant_id\":\"setFlatDiscountData\",\n"
                + "            \"recommended\":false\n"
                + "         }],"
                + "      \"criteria_type\":\"variant\","
                + "      \"limit_account\":10"
                + "   }";

        assertThat(jsonUtil.parseToDealCondition(data).getCriteriaType(), is("variant"));
        assertThat(jsonUtil.parseToDealCondition(data).getLimitAccount(), is(10));
        assertThat(jsonUtil.parseToDealCondition(data).getCriteriaVariants().size(), is(2));
    }

    @Test
    public void shouldReturnEmptyPromotionConditionWhenInvalidJSON() {
        assertThat(jsonUtil.parseToPromotionCondition("").getFreeVariants(), is(nullValue()));
    }

    @Test
    public void shouldCorrectStringWhenValidObject() {
        PromotionCondition promotionCondition = new PromotionCondition();
        List<String> freeSKU = new ArrayList();
        freeSKU.add("1");

        promotionCondition.setFreeVariants(freeSKU);
        promotionCondition.setQuantity(10);

        assertThat(jsonUtil.toString(promotionCondition), is(not("")));
    }

    @Test
    public void shouldReturnEmptyStringWhenInvalidObject() {
        Object fakeObject = new Object();
        assertThat(jsonUtil.toString(fakeObject), is(""));
    }

    @Test
    public void shouldReturnImageBannerWhenParseWithValidStringJSON() {
        String data = "{\"banner_web\":\"Banner\",\"banner_web_translation\":\"BannerTranslation\",\"banner_mobile\":"
                + "\"BannerMobile\",\"banner_mobile_translation\":\"BannerMobileTranslation\"}";

        assertThat(jsonUtil.parseToToImageBanner(data).getBanner(), is("Banner"));
        assertThat(jsonUtil.parseToToImageBanner(data).getBannerMobile(), is("BannerMobile"));
        assertThat(jsonUtil.parseToToImageBanner(data).getBannerMobileTranslation(), is("BannerMobileTranslation"));
        assertThat(jsonUtil.parseToToImageBanner(data).getBannerTranslation(), is("BannerTranslation"));
    }

    @Test
    public void shouldReturnEmptyImageBannerWhenParseJSONToImageBannerWithIncorrectData() {
        assertThat(jsonUtil.parseToToImageBanner("").getBanner(), is(nullValue()));
    }

    @Test
    public void shouldReturnAuthenticationModelWhenParseWithValidStringJSON() {
        String data = "{\"dnm\":\"Dnm\",\"usr\":\"Usr\",\"typ\":\"Typ\",\"exp\":5}";

        assertThat(jsonUtil.parseToAuthenticationModel(data).getDnm(), is("Dnm"));
        assertThat(jsonUtil.parseToAuthenticationModel(data).getExp(), is(5L));
        assertThat(jsonUtil.parseToAuthenticationModel(data).getTyp(), is("Typ"));
        assertThat(jsonUtil.parseToAuthenticationModel(data).getUsr(), is("Usr"));
    }

    @Test
    public void shouldReturnEmptyAuthenticationModelWhenParseJSONToAuthenticationModelWithIncorrectData() {
        assertThat(jsonUtil.parseToAuthenticationModel("").getDnm(), is(nullValue()));
    }

    @Test
    public void shouldReturnPromotionDataWhenParseWithValidStringJSON() {
        String data = "{\"html_note_translation\":\"HtmlNoteTranslation\",\"html_note\":\"HtmlNote\",\"plain_note\":"
                + "null,\"plain_note_translation\":null,\"img_web\":null,\"img_mobile\":\"ImgMobile\","
                + "\"img_web_translation\":null,\"img_mobile_translation\":null,\"thumb_web\":null,\"thumb_mobile\""
                + ":null,\"thumb_web_translation\":null,\"thumb_mobile_translation\":null}";

        assertThat(jsonUtil.parseToPromotionData(data).getHtmlNote(), is("HtmlNote"));
        assertThat(jsonUtil.parseToPromotionData(data).getHtmlNoteTranslation(), is("HtmlNoteTranslation"));
        assertThat(jsonUtil.parseToPromotionData(data).getImgMobile(), is("ImgMobile"));
    }


}
