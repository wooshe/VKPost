package com.wooshe.VKContests.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import static com.wooshe.VKContests.Util.Images.Corner;

public class Fragment_Photo extends Fragment implements View.OnClickListener
{

    private Context context;
    private View viewRoot;
    private ImageView imageViewPhoto;
    private TextView textViewPhoto;
    public int id = -1;
    public String path_photo_604;
    public String text_photo;

    public static Fragment_Photo newInstance(String text_photo, String path_photo_604)
    {
        Bundle args = new Bundle();
        args.putString("text_photo",text_photo);
        args.putString("path_photo_604",path_photo_604);
        Fragment_Photo fragment = new Fragment_Photo();
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
        imageViewPhoto = (ImageView) viewRoot.findViewById(R.id.imageViewPhoto);
        imageViewPhoto.setOnClickListener(this);
        textViewPhoto = (TextView) viewRoot.findViewById(R.id.textPhoto);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_photo, container,false);
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
        text_photo = getArguments().getString("text_photo",null);
        path_photo_604 = getArguments().getString("path_photo_604",null);
        try
        {
            Picasso.with(context).load(new File(path_photo_604)).into(imageViewPhoto);
            imageViewPhoto.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {

        }

        textViewPhoto.setText(text_photo);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.imageViewPhoto:
                ((Fragment_Detail) getParentFragment()).showImages();
                break;
        }
    }
}
