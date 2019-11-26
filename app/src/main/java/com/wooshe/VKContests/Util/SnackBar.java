package com.wooshe.VKContests.Util;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.wooshe.R;

import static com.wooshe.VKContests.no_use.Constants.NET_ACCESS_ERROR;
import static com.wooshe.VKContests.no_use.Constants.OK;


public class SnackBar
{
    public static void SnackBarShow(View view, String text,int lenght,String buttonText, View.OnClickListener listener)
    {
        Snackbar mSnackbar = Snackbar.make(view, text, lenght);
        View snackbarView = mSnackbar.getView();
        TextView snackTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.WHITE);
        if(listener!=null)
        {
            mSnackbar.setAction(buttonText,listener);
        }

        mSnackbar.show();
    }

    public static void SnackBarShow(View view, String text,int lenght)
    {
        Snackbar mSnackbar = Snackbar.make(view, text, lenght);
        View snackbarView = mSnackbar.getView();
        TextView snackTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.WHITE);
        mSnackbar.show();
    }

    public static void SnackBarShow(Context context, View view, int error, int lenght)
    {
            String text = "";
            if(error==NET_ACCESS_ERROR)
                text = context.getString(R.string.NetError);
            else
                text = context.getString(R.string.LoginError);
            SnackBar.SnackBarShow(view,text, Snackbar.LENGTH_SHORT);
    }
}
