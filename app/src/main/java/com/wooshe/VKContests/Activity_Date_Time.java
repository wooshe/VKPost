package com.wooshe.VKContests;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.wooshe.R;
import com.wooshe.VKContests.Realm.Event;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.wooshe.VKContests.Realm.Util.OpenTable;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_END;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_NOTIF;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_REPOST;
import static com.wooshe.VKContests.no_use.Constants.DATE_TIME_TYPE;
import static com.wooshe.VKContests.no_use.Constants.REFRESH_SERVICE_DAY;
import static com.wooshe.VKContests.no_use.Constants.WID;


public class Activity_Date_Time extends AppCompatActivity implements View.OnClickListener
{
    private DatePicker dp;
    private TimePicker tp;
    private Button btn_dt_save;
    private Button btn_dt_close;
    private Realm mRealm;
    private String wid=null;
    private Event event;
    private int date_time_type =0;
    private Intent intent;

    private void Init()
    {
        dp= (DatePicker) findViewById(R.id.datePicker);
        tp= (TimePicker) findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        btn_dt_save=(Button) findViewById(R.id.btn_dt_save);
        btn_dt_save.setOnClickListener(this);
        btn_dt_close=(Button) findViewById(R.id.btn_dt_close);
        btn_dt_close.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time);
        Init();
        intent = getIntent();
        wid = intent.getStringExtra(WID);
        setResult(RESULT_CANCELED,intent);
        date_time_type = intent.getIntExtra(DATE_TIME_TYPE,0);

        if(wid==null || date_time_type ==0)
            return;

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();

        mRealm=OpenTable(realmConfiguration);
        event = mRealm.where(Event.class).equalTo("wid",wid).findFirst();

        Date date = new Date();
        Date buf = null;
        Calendar calendar = Calendar.getInstance();

        switch (date_time_type)
        {
            case DATE_TIME_NOTIF:
                buf= event.getEvent_date_notif();
                break;

            case DATE_TIME_REPOST:
                buf= event.getEvent_date_repost();
                break;
            case DATE_TIME_END:
                buf= event.getEvent_date_end();
                break;
        }
        if(date!=null)
        {
            if(buf.after(date))
            {
                calendar.setTime(buf);
            }
            else
            {
                calendar.setTime(date);
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            dp.updateDate(year, month, day);
            tp.setCurrentHour(hour);
            tp.setCurrentMinute(minute);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mRealm.close();
        overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_dt_save:

                int year = dp.getYear();
                int month= dp.getMonth();
                int day= dp.getDayOfMonth();
                int hour= tp.getCurrentHour();
                int minutes= tp.getCurrentMinute();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,day,hour,minutes);
                Date date = calendar.getTime();

                mRealm.beginTransaction();
                switch (date_time_type)
                {
                    case DATE_TIME_NOTIF:
                        event.setEvent_date_notif(date);
                        break;

                    case DATE_TIME_REPOST:
                        event.setEvent_date_repost(date);
                        break;

                    case DATE_TIME_END:
                        event.setEvent_date_end(date);
                        break;
                }
                mRealm.commitTransaction();
                setResult(RESULT_OK,intent);
                finish();
                break;

            case R.id.btn_dt_close:
                setResult(RESULT_CANCELED,intent);
                finish();
                break;
        }
    }
}