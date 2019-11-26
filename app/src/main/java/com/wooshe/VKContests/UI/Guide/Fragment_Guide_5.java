package com.wooshe.VKContests.UI.Guide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wooshe.R;
import com.wooshe.VKContests.no_use.Constants;

public class Fragment_Guide_5 extends Fragment implements View.OnClickListener
{

    private Context context;
    private View viewRoot;
    private Button start;

    public static Fragment_Guide_5 newInstance()
    {
        Fragment_Guide_5 fragment = new Fragment_Guide_5();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void InitView()
    {
        start = (Button) viewRoot.findViewById(R.id.start);
        start.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_guide_5, container,false);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.start:
                SharedPreferences APP_PREFERENCES = getActivity().getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor settings = APP_PREFERENCES.edit();
                settings.putInt("First_launch",1);
                settings.commit();
                getActivity().finish();
                break;
        }
    }
}
