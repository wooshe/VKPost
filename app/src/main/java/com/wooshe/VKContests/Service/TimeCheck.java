package com.wooshe.VKContests.Service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wooshe.VKContests.Activity_Detail;
import com.wooshe.VKContests.Net.Repost;
import com.wooshe.VKContests.Realm.Alarm;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Realm.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.wooshe.VKContests.Net.Util.DeletePostFromWall;
import static com.wooshe.VKContests.Net.Util.ResetAlarm;
import static com.wooshe.VKContests.Net.Util.SetAlarm;
import static com.wooshe.VKContests.Net.Util.isNetisLogin;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.Service.ClipService.sendNotification;
import static com.wooshe.VKContests.Service.Util.refresh;
import static com.wooshe.VKContests.no_use.Application.getAppContext;
import static com.wooshe.VKContests.no_use.Constants.ALARM_PHOTO;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_BOOT;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_NOTIFY;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_REFRESH;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_ALL;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_END;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_NOTIFY;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_REPOST;
import static com.wooshe.VKContests.no_use.Constants.ERR;
import static com.wooshe.VKContests.no_use.Constants.LOGGED_ERROR;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.NET_ACCESS_ERROR;
import static com.wooshe.VKContests.no_use.Constants.OK;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_ALL;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_SOON;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.event_state_end;
import static com.wooshe.VKContests.no_use.Constants.event_state_repost;
import static com.wooshe.VKContests.no_use.Constants.refresh_timeout;

public class TimeCheck extends BroadcastReceiver implements Repost.RepostListener
{
    private Context context;

    @Override
    public void RepostSuccessfully(Integer code, String wid, Integer repost_id)
    {
        Intent notificationIntent = new Intent(context, Activity_Detail.class);
        notificationIntent.putExtra(WID,wid);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(RECYCLER_TYPE,RECYCLER_TYPE_REPOST);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
        Realm mRealm=OpenTable(realmConfiguration);
        Event event = mRealm.where(Event.class).equalTo("wid",wid).findFirst();
        if(event.getPhoto().size()>0)
            notificationIntent.putExtra(ALARM_PHOTO,event.getPhoto().get(0).getPath_photo_130());

        switch (code)
        {
            case OK:
                sendNotification(context,notificationIntent,APP_NOTIFICATION_REPOST,"ВК Конкурсы","Репост!", wid);
                mRealm.beginTransaction();
                event.setSwitch_repost(false);
                event.setRepost_id(repost_id);
                event.setEvent_state(event_state_repost);
                mRealm.commitTransaction();
                ResetAlarm(context,wid,ALARM_TYPE_REPOST);
                break;

            case ERR:
                //notificationIntent.putExtra(RECYCLER_TYPE,RECYCLER_TYPE_SOON);
                //sendNotification(context,notificationIntent,APP_NOTIFICATION_ALL,"ВК Конкурсы","Произошла ошибка!", wid);
                break;

            case NET_ACCESS_ERROR:
                notificationIntent.putExtra(RECYCLER_TYPE,RECYCLER_TYPE_SOON);
                sendNotification(context,notificationIntent,APP_NOTIFICATION_ALL,"ВК Конкурсы","Проверьте соединение с интернетом!", wid);
                break;

            case LOGGED_ERROR:
                notificationIntent.putExtra(RECYCLER_TYPE,RECYCLER_TYPE_SOON);
                sendNotification(context,notificationIntent,APP_NOTIFICATION_ALL,"ВК Конкурсы","Войдите в учётную запись!", wid);
                break;
        }

        mRealm.close();
    }

    private void EndPost(Event event, Intent notificationIntent, Realm mRealm, String wid,boolean state)
    {
        if(event.getPhoto().size()>0)
            notificationIntent.putExtra(ALARM_PHOTO,event.getPhoto().get(0).getPath_photo_130());
        notificationIntent.putExtra(RECYCLER_TYPE,RECYCLER_TYPE_END);
        if(event.getEvent_state()==event_state_repost)
        {
            RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(context)
                    .name("User")
                    .build();
            Realm userRealm = OpenTable(nrealmConfiguration);
            RealmResults<User> user = userRealm.where(User.class).findAll();
            if(user.get(0).getUser_id()>=0)
            {
                boolean err = DeletePostFromWall(user.get(0).getUser_id(),event.getRepost_id());
                if(err==false)
                {
                    sendNotification(context,notificationIntent,APP_NOTIFICATION_END,"ВК Конкурсы","Конкурс завершен", wid);
                    mRealm.beginTransaction();
                    event.setEvent_state(event_state_end);
                    event.setSwitch_notif(false);
                    event.setSwitch_end(false);
                    event.setSwitch_repost(false);
                    event.setRepost_id(-1);
                    for(int i = 0; i<event.getFriends().size();i++)
                    {
                        event.getFriends().get(i).setToEnter(false);
                    }
                    mRealm.commitTransaction();
                    ResetAlarm(context,wid,ALARM_TYPE_END);
                }
                else
                {
                    int error = com.wooshe.VKContests.Net.Util.isNetisLogin(context);
                    if(error==NET_ACCESS_ERROR && state)
                    {
                        sendNotification(context,notificationIntent,APP_NOTIFICATION_ALL,"ВК Конкурсы","Проверьте соединение с интернетом!", wid);
                    }
                    else if(error==LOGGED_ERROR && state)
                    {
                        sendNotification(context,notificationIntent,APP_NOTIFICATION_ALL,"ВК Конкурсы","Войдите в учётную запись!", wid);
                    }

                }
            }
            userRealm.close();
        }
    }

    @Override
    public void onReceive(Context ctx, Intent intent)
    {

        context=ctx;

        Context ct = getAppContext();
        if(ct!=null)
            context=ct;

        String  wid = intent.getStringExtra(WID);
        String type = intent.getStringExtra(ALARM_TYPE);

        if(type==null)
        {
            refresh(context,refresh_timeout);

            return;
        }
        else
        {

            int err = isNetisLogin(context);

            if(type.equals(ALARM_TYPE_REFRESH))
            {
                if(intent.getBooleanExtra(ALARM_TYPE_BOOT,false)==true)
                {
                    RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(context)
                            .name("Alarm")
                            .build();
                    Realm mRealmAlarm = OpenTable(nrealmConfiguration);
                    RealmResults<Alarm> alarm = mRealmAlarm.where(Alarm.class).findAll();

                    List<Alarm> al = mRealmAlarm.copyFromRealm(alarm);

                    for(int i = 0; i<al.size();i++)
                    {
                        SetAlarm(context,al.get(i).getWid(),al.get(i).getType(),al.get(i).getDate());
                    }

                    mRealmAlarm.close();

                }

                else if(err==OK)
                {

                    RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(context)
                            .name("Alarm")
                            .build();
                    Realm mRealmAlarm = OpenTable(nrealmConfiguration);
                    RealmResults<Alarm> alarm = mRealmAlarm.where(Alarm.class).findAll();

                    List<Alarm> al = mRealmAlarm.copyFromRealm(alarm);

                    for(int i = 0; i<al.size();i++)
                    {
                        SetAlarm(context,al.get(i).getWid(),al.get(i).getType(),al.get(i).getDate());
                    }

                    mRealmAlarm.close();

                }

                refresh(context,refresh_timeout);
                return;
            }
            else if(wid!=null)
            {
                RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
                Realm mRealm=OpenTable(realmConfiguration);
                Event event = mRealm.where(Event.class).equalTo("wid",wid).findFirst();

                if(event==null)
                {
                    refresh(context,refresh_timeout);
                    return;
                }
                Repost repost = null;
                Intent notificationIntent = new Intent(context, Activity_Detail.class);
                notificationIntent.putExtra(WID,wid);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                switch (type)
                {
                    case ALARM_TYPE_REPOST:
                        if(event.getEvent_state()==event_state_repost)
                            break;

                        repost = new Repost(context, event.getWid(), mRealm.copyFromRealm(event.getFriends()),event.isPin());
                        repost.setRepostListener(this);
                        repost.execute();
                        break;

                    case ALARM_TYPE_END:
                        EndPost(event,notificationIntent,mRealm,wid,true);
                        break;

                    case ALARM_TYPE_NOTIFY:
                        notificationIntent.putExtra(RECYCLER_TYPE,RECYCLER_TYPE_ALL);
                        if(event.getPhoto().size()>0)
                            notificationIntent.putExtra(ALARM_PHOTO,event.getPhoto().get(0).getPath_photo_130());
                        sendNotification(context,notificationIntent,APP_NOTIFICATION_NOTIFY,"ВК Конкурсы","Напоминание о конкурсе", wid);
                        mRealm.beginTransaction();
                        event.setSwitch_notif(false);
                        mRealm.commitTransaction();
                        ResetAlarm(context,wid,ALARM_TYPE_NOTIFY);
                        break;
                }
                mRealm.close();
            }
        }

        refresh(context,refresh_timeout);
    }

}
