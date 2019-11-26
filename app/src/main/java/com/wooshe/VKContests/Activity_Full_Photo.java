package com.wooshe.VKContests;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.Native;
import com.squareup.picasso.Picasso;
import com.wooshe.R;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.UI.Fragment_Detail;
import com.wooshe.VKContests.UI.Fragment_Full_Photo;
import com.wooshe.VKContests.UI.Fragment_Photo;
import com.wooshe.VKContests.no_use.ViewPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_END;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_NOTIF;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_REPOST;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_TYPE;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.type_drawable;
import static com.wooshe.VKContests.no_use.Constants.type_photo;


public class Activity_Full_Photo extends AppCompatActivity
{

    private ViewPager viewPagerPhoto;
    private TabLayout photo_tab;

    private Intent intent;
    List<String> photo = new ArrayList<>();
    List<Integer> drawable = new ArrayList<>();
    int position = 0;

    public static final void showDrawable(Context context, ArrayList<Integer> ids, int position)
    {
        Intent intent = new Intent(context, Activity_Full_Photo.class);
        intent.putExtra("type",type_drawable);
        intent.putExtra("position",position);
        intent.putIntegerArrayListExtra("drawable",ids);
        context.startActivity(intent);
    }

    public static final void showImage(Context context, ArrayList<String> path, int position)
    {
        Intent intent = new Intent(context, Activity_Full_Photo.class);
        intent.putExtra("type",type_photo);
        intent.putExtra("position",position);
        intent.putStringArrayListExtra("photo",path);
        context.startActivity(intent);
    }


    private void InitViewpager()
    {
        viewPagerPhoto = (ViewPager) findViewById(R.id.viewpager_photo);
        photo_tab = (TabLayout) findViewById(R.id.photo_tab);
    }

    public void viewPagerPhotoSettings()
    {
        try
        {
            ViewPagerAdapter adapters = new ViewPagerAdapter(viewPagerPhoto, this,getSupportFragmentManager());
            for(int i=0;i<photo.size();i++)
            {
                String path = photo.get(i);
                if(path==null || path.length()==0)
                    continue;
                Fragment_Full_Photo fragment_photo = new Fragment_Full_Photo().newInstance(path);
                adapters.addFragment(fragment_photo," ");
            }
            viewPagerPhoto.setAdapter(adapters);
            viewPagerPhoto.setCurrentItem(position);
            photo_tab.setupWithViewPager(viewPagerPhoto);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void viewPagerDrawableSettings()
    {
        try
        {
            ViewPagerAdapter adapters = new ViewPagerAdapter(viewPagerPhoto, this,getSupportFragmentManager());
            for(int i=0;i<drawable.size();i++)
            {
                Integer path = drawable.get(i);
                if(path==null || path==0)
                    continue;
                Fragment_Full_Photo fragment_photo = new Fragment_Full_Photo().newInstance(path);
                adapters.addFragment(fragment_photo," ");
            }
            viewPagerPhoto.setAdapter(adapters);
            viewPagerPhoto.setCurrentItem(position);
            photo_tab.setupWithViewPager(viewPagerPhoto);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        intent = getIntent();

        setContentView(R.layout.activity_full_photo);
        position = intent.getIntExtra("position",0);
        InitViewpager();

        switch (intent.getIntExtra("type",type_photo))
        {
            case 0:
            photo = intent.getStringArrayListExtra("photo");
            viewPagerPhotoSettings();
                break;

            case 1:
            drawable = intent.getIntegerArrayListExtra("drawable");
            viewPagerDrawableSettings();
            break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        String appKey = "6fec25389afb4e82f46323a3b3f08b0289bc37ff5b7380f6";
        Appodeal.setBannerViewId(R.id.full_photo_banner);

        Appodeal.initialize(this, appKey,Appodeal.BANNER_VIEW);

        Appodeal.show(this, Appodeal.BANNER_VIEW);

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Appodeal.onResume(this, Appodeal.BANNER_VIEW);
    }
}