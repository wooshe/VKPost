package com.wooshe.VKContests.no_use;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.wooshe.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragment_name = new ArrayList<>();

    FragmentManager fragmentManager;
    ViewPager viewPager;
    Activity act;

    public Fragment getFR(int pos)
    {
        return fragments.get(pos);
    }

    public ViewPagerAdapter(ViewPager viewPager, Activity act, FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        this.viewPager = viewPager;
        this.act=act;
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object)
    {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return fragment_name.get(position);
    }

    public void addFragment(Fragment fragment, String title)
    {
        fragments.add(fragment);
        fragment_name.add(title);
    }

    public int GetPos(Fragment fragment)
    {
        for(int i = 0; i<fragments.size();i++)
        {
            if(fragment.equals(fragments.get(i)))
                return i+1;
        }
        return 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        super.destroyItem(container, position, object);
    }

}