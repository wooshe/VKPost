package com.wooshe.VKContests.UI;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.wooshe.R;
import com.wooshe.VKContests.MainActivity;
import com.wooshe.VKContests.Util.OtherUtil;

import java.util.List;
import java.util.Random;

import static com.wooshe.VKContests.UI.LoadAd.Show;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.SETTINGS;

public class Fragment_About extends Fragment implements View.OnClickListener,LoadAd.LoadADListener

{
    private Context context;
    private View viewRoot;
    public int id = -2;
    private Toolbar toolbar_about;
    private TextView txt_abaout_version;
    private Button btn_send_to_developer;
    private Button btn_app_group;
    private Button btn_rate;
    private Button btn_friend;
    private NavigationView navigationView;
    private Activity activity;
    NativeAdViewContentStream nativeAdViewContentStream;
    NativeAd old = null;
    private CardView cv;

    private void Init() {
        toolbar_about = (Toolbar) viewRoot.findViewById(R.id.toolbar_about);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar_about);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        activity.setTitle("О программе");

        btn_send_to_developer = (Button) viewRoot.findViewById(R.id.btn_send_to_developer);
        btn_send_to_developer.setOnClickListener(this);
        btn_app_group = (Button) viewRoot.findViewById(R.id.btn_app_group);
        btn_app_group.setOnClickListener(this);
        btn_rate = (Button) viewRoot.findViewById(R.id.btn_rate);
        btn_rate.setOnClickListener(this);
        btn_friend = (Button) viewRoot.findViewById(R.id.btn_friend);
        btn_friend.setOnClickListener(this);

        cv = (CardView) viewRoot.findViewById(R.id.natcv);

        txt_abaout_version = (TextView) viewRoot.findViewById(R.id.txt_abaout_version);

        try {
            txt_abaout_version.setText(activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        context = container.getContext();

        viewRoot = v;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init();
        nativeAdViewContentStream = (NativeAdViewContentStream) activity.findViewById(R.id.native_about);
        ((MainActivity)getActivity()).ad.setLoadADListener(this);
        Show(context,nativeAdViewContentStream, cv,((MainActivity)getActivity()).nativeAds);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_about).setChecked(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.getMenu().findItem(R.id.nav_about).setChecked(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        id = SETTINGS++;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ((MainActivity)getActivity()).ad.resetLoadADListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {

            case R.id.btn_friend:
                OtherUtil.Friend(activity,"https://play.google.com/store/apps/details?id=" + activity.getPackageName());
                break;

            case R.id.btn_send_to_developer:
                OtherUtil.SendToDevelop(activity);
                break;

            case R.id.btn_rate:
                    OtherUtil.Rate(activity);
                break;

            case R.id.btn_app_group:
                OtherUtil.VKGroup(activity);
                break;
        }
    }

    @Override
    public void LoadAd(boolean load, List<NativeAd> nativeAds)
    {
        if(old!=null)
        {
            if(old.containsVideo())
            {
                if(load)
                    old =  Show(context,nativeAdViewContentStream,cv,nativeAds);
            }
            else
                old =  Show(context,nativeAdViewContentStream,cv,nativeAds);
        }
        else
       old =  Show(context,nativeAdViewContentStream,cv,nativeAds);
    }
}
