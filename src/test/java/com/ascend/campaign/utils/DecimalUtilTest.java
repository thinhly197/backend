package com.ascend.campaign.utils;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DecimalUtilTest {

    @Autowired
    DecimalUtil decimalUtil;

    @Before
    public void setUp() {
        this.decimalUtil = new DecimalUtil();
    }

    @Test
    public void shouldRoundUpTwoDoubleDigitWhenRoundToDecimal() {
        assertThat(decimalUtil.roundTo2Decimals(2.5575d), is(2.56d));
    }

    @Test
    public void shouldRoundUpTwoDoubleDigitWhenRoundToDecimalCeiling() {
        assertThat(decimalUtil.roundTo2DecimalsCEILING(2.5575d), is(2.56d));
    }

    @Test
    public void shouldRoundUpTwoDoubleDigitWhenRoundToDecimalFloor() {
        assertThat(decimalUtil.roundTo2DecimalsFLOOR(2.5575d), is(2.55d));
    }
}
