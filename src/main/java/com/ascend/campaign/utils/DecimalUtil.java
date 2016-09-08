package com.ascend.campaign.utils;

import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@Component
public class DecimalUtil {
    public double roundTo2DecimalsCEILING(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        df2.setRoundingMode(RoundingMode.CEILING);
        return Double.valueOf(df2.format(val));
    }

    public double roundTo2DecimalsFLOOR(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        df2.setRoundingMode(RoundingMode.FLOOR);
        return Double.valueOf(df2.format(val));
    }

    public double roundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
        return Double.valueOf(df2.format(val));
    }
}
