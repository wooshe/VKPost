package com.wooshe.VKContests.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wooshe.R;

import java.io.File;

import static com.wooshe.VKContests.no_use.Constants.type_drawable;
import static com.wooshe.VKContests.no_use.Constants.type_photo;

public class Fragment_Full_Photo extends Fragment
{

    private Context context;
    private View viewRoot;
    private ImageView image;
    public String path_photo;
    public int drawable_photo;

    public int type = type_photo;

    public static Fragment_Full_Photo newInstance(String path_photo)
    {
        Bundle args = new Bundle();
        args.putString("path_photo",path_photo);
        args.putInt("type",type_photo);
        Fragment_Full_Photo fragment = new Fragment_Full_Photo();
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment_Full_Photo newInstance(int drawable_photo)
    {
        Bundle args = new Bundle();
        args.putInt("drawable_photo",drawable_photo);
        args.putInt("type",type_drawable);
        Fragment_Full_Photo fragment = new Fragment_Full_Photo();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void InitView()
    {
        image = (ImageView) viewRoot.findViewById(R.id.image);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_full_photo, container,false);

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
        switch (getArguments().getInt("type",0))
        {
            case type_photo:
                path_photo = getArguments().getString("path_photo",null);
                try
                {
                    Picasso.with(context).load(new File(path_photo)).into(image);
                    image.setVisibility(View.VISIBLE);
                }
                catch (Exception e)
                {

                }
                break;

            case type_drawable:
                drawable_photo = getArguments().getInt("drawable_photo",0);
                try
                {
                    Picasso.with(context).load(drawable_photo).into(image);
                    image.setVisibility(View.VISIBLE);
                }
                catch (Exception e)
                {

                }
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
