package com.wooshe.VKContests.Net;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Realm.EventFriends;
import com.wooshe.VKContests.Realm.EventPhoto;
import com.wooshe.VKContests.Util.UtilDate;
import com.wooshe.VKContests.no_use.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.vk.sdk.api.model.VKAttachments.TYPE_LINK;
import static com.vk.sdk.api.model.VKAttachments.TYPE_PHOTO;
import static com.wooshe.VKContests.Net.Download.DownloadImage;
import static com.wooshe.VKContests.Net.Util.DeleteCache;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.Util.UtilDate.DateunixTime;
import static com.wooshe.VKContests.no_use.Constants.ALLWALL;
import static com.wooshe.VKContests.no_use.Constants.ERROR;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.PUBLIC;
import static com.wooshe.VKContests.no_use.Constants.REFRESH_SERVICE_DAY;
import static com.wooshe.VKContests.no_use.Constants.SETTINGS_TRAFFIC;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_DOWNLOAD;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_ERROR;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_OBJECT_EXIST;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_OK;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_UPDATE;
import static com.wooshe.VKContests.no_use.Constants.USER;
import static com.wooshe.VKContests.no_use.Constants.WALL;
import static com.wooshe.VKContests.no_use.Constants.event_state_new;

public class Update extends AsyncTask<Void, Void, Integer>
{

    UpdateListener updateListener;
    RealmConfiguration realmConfiguration;
    String wid;
    Context context;
    int mode;
    String path;

    public interface UpdateListener
    {
        void UpdateSuccessfully(Integer code,String path);
    }

    public void setUpdateListener(UpdateListener listener)
    {
        this.updateListener = listener;
    }

    public Update(Context context,int mode , RealmConfiguration realmConfiguration, String wid)
    {
        this.realmConfiguration=realmConfiguration;
        this.wid=wid;
        this.context=context;
        this.mode=mode;
    }

    @Override
    protected Integer doInBackground(Void... voids)
    {
        int code = 0;
        code = UpdateEvent(context,mode,realmConfiguration,wid);
        return code;
    }

    @Override
    protected void onPostExecute(Integer code)
    {
        updateListener.UpdateSuccessfully(code, this.path);
    }

    public  int UpdateEvent(Context context,int mode, RealmConfiguration realmConfiguration, String wid)
    {
        int result_code=UPDATE_ERROR;
        final VKList[] vkresponse = new VKList[1];
        final VKApiPost[] post = {null};
        final VKApiLink[] link = {null};
        final VKApiCommunity[] group = {null};
        final VKApiUser[] user = {null};
        final VKAttachments[] vkAttachments = {null};
        boolean community = false;
        final boolean[] err = {false};
        int post_from_id;
        int group_id;
        int post_owner_id;
        long    post_date;
        String linkstr="";
        String post_text="";
        String group_site="";
        String group_name="";
        String group_post_id= wid.substring(wid.lastIndexOf(WALL)+WALL.length());

        Realm mRealm=null;
        Event event =null;
        EventPhoto eventPhoto =null;
        mRealm = OpenTable(realmConfiguration);
        final JSONArray[] groupsss = new JSONArray[1];
        try
        {

            VKParameters parameters = new VKParameters();
            parameters.put(VKApiConst.POSTS,group_post_id);
            parameters.put(VKApiConst.EXTENDED,true);
            VKRequest vkr = VKApi.wall().getById(parameters);

            vkr.executeSyncWithListener(new VKRequest.VKRequestListener()
            {
                @Override
                public void onComplete(VKResponse response)
                {
                    super.onComplete(response);

                    vkresponse[0] = (VKList) response.parsedModel;
                    post[0] = (VKApiPost) vkresponse[0].get(0);

                    try
                    {
                        groupsss[0] = response.json.getJSONObject("response").getJSONArray("groups");
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VKError error)
                {
                    super.onError(error);
                    err[0]=true;
                    throw new NullPointerException("MYERROR");
                }
            });



            if(!post[0].copy_history.isEmpty())
            {
                parameters = new VKParameters();
                wid=ALLWALL+post[0].copy_history.get(0).from_id+"_"+post[0].copy_history.get(0).id;
                group_post_id=wid.substring(wid.lastIndexOf(WALL)+WALL.length());
                parameters.put(VKApiConst.POSTS,group_post_id);
                parameters.put(VKApiConst.EXTENDED,true);
                vkr = VKApi.wall().getById(parameters);

                vkr.executeSyncWithListener(new VKRequest.VKRequestListener()
                {
                    @Override
                    public void onComplete(VKResponse response)
                    {
                        super.onComplete(response);
                        vkresponse[0] = (VKList) response.parsedModel;
                        post[0] = (VKApiPost) vkresponse[0].get(0);
                    }

                    @Override
                    public void onError(VKError error)
                    {
                        super.onError(error);

                        err[0]=true;
                        throw new NullPointerException("MYERROR");
                    }
                });
            }


            boolean first = true;
            vkAttachments[0] = post[0].attachments;

            SharedPreferences APP_PREFERENCES = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
            boolean traffic = APP_PREFERENCES.getBoolean(SETTINGS_TRAFFIC,false);

            List<EventPhoto> allPhoto = new ArrayList<EventPhoto>();

            if(!vkAttachments[0].isEmpty())
            {
                if(!traffic || mode==UPDATE_UPDATE)
                {
                    event = mRealm.where(Event.class).equalTo("wid",wid).findFirst();
                    if(event!=null)
                    {
                        mRealm.beginTransaction();
                        DeleteCache(event);
                        mRealm.commitTransaction();
                    }
                    for(int i =0;i<vkAttachments[0].size();i++)
                    {
                        if( vkAttachments[0].get(i).getType().equals(TYPE_PHOTO) )
                        {
                            VKApiPhoto vkPhoto = (VKApiPhoto) vkAttachments[0].get(i);
                            eventPhoto = new EventPhoto();
                            eventPhoto.setText(vkPhoto.text);
                            eventPhoto.setPhoto_604(vkPhoto.photo_604);

                            String Dir="Image";
                            String url;
                            String Filename;
                            String   result;

                            if(first)
                            {
                                eventPhoto.setPhoto_130(vkPhoto.photo_130);
                                url= eventPhoto.getPhoto_130();
                                Filename= String.valueOf(vkPhoto.id)+"photo_130";
                                result = DownloadImage(Dir,Filename,url,true);
                                if(!result.equals(ERROR))
                                    eventPhoto.setPath_photo_130(result);
                                first=false;
                            }

                            url= eventPhoto.getPhoto_604();
                            Filename= String.valueOf(vkPhoto.id)+"photo_604";
                            result = DownloadImage(Dir,Filename,url,false);
                            if(!result.equals(ERROR))
                                eventPhoto.setPath_photo_604(result);

                            allPhoto.add(eventPhoto);
                        }

                        else if( vkAttachments[0].get(i).getType().equals(TYPE_LINK)==true )
                        {
                            link[0] = (VKApiLink) vkAttachments[0].get(i);
                        }
                    }
                }
                else if(mode==UPDATE_DOWNLOAD && vkAttachments[0].size()>=1)
                {
                        for(int i = 0; i<vkAttachments[0].size();i++)
                        {
                            if( vkAttachments[0].get(i).getType().equals(TYPE_PHOTO) )
                            {
                                VKApiPhoto vkPhoto = (VKApiPhoto) vkAttachments[0].get(i);
                                eventPhoto = new EventPhoto();
                                eventPhoto.setText(vkPhoto.text);
                                eventPhoto.setPhoto_130(vkPhoto.photo_130);

                                String Dir="Image";
                                String url= eventPhoto.getPhoto_130();
                                String Filename= String.valueOf(vkPhoto.id)+"photo_130";

                                String   result=DownloadImage(Dir,Filename,url,true);
                                if(!result.equals(ERROR))
                                    eventPhoto.setPath_photo_130(result);

                                eventPhoto.setPhoto_604(vkPhoto.photo_604);
                                url= eventPhoto.getPhoto_604();
                                Filename= String.valueOf(vkPhoto.id)+"photo_604";
                                result = DownloadImage(Dir,Filename,url,false);
                                if(!result.equals(ERROR))
                                    eventPhoto.setPath_photo_604(result);

                                allPhoto.add(eventPhoto);
                                break;
                            }
                        }

                        for (int i = 0; i<vkAttachments[0].size();i++)
                        {
                            if( vkAttachments[0].get(i).getType().equals(TYPE_LINK)==true )
                            {
                                link[0] = (VKApiLink) vkAttachments[0].get(i);
                                break;
                            }
                        }
                }
            }

            if(allPhoto.size()>0)
            path = allPhoto.get(0).getPath_photo_130();

            post_from_id= post[0].from_id;
            post_owner_id= post[0].to_id;
            post_text= post[0].text;
            post_date= post[0].date;
            if(link[0]!=null)
            linkstr = link[0].url;

            if(post_from_id<0)
            {
                community = true;
                post_from_id*=-1;
                group_site=PUBLIC;

                parameters = new VKParameters();
                parameters.put(VKApiConst.GROUP_ID, String.valueOf(post_from_id));
                VKRequest vkrz = VKApi.groups().getById(parameters);

                vkrz.executeSyncWithListener(new VKRequest.VKRequestListener()
                {
                    @Override
                    public void onComplete(VKResponse response)
                    {
                        super.onComplete(response);

                        vkresponse[0] = (VKList) response.parsedModel;
                        group[0] = (VKApiCommunity) vkresponse[0].get(0);
                    }

                    @Override
                    public void onError(VKError error)
                    {
                        super.onError(error);

                        throw new NullPointerException("MYERROR");
                    }
                });
            }

            else
            {
                community = false;
                group_site=USER;
                parameters = new VKParameters();

                parameters.put(VKApiConst.USER_IDS, String.valueOf(post_from_id));
                VKRequest vkrz = VKApi.users().get(parameters);

                vkrz.executeSyncWithListener(new VKRequest.VKRequestListener()
                {
                    @Override
                    public void onComplete(VKResponse response)
                    {
                        super.onComplete(response);

                        vkresponse[0] = (VKList) response.parsedModel;
                        user[0] = (VKApiUser) vkresponse[0].get(0);
                    }

                    @Override
                    public void onError(VKError error)
                    {
                        super.onError(error);

                        throw new NullPointerException("MYERROR");
                    }
                });
            }

            group_name = group[0].name;
            group_id = group[0].id;
            group_site+=group_id;


            mRealm.beginTransaction();

            if(mode==UPDATE_DOWNLOAD)
            {
                event = mRealm.createObject(Event.class);
                event.setWid(wid);
            }
            else
            {
                event = mRealm.where(Event.class).equalTo("wid",wid).findFirst();
                if(event==null)
                    return UPDATE_ERROR;
            }

            event.getPhoto().addAll(allPhoto);
            event.setAuthor_id(post_from_id);
            event.setBoss_id(post_owner_id);

            if(groupsss[0]!=null)
            {
                for(int i = 0; i<groupsss[0].length();i++)
                {
                    try
                    {
                        String name = (String) ((JSONObject) groupsss[0].get(i)).get("name");
                        Integer id = (Integer) ((JSONObject) groupsss[0].get(i)).get("id");
                        EventFriends eventFriends = new EventFriends();
                        eventFriends.setUrl("https://vk.com/public"+name);
                        eventFriends.setId(String.valueOf(id));
                        event.getFriends().add(eventFriends);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            try
            {
                String str = preFindHyper(post_text,1);
                str = preFindHyper(str,2);
                List<String> hyper = findHyper(str);

                event.setFriends(null);
                if(community)
                {
                    String innergeroup = "<a href=\""+group_site+"\">"+group_name+"</a>";

                    if(!hyper.contains(innergeroup))
                    {
                        hyper.add(innergeroup);
                        hyper.add(String.valueOf(group_id));
                    }
                }

                for(int i =1; i<hyper.size();i+=2)
                {
                    boolean s = false;
                    if(event.getFriends()!=null)
                    {
                        for(int j = 0; j< event.getFriends().size();j++)
                        {
                            if(event.getFriends().get(j).getId().equals(hyper.get(i+1)))
                                s = true;
                        }
                    }
                    if(!s)
                    {
                        EventFriends eventFriends = new EventFriends();
                        eventFriends.setUrl(hyper.get(i));
                        eventFriends.setId(hyper.get(i+1));
                        event.getFriends().add(eventFriends);
                    }
                }

                event.setPost_text(hyper.get(0));
            }
            catch (Exception e)
            {
                event.setPost_text(post_text);
                event.getFriends().clear();
            }

            event.setPost_date_create(UtilDate.DateunixTime(post_date));
            event.setGroup_name(group_name);
            event.setGroup_id(group_id);
            event.setEvent_link(linkstr);
            event.setGroup_site(group_site);
            if(mode==UPDATE_DOWNLOAD)
            {
                event.setEvent_state(event_state_new);
                event.setEvent_date_add(DateunixTime((System.currentTimeMillis()) / 1000));
                event.setEvent_date_repost(DateunixTime((System.currentTimeMillis() + 2*REFRESH_SERVICE_DAY) / 1000));
                event.setEvent_date_notif(DateunixTime((System.currentTimeMillis() + 1*REFRESH_SERVICE_DAY) / 1000));
                event.setEvent_date_end(DateunixTime((System.currentTimeMillis() + 3*REFRESH_SERVICE_DAY) / 1000));
            }
            Event.Complete(event);
            mRealm.commitTransaction();
            result_code = UPDATE_OK;
        }
        catch (io.realm.exceptions.RealmPrimaryKeyConstraintException e)
        {
            result_code=UPDATE_OBJECT_EXIST;
        }

        catch (NullPointerException e)
        {
            if(e.getMessage()=="MYERROR")
            {
                mRealm.cancelTransaction();
                mRealm.beginTransaction();
                event = mRealm.createObject(Event.class);
                if(mode==UPDATE_DOWNLOAD)
                event.setWid(wid);
                mRealm.commitTransaction();
            }
        }

        mRealm.close();
        return result_code;
    }

    public static final String preFindHyper(String mess,int type)
    {
        String result;
        int cycle =0;
        String startindex="https://vk.com/club";
        String endindex=" ";
        String preambula = "[club";
        int subint = 5;
        int num = -1;
        if(type==2)
        {
            startindex="https://vk.com/";
            subint = 0;
            num=0;
        }

        while(true)
        {
            if (cycle==20)
                return mess;
            cycle++;
            int start=0;
            int end = 0;


            start = mess.indexOf(startindex);
            end = mess.indexOf(endindex,start);

            if(start==-1)
            {
                return mess;
            }
            result = mess.substring(start, end+1);
            String fin = result;
            String club;
            String name="NAME";

            Pattern pattern  = Pattern.compile("(?:([^\\.]+))\\w");
            fin=fin.substring(startindex.length());
            Matcher matcher = pattern.matcher(fin);
            matcher.find();
            club = matcher.group();
            System.out.println(club);
            if(type==2)
            club = club.substring(subint, club.length());

            result=preambula+club+"|name]";

            StringBuilder str = new StringBuilder(mess);
            str.delete(start, end+1);
            str.insert(start, result);
            mess=str.toString();
        }

    }

    public static final List<String> findHyper(String mess)
    {
        int cycle=0;
        String result;
        final VKList[] vkresponse = new VKList[1];
        final VKApiCommunity[] group = {null};
        final boolean err[] = {false};
        List<String> returnvalue=new ArrayList<>();
        returnvalue.add(0,mess);

        while(true)
       {
           int start=0;
           int end = 0;
           int next = 0;
           int split = 0;

           cycle++;
           if(cycle>20)
               return returnvalue;

           start = mess.indexOf("[club");
           end = mess.indexOf("]");
           split = mess.indexOf("|",start);
           next = mess.indexOf("[club",start+5);

            if(start==-1 | end ==-1 | split == -1)
            {
                returnvalue.set(0,mess);
                return returnvalue;
            }

            if(end>start)
            {
                if(split<end & split>start)
                {
                    result = mess.substring(start, end+1);

                    String PUBLIC = "https://vk.com/public";
                    String fin = result;
                    String club;
                    String name="NAME";

                    try{
                        Pattern pattern  = Pattern.compile("\\[(?:([^\\.]+))\\d");
                        Matcher matcher = pattern.matcher(fin);
                        matcher.find();
                        club = matcher.group();
                    }
                    catch(Exception e)
                    {
                        Pattern pattern  = Pattern.compile("\\[(?:([^\\.]+))\\w");
                        Matcher matcher = pattern.matcher(fin);
                        matcher.find();
                        club = matcher.group();
                    }
                    club = club.substring(5, club.length());

                    if(club.contains("|nam"))
                    {
                        club=club.substring(0, club.indexOf("|nam"));
                    }

                    VKParameters parameters = new VKParameters();
                    parameters.put(VKApiConst.GROUP_ID, club);
                    VKRequest vkrz = VKApi.groups().getById(parameters);

                    vkrz.executeSyncWithListener(new VKRequest.VKRequestListener()
                    {
                        @Override
                        public void onComplete(VKResponse response)
                        {
                            super.onComplete(response);
                            err[0] = false;
                            vkresponse[0] = (VKList) response.parsedModel;
                            group[0] = (VKApiCommunity) vkresponse[0].get(0);
                        }

                        @Override
                        public void onError(VKError error)
                        {
                            super.onError(error);
                            err[0] = true;

                            throw new NullPointerException("MYERROR");
                        }
                    });
                    if(err[0] == false)
                    {
                        name = group[0].name;
                        club = String.valueOf(group[0].id);
                        result = "<a href=\"" + PUBLIC + club + "\">" + name + "</a>";
                        if (!returnvalue.contains(result)) {
                            returnvalue.add(result);
                            returnvalue.add(club);
                        }
                        StringBuilder str = new StringBuilder(mess);
                        str.delete(start, end + 1);
                        str.insert(start, result);
                        mess = str.toString();
                    }
                }
            }
        }
    }
}