package com.ascend.campaign.utils;

import com.ascend.campaign.constants.CampaignEnum;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnumUtilTest {

    EnumUtil enumUtil;

    @Before
    public void setUp() {
        this.enumUtil = new EnumUtil();
    }

    @Test
    public void shouldTrueWhenValueIsInEnum() {
        assertThat(enumUtil.isInEnum("user", CampaignEnum.class), is(true));
    }

    @Test
    public void shouldFalseWhenValueIsNotInEnum() {
        assertThat(enumUtil.isInEnum("AWIADJISD", CampaignEnum.class), is(false));
    }
}
