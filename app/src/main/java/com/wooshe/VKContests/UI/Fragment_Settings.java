package com.wooshe.VKContests.UI;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.wooshe.R;
import com.wooshe.VKContests.GuideActivity;
import com.wooshe.VKContests.MainActivity;
import com.wooshe.VKContests.no_use.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.wooshe.VKContests.Activity_Full_Photo.showDrawable;
import static com.wooshe.VKContests.UI.LoadAd.Show;
import static com.wooshe.VKContests.no_use.Constants.CARD_COLOR_RANDOM;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.SETTINGS;
import static com.wooshe.VKContests.no_use.Constants.SETTINGS_HOOK;
import static com.wooshe.VKContests.no_use.Constants.SETTINGS_TRAFFIC;

public class Fragment_Settings extends Fragment implements
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,LoadAd.LoadADListener
{
    private Context context;
    private View viewRoot;
    private SwitchCompat sw_settings_traffic;
    private SwitchCompat sw_settings_hook;
    private SwitchCompat sw_settings_card_color_random;
    private SharedPreferences APP_PREFERENCES;
    private Activity activity;
    public int id = -2;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Button xiaomi;
    private Button appset;
    private Button help;
    private ImageView iv1;
    private ImageView iv2;
    private CardView cv;
    NativeAdViewAppWall nativeAdViewAppWall;

    private void Init()
    {
        sw_settings_traffic = (SwitchCompat) viewRoot.findViewById(R.id.sw_settings_traffic);
        sw_settings_traffic.setOnCheckedChangeListener(this);

        sw_settings_hook = (SwitchCompat) viewRoot.findViewById(R.id.sw_settings_hook);
        sw_settings_hook.setOnCheckedChangeListener(this);

        sw_settings_card_color_random = (SwitchCompat) viewRoot.findViewById(R.id.sw_settings_card_color_random);
        sw_settings_card_color_random.setOnCheckedChangeListener(this);

        toolbar = (Toolbar) viewRoot.findViewById(R.id.toolbar2);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        activity.setTitle("Настройки");

        iv1 = (ImageView) viewRoot.findViewById(R.id.iv1);
        iv1.setOnClickListener(this);
        iv2 = (ImageView) viewRoot.findViewById(R.id.iv2);
        iv2.setOnClickListener(this);
        cv = (CardView) viewRoot.findViewById(R.id.natcv);

        xiaomi = (Button) viewRoot.findViewById(R.id.xiaomi);
        xiaomi.setOnClickListener(this);

        appset = (Button) viewRoot.findViewById(R.id.appset);
        appset.setOnClickListener(this);

        help = (Button) viewRoot.findViewById(R.id.help);
        help.setOnClickListener(this);

        navigationView = (NavigationView)activity.findViewById(R.id.nav_view);
    }

    private void loadSettings()
    {
        APP_PREFERENCES = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);

        sw_settings_hook.setOnCheckedChangeListener(null);
        sw_settings_traffic.setOnCheckedChangeListener(null);
        sw_settings_card_color_random.setOnCheckedChangeListener(null);

        sw_settings_hook.setChecked(APP_PREFERENCES.getBoolean(SETTINGS_HOOK,true));
        sw_settings_traffic.setChecked(APP_PREFERENCES.getBoolean(SETTINGS_TRAFFIC,true));
        sw_settings_card_color_random.setChecked(APP_PREFERENCES.getBoolean(CARD_COLOR_RANDOM,false));

        sw_settings_hook.setOnCheckedChangeListener(this);
        sw_settings_traffic.setOnCheckedChangeListener(this);
        sw_settings_card_color_random.setOnCheckedChangeListener(this);
    }

    private void saveSettings()
    {
        APP_PREFERENCES = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor settings = APP_PREFERENCES.edit();

        settings.putBoolean(SETTINGS_HOOK,sw_settings_hook.isChecked());
        settings.putBoolean(SETTINGS_TRAFFIC,sw_settings_traffic.isChecked());
        settings.putBoolean(CARD_COLOR_RANDOM,sw_settings_card_color_random.isChecked());
        settings.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
    {
        saveSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_settings, container,false);
        context=container.getContext();
        viewRoot=v;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Init();
        loadSettings();
        nativeAdViewAppWall = (NativeAdViewAppWall) activity.findViewById(R.id.native_settings);
        ((MainActivity)getActivity()).ad.setLoadADListener(this);
        Show(context,nativeAdViewAppWall,cv,((MainActivity)getActivity()).nativeAds);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        id=SETTINGS++;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        navigationView.getMenu().findItem(R.id.nav_settings).setChecked(false);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ((MainActivity)getActivity()).ad.resetLoadADListener(this);
        saveSettings();
    }

    @Override
    public void onClick(View view)
    {
        ArrayList<Integer> dr = new ArrayList<Integer>();
        switch (view.getId())
        {

            case R.id.iv1:
                dr.add(R.drawable.auto);
                showDrawable(context, dr,0);
                break;

            case R.id.iv2:
                dr.add(R.drawable.s1);
                dr.add(R.drawable.s2);
                showDrawable(context, dr,0);
                break;

            case R.id.xiaomi:
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case R.id.help:
                try
                {
                    Intent intent = new Intent(context, GuideActivity.class);
                    intent.putExtra("help", true);
                    startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;

            case R.id.appset:

                try {


                    if (context == null)
                    {
                        return;
                    }
                    final Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + context.getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(i);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void LoadAd(boolean load, List<NativeAd> nativeAds)
    {
        Show(context,nativeAdViewAppWall,cv,nativeAds);
    }
}
