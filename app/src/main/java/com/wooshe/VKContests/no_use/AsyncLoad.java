package com.wooshe.VKContests.no_use;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.wooshe.VKContests.Util.Images;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.vk.sdk.VKUIHelper.getApplicationContext;
import static com.wooshe.VKContests.no_use.Constants.ERROR;
import static com.wooshe.VKContests.no_use.Constants.MyDebugTag;

public class AsyncLoad extends AsyncTask<Void, String, String>
{

    AsyncLoadListener asyncLoadListener;

    public interface AsyncLoadListener
    {
        void AsyncLoadSuccessfully();
    }

    public void setAsyncLoadListener(AsyncLoadListener listener)
    {
        this.asyncLoadListener = listener;
    }

    public AsyncLoad()
    {

    }

    @Override
    protected String doInBackground(Void... params)
    {

        return "";
    }

    @Override
    protected void onPostExecute(String result)
    {
        asyncLoadListener.AsyncLoadSuccessfully();
    }
}