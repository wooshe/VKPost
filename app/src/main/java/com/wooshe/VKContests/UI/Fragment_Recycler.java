package com.wooshe.VKContests.UI;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.wooshe.R;
import com.wooshe.VKContests.Activity_Confirm_Delete;
import com.wooshe.VKContests.Activity_Detail;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Recycler.RVAdapter;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.Util.OtherUtil.INTERVIDEO;
import static com.wooshe.VKContests.Util.OtherUtil.IncreaseInterCount;
import static com.wooshe.VKContests.no_use.Constants.INTER;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.POSITION_EVENT;
import static com.wooshe.VKContests.no_use.Constants.POSITION_RECYCLER;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_ALL;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_NEW;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_SOON;
import static com.wooshe.VKContests.no_use.Constants.REQUEST_CODE_ACTIVITY_DETAIL;
import static com.wooshe.VKContests.no_use.Constants.REQUEST_CODE_DELETE;
import static com.wooshe.VKContests.no_use.Constants.SETTINGS_VIEW_TYPE;
import static com.wooshe.VKContests.no_use.Constants.VIDEO;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.event_state_end;
import static com.wooshe.VKContests.no_use.Constants.event_state_new;
import static com.wooshe.VKContests.no_use.Constants.event_state_repost;
import static com.wooshe.VKContests.no_use.Constants.event_state_soon;

public class Fragment_Recycler extends Fragment implements
        View.OnClickListener,
        RVAdapter.RVAdapterListener,
        AdapterView.OnItemSelectedListener
{
    public int item = 0;
    public int id = -3;
    private Context context;
    public RecyclerView recyclerView;
    private RVAdapter adapter=null;
    private Realm mRealm;
    private Integer poss=0;
    private View viewRoot;
    private FrameLayout frltp = null;
    private NestedScrollView nstscrview=null;
    private SharedPreferences APP_PREFERENCES;
    public int type;
    private Activity activity;
    private int recyclerView_type_id;
    protected String event_det;
    protected Integer pos_det;
    protected String wid_det;
    private TextView txt_empty;
    private CardView cvv = null;

    public static Fragment_Recycler newInstance(int type)
    {
        Bundle args = new Bundle();
        args.putInt("RECYCLER_TYPE",type);
        Fragment_Recycler fragment = new Fragment_Recycler();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void RVAdapterEvent(String event, Integer position, String wid, CardView cv)
    {
        switch (event)
        {
            case "menu_recycler_item_edit":
                showDetail(event,position,wid);
                break;

            case "menu_recycler_item_delete":
                cvv = cv;
                cv.setCardBackgroundColor(context.getResources().getColor(R.color.card_selected));
                Intent intent = new Intent(context, Activity_Confirm_Delete.class);
                intent.putExtra(WID, wid);
                intent.putExtra(POSITION_EVENT,position);
                startActivityForResult(intent, REQUEST_CODE_DELETE);
                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (data == null)
        {
            return;
        }
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_CODE_ACTIVITY_DETAIL:
                    poss = data.getIntExtra(POSITION_RECYCLER,0);
                    initializeAdapter();
                    if(poss>0 && poss<=recyclerView.getAdapter().getItemCount())
                        recyclerView.scrollToPosition(poss);
                    break;

                case REQUEST_CODE_DELETE:
                    int position = data.getIntExtra(POSITION_EVENT,-1);
                    if(position!=-1)
                        ((RVAdapter) recyclerView.getAdapter()).remove(position);
                    break;
            }
        }
        else
        {
            switch (requestCode)
            {
                case REQUEST_CODE_DELETE:
                    break;
            }
        }
    }

    protected void Init()
    {
        recyclerView=(RecyclerView) viewRoot.findViewById(R.id.recyclerView);
        txt_empty = (TextView) viewRoot.findViewById(R.id.txt_empty);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadSettings()
    {
        APP_PREFERENCES = activity.getPreferences(MODE_PRIVATE);
        recyclerView_type_id = APP_PREFERENCES.getInt(SETTINGS_VIEW_TYPE, 1);
    }

    private void saveSettings()
    {
        APP_PREFERENCES = activity.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = APP_PREFERENCES.edit();
        ed.putInt(SETTINGS_VIEW_TYPE, recyclerView_type_id);
        ed.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_fragment_main_recycler_type:

                loadSettings();

                recyclerView_type_id++;
                if(recyclerView_type_id==3)
                    recyclerView_type_id=1;

                saveSettings();

                ViewPager vp = (ViewPager) activity.findViewById(R.id.viewpager);
                FragmentStatePagerAdapter fst = (FragmentStatePagerAdapter) vp.getAdapter();
                for (int i = 0;i<fst.getCount();i++)
                {
                    try
                    {
                        Fragment_Recycler fg = (Fragment_Recycler) fst.getItem(i);
                        fg.initializeAdapter();
                    }
                    catch (Exception e)
                    {

                    }
                }
                return true;
        }
        return false;
    }

    private RecyclerView.LayoutManager getLayoutManager(int recyclerView_type_id)
    {
        RecyclerView.LayoutManager lm = null;

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(activity);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        lm=mLinearLayoutManagerVertical;

        switch (recyclerView_type_id)
        {
            case 1:
                break;

            case 2:
                StaggeredGridLayoutManager mStaggeredVerticalLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                lm=mStaggeredVerticalLayoutManager;
                break;
        }
        return lm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_recycler, container,false);
        context=container.getContext();
        viewRoot=v;
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Init();
        type = getArguments().getInt(RECYCLER_TYPE,0);
        initializeAdapter();

        Fragment_Main fr = (Fragment_Main) getParentFragment();
    }


    private void showDetail(String event, Integer position, String wid)
    {

        if(event.equals("menu_recycler_item_edit"))
        {
                INTERVIDEO(getActivity());
                Intent intent = new Intent(context, Activity_Detail.class);
                intent.putExtra(POSITION_EVENT, position);
                intent.putExtra(RECYCLER_TYPE,type);
                startActivityForResult(intent, REQUEST_CODE_ACTIVITY_DETAIL);
                getActivity().overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
        }
        else if(event.equals("menu_recycler_long_click"))
        {

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void initializeAdapter()
    {
        RealmResults<Event> sort;
        adapter = new RVAdapter(activity,context,mRealm,mRealm.allObjects(Event.class));
        switch (type)
        {
            case RECYCLER_TYPE_ALL:
                adapter = new RVAdapter(activity,context,mRealm,mRealm.allObjects(Event.class));
                break;

            case RECYCLER_TYPE_REPOST:
                sort= mRealm.where(Event.class)
                        .equalTo("event_state",event_state_repost)
                        .findAll();
                adapter = new RVAdapter(activity,context,mRealm,sort);
                break;

            case RECYCLER_TYPE_SOON:

                sort= mRealm.where(Event.class)
                        .equalTo("event_state",event_state_soon)
                        .findAll();
                adapter = new RVAdapter(activity,context,mRealm,sort);
                break;

            case RECYCLER_TYPE_END:
                sort= mRealm.where(Event.class)
                        .equalTo("event_state",event_state_end)
                        .findAll();
                adapter = new RVAdapter(activity,context,mRealm,sort);
                break;

            case RECYCLER_TYPE_NEW:
                sort= mRealm.where(Event.class)
                        .equalTo("event_state",event_state_new)
                        .findAll();
                adapter = new RVAdapter(activity,context,mRealm,sort);
                break;
        }

        loadSettings();
        adapter.setViewEmpty(txt_empty);
        adapter.setType(type);
        adapter.setVP((ViewPager) activity.findViewById(R.id.viewpager));
        adapter.setRVAdapterListener(this);
        int lay = 1;
        if(recyclerView_type_id==2)
        {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float density  = getResources().getDisplayMetrics().density;
            float dpHeight = outMetrics.heightPixels / density;
            float dpWidth  = outMetrics.widthPixels / density;
            if(dpWidth<400)
                lay=2;
        }
        adapter.setLay(recyclerView_type_id);
        adapter.setTypeRec(lay);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(getLayoutManager(recyclerView_type_id));
        recyclerView.setLayoutAnimation(animobj());
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
    }

    LayoutAnimationController animobj ()
    {

        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(50);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);

        return controller;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        id=RECYCLER++;
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(getContext()).build();
        mRealm=OpenTable(realmConfiguration);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        adapter.Sort(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

}

