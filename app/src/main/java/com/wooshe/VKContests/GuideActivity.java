package com.wooshe.VKContests;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.wooshe.R;
import com.wooshe.VKContests.UI.Guide.Fragment_Guide_1;
import com.wooshe.VKContests.UI.Guide.Fragment_Guide_2;
import com.wooshe.VKContests.UI.Guide.Fragment_Guide_3;
import com.wooshe.VKContests.UI.Guide.Fragment_Guide_31;
import com.wooshe.VKContests.UI.Guide.Fragment_Guide_32;
import com.wooshe.VKContests.UI.Guide.Fragment_Guide_4;
import com.wooshe.VKContests.UI.Guide.Fragment_Guide_5;
import com.wooshe.VKContests.Util.Animation;
import com.wooshe.VKContests.Util.ParallaxPageTransformer;
import com.wooshe.VKContests.no_use.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.wooshe.VKContests.Util.ParallaxPageTransformer.ParallaxTransformInformation.PARALLAX_EFFECT_DEFAULT;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.type_drawable;
import static com.wooshe.VKContests.no_use.Constants.type_photo;


public class GuideActivity extends AppCompatActivity
{

    ViewPager viewPager;
    Context context;
    ViewPagerAdapter adapter;
    private TabLayout guide_tab;
    boolean help = false;

    @Override
    public void onBackPressed()
    {
        if(help)
            super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        help = getIntent().getBooleanExtra("help",false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        Init();
        createViewPager();

    }

    private void createViewPager()
    {
        try
        {
            adapter = new ViewPagerAdapter(viewPager,this,getSupportFragmentManager());
            Fragment fr;
            int i = 0;


            i++;
            fr =  Fragment_Guide_1.newInstance();
            adapter.addFragment(fr, String.valueOf(i));

            if(!help)
            {
                i++;
                fr =  Fragment_Guide_2.newInstance();
                adapter.addFragment(fr, String.valueOf(i));
            }

            i++;
            fr =  Fragment_Guide_3.newInstance();
            adapter.addFragment(fr, String.valueOf(i));

            i++;
            fr =  Fragment_Guide_31.newInstance();
            adapter.addFragment(fr, String.valueOf(i));

            i++;
            fr =  Fragment_Guide_32.newInstance();
            adapter.addFragment(fr, String.valueOf(i));

            i++;
            fr =  Fragment_Guide_4.newInstance();
            adapter.addFragment(fr, String.valueOf(i));

            i++;
            fr =  Fragment_Guide_5.newInstance();
            adapter.addFragment(fr, String.valueOf(i));

            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);
            guide_tab.setupWithViewPager(viewPager);
        }
        catch (Exception e)
        {

        }
    }

    private void Init()
    {
        context=getApplicationContext();
        viewPager = (ViewPager) findViewById(R.id.guideviewpager);
        ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.txt1, 2, 2))
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.txt2, -0.65f,PARALLAX_EFFECT_DEFAULT))
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.txt3, 2, 2))
                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.txt4, -0.65f,PARALLAX_EFFECT_DEFAULT))
                ;
        viewPager.setPageTransformer(true,pageTransformer);
        guide_tab = (TabLayout) findViewById(R.id.guide_tab);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }

}