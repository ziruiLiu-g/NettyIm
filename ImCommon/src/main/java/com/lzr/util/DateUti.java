package com.lzr.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUti
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
public class DateUti
{

    /**
     * get today date
     *
     * @return date
     */
    public static String getToday()
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(new Date().getTime());
    }

    /**
     * get yesterday
     *
     * @return Yestoday
     */
    public static String getYestoday()
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        return sdf.format(date.getTime());
    }

    public static String getNow()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return sdf.format(new Date().getTime());

    }
}
