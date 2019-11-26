package com.wooshe.VKContests.UI.Guide;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wooshe.BuildConfig;
import com.wooshe.R;
import com.wooshe.VKContests.GuideActivity;

import java.util.ArrayList;

import static com.wooshe.VKContests.Activity_Full_Photo.showDrawable;

public class Fragment_Guide_31 extends Fragment implements View.OnClickListener
{

    private Context context;
    private View viewRoot;
    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;

    public static Fragment_Guide_31 newInstance()
    {
        Fragment_Guide_31 fragment = new Fragment_Guide_31();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void InitView()
    {
        iv1 = (ImageView) viewRoot.findViewById(R.id.iv1);
        iv2 = (ImageView) viewRoot.findViewById(R.id.iv2);
        iv3 = (ImageView) viewRoot.findViewById(R.id.iv3);
        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_guide_31, container,false);
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
        ArrayList<Integer> dr = new ArrayList<Integer>();
        dr.add(R.drawable.friend);
        dr.add(R.drawable.fr2);
        dr.add(R.drawable.paste);
        int pos = 0;

        switch (v.getId())
        {

            case R.id.iv1:

                pos = 0;
                break;

            case R.id.iv2:
                pos = 1;
                break;

            case R.id.iv3:

                pos = 2;
            break;
        }

        showDrawable(context, dr,pos);
    }
}
