package com.wooshe.VKContests;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.Native;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.wooshe.R;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Recycler.RVAdapter;
import com.wooshe.VKContests.Service.ClipService;
import com.wooshe.VKContests.Service.Util;
import com.wooshe.VKContests.UI.Fragment_About;
import com.wooshe.VKContests.UI.Fragment_Detail;
import com.wooshe.VKContests.UI.Fragment_Main;
import com.wooshe.VKContests.UI.Fragment_Recycler;
import com.wooshe.VKContests.UI.Fragment_Settings;
import com.wooshe.VKContests.UI.LoadAd;
import com.wooshe.VKContests.Util.SnackBar;
import com.wooshe.VKContests.no_use.Constants;
import com.wooshe.VKContests.no_use.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.wooshe.VKContests.Net.Util.isNetisLogin;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.Service.Util.isMyServiceRunning;
import static com.wooshe.VKContests.Util.OtherUtil.INTERVIDEO;
import static com.wooshe.VKContests.Util.OtherUtil.IncreaseCountOfLaunch;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_ALL;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_DOWNLOAD;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_END;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_NEW;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_NOTIFY;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_REPOST;
import static com.wooshe.VKContests.no_use.Constants.APP_NOTIFICATION_TAG;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.OK;
import static com.wooshe.VKContests.no_use.Constants.POSITION_EVENT;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_NEW;
import static com.wooshe.VKContests.no_use.Constants.REQUEST_CODE_ACTIVITY_DETAIL;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.event_state_new;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        Fragment_Detail.Callbacks
{
    private static long back_pressed;
    private static long back_pressed_time = 2000;
    private FrameLayout framelayout;
    private FragmentTransaction fragmentTransaction;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View header;
    private ActionBarDrawerToggle toggle;
    private Realm mRealm;
    private Context context;
    MainActivityListener mainActivityListener;
    public LoadAd ad;
    public List<NativeAd> nativeAds = new ArrayList<>();
    private Timer mTimer;
    boolean load = true;
    int count = 0;

    public interface MainActivityListener
    {
        void MainActivitySuccessfully(Menu menu);
    }

    public void setMainActivityListener(MainActivityListener mainActivityListener)
    {
        this.mainActivityListener = mainActivityListener;
    }

    private void Init()
    {
        context = getApplicationContext();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        framelayout = (FrameLayout) findViewById(R.id.framelayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        IncreaseCountOfLaunch(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences APP_PREFERENCES = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        int first_launch = APP_PREFERENCES.getInt("First_launch",0);


        if(first_launch==0)
        {

            Intent intent = new Intent(this, GuideActivity.class);
            intent.putExtra("help", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        Init();

        Fragment_Main frmain = (Fragment_Main)getSupportFragmentManager().findFragmentByTag("MAIN");

        if (frmain == null)
        {
            Fragment_Main fragment_main = new Fragment_Main();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.framelayout, fragment_main,"MAIN");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        }

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        mRealm=OpenTable(realmConfiguration);

        Intent Getintent = getIntent();
        String action = Getintent.getAction();
        String type = Getintent.getType();

        Intent clipServiceintent = new Intent(this, ClipService.class);

        if (Intent.ACTION_SEND.equals(action) && type != null)
        {
            if ("text/plain".equals(type))
            {
                String sharedText = Getintent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null)
                {
                    clipServiceintent.putExtra(WID,sharedText);
                    stopService(clipServiceintent);
                    startService(clipServiceintent);
                }
            }
        }
        else if (!isMyServiceRunning(context, ClipService.class))
        {
            startService(clipServiceintent);
        }

        Util.refresh(context,3);


        String appKey = "6fec25389afb4e82f46323a3b3f08b0289bc37ff5b7380f6";
        Appodeal.setBannerViewId(R.id.full_photo_banner);
        Appodeal.setTesting(false);
        Appodeal.setAutoCache(Appodeal.NATIVE,false);
        Appodeal.setNativeAdType(Native.NativeAdType.Auto);
        Appodeal.requestAndroidMPermissions( this, null);

        Appodeal.initialize(this, appKey,Appodeal.NATIVE | Appodeal.BANNER_VIEW | Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO);

        Appodeal.setAutoCacheNativeIcons(true);
        Appodeal.setAutoCacheNativeMedia(true);
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean b) {

            }

            @Override
            public void onInterstitialFailedToLoad() {

            }

            @Override
            public void onInterstitialShown() {

            }

            @Override
            public void onInterstitialClicked() {

            }

            @Override
            public void onInterstitialClosed() {

            }
        });

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded() {

            }

            @Override
            public void onRewardedVideoFailedToLoad() {

            }

            @Override
            public void onRewardedVideoShown() {

            }

            @Override
            public void onRewardedVideoFinished(int i, String s) {

            }

            @Override
            public void onRewardedVideoClosed(boolean b) {

            }
        });

        Appodeal.setNativeCallbacks(new NativeCallbacks()
        {
            @Override
            public void onNativeLoaded()
            {
                Post();
            }

            @Override
            public void onNativeFailedToLoad()
            {
            }

            @Override
            public void onNativeShown(com.appodeal.ads.NativeAd nativeAd)
            {

            }

            @Override
            public void onNativeClicked(com.appodeal.ads.NativeAd nativeAd)
            {

            }
        });

        ad = new LoadAd();

    }

    public void Post()
    {
        nativeAds.addAll(Appodeal.getNativeAds(1));

        if(nativeAds.size()<2)
            Get();
        else if(nativeAds.size()>2)
        {
            nativeAds.remove(0);
        }

        try {
            ViewPager vp = findViewById(R.id.viewpager);
            ViewPagerAdapter a = (ViewPagerAdapter) vp.getAdapter();


            ((RVAdapter) ((Fragment_Recycler) a.getFR(vp.getCurrentItem())).recyclerView.getAdapter()).LoadAd(false,nativeAds);
        }
        catch (Exception e)
        {

        }

    }

    public void Get()
    {
        if(isNetisLogin(this)==OK)
        Appodeal.cache(this,Appodeal.NATIVE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Appodeal.onResume(this, Appodeal.BANNER_VIEW);

        if (mTimer != null)
        {
            mTimer.cancel();
            count=0;
            load=true;
        }
        mTimer = new Timer();

        mTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable( )
                {
                    @Override
                    public void run()
                    {
                        Get();
                    }
                });
            }
        }, 1000, 30000);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.cancel(APP_NOTIFICATION_TAG,APP_NOTIFICATION_ALL);
        notificationManager.cancel(APP_NOTIFICATION_TAG,APP_NOTIFICATION_DOWNLOAD);
        notificationManager.cancel(APP_NOTIFICATION_TAG,APP_NOTIFICATION_REPOST);
        notificationManager.cancel(APP_NOTIFICATION_TAG,APP_NOTIFICATION_END);
        notificationManager.cancel(APP_NOTIFICATION_TAG,APP_NOTIFICATION_NOTIFY);
        notificationManager.cancel(APP_NOTIFICATION_TAG,APP_NOTIFICATION_NEW);
        RealmResults<Event> sort= mRealm.where(Event.class)
                .equalTo("event_state",event_state_new)
                .findAll();

        View.OnClickListener oncl = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, Activity_Detail.class);
                intent.putExtra(RECYCLER_TYPE,RECYCLER_TYPE_NEW);
                intent.putExtra(POSITION_EVENT, 0);
                startActivity(intent);
            }
        };

        if(!sort.isEmpty() && findViewById(R.id.mcl)!=null)
        {
            SnackBar.SnackBarShow(findViewById(R.id.mcl), "Имеются новые задачи!", Snackbar.LENGTH_LONG,"Открыть",oncl);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mTimer != null)
        {
            mTimer.cancel();
        }
    }

    @Override
    public void FragmentCallback()
    {
            Intent intent = new Intent(this, Activity_Detail.class);
            intent.putExtra(POSITION_EVENT, 0);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY_DETAIL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_global_settings:
                INTERVIDEO(this);
                goToSettings();

                return true;

            case R.id.menu_global_exit:
                finish();
                return true;

            case R.id.menu_global_about:
                INTERVIDEO(this);
                Fragment_About frsabout = (Fragment_About)getSupportFragmentManager().findFragmentByTag("ABOUT");
                if (frsabout == null)
                {
                    Fragment_About fragment_about = new Fragment_About();
                    fragmentTransaction= getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment_about,"ABOUT");
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.addToBackStack("ABOUT");
                    fragmentTransaction.commit();
                }
                else
                {
                    getSupportFragmentManager().popBackStack("ABOUT",0);
                }
                return true;

            case R.id.menu_global_help:
                try
                {

                    Intent intent = new Intent(context, GuideActivity.class);
                    intent.putExtra("help", true);
                    startActivity(intent);
                    INTERVIDEO(this);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;

            default:
                break;
        }
        return false;
    }

    public void goToSettings()
    {
        Fragment_Settings frsettings = (Fragment_Settings)getSupportFragmentManager().findFragmentByTag("SETTINGS");
        if (frsettings == null)
        {
            Fragment_Settings fragment_settings = new Fragment_Settings();
            fragmentTransaction= getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.framelayout, fragment_settings,"SETTINGS");
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onContextMenuClosed(Menu menu)
    {
        super.onContextMenuClosed(menu);
        mainActivityListener.MainActivitySuccessfully(menu);
    }

    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0)
            {
                ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
                for (int i = 0; i < root.getChildCount(); i++)
                {
                    View child = root.getChildAt(i);
                    Object tag = child.getTag();
                    if (tag != null && tag.equals("appodeal"))
                    {
                        root.removeView(child);
                        break;
                    }
                }

                fm.popBackStack();
            }
            else
            {
                if (back_pressed + back_pressed_time > System.currentTimeMillis())
                {
                    super.onBackPressed();
                }
                else
                {
                    Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
                }
                back_pressed = System.currentTimeMillis();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {

            case R.id.nav_main:
                INTERVIDEO(this);
                Fragment_Main frmain = (Fragment_Main)getSupportFragmentManager().findFragmentByTag("MAIN");

                while (getSupportFragmentManager().getBackStackEntryCount() > 0)
                {
                    getSupportFragmentManager().popBackStackImmediate();
                }

                if (frmain == null)
                {
                    Fragment_Main fragment_main = new Fragment_Main();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment_main,"MAIN");
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.addToBackStack("MAIN");
                    fragmentTransaction.commit();
                }
                else
                {
                    getSupportFragmentManager().popBackStack();
                }

                break;

            case R.id.nav_settings:
                INTERVIDEO(this);
                Fragment_Settings frsettings = (Fragment_Settings)getSupportFragmentManager().findFragmentByTag("SETTINGS");
                if (frsettings == null)
                {
                    Fragment_Settings fragment_settings = new Fragment_Settings();
                    fragmentTransaction= getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment_settings,"SETTINGS");
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.addToBackStack("SETTINGS");
                    fragmentTransaction.commit();
                }
                else
                {
                    getSupportFragmentManager().popBackStack("SETTINGS",0);
                }

                break;

            case R.id.nav_about:
                INTERVIDEO(this);
                Fragment_About frsabout = (Fragment_About)getSupportFragmentManager().findFragmentByTag("ABOUT");
                if (frsabout == null)
                {
                    Fragment_About fragment_about = new Fragment_About();
                    fragmentTransaction= getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.framelayout, fragment_about,"ABOUT");
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.addToBackStack("ABOUT");
                    fragmentTransaction.commit();
                }
                else
                {
                    getSupportFragmentManager().popBackStack("ABOUT",0);
                }

                break;

            case R.id.nav_exit:
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mTimer != null)
        {
            mTimer.cancel();
        }
        mRealm.close();

    }
}