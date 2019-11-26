package com.wooshe.VKContests.UI;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import com.wooshe.R;
import com.wooshe.VKContests.MainActivity;
import com.wooshe.VKContests.Net.Download;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Realm.User;
import com.wooshe.VKContests.Recycler.RVAdapter;
import com.wooshe.VKContests.no_use.ViewPagerAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.google.android.gms.internal.zzagr.runOnUiThread;
import static com.wooshe.VKContests.Net.Download.DeleteFile;
import static com.wooshe.VKContests.Net.Util.checkPost;
import static com.wooshe.VKContests.Net.Util.isLogin;
import static com.wooshe.VKContests.Net.Util.isNetAccess;
import static com.wooshe.VKContests.Net.Util.isNetisLogin;
import static com.wooshe.VKContests.Net.Util.restartEvent;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.Util.OtherUtil.GetCountOfLaunch;
import static com.wooshe.VKContests.Util.OtherUtil.GetRateInfo;
import static com.wooshe.VKContests.Util.OtherUtil.SetCountOfLaunch;
import static com.wooshe.VKContests.Util.OtherUtil.SetRateInfo;
import static com.wooshe.VKContests.Util.SnackBar.SnackBarShow;
import static com.wooshe.VKContests.no_use.Constants.MAIN;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.OK;
import static com.wooshe.VKContests.no_use.Constants.RATE_BEFORE;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_ALL;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_NEW;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_SOON;
import static com.wooshe.VKContests.no_use.Constants.USER;
import static com.wooshe.VKContests.no_use.Constants.event_state_end;
import static com.wooshe.VKContests.no_use.Constants.event_state_repost;

public class Fragment_Main extends Fragment implements
        View.OnClickListener,
        Download.DownloadListener,ViewPager.OnPageChangeListener
{
    Context context;
    private boolean mTwoPane;
    private VKList vkresponse = null;
    private VKApiUser vkuser = null;
    private TextView txt_navigation_name;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView image_navigation_avatar;
    private Toolbar toolbar;
    private View header;
    private AppBarLayout appbarLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawerLayout;
    private Button btn_logins;
    private Button btn_navigation_wall;
    private Button btn_navigation_groups;
    private Realm mRealmUser;
    private Realm mRealm;
    private Fragment_Recycler fragmentRecycler;
    private RelativeLayout navigation_main_group_relativelayout;
    private CollapsingToolbarLayout maincollapsing;
    public int id = -1;
    private User user;
    private View viewRoot;
    private Activity activity;
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

    private void InitView()
    {
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);

        viewPager = (ViewPager) activity.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        appbarLayout = (AppBarLayout) activity.findViewById(R.id.mainappbar);

        navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);

        txt_navigation_name = (TextView) header.findViewById(R.id.txt_navigation_name);
        image_navigation_avatar = (ImageView) header.findViewById(R.id.image_navigation_avatar);
        navigation_main_group_relativelayout = (RelativeLayout) header.findViewById(R.id.navigation_main_group_relativelayout);

        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        btn_logins = (Button) header.findViewById(R.id.btn_navigation_logins);
        btn_logins.setOnClickListener(this);

        btn_navigation_wall = (Button) header.findViewById(R.id.btn_navigation_wall);
        btn_navigation_wall.setOnClickListener(this);

        btn_navigation_groups = (Button) header.findViewById(R.id.btn_navigation_groups);
        btn_navigation_groups.setOnClickListener(this);
        maincollapsing = (CollapsingToolbarLayout) activity.findViewById(R.id.maincollapsing);
    }

    private void SettingView()
    {
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        toggle.syncState();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_main, container,false);
        context=container.getContext();
        setHasOptionsMenu(true);
        viewRoot=v;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        InitView();
        SettingView();

        RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(context)
                .name("User")
                .build();
        mRealmUser = OpenTable(nrealmConfiguration);
        user = mRealmUser.where(User.class).findFirst();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(activity).build();
        mRealm=OpenTable(realmConfiguration);


        AlertDialog.Builder builder;
        int count_of_launch = GetCountOfLaunch(activity);


        if(GetRateInfo(activity)==false && count_of_launch>=RATE_BEFORE)
        {
            builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
            SetCountOfLaunch(activity,0);
            builder.setTitle("Нравится ВК Конкурсы?");
            builder.setMessage("Хотите оценить это приложение?");
            builder.setPositiveButton("Да", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    SetRateInfo(activity);
                    final String appPackageName = activity.getPackageName();
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    }
                    catch (android.content.ActivityNotFoundException anfe)
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
            builder.setNegativeButton("Позже", null);
            builder.setNeutralButton("Не показывать", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    SetRateInfo(activity);
                }
            });

            builder.show();
        }

        if(isNetisLogin(activity)==OK)
            aboutUser();

    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(viewPager,activity,getChildFragmentManager());

        fragmentRecycler = Fragment_Recycler.newInstance(RECYCLER_TYPE_ALL);
        adapter.addFragment(fragmentRecycler, "Все");

        fragmentRecycler = Fragment_Recycler.newInstance(RECYCLER_TYPE_NEW);
        adapter.addFragment(fragmentRecycler, "Новые");

        fragmentRecycler = Fragment_Recycler.newInstance(RECYCLER_TYPE_REPOST);
        adapter.addFragment(fragmentRecycler, "На стене");

        fragmentRecycler = Fragment_Recycler.newInstance(RECYCLER_TYPE_SOON);
        adapter.addFragment(fragmentRecycler, "Запланированые");

        fragmentRecycler = Fragment_Recycler.newInstance(RECYCLER_TYPE_END);
        adapter.addFragment(fragmentRecycler, "Законченные");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    public void refreshui(Context context)
    {
        boolean login = isLogin();
        boolean network = isNetAccess(context);

        if (login)
        {
            btn_logins.setText("Выйти");
            navigation_main_group_relativelayout.setVisibility(View.VISIBLE);

            User user = mRealmUser.where(User.class).findFirst();
            if(user!=null)
            {
                image_navigation_avatar.setImageBitmap(BitmapFactory.decodeFile(user.getPath_photo_200()));
                maincollapsing.setTitle(user.getFirst_name()+" "+user.getLast_namel());
                txt_navigation_name.setText(user.getFirst_name()+" "+user.getLast_namel());
            }

        }
        else
        {
            btn_logins.setText("Войти");
            navigation_main_group_relativelayout.setVisibility(View.GONE);
            maincollapsing.setTitle(" ");
        }
    }

    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {
            case R.id.btn_navigation_logins:
                if (VKSdk.isLoggedIn() == true)
                {
                    Toast.makeText(context, "Вы вышли", Toast.LENGTH_SHORT).show();
                    VKSdk.logout();
                    deleteUser();
                    refreshui(context);
                }
                else if (isNetAccess(context))
                {
                    Intent intent = new Intent(activity, VKServiceActivity.class);
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

            case R.id.btn_navigation_wall:
                if(user!=null)
                {

                    try
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getUrl()));
                        intent.setPackage("com.vkontakte.android");
                        startActivity(intent);
                    }
                    catch (android.content.ActivityNotFoundException anfe)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(user.getUrl()));
                        startActivity(intent);
                    }

                }
                break;

            case R.id.btn_navigation_groups:
                if(user!=null)
                {
                    try
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/groups"));
                        intent.setPackage("com.vkontakte.android");
                        startActivity(intent);
                    }
                    catch (android.content.ActivityNotFoundException anfe)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/groups"));
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    protected void deleteUser()
    {
        mRealmUser.beginTransaction();
        RealmResults<User> users = mRealmUser.where(User.class).findAll();

        if(!users.isEmpty())
        {
            for(int i = users.size() - 1; i >= 0; i--)
            {
                String path = users.get(i).getPath_photo_200();
                if(path!=null)
                {
                    DeleteFile(path);
                }
                users.get(i).removeFromRealm();
            }
        }
        mRealmUser.commitTransaction();
    }

    protected void aboutUser()
    {

            VKParameters parameters = new VKParameters();
            parameters.put(VKApiConst.FIELDS, "access_token,photo_200,photo_100,photo_50,photo_400,has_mobile");
            VKRequest vkr = VKApi.users().get(parameters);
            vkr.executeWithListener(new VKRequest.VKRequestListener()
            {
                @Override
                public void onComplete(VKResponse response)
                {
                    super.onComplete(response);

                    try {

                        vkresponse = (VKList) response.parsedModel;
                        vkuser = (VKApiUser) vkresponse.get(0);


                        txt_navigation_name.setText(vkuser.first_name + " " + vkuser.last_name);
                        maincollapsing.setTitle(vkuser.first_name + " " + vkuser.last_name);


                        mRealmUser.beginTransaction();
                        user = mRealmUser.where(User.class).equalTo("user_id", vkuser.id).findFirst();

                        if (user == null) {
                            user = mRealmUser.createObject(User.class);
                            user.setUser_id(vkuser.id);
                        }
                        int mobile = 0;
                        try {
                            mobile = (int) vkresponse.get(0).fields.get("has_mobile");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        user.setMobile(mobile);
                        user.setFirst_name(vkuser.first_name);
                        user.setLast_namel(vkuser.last_name);
                        user.setUrl(USER + vkuser.id);
                        mRealmUser.commitTransaction();

                        Download d1 = new Download(vkuser.photo_200, vkuser.id + "_photo_200", "photo_200");
                        d1.setDownloadListener(Fragment_Main.this);
                        d1.execute();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
    }

    @Override
    public void DownloadedSuccessfully(String path, String id, Integer exitcode)
    {
        if (exitcode == 1)
        {
            switch (id)
            {
                case "photo_200":

                    try
                    {
                        mRealmUser.beginTransaction();
                        User user = mRealmUser.where(User.class).findFirst();
                        if(user!=null)
                        {
                            user.setPath_photo_200(path);
                        }
                        mRealmUser.commitTransaction();

                        image_navigation_avatar.setImageBitmap(BitmapFactory.decodeFile(user.getPath_photo_200()));
                    }
                    catch (Exception e)
                    {

                    }

                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>()
        {
            @Override
            public void onResult(VKAccessToken res)
            {
                Toast.makeText(context, "Авторизация успешна ", Toast.LENGTH_SHORT).show();
                if(isNetisLogin(activity)==OK)
                aboutUser();
                refreshui(context);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_global, menu);
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_global_add:

                try
                {
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = cm.getPrimaryClip();

                    if (clipData != null)
                    {
                        String wid = clipData.getItemAt(0).getText().toString();
                        if (!wid.contains("vk.com/wall"))
                        {
                            SnackBarShow(activity.findViewById(R.id.mcl),"Нет ссылок в буфере обмена!",Snackbar.LENGTH_SHORT);
                            return true;
                        }
                        cm.setPrimaryClip(ClipData.newPlainText("simple text",clipData.getItemAt(0).getText()));
                    }

                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return true;

            default:
                break;
        }

        FragmentStatePagerAdapter fspa = (FragmentStatePagerAdapter) viewPager.getAdapter();
        Fragment_Recycler frz = (Fragment_Recycler) fspa.getItem(viewPager.getCurrentItem());


        if(frz.onOptionsItemSelected(item))
                return true;
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(isNetisLogin(activity)==OK)
        {
            if(user!=null && user.isValid())
            {
                int User_id = user.getUser_id();

                RealmResults<Event> sort = mRealm.where(Event.class)
                        .equalTo("event_state",event_state_repost)
                        .findAll();

                for(int i =0; i<sort.size();i++)
                {
                    Event event = sort.get(i);
                    if(checkPost(User_id,event.getRepost_id())==false)
                    {
                        restartEvent(context,mRealm,event);
                        mRealm.beginTransaction();
                        event.setEvent_state(event_state_end);
                        mRealm.commitTransaction();
                    }
                }
            }
        }

        refreshui(context);
        navigationView.getMenu().findItem(R.id.nav_main).setChecked(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        id=MAIN++;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        navigationView.getMenu().findItem(R.id.nav_main).setChecked(false);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mRealmUser!=null)
        mRealmUser.close();

        if(mRealm!=null)
            mRealm.close();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position)
    {
        try {
            ((RVAdapter) ((Fragment_Recycler) ((ViewPagerAdapter) viewPager.getAdapter()).getFR(position)).recyclerView.getAdapter()).LoadAd(false,((MainActivity) activity).nativeAds);
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
