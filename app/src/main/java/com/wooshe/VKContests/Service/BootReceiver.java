package com.wooshe.VKContests.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import static com.wooshe.VKContests.Service.Util.isMyServiceRunning;
import static com.wooshe.VKContests.Service.Util.refresh;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_BOOT;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_REFRESH;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.REFRESH_SERVICE_SECOND;

public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

         if(intent!=null)
            {
                if(intent.getAction()!=null)
                {
                    if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
                    {

                        if (!isMyServiceRunning(context, ClipService.class))
                        {
                            context.startService(new Intent(context, ClipService.class));
                        }

                        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent refreshIntent = new Intent(context, TimeCheck.class);
                        refreshIntent.putExtra(ALARM_TYPE,ALARM_TYPE_REFRESH);
                        refreshIntent.putExtra(ALARM_TYPE_BOOT,true);
                        refreshIntent.setData(Uri.parse(refreshIntent.toUri(Intent.URI_INTENT_SCHEME)));
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT );
                        am.cancel(pendingIntent);
                        am.set(AlarmManager.RTC, System.currentTimeMillis() + REFRESH_SERVICE_SECOND * 15, pendingIntent);
                    }
                }
            }
    }
}