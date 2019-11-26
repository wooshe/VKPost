package com.wooshe.VKContests.Net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.wooshe.VKContests.Realm.EventFriends;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.wooshe.VKContests.Net.Util.isNetisLogin;
import static com.wooshe.VKContests.no_use.Constants.ERR;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.OK;

public class Repost extends AsyncTask<Void, Void, Integer>
{

    String wid;
    int user_id;
    int repost_id;
    boolean pin;
    List<EventFriends>  eventFriendses;
    Context context;
    RepostListener repostListener;

    public interface RepostListener
    {
        void RepostSuccessfully(Integer code, String wid, Integer repost_id);
    }

    public void setRepostListener(RepostListener listener)
    {
        this.repostListener = listener;
    }

    public Repost(Context context, String wid, List<EventFriends> eventFriendses, boolean pin)
    {
        this.pin=pin;
        this.wid=wid;
        this.eventFriendses=eventFriendses;
        this.context=context;
    }

    @Override
    protected Integer doInBackground(Void... voids)
    {

        int retcode = isNetisLogin(context);
        if(retcode!=OK)
            return retcode;

        final boolean[] err={false};
        final int[] repost_id={0};

        VKParameters parameters = new VKParameters();
        parameters.put("object", wid);
        VKRequest vk = VKApi.wall().repost(parameters);
        vk.executeSyncWithListener(new VKRequest.VKRequestListener()
        {
            @Override
            public void onComplete(VKResponse response)
            {
                super.onComplete(response);
                try
                {
                    JSONObject json = response.json.getJSONObject("response");
                    repost_id[0] = json.getInt("post_id");
                    if(repost_id[0]==0)
                        err[0]=true;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    err[0]=true;
                }
            }



            @Override
            public void onError(VKError error)
            {
                super.onError(error);
                err[0]=true;
            }

        });

        if(err[0])
            return ERR;

        if(pin)
        {
            vk = new VKRequest("wall.pin",VKParameters.from(VKApiConst.POST_ID,repost_id[0]));

            vk.executeSyncWithListener(new VKRequest.VKRequestListener()
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

        for(int i = 0; i<eventFriendses.size();i++)
        {

            if(eventFriendses.get(i).isToEnter())
            {
                parameters = new VKParameters();
                parameters.put(VKApiConst.GROUP_ID, eventFriendses.get(i).getId());
                vk = VKApi.groups().join(parameters);
                vk.executeSyncWithListener(new VKRequest.VKRequestListener()
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

        this.repost_id=repost_id[0];
        return OK;
    }

    @Override
    protected void onPostExecute(Integer code)
    {
        repostListener.RepostSuccessfully(code,this.wid,this.repost_id);
    }
}