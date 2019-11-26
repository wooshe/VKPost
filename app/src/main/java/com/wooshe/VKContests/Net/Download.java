package com.wooshe.VKContests.Net;


import android.content.Intent;
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

public class Download extends AsyncTask<Void, String, String>
{

    DownloadListener downloadListener;
    String Dir="Image";
    String Extensible=".jpg";
    String url;
    String Filename;
    String result="";
    String id="";
    Integer exitcode=0;

    public interface DownloadListener
    {
        void DownloadedSuccessfully(String path, String id ,Integer exitcode);
    }

    public void setDownloadListener(DownloadListener listener)
    {
        this.downloadListener = listener;
    }

    public Download(String url, String filename,String id)
    {
        this.url=url;
        this.Filename=filename+Extensible;
        this.id=id;
    }

    public final static boolean DeleteFile(String path)
    {
        File f = new File(path);
        if (f.exists())
        {
            if (f.delete())
            {
                return true;
            }
        }
        return false;
    }

    public final static File GetDirectory(String Dir)
    {
        File localFile =  getApplicationContext().getExternalFilesDir(null);

        localFile = new File(localFile.getParent(),Dir);

        if (!localFile.exists())
        {
            if (!localFile.mkdirs())
            {
                localFile=null;
            }
        }

        if(localFile==null)
        {
            localFile =  getApplicationContext().getFilesDir();

            localFile = new File(localFile.getParent(),Dir);
            if (!localFile.exists())
            {
                if (!localFile.mkdirs())
                {
                    return null;
                }
            }
        }
        return localFile;
    }

    public final static String DownloadImage(String Dir, String Filename,String url, boolean round)
    {
        Bitmap bmp = null;
        FileOutputStream fos=null;
        String result = ERROR;

        File localFile = GetDirectory(Dir);

        if(localFile==null)
            return result;

        File file = new File(localFile,Filename);

        try
        {
            InputStream in = new java.net.URL(url).openStream();
            bmp = BitmapFactory.decodeStream(in);
            if(round)
            {
                bmp = Images.Corner(bmp, 5);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return result;
        }
        try
        {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        catch (FileNotFoundException e)
        {
            return result;
        }
        finally
        {
            try
            {
                if(fos!=null)
                {
                    fos.close();
                    result = file.getPath();
                    return result;
                }
            }
            catch (IOException e)
            {

            }
        }
        return result;
    }

    @Override
    protected String doInBackground(Void... params)
    {

        result=DownloadImage(Dir,Filename,url,false);

        if(result.equals(ERROR))
        {
            this.exitcode=0;
        }
        else
        {
            this.exitcode=1;
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        downloadListener.DownloadedSuccessfully(result, this.id, this.exitcode);
    }
}