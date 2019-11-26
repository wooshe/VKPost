package com.wooshe.VKContests.Service;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_REFRESH;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.REFRESH_SERVICE_SECOND;

public class Util
{

    public static final void refresh(Context context, int seconds)
    {
        if (!isMyServiceRunning(context, ClipService.class))
        {
            context.startService(new Intent(context, ClipService.class));
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent refreshIntent = new Intent(context, TimeCheck.class);
        refreshIntent.putExtra(ALARM_TYPE,ALARM_TYPE_REFRESH);
        refreshIntent.setData(Uri.parse(refreshIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT );
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + REFRESH_SERVICE_SECOND * seconds, pendingIntent);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

}
