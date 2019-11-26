package com.wooshe.VKContests;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wooshe.R;
import com.wooshe.VKContests.Realm.Event;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.no_use.Constants.WID;

public class Activity_Confirm_Repost extends AppCompatActivity implements View.OnClickListener
{

    private LinearLayout llconfirm;
    private List<View> views=new ArrayList<View>();
    private Realm mRealm;
    private Event event;
    private String wid=null;
    private Button btn_ok;
    private Button btn_close;
    private SwitchCompat swpin;


    private void init()
    {
        llconfirm = (LinearLayout) findViewById(R.id.llconfirm);
        btn_ok=(Button) findViewById(R.id.confirm_btn_ok);
        btn_ok.setOnClickListener(this);
        btn_close=(Button) findViewById(R.id.confirm_btn_close);
        btn_close.setOnClickListener(this);
        swpin = (SwitchCompat) findViewById(R.id.swpin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        init();

        Intent intent = getIntent();
        wid = intent.getStringExtra(WID);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        mRealm=OpenTable(realmConfiguration);

        if(wid!=null)
        {
            event = mRealm.where(Event.class).equalTo("wid", wid).findFirst();
            for(int i =0 ; i<event.getFriends().size();i++)
            {
                View group = getLayoutInflater().inflate(R.layout.confirm_item, null);
                TextView tv = (TextView) group.findViewById(R.id.url);
                tv.setText(Html.fromHtml(event.getFriends().get(i).getUrl(), null, null));
                llconfirm.addView(group);
                views.add(group);
            }
        }
        else
            finish();
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
            case R.id.confirm_btn_ok:
                mRealm.beginTransaction();
                for(int i =0 ; i<event.getFriends().size();i++)
                {
                    SwitchCompat sw = (SwitchCompat) views.get(i).findViewById(R.id.sw_confirm_ingroup);
                        event.getFriends().get(i).setToEnter(sw.isChecked());
                }
                event.setPin(swpin.isChecked());
                mRealm.commitTransaction();
                mRealm.close();
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.confirm_btn_close:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }

    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
    }
}
