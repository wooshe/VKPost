package com.wooshe.VKContests.UI.Guide;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wooshe.R;

public class Fragment_Guide_32 extends Fragment
{

    private Context context;
    private View viewRoot;

    public static Fragment_Guide_32 newInstance()
    {
        Fragment_Guide_32 fragment = new Fragment_Guide_32();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void InitView()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_guide_32, container,false);
        if(container!=null)
            context=container.getContext();
        else
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        viewRoot=v;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        InitView();
    }
}
