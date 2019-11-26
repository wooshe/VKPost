package com.wooshe.VKContests;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.Native;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import com.wooshe.R;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.UI.Fragment_Detail;
import com.wooshe.VKContests.UI.LoadAd;
import com.wooshe.VKContests.Util.ParallaxPageTransformer;
import com.wooshe.VKContests.no_use.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.Util.OtherUtil.VINTER;
import static com.wooshe.VKContests.no_use.Constants.ACT_DET;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.POSITION_EVENT;
import static com.wooshe.VKContests.no_use.Constants.POSITION_RECYCLER;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_ALL;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_NEW;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_SOON;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.event_state_end;
import static com.wooshe.VKContests.no_use.Constants.event_state_new;
import static com.wooshe.VKContests.no_use.Constants.event_state_repost;
import static com.wooshe.VKContests.no_use.Constants.event_state_soon;

public class Activity_Detail extends AppCompatActivity implements
        ViewPager.OnPageChangeListener,
        Fragment_Detail.Callbacks,
        Fragment_Detail.FRD
{
    private Realm mRealm;
    private Context context;
    private ViewPager viewPager;
    private RealmResults<Event> events;
    public int id = -1;
    private Intent intent;
    private ViewPagerAdapter adapter;
    private int type=-1;
    private int event_position = -1;
    private String widparam = "";

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
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    protected void Init()
    {
        context=getApplicationContext();
        viewPager = (ViewPager) findViewById(R.id.viewpager_detail);
        ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.btn_detail_repost, 2, -1))
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.labelRepost, -1, 2))
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.labelDelete, 2, -1))
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.labelNotify, -1, 2))
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.detailappbar, 2, -1))
                ;
        viewPager.setPageTransformer(true,pageTransformer);
    }

    protected void setupViewPager(ViewPager viewPager)
    {
        try
        {
            adapter = new ViewPagerAdapter(viewPager,this,getSupportFragmentManager());

            for(int i = 0; i< events.size(); i++)
            {
                mRealm.beginTransaction();
                String wid = events.get(i).getWid();
                if(event_position==-1 && widparam.equals(wid))
                {
                    event_position=i;
                }
                Fragment_Detail  fr =  Fragment_Detail.newInstance(wid);
                fr.setDownloadListener(this);
                adapter.addFragment(fr, "Все" + String.valueOf(i));
                mRealm.commitTransaction();
            }
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(event_position);
            viewPager.addOnPageChangeListener(this);
            setTitle(viewPager.getCurrentItem()+1+" из "+viewPager.getAdapter().getCount());
        }
        catch (Exception e)
        {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        id=ACT_DET++;

        Init();
        intent = getIntent();
        createViewPager(intent);

        String appKey = "6fec25389afb4e82f46323a3b3f08b0289bc37ff5b7380f6";
        Appodeal.setBannerViewId(R.id.full_photo_banner);
        Appodeal.setTesting(false);
        Appodeal.setAutoCache(Appodeal.NATIVE,false);
        Appodeal.setNativeAdType(Native.NativeAdType.Auto);

        Appodeal.initialize(this, appKey,Appodeal.NATIVE | Appodeal.BANNER_VIEW | Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO);

        Appodeal.setAutoCacheNativeIcons(true);
        Appodeal.setAutoCacheNativeMedia(true);

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

        ((Fragment_Detail) ((ViewPagerAdapter) viewPager.getAdapter()).getFR(viewPager.getCurrentItem())).SH(nativeAds);
    }

    public void Get()
    {
        Appodeal.cache(this,Appodeal.NATIVE);
    }

    public List<NativeAd> nativeAds = new ArrayList<>();
    private Timer mTimer;
    boolean load = true;
    int count = 0;

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

        setTitle(viewPager.getCurrentItem()+1+" из "+viewPager.getAdapter().getCount());
    }

    @Override
    public void onPageSelected(int position)
    {
        setTitle(viewPager.getCurrentItem()+1+" из "+viewPager.getAdapter().getCount());

        ((Fragment_Detail) ((ViewPagerAdapter) viewPager.getAdapter()).getFR(position)).SH(nativeAds);

        try{
            VINTER(this);
        }
        catch (Exception e) {}
    }

    private void createViewPager(Intent intent)
    {
        event_position = intent.getIntExtra(POSITION_EVENT,-1);
        widparam=intent.getStringExtra(WID);
        type=intent.getIntExtra(RECYCLER_TYPE,-1);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        mRealm = OpenTable(realmConfiguration);
        events = mRealm.where(Event.class).findAll();


        switch (type)
        {
            case RECYCLER_TYPE_ALL:
                events.sort("event_date_add", Sort.DESCENDING);
                break;

            case RECYCLER_TYPE_NEW:
                events = mRealm.where(Event.class)
                        .equalTo("event_state",event_state_new)
                        .findAll();
                events.sort("event_date_add", Sort.DESCENDING);
                break;

            case RECYCLER_TYPE_REPOST:
                events = mRealm.where(Event.class)
                        .equalTo("event_state",event_state_repost)
                        .findAll();
                events.sort("event_date_repost", Sort.DESCENDING);
                break;

            case RECYCLER_TYPE_SOON:

                events = mRealm.where(Event.class)
                        .equalTo("event_state",event_state_soon)
                        .findAll();
                events.sort("event_date_repost", Sort.DESCENDING);
                break;

            case RECYCLER_TYPE_END:
                events = mRealm.where(Event.class)
                        .equalTo("event_state",event_state_end)
                        .findAll();
                events.sort("event_date_end", Sort.DESCENDING);
                break;
        }

        setupViewPager(viewPager);
        setTitle((viewPager.getCurrentItem()+1)+" из "+ events.size());
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if(intent.getIntExtra(RECYCLER_TYPE,-1)!=type)
            createViewPager(intent);
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
    protected void onDestroy()
    {
        super.onDestroy();
        if (mTimer != null)
        {
            mTimer.cancel();
        }
        mRealm.close();

    }

    @Override
    public void onBackPressed()
    {
        intent.putExtra(POSITION_RECYCLER,viewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_EVENT, event_position);
        outState.putInt(RECYCLER_TYPE, type);
    }

    @Override
    public void frd(int pos)
    {
        adapter = null;
        viewPager.setAdapter(adapter);
        event_position=pos--;
        setupViewPager(viewPager);
    }

    @Override
    public void FragmentCallback()
    {
        Integer p =1;
    }
}
