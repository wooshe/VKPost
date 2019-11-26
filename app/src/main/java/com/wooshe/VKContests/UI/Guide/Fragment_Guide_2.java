package com.wooshe.VKContests.UI.Guide;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKError;
import com.wooshe.BuildConfig;
import com.wooshe.R;
import com.wooshe.VKContests.GuideActivity;

import java.util.ArrayList;

import static com.wooshe.VKContests.Activity_Full_Photo.showDrawable;
import static com.wooshe.VKContests.Net.Util.isNetAccess;

public class Fragment_Guide_2 extends Fragment implements View.OnClickListener {

    private Context context;
    private View viewRoot;
    private Button auth;
    private ImageView iv2;
    ArrayList scopes = new ArrayList<>();


    public void setScope()
    {
        scopes.add(VKScope.FRIENDS);
        scopes.add(VKScope.MESSAGES);
        scopes.add(VKScope.STATS);
        scopes.add(VKScope.GROUPS);
        scopes.add(VKScope.WALL);
        scopes.add(VKScope.OFFLINE);
        scopes.add(VKScope.PAGES);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>()
        {
            @Override
            public void onResult(VKAccessToken res)
            {
                auth.setEnabled(false);
                auth.setText("Авторизация пройдена!");
                Toast.makeText(context, "Авторизация успешна ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error)
            {
                Toast.makeText(context, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            }
        }))
        {
            Toast.makeText(context, "Внутрення ошибка", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Fragment_Guide_2 newInstance()
    {
        Fragment_Guide_2 fragment = new Fragment_Guide_2();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void InitView()
    {
        auth = (Button) viewRoot.findViewById(R.id.auth);
        auth.setOnClickListener(this);

        iv2 = (ImageView) viewRoot.findViewById(R.id.iv2);
        iv2.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_guide_2, container,false);
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
            case R.id.iv2:

                ArrayList<Integer> dr = new ArrayList<Integer>();
                dr.add(R.drawable.side);
                showDrawable(context, dr,0);

                break;

            case R.id.auth:
                if (isNetAccess(context))
            {
                Intent intent = new Intent(getActivity(), VKServiceActivity.class);
                intent.putExtra("arg1", "Authorization");
                setScope();
                intent.putStringArrayListExtra("arg2", scopes);
                intent.putExtra("arg4", VKSdk.isCustomInitialize());
                startActivityForResult(intent, VKServiceActivity.VKServiceType.Authorization.getOuterCode());
            }
            else
            {
                Toast.makeText(context, "Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
            }
                break;
        }
    }
}
