package com.wooshe.VKContests.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wooshe.R;
import com.wooshe.VKContests.Activity_Confirm_Delete;
import com.wooshe.VKContests.Activity_Confirm_Repost;
import com.wooshe.VKContests.Activity_Date_Time;
import com.wooshe.VKContests.Activity_Detail;
import com.wooshe.VKContests.MainActivity;
import com.wooshe.VKContests.Net.Repost;
import com.wooshe.VKContests.Net.Update;
import com.wooshe.VKContests.Net.Util;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Realm.EventPhoto;
import com.wooshe.VKContests.Realm.User;
import com.wooshe.VKContests.Util.OtherUtil;
import com.wooshe.VKContests.Util.SnackBar;
import com.wooshe.VKContests.Util.UtilDate;
import com.wooshe.VKContests.no_use.Constants;
import com.wooshe.VKContests.no_use.ViewPagerAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.wooshe.VKContests.Activity_Full_Photo.showImage;
import static com.wooshe.VKContests.Net.Util.DeleteCache;
import static com.wooshe.VKContests.Net.Util.DeletePostFromWall;
import static com.wooshe.VKContests.Net.Util.GroupLeave;
import static com.wooshe.VKContests.Net.Util.ResetAlarm;
import static com.wooshe.VKContests.Net.Util.SetAlarm;
import static com.wooshe.VKContests.Net.Util.checkPost;
import static com.wooshe.VKContests.Net.Util.isNetAccess;
import static com.wooshe.VKContests.Net.Util.restartEvent;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.UI.LoadAd.Show;
import static com.wooshe.VKContests.Util.UtilDate.DateunixTime;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_NOTIFY;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_END;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_NOTIF;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_REPOST;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_TYPE;
import static com.wooshe.VKContests.no_use.Constants.ERR;
import static com.wooshe.VKContests.no_use.Constants.LOGGED_ERROR;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.NET_ACCESS_ERROR;
import static com.wooshe.VKContests.no_use.Constants.OK;
import static com.wooshe.VKContests.no_use.Constants.REQUEST_CODE_CONFIRM;
import static com.wooshe.VKContests.no_use.Constants.REQUEST_CODE_DATE;
import static com.wooshe.VKContests.no_use.Constants.REQUEST_CODE_DELETE;
import static com.wooshe.VKContests.no_use.Constants.REQUEST_CODE_SOON;
import static com.wooshe.VKContests.no_use.Constants.UPDATE_UPDATE;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.event_state_end;
import static com.wooshe.VKContests.no_use.Constants.event_state_new;
import static com.wooshe.VKContests.no_use.Constants.event_state_repost;
import static com.wooshe.VKContests.no_use.Constants.event_state_soon;


public class Fragment_Detail extends Fragment implements
        View.OnClickListener,
        SwitchCompat.OnCheckedChangeListener,
        Repost.RepostListener,
        SwipeRefreshLayout.OnRefreshListener,
        Update.UpdateListener,
        Toolbar.OnMenuItemClickListener
{
    private Context context;
    private EditText tv_detail_name;
    private EditText tv_detail_description;
    private TextView tv_detail_group;
    private TextView tv_detail_site;
    private EditText tv_detail_text;
    private Button btn_detail_site;
    private Button btn_detail_group;
    private Button btn_detail_repost;
    private RelativeLayout rlv_layout_photo;
    private RelativeLayout llDataRepost;
    private RelativeLayout llDataNotif;
    private RelativeLayout llDataEnd;
    private RelativeLayout rlDataRepost;
    private RelativeLayout rlDataNotif;
    private RelativeLayout rlDataEnd;
    public TextView labelRepost;
    public TextView labelDelete;
    public TextView labelNotify;
    public TextView tvDataRepost;
    public TextView tvDataNotif;
    public TextView tvDataEnd;
    public TextView tvDataAdd;
    public TextView tvDataPost;
    private ImageView ivDataRepost;
    private ImageView ivDataNotif;
    private ImageView ivDataEnd;
    private SwitchCompat swDataRepost;
    private SwitchCompat swDataNotif;
    private SwitchCompat swDataEnd;
    private Event event;
    public String wid=null;
    public int id = -1;
    private int user_id;
    private List<EventPhoto> photo;
    private Realm mRealm;
    private Realm userRealm;
    private View viewRoot;
    private ViewPager viewPager;
    private ViewPager viewPagerPhoto;
    private Callbacks mCallbacks;
    private TabLayout photo_tab;
    private Fragment_Photo fragment_photo;
    private SwipeRefreshLayout fragment_detail_swipe;
    private View fragment_detail_indicator;
    private FRD frdListener;
    private TextView dl_info;
    private ImageButton fragment_detail_prev_photo;
    private ImageButton fragment_detail_next_photo;
    private Activity activity;
    private ImageView detphoto;
    private Button btn_detail_link;
    public  Toolbar toolbar;
    public RelativeLayout relativeMain;
    public RelativeLayout relativeprogress;
    public RelativeLayout relativePart1;
    public RelativeLayout relativePart2;
    public RelativeLayout relativePart3;
    public TextView t1;
    public TextView t2;
    public TextView t3;
    public ProgressBar pb;
    NestedScrollView scw;
    ViewPagerAdapter adapters;
    File PhotoF = null;
    NativeAdViewAppWall nativeAdViewAppWall;
    NativeAdViewContentStream nativeAdViewContentStream;
    private CardView cv;
    private CardView cv2;
    float dpWidth;
    int wPart1;

    public void RemoveFromRealFromViewPager()
    {
        int error = Util.isNetisLogin(context);
        if (event.getEvent_state() == event_state_repost)
        {
            if(error==OK)
            {
                boolean ret = DeletePost();
                if(ret==true)
                    return;
            }
            else
            {
                SnackBar.SnackBarShow(context, viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                return;
            }
        }

        if(error==OK)
        {
            GroupLeave(mRealm,event.getFriends());
        }

        ResetAlarm(context,wid,ALARM_TYPE_REPOST);
        ResetAlarm(context,wid,ALARM_TYPE_NOTIFY);
        ResetAlarm(context,wid,ALARM_TYPE_END);
        mRealm.beginTransaction();
        DeleteCache(event);
        event.removeFromRealm();
        mRealm.commitTransaction();
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        int count = adapter.getCount();
        count--;

        if(count==0)
        {
            activity.onBackPressed();
        }
        else
        {
            this.setDownloadListener((FRD) context);
            frdListener.frd(viewPager.getCurrentItem());
        }
    }

    public boolean DeletePost()
    {

        boolean error = DeletePostFromWall(user_id,event.getRepost_id());

        if(error==false)
        {
            mRealm.beginTransaction();
            for(int i = 0; i<event.getFriends().size();i++)
            {
                event.getFriends().get(i).setToEnter(false);
            }
            event.setRepost_id(-1);
            event.setEvent_state(event_state_end);
            mRealm.commitTransaction();
        }
        btn_detail_repost.setEnabled(true);
        return error;
    }

    public void showImages()
    {
        ArrayList<String> photostr = new ArrayList<>();
        for(int i = 0; i<photo.size();i++)
            photostr.add(photo.get(i).getPath_photo_604());

        if(photostr.size()==0)
            return;
        showImage(context,photostr,viewPagerPhoto.getCurrentItem());
    }

    @Override
    public void onClick(View view)
    {
        Intent intent;
        int error = Util.isNetisLogin(context);

        switch (view.getId())
        {
            case R.id.toolbarImage:
                ArrayList<String> photostr = new ArrayList<>();
                for(int i = 0; i<photo.size();i++)
                    photostr.add(photo.get(i).getPath_photo_604());

                if(photostr.size()==0)
                    return;
                showImage(context,photostr,0);
                break;

            case R.id.btn_detail_repost:

                if(event.getEvent_state()==event_state_end)
                {
                    restartEvent(context,mRealm,event);
                    BindView();
                    break;
                }

                if(error==OK)
                {
                    btn_detail_repost.setEnabled(false);
                    if (event.getEvent_state() == event_state_repost)
                    {
                        DeletePost();
                        BindView();
                    }
                    else
                    {
                        intent = new Intent(context, Activity_Confirm_Repost.class);
                        intent.putExtra(WID, wid);
                        startActivityForResult(intent, REQUEST_CODE_CONFIRM);
                        getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                    }
                }
                else
                {
                    SnackBar.SnackBarShow(context, viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                }
                break;

            case R.id.rlDataNotif:
                intent = new Intent(context, Activity_Date_Time.class);
                intent.putExtra(DATE_TIME_TYPE,DATE_TIME_NOTIF);
                intent.putExtra(WID, event.getWid());
                startActivityForResult(intent,REQUEST_CODE_DATE);
                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;

            case R.id.rlDataRepost:
                intent = new Intent(context, Activity_Date_Time.class);
                intent.putExtra(DATE_TIME_TYPE,DATE_TIME_REPOST);
                intent.putExtra(WID, event.getWid());
                startActivityForResult(intent,REQUEST_CODE_DATE);
                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;

            case R.id.rlDataEnd:
                intent = new Intent(context, Activity_Date_Time.class);
                intent.putExtra(DATE_TIME_TYPE,DATE_TIME_END);
                intent.putExtra(WID, event.getWid());
                startActivityForResult(intent,REQUEST_CODE_DATE);
                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;

            case R.id.btn_detail_site:
                if(error!=OK)
                {
                    SnackBar.SnackBarShow(context,viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                    break;
                }


                try
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getWid()));
                    intent.setPackage("com.vkontakte.android");
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getWid()));
                    startActivity(intent);
                }

                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;

            case R.id.btn_detail_group:
                if(error!=OK)
                {
                    SnackBar.SnackBarShow(context,viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                    break;
                }


                try
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getGroup_site()));
                    intent.setPackage("com.vkontakte.android");
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getGroup_site()));
                    startActivity(intent);
                }

                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;

                case R.id.btn_detail_link:
                if(error!=OK)
                {
                    SnackBar.SnackBarShow(context,viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                    break;
                }


                    try
                    {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getEvent_link()));
                        intent.setPackage("com.vkontakte.android");
                        startActivity(intent);
                    }
                    catch (android.content.ActivityNotFoundException anfe)
                    {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getEvent_link()));
                        startActivity(intent);
                    }

                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;

            case R.id.fragment_detail_prev_photo:
                viewPagerPhoto.setCurrentItem(viewPagerPhoto.getCurrentItem()-1,true);
                break;

            case R.id.fragment_detail_next_photo:
                viewPagerPhoto.setCurrentItem(viewPagerPhoto.getCurrentItem()+1,true);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_CODE_CONFIRM:
                if(resultCode==RESULT_OK)
                {
                    Repost repost = new Repost(context, event.getWid(), mRealm.copyFromRealm(event.getFriends()),event.isPin());
                    repost.setRepostListener(this);
                    repost.execute();
                }
                else if (resultCode==RESULT_CANCELED)
                {
                    btn_detail_repost.setEnabled(true);
                }
                break;

            case REQUEST_CODE_SOON:
                if(resultCode==RESULT_OK)
                {
                    onClick(rlDataRepost);
                }
                else
                {
                    BindView();
                }
                break;

            case REQUEST_CODE_DELETE:
                if(resultCode==RESULT_OK)
                {
                    RemoveFromRealFromViewPager();
                }
                break;

            case REQUEST_CODE_DATE:

                int date_time_type = data.getIntExtra(DATE_TIME_TYPE,0);

                if(resultCode==RESULT_OK)
                {
                    switch (date_time_type)
                    {
                        case DATE_TIME_NOTIF:
                            mRealm.beginTransaction();
                            event.setSwitch_notif(true);
                            mRealm.commitTransaction();
                            SetAlarm(context,wid,ALARM_TYPE_NOTIFY,event.getEvent_date_notif());
                            SetView(true,swDataNotif,tvDataNotif,rlDataNotif,ivDataNotif);
                            Toast.makeText(activity,"Напоминание запланировано на: " + DateunixTime(event.getEvent_date_notif()),Toast.LENGTH_SHORT).show();
                            break;

                        case DATE_TIME_REPOST:
                            mRealm.beginTransaction();
                            event.setSwitch_repost(true);
                            event.setEvent_state(event_state_soon);
                            mRealm.commitTransaction();
                            SetAlarm(context,wid,ALARM_TYPE_REPOST,event.getEvent_date_repost());
                            Toast.makeText(activity,"Репост запланирован на: " + DateunixTime(event.getEvent_date_repost()),Toast.LENGTH_SHORT).show();
                            break;

                        case DATE_TIME_END:
                            mRealm.beginTransaction();
                            event.setSwitch_end(true);
                            mRealm.commitTransaction();
                            SetAlarm(context,wid,ALARM_TYPE_END,event.getEvent_date_end());
                            BindView();
                            Toast.makeText(activity,"Удаление поста запланировано на: " + DateunixTime(event.getEvent_date_end()),Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                else
                {
                    switch (date_time_type)
                    {
                        case DATE_TIME_NOTIF:
                            if(!event.isSwitch_notif())
                            {
                                SetView(false,swDataNotif,tvDataNotif,rlDataNotif,ivDataNotif);
                            }
                            break;

                        case DATE_TIME_REPOST:
                            if(!event.isSwitch_repost())
                            {
                                SetView(false,swDataRepost,tvDataRepost,rlDataRepost,ivDataRepost);
                            }
                            break;

                        case DATE_TIME_END:
                            if(!event.isSwitch_end())
                            {
                                SetView(false,swDataEnd,tvDataEnd,rlDataEnd,ivDataEnd);
                            }
                            break;
                    }
                }
                break;

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean state)
    {
        switch (compoundButton.getId())
        {
            case R.id.swDataRepost:
                    if(state)
                    {
                        Intent intent = new Intent(context, Activity_Confirm_Repost.class);
                        intent.putExtra(WID, wid);
                        startActivityForResult(intent, REQUEST_CODE_SOON);
                        getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                    }
                    else
                    {
                        mRealm.beginTransaction();
                        event.setSwitch_repost(false);

                        event.setEvent_state(event_state_new);
                        mRealm.commitTransaction();
                        ResetAlarm(context,wid,ALARM_TYPE_REPOST);
                        BindView();
                    }
                break;
            case R.id.swDataEnd:
                if(state)
                {
                    onClick(rlDataEnd);
                }
                else
                {
                    mRealm.beginTransaction();
                    event.setSwitch_end(false);

                    mRealm.commitTransaction();
                    ResetAlarm(context,wid,ALARM_TYPE_END);
                    BindView();
                }
                break;

            case R.id.swDataNotif:
                if(state)
                {
                    onClick(rlDataNotif);
                }
                else
                {
                    mRealm.beginTransaction();
                    event.setSwitch_notif(false);
                    mRealm.commitTransaction();
                    ResetAlarm(context,wid,ALARM_TYPE_NOTIFY);
                    BindView();
                }
                break;
        }
    }

    @Override
    public void RepostSuccessfully(Integer code, String wid, Integer repost_id)
    {
        switch (code)
        {
            case OK:
                mRealm.beginTransaction();
                event.setEvent_state(event_state_repost);
                event.setSwitch_repost(false);
                event.setRepost_id(repost_id);
                ResetAlarm(context,wid,ALARM_TYPE_REPOST);
                BindView();
                mRealm.commitTransaction();

                Toast.makeText(context,"Успешно!",Toast.LENGTH_SHORT).show();
                break;

            case ERR:
                Toast.makeText(context,"Произошла ошибка!",Toast.LENGTH_SHORT).show();
                break;

            case NET_ACCESS_ERROR:
                Toast.makeText(context,"Проверьте соединение с интернетом!",Toast.LENGTH_SHORT).show();
                break;

            case LOGGED_ERROR:
                Toast.makeText(context,"Войдите в учётную запись",Toast.LENGTH_SHORT).show();
                break;
        }
        btn_detail_repost.setEnabled(true);
    }

    @Override
    public void onRefresh()
    {
        if(isNetAccess(context))
        {
            dl_info.setVisibility(View.VISIBLE);
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();
            Update upd = new Update(context, UPDATE_UPDATE, realmConfiguration, wid);
            upd.setUpdateListener(this);
            upd.execute();
        }
        else
        {
            if(fragment_detail_swipe!=null)
                fragment_detail_swipe.setRefreshing(false);
            SnackBar.SnackBarShow(viewRoot.findViewById(R.id.mcll), "Проверьте соединение с интернетом", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void UpdateSuccessfully(Integer code,String path)
    {
        if(mCallbacks==null)
            return;

        if(fragment_detail_swipe!=null)
            fragment_detail_swipe.setRefreshing(false);

        if(event.getEvent_state()==event_state_repost)
        {
            if(checkPost(user_id,event.getRepost_id())==false)
            {
                restartEvent(context,mRealm,event);
            }
        }
        preBind();
        BindView();
        dl_info.setVisibility(View.GONE);
    }

    public void viewPagerPhotoSettings()
    {
        try
        {
            adapters = new ViewPagerAdapter(viewPagerPhoto, activity,getChildFragmentManager());
            for(int i=0;i<photo.size();i++)
            {
                String text_photo = photo.get(i).getText();
                String path_photo_604 = photo.get(i).getPath_photo_604();
                if(path_photo_604==null)
                    continue;
                fragment_photo = new Fragment_Photo().newInstance(text_photo,path_photo_604);
                adapters.addFragment(fragment_photo," ");
            }
            viewPagerPhoto.setAdapter(adapters);

            photo_tab.setupWithViewPager(viewPagerPhoto);
            if(adapters.getCount()==0)
                rlv_layout_photo.setVisibility(View.GONE);
            else
                rlv_layout_photo.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void InitView()
    {
        if(activity==null)
            return;

         cv = (CardView) viewRoot.findViewById(R.id.natcv);
        cv2 = (CardView) viewRoot.findViewById(R.id.natcv2);

        detphoto = (ImageView) viewRoot.findViewById(R.id.toolbarImage);
        detphoto.setOnClickListener(this);
        tv_detail_name = (EditText) viewRoot.findViewById(R.id.tv_detail_name);
        tv_detail_description = (EditText) viewRoot.findViewById(R.id.tv_detail_description);
        tv_detail_group = (TextView) viewRoot.findViewById(R.id.tv_detail_group);
        tv_detail_text = (EditText) viewRoot.findViewById(R.id.tv_detail_text);
        tv_detail_site = (TextView) viewRoot.findViewById(R.id.tv_detail_site);

        pb = (ProgressBar) viewRoot.findViewById(R.id.progressBars);
        relativeMain= (RelativeLayout) viewRoot.findViewById(R.id.relativeMain);
        relativeprogress= (RelativeLayout) viewRoot.findViewById(R.id.relativeprogress);
        relativePart1= (RelativeLayout) viewRoot.findViewById(R.id.relativePart1);
        relativePart2= (RelativeLayout) viewRoot.findViewById(R.id.relativePart2);
        relativePart3= (RelativeLayout) viewRoot.findViewById(R.id.relativePart3);
        t1 = (TextView) viewRoot.findViewById(R.id.t1);
        t2 = (TextView) viewRoot.findViewById(R.id.t2);
        t3 = (TextView) viewRoot.findViewById(R.id.t3);

        tvDataAdd= (TextView) viewRoot.findViewById(R.id.tvDataAdd);
        tvDataPost= (TextView) viewRoot.findViewById(R.id.tvDataPost);

        btn_detail_link = (Button) viewRoot.findViewById(R.id.btn_detail_link);
        btn_detail_link.setOnClickListener(this);

        btn_detail_site = (Button) viewRoot.findViewById(R.id.btn_detail_site);
        btn_detail_site.setOnClickListener(this);

        btn_detail_group = (Button) viewRoot.findViewById(R.id.btn_detail_group);
        btn_detail_group.setOnClickListener(this);

        btn_detail_repost = (Button) viewRoot.findViewById(R.id.btn_detail_repost);
        btn_detail_repost.setOnClickListener(this);

        rlDataRepost = (RelativeLayout) viewRoot.findViewById(R.id.rlDataRepost);
        rlDataNotif = (RelativeLayout) viewRoot.findViewById(R.id.rlDataNotif);
        rlDataEnd = (RelativeLayout) viewRoot.findViewById(R.id.rlDataEnd);

        llDataRepost = (RelativeLayout) viewRoot.findViewById(R.id.llDataRepost);
        llDataNotif = (RelativeLayout) viewRoot.findViewById(R.id.llDataNotif);
        llDataEnd = (RelativeLayout) viewRoot.findViewById(R.id.llDataEnd);

        tvDataRepost= (TextView) viewRoot.findViewById(R.id.tvDataRepost);
        tvDataNotif= (TextView) viewRoot.findViewById(R.id.tvDataNotif);
        tvDataEnd= (TextView) viewRoot.findViewById(R.id.tvDataEnd);

        labelRepost= (TextView) viewRoot.findViewById(R.id.labelRepost);
        labelDelete= (TextView) viewRoot.findViewById(R.id.labelDelete);
        labelNotify= (TextView) viewRoot.findViewById(R.id.labelNotify);

        ivDataRepost= (ImageView) viewRoot.findViewById(R.id.ivDataRepost);
        ivDataNotif= (ImageView) viewRoot.findViewById(R.id.ivDataNotif);
        ivDataEnd= (ImageView) viewRoot.findViewById(R.id.ivDataEnd);

        swDataRepost = (SwitchCompat) viewRoot.findViewById(R.id.swDataRepost);

        swDataNotif = (SwitchCompat) viewRoot.findViewById(R.id.swDataNotif);

        swDataEnd = (SwitchCompat) viewRoot.findViewById(R.id.swDataEnd);

        swDataNotif.setOnCheckedChangeListener(null);
        swDataRepost.setOnCheckedChangeListener(null);
        swDataEnd.setOnCheckedChangeListener(null);

        //swDataNotif.setOnClickListener(this);
        //swDataRepost.setOnClickListener(this);
        //swDataEnd.setOnClickListener(this);

        viewPager = (ViewPager) activity.findViewById(R.id.viewpager_detail);
        viewPagerPhoto = (ViewPager) viewRoot.findViewById(R.id.viewpager_photo);;
        photo_tab = (TabLayout) viewRoot.findViewById(R.id.photo_tab);
        fragment_detail_indicator = (View) viewRoot.findViewById(R.id.fragment_detail_indicator);
        rlv_layout_photo = (RelativeLayout) viewRoot.findViewById(R.id.rlv_layout_photo);

        dl_info = (TextView) viewRoot.findViewById(R.id.dl_info);

        fragment_detail_prev_photo = (ImageButton) viewRoot.findViewById(R.id.fragment_detail_prev_photo);
        fragment_detail_prev_photo.setOnClickListener(this);

        fragment_detail_next_photo = (ImageButton) viewRoot.findViewById(R.id.fragment_detail_next_photo);
        fragment_detail_next_photo.setOnClickListener(this);

        fragment_detail_swipe = (SwipeRefreshLayout) viewRoot.findViewById(R.id.fragment_detail_swipe);
        fragment_detail_swipe.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe,R.color.orange_swipe, R.color.red_swipe);
        fragment_detail_swipe.setOnRefreshListener(this);

        scw = (NestedScrollView) viewRoot.findViewById(R.id.nstr);

        wid = getArguments().getString(WID,null);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(activity).build();
        mRealm=OpenTable(realmConfiguration);

        RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(activity)
                .name("User")
                .build();
        userRealm = OpenTable(nrealmConfiguration);

        RealmResults<User> user = userRealm.where(User.class).findAll();
        if(user.isEmpty())
        {
            user_id=-1;
        }
        else
        {
            user_id=user.get(0).getUser_id();
        }

        event = mRealm.where(Event.class).equalTo("wid",wid).findFirst();
        preBind();
    }

    public void preBind()
    {
        try
        {
            photo = mRealm.copyFromRealm(event.getPhoto());
            PhotoF = new File(event.getPhoto().get(0).getPath_photo_604());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void BindView()
    {

        if(wid!=null) {
            Picasso.with(context).load(PhotoF).into(detphoto);
            tv_detail_name.setText(event.getEvent_name());
            tv_detail_description.setText(event.getEvent_desc());
            tv_detail_group.setText(event.getGroup_name());

            tv_detail_text.setText(Html.fromHtml(event.getPost_text().replace("\n", "<br>"), null, null));
            tv_detail_text.setMovementMethod(LinkMovementMethod.getInstance());
            tv_detail_site.setText(event.getWid());

            tvDataRepost.setText(DateunixTime(event.getEvent_date_repost()));
            tvDataNotif.setText(DateunixTime(event.getEvent_date_notif()));
            tvDataEnd.setText(DateunixTime(event.getEvent_date_end()));

            tvDataAdd.setText(DateunixTime(event.getEvent_date_add()));
            tvDataPost.setText(DateunixTime(event.getPost_date_create()));

            int event_state = event.getEvent_state();

            switch (event_state) {
                case event_state_end:

                    llDataRepost.setVisibility(View.GONE);
                    llDataNotif.setVisibility(View.GONE);
                    llDataEnd.setVisibility(View.GONE);
                    labelRepost.setVisibility(View.GONE);
                    labelNotify.setVisibility(View.GONE);
                    labelDelete.setVisibility(View.GONE);
                    btn_detail_repost.setText("Событие завершено. Подать заново");
                    fragment_detail_indicator.setBackgroundResource(R.drawable.gradient_red);
                    break;

                case event_state_new:
                    llDataRepost.setVisibility(View.VISIBLE);
                    llDataNotif.setVisibility(View.VISIBLE);
                    llDataEnd.setVisibility(View.VISIBLE);
                    labelRepost.setVisibility(View.VISIBLE);
                    labelNotify.setVisibility(View.VISIBLE);
                    labelDelete.setVisibility(View.VISIBLE);
                    btn_detail_repost.setText("Репостнуть сейчас");
                    fragment_detail_indicator.setBackgroundResource(R.drawable.gradient_green);
                    break;

                case event_state_repost:
                    if (event.isSwitch_end())
                        btn_detail_repost.setText("Удаление запланировано. Удалить пост сейчас");
                    else
                        btn_detail_repost.setText("Удалить пост сейчас");

                    llDataRepost.setVisibility(View.GONE);
                    labelRepost.setVisibility(View.GONE);

                    fragment_detail_indicator.setBackgroundResource(R.drawable.gradient_blue);
                    break;

                case event_state_soon:
                    btn_detail_repost.setText("Репост запланирован. Репостнуть сейчас");
                    fragment_detail_indicator.setBackgroundResource(R.drawable.gradient_yellow);
                    break;
            }


            SetView(event.isSwitch_repost(), swDataRepost, tvDataRepost, rlDataRepost, ivDataRepost);
            SetView(event.isSwitch_notif(), swDataNotif, tvDataNotif, rlDataNotif, ivDataNotif);
            SetView(event.isSwitch_end(), swDataEnd, tvDataEnd, rlDataEnd, ivDataEnd);


            swDataNotif.setOnCheckedChangeListener(this);
            swDataRepost.setOnCheckedChangeListener(this);
            swDataEnd.setOnCheckedChangeListener(this);

            viewPagerPhotoSettings();

            int pos = 0;
            pos = ((ViewPagerAdapter) viewPager.getAdapter()).GetPos(this);
            toolbar.setTitle(String.valueOf(pos) + " из " + viewPager.getAdapter().getCount());

            if (event.getEvent_state() == event_state_repost && event.getRepost_id() != -1)
                toolbar.getMenu().getItem(0).setVisible(true);
            else
                toolbar.getMenu().getItem(0).setVisible(false);

            if (event.getEvent_link() != null && event.getEvent_link().length() > 1) {
                btn_detail_link.setVisibility(View.VISIBLE);
                toolbar.getMenu().getItem(5).setVisible(true);

            } else {
                btn_detail_link.setVisibility(View.GONE);
                toolbar.getMenu().getItem(5).setVisible(false);
            }


            RelativeLayout.LayoutParams lpProgress = (RelativeLayout.LayoutParams) relativeprogress.getLayoutParams();
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                pb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#018e89"), PorterDuff.Mode.SRC_IN);
            }

            dpWidth  = outMetrics.widthPixels;

            List<Long> l = new ArrayList<>();
            relativePart1.setVisibility(View.GONE);
            relativePart2.setVisibility(View.GONE);
            relativePart3.setVisibility(View.GONE);

            Long rep = event.getEvent_date_repost().getTime();
            Long en = event.getEvent_date_end().getTime();
            Long not = event.getEvent_date_notif().getTime();

            if(event.isSwitch_repost())
            {
                l.add(rep);
            }

            if(event.isSwitch_notif())
            {
                l.add(not);
            }

            if(event.isSwitch_end())
            {
                l.add(en);
            }

            switch (l.size())
            {
                case 1:
                    relativePart1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    relativePart1.setVisibility(View.VISIBLE);
                    relativePart2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    relativePart1.setVisibility(View.VISIBLE);
                    relativePart2.setVisibility(View.VISIBLE);
                    relativePart3.setVisibility(View.VISIBLE);
                    break;
            }

            Collections.sort(l);
            if(l.size()==0)
            {
                relativeMain.setVisibility(View.GONE);
            }
            else
            {
                relativeMain.setVisibility(View.VISIBLE);
                wPart1 = (int) (dpWidth/l.size());
                lpProgress.width=0;

                for (int i = 0; i<l.size();i++)
                {
                    if(i==0)
                    {
                        lpProgress.width += getProgress(event.getPost_date_create().getTime(), l.get(i));
                    }
                    else
                    {
                        lpProgress.width += getProgress(l.get(i - 1), l.get(i));
                    }
                    TextView temp;

                    String str = "";

                    if(l.get(i)==rep)
                    {
                        str="Репост\n"+ UtilDate.String_unixTimeWithoutTime(rep);
                    }
                    else if(l.get(i)==en)
                    {
                        str="Удалить\n"+ UtilDate.String_unixTimeWithoutTime(en);
                    }
                    else if(l.get(i)==not)
                    {
                        str="Напомнить\n"+ UtilDate.String_unixTimeWithoutTime(not);
                    }

                    switch (i)
                    {
                        case 0:
                            t1.setText(str);
                            break;

                        case 1:
                            t2.setText(str);
                            break;

                        case 2:
                            t3.setText(str);
                            break;
                    }
                }
                relativeprogress.setLayoutParams(lpProgress);
            }

        }
    }

    private int getProgress(Long ad, Long endF)
    {
        int progress = 0;

        Long add = ad;
        Long c = System.currentTimeMillis();

        int st = (int)(add/86400000);
        int end = (int)(endF/86400000);
        int cur = (int)(c/86400000);
        int procent = 0;
        int diff = end - st;
        int gg = cur - st;
        if(gg==0)
            gg=1;
        if(diff>=1)
        {
            procent = 100*gg/diff;
            double d = (double) procent/100.0*(double) wPart1;
            progress = (int) d;
        }
        else
        {
            progress = (int) dpWidth;
        }


        if(progress>dpWidth)
            progress = (int) dpWidth;

        return progress;
    }

    private void SetView(boolean state,SwitchCompat sw, TextView tv , RelativeLayout rl, ImageView iv)
    {
        sw.setOnCheckedChangeListener(null);
        if(state)
        {
            String str = "";
            switch (tv.getId())
            {
                case R.id.tvDataRepost:
                    str = DateunixTime(event.getEvent_date_repost());
                    break;
                case R.id.tvDataNotif:
                    str = DateunixTime(event.getEvent_date_notif());
                    break;
                case R.id.tvDataEnd:
                    str = DateunixTime(event.getEvent_date_end());
                    break;
            }
            tv.setText(str);
            tv.setTextColor(getResources().getColor(R.color.tv_enabled));
            rl.setOnClickListener(this);
            iv.setColorFilter(R.color.color_text);
        }
        else
        {
            tv.setText("");
            tv.setTextColor(getResources().getColor(R.color.tv_disable));
            iv.setColorFilter(R.color.colorPrimary);
            rl.setOnClickListener(null);
        }
        sw.setChecked(state);
        sw.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {

        Intent intent;
        int error = Util.isNetisLogin(context);
        switch (item.getItemId())
        {
            case R.id.menu_detail_friend:
                OtherUtil.Friend(activity, event.getWid());
                return true;

            case R.id.menu_detail_delete:
                intent = new Intent(context, Activity_Confirm_Delete.class);
                intent.putExtra(WID, wid);
                startActivityForResult(intent, REQUEST_CODE_DELETE);
                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                return true;

            case R.id.menu_detail_repost:
                if(error!=OK)
                {
                    SnackBar.SnackBarShow(context,viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                    break;
                }

                if(event.getRepost_id()==-1 || event.getEvent_state()!=event_state_repost)
                    break;

                String str = "https://vk.com/wall";
                str = str+user_id+"_"+event.getRepost_id();



                try
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                    intent.setPackage("com.vkontakte.android");
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
                    startActivity(intent);
                }

                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                return true;

            case R.id.menu_detail_link:
                if(error!=OK)
                {
                    SnackBar.SnackBarShow(context,viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                    break;
                }


                try
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getEvent_link()));
                    intent.setPackage("com.vkontakte.android");
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getEvent_link()));
                    startActivity(intent);
                }


                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;

            case R.id.menu_detail_post:
                if(error!=OK)
                {
                    SnackBar.SnackBarShow(context,viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                    break;
                }



                try
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getWid()));
                    intent.setPackage("com.vkontakte.android");
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getWid()));
                    startActivity(intent);
                }

                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                return true;

            case R.id.menu_detail_group:
                if(error!=OK)
                {
                    SnackBar.SnackBarShow(context,viewRoot.findViewById(R.id.mcll),error, Snackbar.LENGTH_SHORT);
                    break;
                }


                try
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getGroup_site()));
                    intent.setPackage("com.vkontakte.android");
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException anfe)
                {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getGroup_site()));
                    startActivity(intent);
                }

                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                return true;
        }
        return false;
    }

    public interface FRD
    {
        void frd(int pos);
    }

    public void setDownloadListener(FRD listener)
    {
        this.frdListener = listener;
    }

    public interface Callbacks
    {
        void FragmentCallback();
    }

    @Override
    public void onAttach(Context activity)
    {
        super.onAttach(activity);

        try
        {
            mCallbacks = (Callbacks) activity;
            mCallbacks.FragmentCallback();
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.mCallbacks = null;
    }

    public static Fragment_Detail newInstance(String wid)
    {
        Bundle args = new Bundle();
        args.putString(WID,wid);
        Fragment_Detail fragment = new Fragment_Detail();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        id= Constants.DETAIL++;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        long timeout= System.currentTimeMillis();
        View v = inflater.inflate(R.layout.fragment_detail, container,false);
        if(container!=null)
            context=container.getContext();
        else
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        viewRoot=v;

        setHasOptionsMenu(true);
        toolbar = (Toolbar) viewRoot.findViewById(R.id.activity_detail_toolbar);
        toolbar.inflateMenu(R.menu.menu_detail);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        InitView();

        nativeAdViewAppWall = (NativeAdViewAppWall) viewRoot.findViewById(R.id.native_detail);
        nativeAdViewContentStream = (NativeAdViewContentStream) viewRoot.findViewById(R.id.native_detail2);

        return v;
    }

    public  void SH(List<NativeAd> ads)
    {
        if(ads.size()==1)
        {
            List<NativeAd> a = new ArrayList<>();
            a.add(ads.get(0));
            Show(context,nativeAdViewAppWall,cv, a);
        }
        else if(ads.size()>1)
        {
            List<NativeAd> a = new ArrayList<>();
            a.add(ads.get(0));
            Show(context,nativeAdViewAppWall,cv, a);

            List<NativeAd> b = new ArrayList<>();
            b.add(ads.get(1));
            Show(context,nativeAdViewContentStream,cv2,b);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        BindView();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try {
            if(event.isValid())
            {
                mRealm.beginTransaction();
                event.setEvent_name(tv_detail_name.getText().toString());
                event.setEvent_desc(tv_detail_description.getText().toString());
                mRealm.commitTransaction();
            }
            mRealm.close();
            userRealm.close();
        }
        catch (Exception e){}
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }
}
