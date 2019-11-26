package com.wooshe.VKContests.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.wooshe.R;
import com.wooshe.VKContests.MainActivity;
import com.wooshe.VKContests.Net.Update;
import com.wooshe.VKContests.Util.ViewIdGenerator;
import com.wooshe.VKContests.no_use.Constants;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.RealmConfiguration;

import static com.wooshe.VKContests.Net.Util.isNetAccess;
import static com.wooshe.VKContests.Service.Util.refresh;
import static com.wooshe.VKContests.no_use.Constants.ALARM_PHOTO;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_DOWNLOAD;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_END;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_NEW;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_NOTIFY;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_REPOST;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_TAG;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.SETTINGS_HOOK;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_DOWNLOAD;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_ERROR;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_OBJECT_EXIST;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_OK;
import static com.wooshe.VKContests.no_use.Constants.WID;


public class ClipService extends android.app.Service implements ClipboardManager.OnPrimaryClipChangedListener, Update.UpdateListener
{
    private ClipboardManager clipboard;
    private int numMessages = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {

        return null;
    }

    public void onCreate()
    {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String wid = intent.getStringExtra(WID);
        if(wid!=null)
            preUpd(wid);

        if(startId!=1)
                stopSelf(startId);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        try
        {
            clipboard.removePrimaryClipChangedListener(this);
        }
        catch (Exception ex)
        {

        }
        clipboard.addPrimaryClipChangedListener(this);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onPrimaryClipChanged()
    {
        SharedPreferences APP_PREFERENCES = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);

        if(APP_PREFERENCES.getBoolean(SETTINGS_HOOK,true)==true)
        {
            String wid;
            ClipData clipData = clipboard.getPrimaryClip();

            if (clipData != null)
            {
                wid = clipData.getItemAt(0).getText().toString();
                preUpd(wid);
            }
        }
    }

    @Override
    public void UpdateSuccessfully(Integer code, String path)
    {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.cancel(APP_NOTIFICATION_TAG,APP_NOTIFICATION_DOWNLOAD);
        switch (code)
        {
            case UPDATE_OK:
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notificationIntent.putExtra(ALARM_PHOTO,path);
                sendNotification(this,notificationIntent,APP_NOTIFICATION_NEW,"ВК Конкурсы", "Новый конкурс!","Нажмите, чтобы посмотреть");
                break;
            case UPDATE_OBJECT_EXIST:
                Toast.makeText(getApplicationContext(),"ВК Конкурсы. Данное событие уже существует!",Toast.LENGTH_SHORT).show();
                break;
            case UPDATE_ERROR:
                Toast.makeText(getApplicationContext(), "Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void preUpd(String wid)
    {
        if (wid.contains("vk.com/wall"))
        {
            if (isNetAccess(getApplicationContext()))
                Upd(wid);
            else
                Toast.makeText(getApplicationContext(), "Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
        }
    }

    public void Upd(String wid)
    {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Update upd = new Update(getApplicationContext(),UPDATE_DOWNLOAD,realmConfiguration,wid);
        upd.setUpdateListener(this);
        upd.execute();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        sendNotification(this,notificationIntent,APP_NOTIFICATION_DOWNLOAD,"ВК Конкурсы", "Добавление конкурса, ждите!","Для ускорения, включите режим экономии трафика!");
    }

    public final static void sendNotification(Context context, Intent notificationIntent, int id, String Ticker,String Title,String wid)
    {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_launcher);

        String path = notificationIntent.getStringExtra(ALARM_PHOTO);

        if(path!=null)
        {
            try
            {
                bm = BitmapFactory.decodeFile(path);
            }
            catch (Exception e)
            {

            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setTicker(Ticker)
                .setContentTitle(Title)
                .setContentText(wid)
                .setLargeIcon(bm)
                .setWhen(System.currentTimeMillis());

        if(id==APP_NOTIFICATION_DOWNLOAD)
        {
            builder.setProgress(100,0,true);
            builder.setSmallIcon(android.R.drawable.stat_sys_upload);
        }
        else
        {
            builder.setSmallIcon(R.drawable.icon_notif);
        }

        switch (id)
        {
            case APP_NOTIFICATION_NEW:

            case APP_NOTIFICATION_END:

            case APP_NOTIFICATION_NOTIFY:

            case APP_NOTIFICATION_REPOST:
                id = ViewIdGenerator.generateViewId();
                break;
        }
        builder.setGroup("GK");
        Notification notification;
        if (android.os.Build.VERSION.SDK_INT<=15)
        {
            notification = builder.getNotification();
        }
        else
        {
            notification = builder.build();
        }
        if(id!=APP_NOTIFICATION_DOWNLOAD)
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;

        notificationManager.notify(APP_NOTIFICATION_TAG, id, notification);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
    }

    public void onDestroy()
    {
        super.onDestroy();
    }
}
