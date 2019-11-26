package com.wooshe.VKContests.UI;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeMediaView;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.wooshe.R;
import com.wooshe.VKContests.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;

public class LoadAd
{

    public List<LoadADListener> loadADListener = new ArrayList<>();

    public void Update(boolean load, List<NativeAd> nativeAds)
    {
        for(int i = 0;i<this.loadADListener.size();i++)
            this.loadADListener.get(i).LoadAd(load, nativeAds);
    }

    public interface LoadADListener
    {
        void LoadAd(boolean load, List<NativeAd> nativeAds);
    }

    public void resetLoadADListener(LoadADListener listener)
    {
        this.loadADListener.remove(listener);
    }

    public void setLoadADListener(LoadADListener listener)
    {
        this.loadADListener.add(listener);
    }

    public final static NativeAd Show(Context ctx, Object adView, CardView cv, List<NativeAd> nativeAds)
    {
        Random random = new Random();
        NativeAd ad = null;
        try
        {
            if(cv!=null)
                cv.setVisibility(View.GONE);
            if(adView instanceof NativeAdViewAppWall)
            {
                ((NativeAdViewAppWall) adView).setVisibility(View.GONE);
            }

            if(adView instanceof NativeAdViewContentStream)
            {
                ((NativeAdViewContentStream) adView).setVisibility(View.GONE);
            }

            int adpos = 0;
            if(nativeAds.size()==0)
            {
                return null;
            }
            if(nativeAds.size()>1)
            {
                adpos = random.nextInt(nativeAds.size());
            }
            ad = nativeAds.get(adpos);

            if(ad!=null)
            {
                if(adView instanceof NativeAdViewAppWall)
                {
                    ((NativeAdViewAppWall) adView).setNativeAd(ad);
                    ((NativeAdViewAppWall) adView).setVisibility(View.VISIBLE);
                }

                if(adView instanceof NativeAdViewContentStream)
                {
                    ((NativeAdViewContentStream) adView).setNativeAd(ad);
                    ((NativeAdViewContentStream) adView).setVisibility(View.VISIBLE);
                }

            }

        }
        catch (Exception e)
        {

        }
        if(cv!=null)
            cv.setVisibility(View.VISIBLE);

        return ad;
    }
}
