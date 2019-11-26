package com.wooshe.VKContests.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.appodeal.ads.Appodeal;
import com.wooshe.R;
import com.wooshe.VKContests.no_use.Constants;

import static com.wooshe.VKContests.no_use.Constants.COUNT_OF_INTER;
import static com.wooshe.VKContests.no_use.Constants.COUNT_OF_LAUNCH;
import static com.wooshe.VKContests.no_use.Constants.INTER;
import static com.wooshe.VKContests.no_use.Constants.RATE_INFO;
import static com.wooshe.VKContests.no_use.Constants.VIDEO;

public class OtherUtil
{

    public static int GetVInterCount(Activity ctx)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        return APP_PREFERENCES.getInt("VCOUNT_OF_INTER",0);
    }

    public static void SetVInterCount(Activity ctx, int count)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor settings = APP_PREFERENCES.edit();
        settings.putInt("VCOUNT_OF_INTER",count);
        settings.commit();
    }

    public static int IncreaseVInterCount(Activity ctx)
    {
        int count = GetVInterCount(ctx);
        count++;
        if(count==51)
            count=1;
        SetVInterCount(ctx,count);

        if(count==25)
            return INTER;
        if(count==50)
            return VIDEO;

        return -1;
    }

    public static void VINTER(Activity act)
    {
        int ret = IncreaseVInterCount(act);
        switch (ret)
        {
            case INTER:
                if(Appodeal.isLoaded(Appodeal.INTERSTITIAL))
                {
                    Appodeal.show(act, Appodeal.INTERSTITIAL);

                }
                break;

            case VIDEO:
                if(Appodeal.isLoaded(Appodeal.REWARDED_VIDEO))
                {
                    Appodeal.show(act, Appodeal.REWARDED_VIDEO);

                }
                break;
        }
    }

    public static int GetInterCount(Activity ctx)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        return APP_PREFERENCES.getInt(COUNT_OF_INTER,0);
    }

    public static void SetInterCount(Activity ctx, int count)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor settings = APP_PREFERENCES.edit();
        settings.putInt(COUNT_OF_INTER,count);
        settings.commit();
    }

    public static int IncreaseInterCount(Activity ctx)
    {
        int count = GetInterCount(ctx);
        count++;
        if(count==16)
            count=1;
        SetInterCount(ctx,count);

        if(count==10)
            return INTER;
        if(count==15)
            return VIDEO;

        return -1;
    }

    public static void INTERVIDEO(Activity act)
    {
        int ret = IncreaseInterCount(act);
        switch (ret)
        {
            case INTER:
                if(Appodeal.isLoaded(Appodeal.INTERSTITIAL))
                {
                    Appodeal.show(act, Appodeal.INTERSTITIAL);

                }
                break;

            case VIDEO:
                if(Appodeal.isLoaded(Appodeal.REWARDED_VIDEO))
                {
                    Appodeal.show(act, Appodeal.REWARDED_VIDEO);

                }
                break;
        }
    }

    public static void SendToDevelop(Activity act)
    {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","wooshe55@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, act.getPackageName());
            emailIntent.putExtra(Intent.EXTRA_TEXT," ");
            act.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void Friend(Activity act, String message)
    {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setType("text/plain");
            act.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void VKGroup(Activity act)
    {

        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(act.getString(R.string.appGroup)));
            intent.setPackage("com.vkontakte.android");
            act.startActivity(intent);
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(act.getString(R.string.appGroup)));
            act.startActivity(intent);
        }
    }

    public static void Rate(Activity act)
    {
        OtherUtil.SetRateInfo(act);
        final String appPackageName = act.getPackageName();
        try {
            act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void IncreaseCountOfLaunch(Context ctx)
    {
        int count_off_launch = GetCountOfLaunch(ctx);
        count_off_launch++;
        SetCountOfLaunch(ctx,count_off_launch);
    }

    public static int GetCountOfLaunch(Context ctx)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        return APP_PREFERENCES.getInt(COUNT_OF_LAUNCH,0);
    }

    public static void SetCountOfLaunch(Context ctx, int count_off_launch)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor settings = APP_PREFERENCES.edit();
        settings.putInt(COUNT_OF_LAUNCH,count_off_launch);
        settings.commit();
    }

    public static boolean GetRateInfo(Context ctx)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        return APP_PREFERENCES.getBoolean(RATE_INFO,false);
    }

    public static void SetRateInfo(Context ctx)
    {
        SharedPreferences APP_PREFERENCES = ctx.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor settings = APP_PREFERENCES.edit();
        settings.putBoolean(RATE_INFO,true);
        settings.commit();
    }

}
