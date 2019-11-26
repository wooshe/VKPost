package com.wooshe.VKContests.Net;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;
import com.wooshe.VKContests.Realm.Alarm;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Realm.EventFriends;
import com.wooshe.VKContests.Realm.EventPhoto;
import com.wooshe.VKContests.Service.TimeCheck;
import com.wooshe.VKContests.no_use.Application;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.wooshe.VKContests.Net.Download.DeleteFile;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_NOTIFY;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.LOGGED_ERROR;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.NET_ACCESS_ERROR;
import static com.wooshe.VKContests.no_use.Constants.OK;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.event_state_new;
import static com.wooshe.VKContests.no_use.Constants.event_state_repost;

public class Util
{
    public static final void restartEvent(Context context, Realm mRealm, Event event)
    {
        String wid = event.getWid();
        mRealm.beginTransaction();
        event.setSwitch_notif(false);
        event.setSwitch_repost(false);
        event.setSwitch_end(false);
        event.setRepost_id(-1);
        event.setEvent_state(event_state_new);
        for(int i = 0; i<event.getFriends().size();i++)
        {
            event.getFriends().get(i).setToEnter(false);
        }
        mRealm.commitTransaction();
        ResetAlarm(context,wid,ALARM_TYPE_REPOST);
        ResetAlarm(context,wid,ALARM_TYPE_NOTIFY);
        ResetAlarm(context,wid,ALARM_TYPE_END);
    }

    public final static void SetAlarm(Context context, String wid, String type, Date date)
    {
        ResetAlarm( context,  wid,  type);
        RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(context)
                .name("Alarm")
                .build();
        Realm mRealmAlarm = OpenTable(nrealmConfiguration);
        mRealmAlarm.beginTransaction();
        Alarm alarm = mRealmAlarm.createObject(Alarm.class);
        alarm.setId(wid+type);
        alarm.setWid(wid);
        alarm.setType(type);
        alarm.setDate(date);
        mRealmAlarm.commitTransaction();
        mRealmAlarm.close();

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeCheck.class);
        intent.putExtra(ALARM_TYPE,type);
        intent.putExtra(WID,wid);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);

    }

    public final static void ResetAlarm(Context context, String wid, String type)
    {

        RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(context)
                .name("Alarm")
                .build();
        Realm mRealmAlarm = OpenTable(nrealmConfiguration);
        Alarm alarm = mRealmAlarm.where(Alarm.class).equalTo("id",wid+type).findFirst();
        if(alarm!=null)
        {
            mRealmAlarm.beginTransaction();
            alarm.removeFromRealm();
            mRealmAlarm.commitTransaction();
        }
        mRealmAlarm.close();

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeCheck.class);
        intent.putExtra(ALARM_TYPE,type);
        intent.putExtra(WID,wid);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        am.cancel(pendingIntent);
    }

    public final static boolean DeleteCache(Event obj)
    {
        if(obj==null)
            return false;
        RealmList<EventPhoto> photo = obj.getPhoto();
        if(photo.isEmpty())
            return false;
        while(!photo.isEmpty())
        {
            String path_photo_75 = photo.get(0).getPath_photo_75();
            if(path_photo_75!=null)
            {
                DeleteFile(path_photo_75);
            }

            String path_photo_604 = photo.get(0).getPath_photo_604();
            if(path_photo_604!=null)
            {
                DeleteFile(path_photo_604);
            }

            photo.remove(0);
        }

        return true;
    }

    public final static void GroupLeave(Realm mRealm, RealmList<EventFriends> groups)
    {
        VKRequest vk=null;


        List<String> prepare = new ArrayList<String>();

        RealmResults<Event> sort= mRealm.where(Event.class)
                .equalTo("event_state",event_state_repost)
                .findAll();

        for(int i = 0; i<sort.size();i++)
        {

            for(int j = 0; j<groups.size();j++)
            {
                if( sort.get(i).getFriends().where().equalTo("id",groups.get(j).getId()).findAll().size()==0)
                {
                    if(groups.get(j).isLeave())
                    prepare.add(groups.get(j).getId());
                }
            }

        }
        if(sort.size()==0)
        {
            for(int j = 0; j<groups.size();j++)
            {
                    if(groups.get(j).isLeave())
                        prepare.add(groups.get(j).getId());
            }
        }

        for(int i=0;i<prepare.size();i++)
        {
            VKParameters parameters = new VKParameters();
            parameters.put(VKApiConst.GROUP_ID, prepare.get(i));
            vk = VKApi.groups().leave(parameters);
            vk.executeWithListener(new VKRequest.VKRequestListener()
            {
                @Override
                public void onComplete(VKResponse response)
                {
                    super.onComplete(response);
                }

                @Override
                public void onError(VKError error)
                {
                    super.onError(error);
                }
            });
        }
    }

    public final static boolean DeletePostFromWall(int user_id, int repost_id)
    {

        VKParameters parameters = new VKParameters();
        parameters.put(VKApiConst.OWNER_ID, user_id);
        parameters.put(VKApiConst.POST_ID, repost_id);
        VKRequest vk = VKApi.wall().delete(parameters);
        final boolean[] error = {true};

        vk.executeSyncWithListener(new VKRequest.VKRequestListener()
        {
            @Override
            public void onComplete(VKResponse response)
            {
                super.onComplete(response);
                error[0] = false;
            }
        });
        return error[0];
    }

    public final static boolean checkPost(int user_id, int repost_id)
    {
        final VKList[] vkresponse = new VKList[1];
        final boolean[] code = {false};
        VKParameters parameters = new VKParameters();

        String user_posts_id= user_id+"_"+repost_id;

        parameters.put(VKApiConst.POSTS,user_posts_id);
        VKRequest vkr = VKApi.wall().getById(parameters);

        vkr.executeSyncWithListener(new VKRequest.VKRequestListener()
        {
            @Override
            public void onComplete(VKResponse response)
            {
                super.onComplete(response);
                vkresponse[0] = (VKList) response.parsedModel;
                if(vkresponse[0]==null || vkresponse[0].size()==0)
                {
                    code[0]= false;
                }
                else
                    code[0]= true;
            }

            @Override
            public void onError(VKError error)
            {
                super.onError(error);

                code[0]=false;
                throw new NullPointerException("MYERROR");
            }
        });

        return code[0];
    }

    public final static boolean Req()
    {
        final boolean[] ret = {false};
        VKRequest vvv = new VKRequest("utils.getServerTime");
        vvv.executeSyncWithListener(new VKRequest.VKRequestListener()
        {
            @Override
            public void onComplete(VKResponse response)
            {
                super.onComplete(response);
                ret[0]=true;
            }

            @Override
            public void onError(VKError error)
            {
                super.onError(error);
                ret[0]=false;
            }
        });

        return ret[0];
    }

    public final static int isNetisLogin(Context context)
    {
        if(Req()==false)
        {
            if(isNetAccess(context)==false)
            return NET_ACCESS_ERROR;
        }

        if(isLogin()==false)
            return LOGGED_ERROR;

        return OK;
    }

    public final static boolean isLogin()
    {
        return VKSdk.isLoggedIn();
    }


    public final static boolean isNetAccess(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        long timeOut = 500;

        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            InetAddress inetAddress = null;
            try {
                Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                    @Override
                    public InetAddress call() {
                        try {
                            return InetAddress.getByName("google.com");
                        } catch (UnknownHostException e) {
                            return null;
                        }
                    }
                });
                inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
                future.cancel(true);
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            } catch (TimeoutException e) {
            }
            return inetAddress!=null && !inetAddress.equals("");
        }
        return false;
    }

}
