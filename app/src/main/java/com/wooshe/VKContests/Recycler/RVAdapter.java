package com.wooshe.VKContests.Recycler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.native_ad.views.NativeAdViewContentStream;
import com.squareup.picasso.Picasso;
import com.wooshe.R;
import com.wooshe.VKContests.Activity_Full_Photo;
import com.wooshe.VKContests.MainActivity;
import com.wooshe.VKContests.Net.Util;
import com.wooshe.VKContests.Realm.Event;
import com.wooshe.VKContests.Realm.User;
import com.wooshe.VKContests.UI.LoadAd;
import com.wooshe.VKContests.Util.OtherUtil;
import com.wooshe.VKContests.Util.SnackBar;
import com.wooshe.VKContests.Util.UtilDate;
import com.wooshe.VKContests.no_use.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.wooshe.VKContests.Activity_Full_Photo.showImage;
import static com.wooshe.VKContests.Net.Util.DeleteCache;
import static com.wooshe.VKContests.Net.Util.DeletePostFromWall;
import static com.wooshe.VKContests.Net.Util.GroupLeave;
import static com.wooshe.VKContests.Net.Util.ResetAlarm;
import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_NOTIFY;
import static com.wooshe.VKContests.no_use.Constants.ALARM_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.CARD_COLOR_RANDOM;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;
import static com.wooshe.VKContests.no_use.Constants.NET_ACCESS_ERROR;
import static com.wooshe.VKContests.no_use.Constants.OK;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_ALL;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_END;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_NEW;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_REPOST;
import static com.wooshe.VKContests.no_use.Constants.RECYCLER_TYPE_SOON;
import static com.wooshe.VKContests.no_use.Constants.WID;
import static com.wooshe.VKContests.no_use.Constants.event_state_end;
import static com.wooshe.VKContests.no_use.Constants.event_state_new;
import static com.wooshe.VKContests.no_use.Constants.event_state_repost;
import static com.wooshe.VKContests.no_use.Constants.event_state_soon;
import static com.wooshe.VKContests.no_use.Constants.type_photo;

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RealmChangeListener,LoadAd.LoadADListener

{
    RVAdapterListener rvadapterListener;
    private int previousPosition = 0;
    private Context context;
    private Realm mRealm;
    private Activity act;
    public int lm;
    private final RealmResults<Event> events;
    public int type = 0;
    public static final int TYPE_ITEM = 0;
    public static final int count_per_ads = 5;
    public TextView txt_empty;
    public List<Integer> pos = new ArrayList<>();
    public int cc = 0;
    public int typerec = 0;
    public ViewPager vp;
    public List<NativeAd> nativeAds = new ArrayList<>();
    float dpWidth;
    int wPart1;



    public void setVP(ViewPager v)
    {
        this.vp=v;
    }

    public void setLay(int lm)
    {
        this.lm=lm;
    }

    public void setTypeRec(int rec)
    {
        typerec = rec;
    }

    public void setType(int type)
    {
        this.type=type;
        switch (type)
        {
            case RECYCLER_TYPE_ALL:

            case RECYCLER_TYPE_NEW:
                events.sort("event_date_add", Sort.DESCENDING);
                break;

            case RECYCLER_TYPE_REPOST:

            case RECYCLER_TYPE_SOON:
                events.sort("event_date_repost", Sort.DESCENDING);
                break;

            case RECYCLER_TYPE_END:
                events.sort("event_date_end", Sort.DESCENDING);
                break;

        }
    }

    public void setViewEmpty(TextView txt_empty)
    {
        this.txt_empty = txt_empty;
        if(getItemCount()==0)
            txt_empty.setVisibility(View.VISIBLE);
        else
            txt_empty.setVisibility(View.GONE);
    }

    public void SetPos()
    {
        int count = 0;
        pos.clear();
        if(events.size()>=1)
        {
            pos.add(0);
        }
        for(int i = 0; i<events.size();i++)
        {
            count++;
            if(count==count_per_ads)
            {
                pos.add(i);
                count=0;
            }
        }
    }

    public RVAdapter(Activity act, Context context, Realm mRealm, RealmResults<Event> books) {
        this.act = act;
        this.mRealm = mRealm;
        this.context = context;
        events = books;
        events.addChangeListener(this);
        SetPos();
        cc = act.getResources().getColor(R.color.card_default);
    }

    public interface RVAdapterListener {
        void RVAdapterEvent(String event, Integer position, String wid, CardView cv);
    }

    public void setRVAdapterListener(RVAdapterListener listener) {
        this.rvadapterListener = listener;
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            PopupMenu.OnMenuItemClickListener,
            PopupMenu.OnDismissListener,
            View.OnLongClickListener,
            View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener,
            MainActivity.MainActivityListener
    {
        public CardView cv;
        public TextView item_name;
        public TextView item_group;
        public TextView rec_item_short_description;
        public ImageButton rec_item_options;
        public TextView rec_item_data_add;
        public int color;
        public TextView rec_item_desc;
        public TextView label_rec_item_data_repost;
        public TextView label_rec_item_data_notif;
        public TextView label_rec_item_data_end;
        public TextView rec_item_data_repost;
        public TextView rec_item_data_notif;
        public TextView rec_item_data_end;
        public TextView rec_item_data_post;
        public TextView rec_item_data_addd;
        public TextView label_rec_item_data_add;
        public CardView rcad;
        public View recycler_item_indicator;
        public ImageButton rec_item_detail;
        public LinearLayout llrd;
        public LinearLayout ll_event_name;
        public LinearLayout ll_short_desc;
        public RelativeLayout relativeMain;
        public RelativeLayout relativeprogress;
        public RelativeLayout relativePart1;
        public RelativeLayout relativePart2;
        public RelativeLayout relativePart3;
        public ProgressBar pb;
        public TextView t1;
        public TextView t2;
        public TextView t3;
        public int p = -1;
        public ImageView ivPhotoEvent;
        public Boolean card_color_random = false;
        AlertDialog.Builder builder;

        public ViewHolderItem(View itemView)
        {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            cv.setOnClickListener(this);
            item_name = (TextView) itemView.findViewById(R.id.event_name);
            item_group = (TextView) itemView.findViewById(R.id.group_name);
            rec_item_short_description = (TextView) itemView.findViewById(R.id.rec_item_short_description);
            rec_item_data_add = (TextView) itemView.findViewById(R.id.rec_item_data_add);
            rec_item_options = (ImageButton) itemView.findViewById(R.id.rec_item_options);
            rec_item_options.setOnClickListener(this);
            recycler_item_indicator = (View) itemView.findViewById(R.id.recycler_item_indicator);
            rec_item_detail = (ImageButton) itemView.findViewById(R.id.rec_item_detail);
            rec_item_detail.setOnClickListener(this);
            label_rec_item_data_add = (TextView) itemView.findViewById(R.id.label_rec_item_data_add);
            pb = (ProgressBar) itemView.findViewById(R.id.progressBars);
            rec_item_desc = (TextView) itemView.findViewById(R.id.rec_item_desc);
            rec_item_data_repost = (TextView) itemView.findViewById(R.id.rec_item_data_repost);
            rec_item_data_notif = (TextView) itemView.findViewById(R.id.rec_item_data_notif);
            rec_item_data_end = (TextView) itemView.findViewById(R.id.rec_item_data_end);
            rec_item_data_post = (TextView) itemView.findViewById(R.id.rec_item_data_post);
            rec_item_data_addd = (TextView) itemView.findViewById(R.id.rec_item_data_addd);


            relativeMain= (RelativeLayout) itemView.findViewById(R.id.relativeMain);
            relativeprogress= (RelativeLayout) itemView.findViewById(R.id.relativeprogress);
            relativePart1= (RelativeLayout) itemView.findViewById(R.id.relativePart1);
            relativePart2= (RelativeLayout) itemView.findViewById(R.id.relativePart2);
            relativePart3= (RelativeLayout) itemView.findViewById(R.id.relativePart3);
            t1 = (TextView) itemView.findViewById(R.id.t1);
            t2 = (TextView) itemView.findViewById(R.id.t2);
            t3 = (TextView) itemView.findViewById(R.id.t3);

            label_rec_item_data_repost = (TextView) itemView.findViewById(R.id.label_rec_item_data_repost);
            label_rec_item_data_notif = (TextView) itemView.findViewById(R.id.label_rec_item_data_notif);
            label_rec_item_data_end = (TextView) itemView.findViewById(R.id.label_rec_item_data_end);

            llrd = (LinearLayout) itemView.findViewById(R.id.llrd);
            ll_event_name = (LinearLayout) itemView.findViewById(R.id.ll_event_name);
            ll_short_desc = (LinearLayout) itemView.findViewById(R.id.ll_short_desc);
            ivPhotoEvent = (ImageView) itemView.findViewById(R.id.ivPhotoEvent);
            ivPhotoEvent.setOnClickListener(this);
            cv.setOnLongClickListener(this);
            MainActivity m = (MainActivity) act;
            m.setMainActivityListener(this);
            rcad = (CardView) itemView.findViewById(R.id.rcad);
            SharedPreferences APP_PREFERENCES = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
            card_color_random = APP_PREFERENCES.getBoolean(CARD_COLOR_RANDOM, false);
            color = context.getResources().getColor(R.color.card_default);
            if (card_color_random)
            {
                Random random = new Random();
                switch (random.nextInt(5) + 1)
                {
                    case 1:
                        color = context.getResources().getColor(R.color.card_4);
                        break;
                    case 2:
                        color = context.getResources().getColor(R.color.card_1);
                        break;
                    case 3:
                        color = context.getResources().getColor(R.color.card_2);
                        break;
                    case 4:
                        color = context.getResources().getColor(R.color.card_3);
                        break;
                    case 5:
                        color = context.getResources().getColor(R.color.card_5);
                        break;
                }
            }
            cv.setCardBackgroundColor(color);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = act.getMenuInflater();
            ((CardView)v).setCardBackgroundColor(context.getResources().getColor(R.color.card_selected));
            inflater.inflate(R.menu.menu_recycler_item_context, menu);
            menu.getItem(0).setOnMenuItemClickListener(this);
            menu.getItem(1).setOnMenuItemClickListener(this);
            menu.getItem(2).setOnMenuItemClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.ivPhotoEvent:

                    ArrayList<String> photostr = new ArrayList<>();

                    for(int i = 0; i<events.get(getAdapterPosition()).getPhoto().size();i++)
                        photostr.add(events.get(getAdapterPosition()).getPhoto().get(i).getPath_photo_604());

                    if(photostr.size()==0)
                        break;

                    showImage(context,photostr,0);
                    break;
                case R.id.rec_item_options:
                    cv.setCardBackgroundColor(context.getResources().getColor(R.color.card_selected));
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.inflate(R.menu.menu_recycler_item_context);
                    popupMenu.setOnMenuItemClickListener(this);
                    popupMenu.setOnDismissListener(this);
                    popupMenu.show();
                    break;
                case R.id.cv:
                    int in =getAdapterPosition();
                    String s =  events.get(getAdapterPosition()).getWid();
                    rvadapterListener.RVAdapterEvent("menu_recycler_item_edit", in,s,cv);
                    break;

                case R.id.rec_item_detail:
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rec_item_detail.getLayoutParams();

                    if (llrd.getVisibility() == View.GONE) {
                        llrd.setVisibility(View.VISIBLE);
                        rec_item_data_add.setVisibility(View.GONE);
                        label_rec_item_data_add.setVisibility(View.GONE);
                        rec_item_detail.setImageResource(R.drawable.ic_up);
                        lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.llrd);
                    } else {
                        rec_item_data_add.setVisibility(View.VISIBLE);
                        label_rec_item_data_add.setVisibility(View.VISIBLE);
                        llrd.setVisibility(View.GONE);
                        rec_item_detail.setImageResource(R.drawable.ic_down);
                        lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.rec_item_data_add);
                    }
                    rec_item_detail.setLayoutParams(lp);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view)
        {
            switch (view.getId())
            {
                case R.id.cv:
                    onClick(rec_item_options);
                    return true;
            }
            return false;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            switch (item.getItemId()) {
                case R.id.menu_recycler_item_edit:
                    rvadapterListener.RVAdapterEvent("menu_recycler_item_edit", getAdapterPosition(), events.get(getAdapterPosition()).getWid(),cv);
                    return true;
                case R.id.menu_recycler_item_friend:
                    OtherUtil.Friend(act,events.get(getAdapterPosition()).getWid());
                    return true;
                case R.id.menu_recycler_item_delete:

                    int error = Util.isNetisLogin(context);
                    String text = "";

                    if(events.get(getAdapterPosition()).getEvent_state()==event_state_repost)
                    {
                        if(error != OK)
                        {
                            if(error==NET_ACCESS_ERROR)
                                text = context.getString(R.string.NetError);
                            else
                                text = context.getString(R.string.LoginError);
                            SnackBar.SnackBarShow(act.findViewById(R.id.mcl),text, Snackbar.LENGTH_SHORT);
                            return true;
                        }
                    }

                    rvadapterListener.RVAdapterEvent("menu_recycler_item_delete", getAdapterPosition(), events.get(getAdapterPosition()).getWid(),cv);

                    return true;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDismiss(PopupMenu menu) {
            if (builder == null)
                cv.setCardBackgroundColor(color);
        }

        @Override
        public void MainActivitySuccessfully(Menu menu) {
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh = null;
        View itemLayoutView;

        switch (viewType)
        {
            case TYPE_ITEM:
                if(typerec==1)
                    itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_ad, parent, false);
                else
                    itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_ad_u, parent, false);
                vh = new ViewHolderItem(itemLayoutView);
                break;
        }

        return vh;
    }

    @Override
    public void LoadAd(boolean load, List<NativeAd> nativeAds)
    {
        this.nativeAds=nativeAds;
        boolean b = false;
        vp = (ViewPager) act.findViewById(R.id.viewpager);
        int cur = vp.getCurrentItem();

        switch (type)
        {
            case RECYCLER_TYPE_ALL:
                if(cur==0)
                    b=true;
                break;

            case RECYCLER_TYPE_NEW:
                if(cur==1)
                    b=true;
                break;

            case RECYCLER_TYPE_REPOST:
                if(cur==2)
                    b=true;
                break;

            case RECYCLER_TYPE_SOON:
                if(cur==3)
                    b=true;
                break;

            case RECYCLER_TYPE_END:
                if(cur==4)
                    b=true;
                break;

        }

        if(b)
        {
            for(int i = 0; i<pos.size();i++)
                notifyItemChanged(pos.get(i));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position)
    {
        try
        {
            ViewHolderItem holder = null;
            holder = (ViewHolderItem) viewHolder;
            holder.item_name.setText(events.get(position).getEvent_name());
            Event event = events.get(position);
            holder.p=position;
            holder.item_group.setText(events.get(position).getGroup_name());
            holder.rec_item_short_description.setText(events.get(position).getEvent_desc());
            if (holder.rec_item_short_description.getText().length() == 0)
                holder.ll_short_desc.setVisibility(View.GONE);
            else
                holder.ll_short_desc.setVisibility(View.VISIBLE);
            holder.rec_item_data_add.setText(UtilDate.DateunixTime(event.getEvent_date_add()));
            holder.rec_item_desc.setText(Html.fromHtml(event.getPost_text().replace("\n", "<br>"), null, null));
            holder.rec_item_data_post.setText(UtilDate.DateunixTime(event.getPost_date_create()));
            holder.rec_item_data_addd.setText(UtilDate.DateunixTime(event.getEvent_date_add()));

            if(event.isSwitch_notif())
            {
                holder.rec_item_data_notif.setText(UtilDate.DateunixTime(event.getEvent_date_notif()));
                holder.label_rec_item_data_notif.setVisibility(View.VISIBLE);
                holder.rec_item_data_notif.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.rec_item_data_notif.setVisibility(View.GONE);
                holder.label_rec_item_data_notif.setVisibility(View.GONE);
            }
            if(event.isSwitch_repost())
            {
                holder.rec_item_data_repost.setText(UtilDate.DateunixTime(event.getEvent_date_repost()));
                holder.label_rec_item_data_repost.setVisibility(View.VISIBLE);
                holder.rec_item_data_repost.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.rec_item_data_repost.setVisibility(View.GONE);
                holder.label_rec_item_data_repost.setVisibility(View.GONE);
            }

            if(event.isSwitch_end())
            {
                holder.rec_item_data_end.setText(UtilDate.DateunixTime(event.getEvent_date_end()));
                holder.label_rec_item_data_end.setVisibility(View.VISIBLE);
                holder.rec_item_data_end.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.rec_item_data_end.setVisibility(View.GONE);
                holder.label_rec_item_data_end.setVisibility(View.GONE);
            }



            if(event.getPhoto().size()>0)
            {
                holder.ivPhotoEvent.setVisibility(View.VISIBLE);
                if(event.getPhoto().get(0).getPath_photo_130()!=null)
                Picasso.with(context).load(new File(event.getPhoto().get(0).getPath_photo_130())).into(holder.ivPhotoEvent);
                else
                    holder.ivPhotoEvent.setVisibility(View.GONE);
            }
            else
                holder.ivPhotoEvent.setVisibility(View.GONE);


            switch (events.get(position).getEvent_state())
            {
                case event_state_end:
                    holder.recycler_item_indicator.setBackgroundResource(R.drawable.gradient_red);
                    break;

                case event_state_new:
                    holder.recycler_item_indicator.setBackgroundResource(R.drawable.gradient_green);
                    break;

                case event_state_repost:
                    holder.recycler_item_indicator.setBackgroundResource(R.drawable.gradient_blue);
                    break;

                case event_state_soon:
                    holder.recycler_item_indicator.setBackgroundResource(R.drawable.gradient_yellow);
                    break;
            }


            RelativeLayout.LayoutParams lpProgress = (RelativeLayout.LayoutParams) holder.relativeprogress.getLayoutParams();
            Display display = act.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                holder.pb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#018e89"), PorterDuff.Mode.SRC_IN);
            }

            dpWidth  = outMetrics.widthPixels;

            if(lm==2)
            {
                dpWidth = dpWidth/2-15;
            }

            List<Long> l = new ArrayList<>();
            holder.relativePart1.setVisibility(View.GONE);
            holder.relativePart2.setVisibility(View.GONE);
            holder.relativePart3.setVisibility(View.GONE);

            Long rep = event.getEvent_date_repost().getTime();
            Long en = event.getEvent_date_end().getTime();
            Long not = event.getEvent_date_notif().getTime();

            if(event.isSwitch_repost())
            {
                l.add(rep);
            }

            if(event.isSwitch_notif())
            {
                l.add(not);
            }

            if(event.isSwitch_end())
            {
                l.add(en);
            }

            switch (l.size())
            {
                case 1:
                    holder.relativePart1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    holder.relativePart1.setVisibility(View.VISIBLE);
                    holder.relativePart2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    holder.relativePart1.setVisibility(View.VISIBLE);
                    holder.relativePart2.setVisibility(View.VISIBLE);
                    holder.relativePart3.setVisibility(View.VISIBLE);
                    break;
            }

            Collections.sort(l);
            if(l.size()==0)
            {
                holder.relativeMain.setVisibility(View.GONE);
            }
            else
            {
                wPart1 = (int) (dpWidth/l.size());
                lpProgress.width=0;

                for (int i = 0; i<l.size();i++)
                {
                    if(i==0)
                    {
                        lpProgress.width += getProgress(event.getPost_date_create().getTime(), l.get(i));
                    }
                    else
                        {
                        lpProgress.width += getProgress(l.get(i - 1), l.get(i));
                        }
                        TextView temp;

                    String str = "";

                    if(l.get(i)==rep)
                    {
                        str="Репост\n"+ UtilDate.String_unixTimeWithoutTime(rep);
                    }
                    else if(l.get(i)==en)
                    {
                        str="Удалить\n"+ UtilDate.String_unixTimeWithoutTime(en);
                    }
                    else if(l.get(i)==not)
                    {
                        str="Напомнить\n"+ UtilDate.String_unixTimeWithoutTime(not);
                    }

                    switch (i)
                    {
                        case 0:
                            holder.t1.setText(str);
                            break;

                        case 1:
                            holder.t2.setText(str);
                            break;

                        case 2:
                            holder.t3.setText(str);
                            break;
                    }
                }
                holder.relativeprogress.setLayoutParams(lpProgress);
            }

        }
        catch ( java.lang.ClassCastException e)
        {

        }
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder)
    {
        super.onViewAttachedToWindow(holder);

        this.nativeAds = ((MainActivity)act).nativeAds;

        int po = ((ViewHolderItem) holder).p;

        ((ViewHolderItem) holder).rcad.removeAllViews();
        ((ViewHolderItem) holder).rcad.setVisibility(View.GONE);

        if(po!=-1 && pos.contains(po))
        {

            try
            {

                NativeAd ad = null;
                Random random = new Random();
                if(nativeAds.size()==1)
                {
                    ad = nativeAds.get(0);
                }
                else if(nativeAds.size()>1)
                {
                    int adpos = random.nextInt(nativeAds.size());
                    ad = nativeAds.get(adpos);
                }

                if(ad!=null)
                {
                    if(po>2)
                    {
                        NativeAdViewContentStream nativeAdView = new NativeAdViewContentStream(act, ad);
                        ((ViewHolderItem) holder).rcad.setCardBackgroundColor(cc);
                        ((ViewHolderItem) holder).rcad.addView(nativeAdView);
                        ((ViewHolderItem) holder).rcad.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        NativeAdViewAppWall nativeAdViews = new NativeAdViewAppWall(act, ad);

                        ((ViewHolderItem) holder).rcad.setCardBackgroundColor(cc);
                        ((ViewHolderItem) holder).rcad.addView(nativeAdViews);
                        ((ViewHolderItem) holder).rcad.setVisibility(View.VISIBLE);
                    }
                }
            }
            catch (Exception e)
            {

            }
        }

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    private int getProgress(Long ad, Long endF)
    {
        int progress = 0;

        Long add = ad;
        Long c = System.currentTimeMillis();

        int st = (int)(add/86400000);
        int end = (int)(endF/86400000);
        int cur = (int)(c/86400000);
        int procent = 0;
        int diff = end - st;
        int gg = cur - st;
        if(gg==0)
            gg=1;
        if(diff>=1)
        {
            procent = 100*gg/diff;
            double d = (double) procent/100.0*(double) wPart1;
            progress = (int) d;
        }
        else
        {
            progress = (int) dpWidth;
        }


        if(progress>dpWidth)
            progress = (int) dpWidth;

        return progress;
    }

    @Override
    public int getItemCount()
    {
        return events.size();
    }

    public void remove(int pos)
    {
        Event obj = events.get(pos);
        int user_id;
        RealmConfiguration nrealmConfiguration = new RealmConfiguration.Builder(context)
                .name("User")
                .build();
        Realm userRealm = OpenTable(nrealmConfiguration);

        RealmResults<User> user = userRealm.where(User.class).findAll();
        if(user.isEmpty())
        {
            user_id=-1;
        }
        else
        {
            user_id=user.get(0).getUser_id();
        }
        userRealm.close();
        boolean error = false;
        if(obj.getEvent_state()==event_state_repost)
        {
            error = DeletePostFromWall(user_id, obj.getRepost_id());
        }
        if(error==false)
        {
            String wid = obj.getWid();
            GroupLeave(mRealm, obj.getFriends());
            mRealm.beginTransaction();
            DeleteCache(obj);
            obj.removeFromRealm();
            mRealm.commitTransaction();
            ResetAlarm(context,wid,ALARM_TYPE_REPOST);
            ResetAlarm(context,wid,ALARM_TYPE_NOTIFY);
            ResetAlarm(context,wid,ALARM_TYPE_END);
            Toast.makeText(context, "Удалено!", Toast.LENGTH_SHORT).show();
            SetPos();
            this.notifyItemRemoved(pos);
        }
    }

    @Override
    public void onChange()
    {
        try {
            if(getItemCount()==0 && txt_empty!=null)
                txt_empty.setVisibility(View.VISIBLE);
            else
                txt_empty.setVisibility(View.GONE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        notifyDataSetChanged();
        SetPos();
    }

    public void Sort(int type)
    {
        switch (type)
        {
            case 0:
                events.sort("event_date_add", Sort.ASCENDING);
                break;

            case 1:
                events.sort("event_date_add", Sort.DESCENDING);
                break;

            case 2:
                events.sort("switch_repost", Sort.ASCENDING);
                break;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position)
    {
        return TYPE_ITEM;
    }
}
