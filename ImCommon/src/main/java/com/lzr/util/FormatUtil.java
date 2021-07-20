package com.lzr.util;


import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * FormatUtil
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
public class FormatUtil
{

    /**
     * hold decimal
     *
     * @param fractions bits
     * @return format
     */
    public static DecimalFormat decimalFormat(int fractions)
    {

        DecimalFormat df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(fractions);
        df.setMaximumFractionDigits(fractions);
        return df;
    }
}
