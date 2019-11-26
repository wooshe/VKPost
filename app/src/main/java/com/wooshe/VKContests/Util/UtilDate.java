package com.wooshe.VKContests.Util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.wooshe.VKContests.no_use.Constants.MY_DATA_FORMAT;

public class UtilDate
{
    public static String String_unixTime(long utime)
    {
        long time = utime * (long) 1000;
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(MY_DATA_FORMAT);
        return format.format(date);
    }

    public static String String_unixTimeWithoutTime(long utime)
    {
        long time = utime;
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy HH:mm");
        String str = format.format(date);

        str = str.substring(0,str.length()-6);

        return str;
    }

    public static Date DateunixTime(long utime)
    {
        long time = utime * (long) 1000;
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(MY_DATA_FORMAT);
        String str = format.format(date);
        Date dat=null;
        try
        {
            dat = format.parse(str);
            System.out.println(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return dat;
    }

    public static Date DateunixTime(String str)
    {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(MY_DATA_FORMAT);
        try
        {
            date = format.parse(str);
            System.out.println(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
    public static String DateunixTime(Date date)
    {
        if(date==null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat(MY_DATA_FORMAT);
        return format.format(date);
    }

    public String getCoolTime(Calendar time)
    {
        return (time.get(Calendar.DAY_OF_YEAR) - 1) + "d "
                + time.get(Calendar.HOUR_OF_DAY) + ":"
                + time.get(Calendar.MINUTE) + ":" + time.get(Calendar.SECOND);
    }
}
